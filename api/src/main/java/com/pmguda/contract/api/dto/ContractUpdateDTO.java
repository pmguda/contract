package com.pmguda.contract.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "계약 정보 수정 DTO")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ContractUpdateDTO {

    @Schema(description = "담보코드 Array", required = true)
    @NotEmpty
    private String[] grntCd;

    @Schema(description = "계약기간", example = "3", defaultValue = "1", required = true)
    @Min(value = 1)
    @NotNull
    private Integer cntrPrd;

    @Schema(description = "계약상태", example ="WITHDRAW",required = true, allowableValues = {"NORMAL","WITHDRAW","EXPIRED"})
    @NotBlank
    private String cntrScd;
}
