package com.ikea.assessment.warehouse.repository;

import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductArticle;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductArticleRepository extends JpaRepository<ProductArticle, Long> {
    public Optional<List<ProductArticle>> findByProductAndStatus(Product product, ProductStatus status);
}
