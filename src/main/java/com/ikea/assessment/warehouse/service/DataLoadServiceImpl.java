package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.entity.Article;
import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductArticle;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import com.ikea.assessment.warehouse.mapper.ArticleMapper;
import com.ikea.assessment.warehouse.mapper.ProductMapper;
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
public class DataLoadServiceImpl implements DataLoadService {

    Logger logger = LoggerFactory.getLogger(DataLoadService.class);

    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final ProductArticleRepository productArticleRepository;
    private final JSONParser jsonParser = new JSONParser();

    public DataLoadServiceImpl(ArticleRepository articleRepository, ProductRepository productRepository, ProductArticleRepository productArticleRepository) {
        this.articleRepository = articleRepository;
        this.productRepository = productRepository;
        this.productArticleRepository = productArticleRepository;
    }


    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void loadData(String inventoryFilePath, String productsFilePath) {
        loadInventoryData(inventoryFilePath);
        loadProductsData(productsFilePath);
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


    private void loadProductsData(String productsFilePath) throws DataLoadException {
        try {
            logger.info("Start loading Products data from: " + productsFilePath);
            JSONArray productList = JSONFileReader.getJSONArray(jsonParser, productsFilePath, "products");
            productList.forEach(product -> {
                Product savedProduct = saveProduct((JSONObject) product);
                saveProductArticle(savedProduct, (JSONObject) product);
            });
        } catch (IOException | ParseException | ObjectNotFoundException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }


    private Product saveProduct(JSONObject productJSONObject) {
        logger.info("Saving Products' data to DB.");
        try {
            Product product = ProductMapper.convertToEntity(productJSONObject);
            product.setStatus(ProductStatus.NEW);
            return productRepository.save(product);
        } catch (IllegalArgumentException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }


    private void saveProductArticle(Product product, JSONObject productJSONObject) {
        logger.info("Saving ProductArticles' data to DB.");
        try {
            JSONArray articles = (JSONArray) productJSONObject.get("contain_articles");

            articles.forEach(contain_article -> {
                JSONObject articleJSONObject = (JSONObject) contain_article;
                long articleId = Long.parseLong((String) articleJSONObject.get("art_id"));
                long amountOf = Long.parseLong((String) articleJSONObject.get("amount_of"));

                Article article = articleRepository.findById(articleId)
                        .orElseThrow(() -> new ObjectNotFoundException("Article", "Id", String.valueOf(articleId)));

                productArticleRepository.save(new ProductArticle(product, article, amountOf));
            });
        } catch (ClassCastException | ObjectNotFoundException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }
}
