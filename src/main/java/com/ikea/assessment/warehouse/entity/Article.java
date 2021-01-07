package com.ikea.assessment.warehouse.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "article_id")
    private long id;

    private String name;

    private long stock;

    public Article(Long id, String name, long stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }
}

