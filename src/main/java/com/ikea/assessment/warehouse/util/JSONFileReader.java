package com.ikea.assessment.warehouse.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;

public class JSONFileReader {
    public static JSONArray getJSONArray(JSONParser jsonParser, String filePath, String tagName)
            throws IOException, ParseException {
        FileReader reader = new FileReader(ResourceUtils.getFile(filePath));
        //Read JSON file
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        return (JSONArray) jsonObject.get(tagName);
    }
}
