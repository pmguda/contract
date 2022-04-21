package com.pmguda.contract.api.domain;

import com.pmguda.contract.api.dto.InsuranceDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    @Column(length = 40)
    private String insId;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="poliNo")
    private Contract contract;

    @Column(nullable = false, length = 7)
    private String grntCd;

    //== 연관관계 메서드 ==//
    public void setContract(Contract contract) {
        if (this.contract != null) {    // this.contract가 null이 아니면 이 Insurance객체는 Contract가 있음을 의미
            this.contract.getInsurances().remove(this); //해당 계약의 Insurances에서 삭제
        }
        this.contract = contract;
        contract.getInsurances().add(this);
    }
    public Insurance(InsuranceDTO insDTO) {
        this.grntCd = insDTO.getGrntCd();
    }
}
