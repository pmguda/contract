package com.pmguda.contract.api.common;

import com.pmguda.contract.api.domain.Guarantee;
import com.pmguda.contract.api.domain.Product;
import com.pmguda.contract.api.domain.QGuarantee;
import com.pmguda.contract.api.repository.GuaranteeRepository;
import com.pmguda.contract.api.repository.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.pmguda.contract.api.domain.QGuarantee.*;

@Component
@AllArgsConstructor
public class ContractUtil {

    private final ProductRepository prodRepo;
    private final GuaranteeRepository grntRepo;
    private final EntityManager em;

    /**
     * 상품코드에 따른 계약가능기간 유효성 체크
     * @param prodCd 상품코드
     * @param cntrPrd   계약기간
     * @return void
     */
    public void checkProductInfo(String prodCd, Integer cntrPrd) {
        //상품코드 유효성 체크
        Product product = prodRepo.findById(prodCd).orElseThrow(() -> new IllegalArgumentException(MessageCode.NOT_FOUND_PRODUCT_INFO));

        //계약기간 유효성 체크
        if( product.getMinPrd().compareTo(cntrPrd) > 0
                || product.getMaxPrd().compareTo(cntrPrd) < 0 ) {
            throw new IllegalArgumentException(MessageCode.INVALID_RANGE_VALUE);
        }
    }

    /**
     * 상품코드에 따른 가입 가능 담보 체크
     * @param prodCd  상품코드
     * @param grntCdList  담보코드List
     * @return void
     */
    public void checkGuaranteeCd(String prodCd, List<String> grntCdList) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<Guarantee> guaranteeList = queryFactory.selectFrom(guarantee)
                .where(guarantee.grntCd.in(grntCdList))
                .where(guarantee.prodCd.eq(prodCd))
                .fetch();

        if(guaranteeList.size() != grntCdList.size()) {
            throw new IllegalArgumentException(MessageCode.NOT_INCLUDE_GUARANTEE_CD);
        }
    }
    /**
     * 가입담보에 따른 총 보험료 산출
     * 계약기간 x ((가입금액1 / 기준금액1) + (가입금액2 / 기준금액2))
     * @param grntCdList 담보코드 리스트
     * @param cntrPrd   계약기간
     * @return 총보험료 BigDecimal
     */
    public BigDecimal calcTotPrm(List<String> grntCdList, int cntrPrd){

        //계약기간 x ((가입금액1 / 기준금액1) + (가입금액2 / 기준금액2))
        BigDecimal totPrm =grntCdList.stream()
                .map( x -> {
                    Guarantee grnt = grntRepo.findById(x).orElseThrow(()->new IllegalArgumentException(MessageCode.NOT_FOUND_GUARANTEE_INFO));
                    return grnt.getJoinAmt().divide(grnt.getBaseAmt(),2,RoundingMode.DOWN);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(new BigDecimal(cntrPrd));
        return totPrm;
    }

    /**
     * 계약기간에 따른 종료일자 반환(일자 하루 전)
     * @param cntrDt    계약 시작일자
     * @param cntrPrd   계약기간(월단위)
     * @return 계약 종료일자 String
     */
    public String getContractExdt(String cntrDt, Integer cntrPrd) {

        LocalDate localDate = LocalDate.parse(cntrDt, DateTimeFormatter.BASIC_ISO_DATE);

        String exDt = localDate.plusMonths(cntrPrd)
                .minusDays(1)
                .format(DateTimeFormatter.BASIC_ISO_DATE);

        return exDt;
    }
}
