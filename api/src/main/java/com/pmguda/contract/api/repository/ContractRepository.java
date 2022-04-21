package com.pmguda.contract.api.repository;

import com.pmguda.contract.api.domain.Contract;
import com.pmguda.contract.api.domain.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, String> {
    List<Contract> findAllByExDt(String exDt);
}
