package com.pmguda.contract.api.converter;

import com.pmguda.contract.api.common.ContractStatus;
import com.pmguda.contract.api.common.MessageCode;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;

@Converter
public class ContractStatusConverter implements AttributeConverter<ContractStatus, String> {
    @Override
    public String convertToDatabaseColumn(ContractStatus status) {
        return status.getCode();
    }

    @Override
    public ContractStatus convertToEntityAttribute(String code) {
        return Arrays.stream(ContractStatus.values())
                .filter(status -> status.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MessageCode.INVALID_CODE_VALUE));
    }
}
