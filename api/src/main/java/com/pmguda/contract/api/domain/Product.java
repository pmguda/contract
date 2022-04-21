package com.pmguda.contract.api.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Product {
    @Id
    @Column(nullable = false, length = 5)
    private String prodCd;

    @Column(nullable = false, length = 100)
    private String prodNm;

    @Column(nullable = false)
    private Integer minPrd;

    @Column(nullable = false)
    private Integer maxPrd;
}
