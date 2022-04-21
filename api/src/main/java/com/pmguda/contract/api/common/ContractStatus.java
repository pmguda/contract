package com.pmguda.contract.api.common;

public enum ContractStatus {
    NORMAL("0"),
    WITHDRAW("1"),
    EXPIRED("2");

    private final String code;

    ContractStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }
}
