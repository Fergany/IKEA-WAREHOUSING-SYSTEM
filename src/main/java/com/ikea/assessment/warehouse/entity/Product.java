package com.ikea.assessment.warehouse.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "products")
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_name")
    private String name;

    @Column(name = "product_status")
    private ProductStatus status;

    public Product(String name) {
        this.name = name;
    }

    public Product(String name, ProductStatus status) {
        this.name = name;
        this.status = status;
    }

}
