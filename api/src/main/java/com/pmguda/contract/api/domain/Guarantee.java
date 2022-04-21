package com.pmguda.contract.api.domain;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
public class Guarantee {

    @Id
    @Column(nullable = false, length = 7)
    private String grntCd;

    @Column(nullable = false, length = 5)
    private String prodCd;

    @Column(nullable = false, length = 100)
    private String grntNm;

    @Column(nullable = false)
    private BigDecimal joinAmt;

    @Column(nullable = false)
    private BigDecimal baseAmt;
}
