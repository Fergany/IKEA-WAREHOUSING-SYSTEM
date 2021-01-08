package com.ikea.assessment.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
public class Article {
    @Id
    @Column(name = "article_id")
    @JsonProperty("art_id")
    private Long id;

    private String name;

    private long stock;

    public Article(Long id, String name, long stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }
}

