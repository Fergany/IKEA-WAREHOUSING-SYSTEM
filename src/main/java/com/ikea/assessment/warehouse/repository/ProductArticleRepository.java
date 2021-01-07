package com.ikea.assessment.warehouse.repository;

import com.ikea.assessment.warehouse.entity.ProductArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductArticleRepository extends JpaRepository<ProductArticle, Long> {
}
