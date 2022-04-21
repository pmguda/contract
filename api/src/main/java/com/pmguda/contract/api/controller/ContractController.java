package com.pmguda.contract.api.controller;

import com.pmguda.contract.api.dto.ContractDTO;
import com.pmguda.contract.api.dto.ContractUpdateDTO;
import com.pmguda.contract.api.dto.PremiumDTO;
import com.pmguda.contract.api.dto.ResponseDTO;
import com.pmguda.contract.api.service.ContractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"1. 계약관리"})
@RestController
@RequestMapping("contract")
public class ContractController {

    @Autowired
    private ContractService service;

    //1. 계약 생성
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계약 생성 성공(message: 결과메시지, data: 증권번호(String) )", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "계약 생성 오류 발생시(message: 오류메시지, data: null)", content = @Content(schema = @Schema(implementation = ResponseDTO.class))) })
    @Operation(summary="계약 생성", description="최초 계약 상태는 정상, 총보험료 계산")
    @PostMapping("/create")
    public ResponseEntity<?> createContract(@Valid @RequestBody ContractDTO cntrDTO) {
        return service.create(cntrDTO);
    }

    //2. 계약정보 조회
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계약 조회 성공(message: 결과메시지, data: 하단 리턴 결과 참조)", content = @Content(schema = @Schema(implementation = ContractDTO.class))),
            @ApiResponse(responseCode = "500", description = "계약조회 오류 발생시(message: 오류메시지, data: null)", content = @Content(schema = @Schema(implementation = ResponseDTO.class))) })
    @Operation(summary="계약정보 조회", description="증권번호에 따른 계약 상세 정보조회")
    @GetMapping("/get/{poliNo}")
    public ResponseEntity<?> getContractInfo(@PathVariable String poliNo) {
        return service.getContractInfo(poliNo);
    }

    //3. 계약 정보 수정
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계약 정보 수정 성공(message: 결과메시지, data: 증권번호 )", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "계약 정보 수정 오류 발생시(message: 오류메시지, data: null)", content = @Content(schema = @Schema(implementation = ResponseDTO.class))) })
    @Operation(summary="계약정보 수정", description="계약사항에 대한 수정(담보 추가/삭제, 계약기간 변경, 계약상태변경)")
    @PutMapping("/update/{poliNo}")
    public ResponseEntity<?> updateContract(@PathVariable String poliNo, @Valid @RequestBody ContractUpdateDTO updateDTO){
        return service.updateContract(poliNo, updateDTO);
    }

    //4. 예상 총 보험료 계산
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예상 총 보험료 계산 성공(message: 결과메시지, data: 총 보험료 )", content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "예상 총 보험료 계산 오류 발생시(message: 오류메시지, data: null)", content = @Content(schema = @Schema(implementation = ResponseDTO.class))) })
    @Operation(summary="예상 총 보험료 계산", description="가입전 예상 보험료 산출)")
    @PostMapping("/calc")
    public ResponseEntity<?> getEstimatedPremium(@Valid @RequestBody PremiumDTO prmDTO){
        return service.getEstimatedPremium(prmDTO);
    }

}
