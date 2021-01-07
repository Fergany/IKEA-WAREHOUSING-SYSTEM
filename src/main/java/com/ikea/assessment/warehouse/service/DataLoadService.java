package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.entity.Article;
import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.mapper.ArticleMapper;
import com.ikea.assessment.warehouse.repository.ArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import com.ikea.assessment.warehouse.util.JSONFileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
public class DataLoadService implements IDataLoadService {

    Logger logger = LoggerFactory.getLogger(IDataLoadService.class);

    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final ProductArticleRepository productArticleRepository;
    private final JSONParser jsonParser;

    public DataLoadService(ArticleRepository articleRepository, ProductRepository productRepository, ProductArticleRepository productArticleRepository, JSONParser jsonParser) {
        this.articleRepository = articleRepository;
        this.productRepository = productRepository;
        this.productArticleRepository = productArticleRepository;
        this.jsonParser = new JSONParser();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void loadData(String inventoryFilePath, String productsFilePath) {
        loadInventoryData(inventoryFilePath);
        loadProductData(productsFilePath);
    }

    private void loadInventoryData(String inventoryFilePath) throws DataLoadException {
        try {
            logger.info("Start loading Articles data from: " + inventoryFilePath);
            JSONArray articleList = JSONFileReader.getJSONArray(jsonParser, inventoryFilePath, "inventory");
            articleList.forEach(article -> saveArticle((JSONObject) article));
        } catch (IOException | ParseException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }


    private void saveArticle(JSONObject articleJSONObject) {
        logger.info("Saving Articles' data to DB.");
        try {
            Article article = ArticleMapper.convertToEntity(articleJSONObject);
            articleRepository.save(article);
        } catch (IllegalArgumentException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }

    private void loadProductData(String productFilePath) {

    }
}
