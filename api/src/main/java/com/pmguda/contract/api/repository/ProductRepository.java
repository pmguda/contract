package com.pmguda.contract.api.repository;

import com.pmguda.contract.api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
