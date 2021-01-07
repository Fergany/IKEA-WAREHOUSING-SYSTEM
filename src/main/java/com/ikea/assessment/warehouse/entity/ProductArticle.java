package com.ikea.assessment.warehouse.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "products_articles")
@NoArgsConstructor
public class ProductArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column(name = "amount_of")
    private long amountOf;

    @Column(name = "product_status")
    private ProductStatus status;

    public ProductArticle(Product product, Article article, long amountOf, ProductStatus status){
        this.product = product;
        this.article = article;
        this.amountOf = amountOf;
        this.status = status;
    }
}
