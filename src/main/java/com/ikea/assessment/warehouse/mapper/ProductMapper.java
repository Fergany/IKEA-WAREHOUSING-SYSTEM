package com.ikea.assessment.warehouse.mapper;

import com.ikea.assessment.warehouse.entity.Product;
import org.json.simple.JSONObject;

public class ProductMapper {
    public static Product convertToEntity(JSONObject productJSONObject) {
        String name = (String) productJSONObject.get("name");
        return new Product(name);
    }
}
