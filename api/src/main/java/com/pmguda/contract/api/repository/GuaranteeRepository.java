package com.pmguda.contract.api.repository;

import com.pmguda.contract.api.domain.Guarantee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuaranteeRepository  extends JpaRepository<Guarantee, String> {
}
