package com.ikea.assessment.warehouse.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ikea.assessment.warehouse.entity.Article;
import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductArticle;
import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import com.ikea.assessment.warehouse.repository.ArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class DataLoadServiceImpl implements DataLoadService {


    private final ArticleRepository articleRepository;
    private final ProductRepository productRepository;
    private final ProductArticleRepository productArticleRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public DataLoadServiceImpl(ArticleRepository articleRepository, ProductRepository productRepository, ProductArticleRepository productArticleRepository, ObjectMapper objectMapper) {
        this.articleRepository = articleRepository;
        this.productRepository = productRepository;
        this.productArticleRepository = productArticleRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void loadData(String inventoryFilePath, String productsFilePath) {
        loadInventoryData(inventoryFilePath);
        loadProductsData(productsFilePath);
    }

    private void loadInventoryData(String inventoryFilePath) {
        try {
            log.info("Start loading Articles data from: " + inventoryFilePath);
            InputStream inputStream = getClass().getResourceAsStream(inventoryFilePath);
            JsonNode inventoryJsonNode = objectMapper.readTree(inputStream).get("inventory");
            List<Article> articles = objectMapper.convertValue(inventoryJsonNode, new TypeReference<List<Article>>() {
            });
            log.info("Saving Articles' data to DB.");
            articleRepository.saveAll(articles);
        } catch (IOException | IllegalArgumentException exception) {
            log.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }

    private void loadProductsData(String productFilePath) {
        try {
            log.info("Start loading Products data from: " + productFilePath);
            InputStream inputStream = getClass().getResourceAsStream(productFilePath);
            ArrayNode productsArrayNode = (ArrayNode) objectMapper.readTree(inputStream).get("products");
            productsArrayNode.forEach(productJsonNode -> {
                Product product = objectMapper.convertValue(productJsonNode, Product.class);
                log.info("Saving Products' data to DB.");
                product = productRepository.save(product);
                ArrayNode productArticles = (ArrayNode) productJsonNode.get("contain_articles");
                saveProductArticle(product, productArticles);
            });

        } catch (IOException | IllegalArgumentException exception) {
            log.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }

    private void saveProductArticle(Product product, ArrayNode productArticles) {
        log.info("Saving ProductArticle data to DB.");
        try {
            productArticles.forEach(productArticle -> {
                long articleId = Long.parseLong(productArticle.get("art_id").asText());
                long amountOf = Long.parseLong(productArticle.get("amount_of").asText());

                Article article = articleRepository.findById(articleId)
                        .orElseThrow(() -> new ObjectNotFoundException("Article", "Id", String.valueOf(articleId)));

                productArticleRepository.save(new ProductArticle(product, article, amountOf));
            });
        } catch (ClassCastException | ObjectNotFoundException | IllegalArgumentException  exception) {
            log.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }
}
