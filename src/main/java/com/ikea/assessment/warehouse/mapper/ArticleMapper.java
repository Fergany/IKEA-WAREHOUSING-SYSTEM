package com.ikea.assessment.warehouse.mapper;

import com.ikea.assessment.warehouse.entity.Article;
import org.json.simple.JSONObject;

public class ArticleMapper {

    public static Article convertToEntity(JSONObject articleJSONObject) {
        long id = Long.parseLong((String) articleJSONObject.get("art_id"));
        String name = (String) articleJSONObject.get("name");
        long stock = Long.parseLong((String) articleJSONObject.get("stock"));

        return new Article(id, name, stock);
    }

}

