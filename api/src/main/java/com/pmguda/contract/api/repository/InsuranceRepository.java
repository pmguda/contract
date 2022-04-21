package com.pmguda.contract.api.repository;

import com.pmguda.contract.api.domain.Contract;
import com.pmguda.contract.api.domain.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuranceRepository extends JpaRepository<Insurance, String> {
    //List<Insurance> findAllByContInsuranceList(Contract contract);
}
