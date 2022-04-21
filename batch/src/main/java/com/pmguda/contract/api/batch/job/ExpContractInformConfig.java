package com.pmguda.contract.api.batch.job;


import com.pmguda.contract.api.domain.Contract;
import com.pmguda.contract.api.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j  // log 사용을 위한 lombok Annotation
@RequiredArgsConstructor
@Configuration
public class ExpContractInformConfig {

    private final ContractRepository contRepo;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job expCntrInformJob() {
        log.info("********** This is expCntrInformJob");

        return jobBuilderFactory.get("expCntrInformJob")
                .preventRestart()
                .start(expCntrInformStep())
                .build();
    }

    @Bean
    @JobScope
    public Step expCntrInformStep() {
        log.info("********** This is expCntrInformStep");
        return stepBuilderFactory.get("expCntrInformStep")
                .<Contract, Contract> chunk(10)
                .reader(this.expCntrInformReader(null))
                .processor(this.expCntrInformProcessor())
                .writer(this.expCntrInformWriter())
                .build();
    }

    @Bean
    @JobScope
    public ListItemReader<Contract> expCntrInformReader(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.error("********** This is expCntrInformReader");
        //요청일자 미입력시 오늘날짜로 셋팅
        if(ObjectUtils.isEmpty(requestDate)) {
            requestDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }

        LocalDate localDate = LocalDate.parse(requestDate, DateTimeFormatter.BASIC_ISO_DATE);
        String exdt = localDate.plusDays(7).format(DateTimeFormatter.BASIC_ISO_DATE);

        List<Contract> expCntr = contRepo.findAllByExDt(exdt);
        log.error("********** 수행요청일자 : " + requestDate);
        log.error("********** 만기대상일자 : " + exdt);
        log.error("********** 만기안내장 대상 건수 : " + expCntr.size());
        return new ListItemReader<>(expCntr);
    }

    @Bean
    public ItemProcessor<Contract, Contract> expCntrInformProcessor() {
        return new ItemProcessor<Contract, Contract>() {
            @Override
            public Contract process(Contract cntr) throws Exception {
                log.error("********** 만기 예정 안내 대상 증번 :"+ cntr.getPoliNo());
                return cntr;
            }
        };
    }

    @Bean
    public ItemWriter<Contract> expCntrInformWriter() {
        log.error("********** This is expCntrInformWriter");
        return ((List<? extends Contract> contractList) ->
                log.error("발송완료"));
    }
}