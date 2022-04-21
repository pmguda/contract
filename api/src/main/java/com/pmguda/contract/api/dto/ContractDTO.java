package com.pmguda.contract.api.dto;

import com.pmguda.contract.api.common.ContractStatus;
import com.pmguda.contract.api.common.MessageCode;
import com.pmguda.contract.api.domain.Contract;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Schema(description = "계약 정보 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContractDTO {
    @Schema(description = "증권번호")
    private String poliNo;

    @Schema(description = "상품코드", example = "P0001", maxLength = 5, required = true)
    @NotBlank
    private String prodCd;

    @Schema(description = "상품명")
    private String prodNm;

    @Schema(description = "계약기간", example = "3", defaultValue ="1", required = true)
    @Min(value = 1)
    private Integer cntrPrd;

    @Schema(description = "계약일자", example="yyyyMMdd", maxLength = 8)
    private String cntrDt;

    @Schema(description = "만기일자", example="yyyyMMdd", maxLength = 8)
    private String exDt;

    @Schema(description = "계약상태")
    private String cntrScd;

    @Schema(description = "총보험료")
    private BigDecimal totPrm;

    @Schema(description = "담보정보 리스트", required = true)
    @NotEmpty
    private List<InsuranceDTO> insDto;

    public ContractDTO(final Contract entity) {
        this.poliNo = entity.getPoliNo();
        this.prodCd = entity.getProdCd();
        this.cntrPrd = entity.getCntrPrd();
        this.cntrDt = entity.getCntrDt();
        this.exDt = entity.getExDt();
        this.cntrScd = entity.getCntrScd().toString();
        this.totPrm = entity.getTotPrm();
    }

    public static Contract toEntity(final ContractDTO dto) {
        Contract cntr = Contract.builder()
                .poliNo(dto.getPoliNo())
                .prodCd(dto.getProdCd())
                .cntrPrd(dto.getCntrPrd())
                .cntrDt(dto.getCntrDt())
                .exDt(dto.getExDt())
                //.cntrScd(ContractStatus.valueOf(dto.getCntrScd()))
                .totPrm(dto.getTotPrm())
                .insurances(new ArrayList<>())
                .build();

        if(dto.getCntrScd() != null) {
            cntr.setCntrScd(Arrays.stream(ContractStatus.values())
                    .filter(type -> type.getCode().equals(dto.getCntrScd()))
                    .findFirst()
                    .orElseThrow(()->new IllegalArgumentException(MessageCode.INVALID_CODE_VALUE)));
        }
        return cntr;
    }
}
