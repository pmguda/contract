package com.pmguda.contract.api.dto;

import com.pmguda.contract.api.domain.Insurance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "보험정보 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InsuranceDTO {

    @Schema(description = "보험ID")
    private String insId;

    @Schema(description = "증권번호")
    private String poliNo;

    @Schema(description = "담보코드", example = "I000101", maxLength = 7, required = true)
    @NotBlank
    private String grntCd;

    @Schema(description = "담보명")
    private String grntNm;

    @Schema(description = "가입금액")
    private BigDecimal joinAmt;

    @Schema(description = "기준금액")
    private BigDecimal baseAmt;

    public InsuranceDTO(final Insurance entity) {
        this.insId = entity.getInsId();
        this.poliNo = entity.getContract().getPoliNo();
        this.grntCd = entity.getGrntCd();
    }
}
