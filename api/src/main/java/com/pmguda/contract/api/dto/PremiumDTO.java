package com.pmguda.contract.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Schema(description = "예상 총 보험료 계산 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PremiumDTO {

    @Schema(description = "상품코드", example = "P0001", maxLength = 5, required = true)
    @NotBlank
    private String prodCd;

    @Schema(description = "담보코드 Array", required = true)
    @NotEmpty
    private String[] grntCd;

    @Schema(description = "계약기간", example = "3", defaultValue = "1", required = true)
    @Min(value = 1)
    private Integer cntrPrd;
}
