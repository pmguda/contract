package com.pmguda.contract.api.service;

import com.pmguda.contract.api.common.ContractStatus;
import com.pmguda.contract.api.common.ContractUtil;
import com.pmguda.contract.api.common.MessageCode;
import com.pmguda.contract.api.domain.Contract;
import com.pmguda.contract.api.domain.Guarantee;
import com.pmguda.contract.api.domain.Insurance;
import com.pmguda.contract.api.domain.Product;
import com.pmguda.contract.api.domain.QGuarantee;
import com.pmguda.contract.api.domain.QInsurance;
import com.pmguda.contract.api.dto.ContractDTO;
import com.pmguda.contract.api.dto.ContractUpdateDTO;
import com.pmguda.contract.api.dto.InsuranceDTO;
import com.pmguda.contract.api.dto.PremiumDTO;
import com.pmguda.contract.api.dto.ResponseDTO;
import com.pmguda.contract.api.repository.ContractRepository;
import com.pmguda.contract.api.repository.GuaranteeRepository;
import com.pmguda.contract.api.repository.InsuranceRepository;
import com.pmguda.contract.api.repository.ProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
public class ContractService {

    private final ContractRepository cntrRepo;
    private final InsuranceRepository insRepo;
    private final GuaranteeRepository grntRepo;
    private final ProductRepository prodRepo;
    private final ContractUtil cntrUtil;
    private final EntityManager em;

    /* 1. 계약 생성 */
    @Transactional
    public ResponseEntity<?> create(final ContractDTO cntrDTO) {
        validate(cntrDTO);

        Contract contract = ContractDTO.toEntity(cntrDTO);
        contract.setCntrScd(ContractStatus.NORMAL); //계약상태

        //계약일자, 종료일자 셋팅
        LocalDate now = LocalDate.now();
        String cntrDt = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String exDt = cntrUtil.getContractExdt(cntrDt, contract.getCntrPrd());
        contract.setCntrDt(cntrDt); //계약일자
        contract.setExDt(exDt);     //종료일자

        //총보험료 산출
        List<String> grntCds = cntrDTO.getInsDto().stream()
                .map(InsuranceDTO::getGrntCd)
                .collect(Collectors.toList());
        BigDecimal totPrm = cntrUtil.calcTotPrm(grntCds, contract.getCntrPrd());
        contract.setTotPrm(totPrm);

        //계약정보 생성
        Contract regCntr = cntrRepo.save(contract);

        //보험정보 생성
        List<Insurance> insurances = cntrDTO.getInsDto().stream().map(Insurance::new).collect(Collectors.toList());
        insurances.forEach(ins -> ins.setContract(regCntr));
        insRepo.saveAll(insurances);

        //결과 리턴
        ResponseDTO<Object> response = ResponseDTO.builder().message(MessageCode.SUCCESS_CREATE_CONTRACT).data(regCntr.getPoliNo()).build();
        return ResponseEntity.ok().body(response);

    }

    /* 1.1 계약 입력값 유효성 체크 */
    private void validate(final ContractDTO cntrDTO) {
        if (ObjectUtils.isEmpty(cntrDTO)) {
            throw new IllegalArgumentException(MessageCode.NOT_FOUND_CONTRACT_INFO);
        }

        if(ObjectUtils.isEmpty(cntrDTO.getInsDto())) {
            throw new IllegalArgumentException(MessageCode.NOT_FOUND_GUARANTEE_INFO);
        }

        //상품코드에 따른 계약기간 유효성 체크
        cntrUtil.checkProductInfo(cntrDTO.getProdCd(), cntrDTO.getCntrPrd());

        //상품코드에 따른 담보코드 유효성 체크
        List<String> grntCdList = cntrDTO.getInsDto().stream()
                .map(InsuranceDTO::getGrntCd)
                .collect(Collectors.toList());

        cntrUtil.checkGuaranteeCd(cntrDTO.getProdCd(), grntCdList);
    }

    /* 2. 계약 조회 */
    public ResponseEntity<?> getContractInfo(String poliNo){

        Contract cntr = cntrRepo.findById(poliNo).orElseThrow(()->new IllegalArgumentException(MessageCode.NOT_FOUND_CONTRACT_INFO));
        ContractDTO cntrDto = new ContractDTO(cntr);

        //상품명 셋팅
        Optional<Product> prod = prodRepo.findById(cntrDto.getProdCd());
        prod.ifPresent(p-> cntrDto.setProdNm(p.getProdNm()));

//        List<Insurance> insurances = insRepo.findAllByPoliNo(Contract);
        List<Insurance> insurances = cntr.getInsurances();
        List<InsuranceDTO> insdtos = insurances.stream().map(InsuranceDTO::new).collect(Collectors.toList());
        //담보정보 셋팅
        insdtos.forEach(ins -> {
            Optional<Guarantee> grnt = grntRepo.findById(ins.getGrntCd());
            grnt.ifPresent(g -> {
                ins.setBaseAmt(g.getBaseAmt());
                ins.setJoinAmt(g.getJoinAmt());
                ins.setGrntNm(g.getGrntNm());
            });
        });
        cntrDto.setInsDto(insdtos);

        //결과 리턴
        ResponseDTO<Object> response = ResponseDTO.builder().message(MessageCode.SUCCESS_RETRIVE).data(cntrDto).build();
        return ResponseEntity.ok(response);

    }

    // 3. 예상 보험료 조회
    public ResponseEntity<?> getEstimatedPremium(PremiumDTO prmDTO){

        //상품코드에 따른 계약기간 유효성 체크
        cntrUtil.checkProductInfo(prmDTO.getProdCd(), prmDTO.getCntrPrd());

        //상품코드에 따른 담보코드 유효성 체크
        List<String> grntCdList = Arrays.stream(prmDTO.getGrntCd()).collect(Collectors.toList());
        cntrUtil.checkGuaranteeCd(prmDTO.getProdCd(), grntCdList);

        // 예상보험료 산출
        BigDecimal totPrm = cntrUtil.calcTotPrm(grntCdList, prmDTO.getCntrPrd());

        //결과 리턴
        ResponseDTO<Object> response = ResponseDTO.builder().message(MessageCode.SUCCESS_RETRIVE).data(totPrm).build();
        return ResponseEntity.ok(response);
    }

    // 4. 계약 정보 수정
    @Transactional
    public ResponseEntity<?> updateContract(String poliNo, ContractUpdateDTO updateDTO){

        // 계약 ID가 DB에 존재하는지 확인
        Contract contract = cntrRepo.findById(poliNo).orElseThrow(()->new IllegalArgumentException(MessageCode.NOT_FOUND_CONTRACT_INFO));

        //상품코드에 따른 계약기간 유효성 체크
        cntrUtil.checkProductInfo(contract.getProdCd(), updateDTO.getCntrPrd());

        //상품코드에 따른 담보코드 유효성 체크
        List<String> grntCdList = Arrays.stream(updateDTO.getGrntCd()).collect(Collectors.toList());
        cntrUtil.checkGuaranteeCd(contract.getProdCd(), grntCdList);

        //현재 계약의 계약상태가 기간만료이면 변경 불가
        if(ContractStatus.EXPIRED.equals(contract.getCntrScd())) {
            throw new RuntimeException(MessageCode.STATUS_CHANGE_IMPOSSIBLE);
        }

        contract.setCntrScd(ContractStatus.valueOf(updateDTO.getCntrScd()));

        // 계약기간 변경(시작일은 변경 불가, 기간만 변경 가능)
        if( !contract.getCntrPrd().equals(updateDTO.getCntrPrd()) ) {
            contract.setCntrPrd(updateDTO.getCntrPrd());
            //계약기간 변경시 종료일자 변경
            String exDt = cntrUtil.getContractExdt(contract.getCntrDt(), updateDTO.getCntrPrd());
            contract.setExDt(exDt);
        }

        //보험료 계산 시작
        BigDecimal totPrm = cntrUtil.calcTotPrm(Arrays.stream(updateDTO.getGrntCd()).collect(Collectors.toList()), updateDTO.getCntrPrd());
        //총보험료 수정
        contract.setTotPrm(totPrm);

        //계약정보 수정
        //(영속성 컨텍스트에따라 안해줘도 되는데 어떤게 맞는건지...)
        //cntrRepo.save(contract);

        //보험정보 삭제
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QInsurance qInsurance = QInsurance.insurance;
        Long affected = queryFactory.delete(qInsurance)
                .where(qInsurance.contract.poliNo.eq(contract.getPoliNo()))
                .execute();

        //보험정보 생성
        List<Insurance> insuranceList = Arrays.stream(updateDTO.getGrntCd())
                .map(ins->Insurance
                        .builder()
                        .grntCd(ins)
                        .build()
                ).collect(Collectors.toList());
        insuranceList.forEach(insurance -> insurance.setContract(contract));

        insRepo.saveAll(insuranceList);

        //결과 리턴
        ResponseDTO<Object> response = ResponseDTO.builder().message(MessageCode.SUCCESS_UPDATE_CONTRACT).data(contract.getPoliNo()).build();
        return ResponseEntity.ok().body(response);
    }
}
