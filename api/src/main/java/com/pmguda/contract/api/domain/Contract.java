package com.pmguda.contract.api.domain;

import com.pmguda.contract.api.common.ContractStatus;
import com.pmguda.contract.api.converter.ContractStatusConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length = 40)
    private String poliNo;

    @Column(nullable = false, length = 5)
    private String prodCd;

    @Column(nullable = false)
    private Integer cntrPrd;

    @Convert(converter = ContractStatusConverter.class)
    @Column(nullable = false, length = 20)
    private ContractStatus cntrScd;

    @Column(nullable = false)
    private BigDecimal totPrm;

    @Column(nullable = false, length = 8)
    private String cntrDt;

    @Column(nullable = false, length = 8)
    private String exDt;

    @OneToMany(mappedBy ="contract")
    private List<Insurance> insurances = new ArrayList<>();
}
