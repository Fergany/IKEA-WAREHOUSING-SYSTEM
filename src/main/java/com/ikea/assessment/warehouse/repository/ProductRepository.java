package com.ikea.assessment.warehouse.repository;

import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public Optional<List<Product>> findAllByStatus(ProductStatus status);
    public Optional<Product> findByIdAndStatus(Long id, ProductStatus status);
}
