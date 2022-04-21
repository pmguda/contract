package com.pmguda.contract.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmguda.contract.api.common.ContractStatus;
import com.pmguda.contract.api.domain.Contract;
import com.pmguda.contract.api.domain.Insurance;
import com.pmguda.contract.api.dto.ContractDTO;
import com.pmguda.contract.api.dto.ContractUpdateDTO;
import com.pmguda.contract.api.dto.InsuranceDTO;
import com.pmguda.contract.api.dto.PremiumDTO;
import com.pmguda.contract.api.repository.ContractRepository;
import com.pmguda.contract.api.repository.InsuranceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ContractRepository cntrRepo;

    @Autowired
    InsuranceRepository insRepo;

    public String createTestContract() throws Exception {
        Contract cntr = Contract.builder()
                .prodCd("P0001")
                .cntrDt("20210303")
                .cntrScd(ContractStatus.NORMAL)
                .cntrPrd(3)
                .exDt("20220602")
                .totPrm(BigDecimal.ONE)
                .insurances(new ArrayList<>())
                .build();
        cntrRepo.save(cntr);

        List<Insurance> insuranceList = new ArrayList<Insurance>();
        insuranceList.add(Insurance.builder().grntCd("I000101").build());
        insuranceList.add(Insurance.builder().grntCd("I000102").build());
        insuranceList.forEach(insurance -> insurance.setContract(cntr));
        insRepo.saveAll(insuranceList);

        return cntr.getPoliNo();
    }

    @Test
    @DisplayName("계약 생성 테스트")
    void createContract() throws Exception {

        //담보 정보 생성
        List<InsuranceDTO> insuranceDTOList = new ArrayList<InsuranceDTO>();
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000101").build());
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000102").build());

        //계약 정보 생성
        ContractDTO cntrDTO = ContractDTO.builder()
                .prodCd("P0001")
                .cntrPrd(3)
                .insDto(insuranceDTOList)
                .build();

        //JSON 생성
        String cntrDTOJsonString = objectMapper.writeValueAsString(cntrDTO);

        //API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(cntrDTOJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("계약 생성 오류 테스트(미판매 상품코드 오류)")
    void createContract_1() throws Exception {

        //담보 정보 생성
        List<InsuranceDTO> insuranceDTOList = new ArrayList<InsuranceDTO>();
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000101").build());
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000202").build());

        //계약 정보 생성
        ContractDTO cntrDTO = ContractDTO.builder()
                .prodCd("P0001")
                .cntrPrd(3)
                .insDto(insuranceDTOList)
                .build();

        //JSON 생성
        String cntrDTOJsonString = objectMapper.writeValueAsString(cntrDTO);

        //API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(cntrDTOJsonString))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("계약 생성 오류 테스트(상품 하위 담보 오류)")
    void createContract_2() throws Exception {

        //담보 정보 생성
        List<InsuranceDTO> insuranceDTOList = new ArrayList<InsuranceDTO>();
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000101").build());
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000202").build());

        //계약 정보 생성
        ContractDTO cntrDTO = ContractDTO.builder()
                .prodCd("P0001")
                .cntrPrd(3)
                .insDto(insuranceDTOList)
                .build();

        //JSON 생성
        String cntrDTOJsonString = objectMapper.writeValueAsString(cntrDTO);

        //API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(cntrDTOJsonString))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("계약 생성 오류 테스트(상품별 가입가능 기간 초과오류)")
    void createContract_3() throws Exception {

        //담보 정보 생성
        List<InsuranceDTO> insuranceDTOList = new ArrayList<InsuranceDTO>();
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000101").build());
        insuranceDTOList.add(InsuranceDTO.builder().grntCd("I000102").build());

        //계약 정보 생성
        ContractDTO cntrDTO = ContractDTO.builder()
                .prodCd("P0001")
                .cntrPrd(50)
                .insDto(insuranceDTOList)
                .build();

        //JSON 생성
        String cntrDTOJsonString = objectMapper.writeValueAsString(cntrDTO);

        //API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(cntrDTOJsonString))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("계약정보 조회 테스트")
    void getContractInfo_1() throws Exception{

        String poliNo = createTestContract();

        mockMvc.perform(MockMvcRequestBuilders.get("/contract/get/"+poliNo))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("계약정보 조회 오류 테스트(미등록 증번)")
    void getContractInfo_2() throws Exception{

        String poliNo = "aaaa";

        mockMvc.perform(MockMvcRequestBuilders.get("/contract/get/"+poliNo))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("예상 보험료 조회")
    void getEstimatedPremium() throws Exception {
        String[] grntCd = {"I000201","I000202"};

        //정보 생성
        PremiumDTO prmDTO = PremiumDTO.builder().prodCd("P0002").cntrPrd(3).grntCd(grntCd).build();

        //JSON 생성
        String prmDtoJsonString = objectMapper.writeValueAsString(prmDTO);

        //API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(prmDtoJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("예상 보험료 조회 오류(상품 하위 담보 오류)")
    void getEstimatedPremium_1() throws Exception {
        String[] grntCd = {"I000201","I000102"};

        //정보 생성
        PremiumDTO prmDTO = PremiumDTO.builder().prodCd("P0002").cntrPrd(3).grntCd(grntCd).build();

        //JSON 생성
        String prmDtoJsonString = objectMapper.writeValueAsString(prmDTO);

        //API Call
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(prmDtoJsonString))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("계약 정보 수정")
    void updateContract() throws Exception{
        String poliNo = createTestContract();
        String[] grntCd = {"I000101"};

        ContractUpdateDTO updateDTO = ContractUpdateDTO.builder()
                .cntrScd(ContractStatus.NORMAL.toString())
                .cntrPrd(2)
                .grntCd(grntCd)
                .build();
        //JSON 생성
        String updateDtoJsonString = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/contract/update/"+poliNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(updateDtoJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("계약 정보 수정 오류(상품별 가입가능 기간 초과오류)")
    void updateContract_3() throws Exception{
        String poliNo = createTestContract();
        String[] grntCd = {"I000101"};

        ContractUpdateDTO updateDTO = ContractUpdateDTO.builder()
                .cntrScd(ContractStatus.NORMAL.toString())
                .cntrPrd(50)
                .grntCd(grntCd)
                .build();
        //JSON 생성
        String updateDtoJsonString = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/contract/update/"+poliNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(updateDtoJsonString))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("계약 정보 수정 오류(소멸상태건 변경 불가)")
    void updateContract_2() throws Exception{
        String poliNo = createTestContract();
        String[] grntCd = {"I000101"};

        //소멸상태로 계약변경
        ContractUpdateDTO updateDTO = ContractUpdateDTO.builder()
                .cntrScd(ContractStatus.EXPIRED.name())
                .cntrPrd(2)
                .grntCd(grntCd)
                .build();
        //JSON 생성
        String updateDtoJsonString = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/contract/update/"+poliNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(updateDtoJsonString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        //소멸상태건 상태 변경 요청
        updateDTO = ContractUpdateDTO.builder()
                .cntrScd(ContractStatus.NORMAL.toString()) //정상으로 변경
                .cntrPrd(2)
                .grntCd(grntCd)
                .build();
        //JSON 생성
        updateDtoJsonString = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/contract/update/"+poliNo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(updateDtoJsonString))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andDo(MockMvcResultHandlers.print());
    }
}