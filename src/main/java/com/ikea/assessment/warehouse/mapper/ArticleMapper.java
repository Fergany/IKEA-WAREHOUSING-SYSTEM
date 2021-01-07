package com.ikea.assessment.warehouse.mapper;

import com.ikea.assessment.warehouse.dto.ArticleDTO;
import com.ikea.assessment.warehouse.entity.Article;
import org.json.simple.JSONObject;

public class ArticleMapper {

    public static Article convertToEntity(JSONObject articleJSONObject) {
        long id = Long.parseLong((String) articleJSONObject.get("art_id"));
        String name = (String) articleJSONObject.get("name");
        long stock = Long.parseLong((String) articleJSONObject.get("stock"));

        return new Article(id, name, stock);
    }

    public static ArticleDTO convertToDTO(Article article){
        String art_id = String.valueOf( article.getId());
        String amount_of = String.valueOf( article.getStock());

        return new ArticleDTO(art_id, amount_of);
    }

}

