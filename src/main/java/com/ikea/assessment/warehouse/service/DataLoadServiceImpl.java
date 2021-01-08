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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Service
public class DataLoadServiceImpl implements DataLoadService {

    Logger logger = LoggerFactory.getLogger(DataLoadServiceImpl.class);

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
            logger.info("Start loading Articles data from: " + inventoryFilePath);
            FileReader reader = new FileReader(ResourceUtils.getFile(inventoryFilePath));
            JsonNode inventoryJsonNode = objectMapper.readTree(reader).get("inventory");
            List<Article> articles = objectMapper.convertValue(inventoryJsonNode, new TypeReference<List<Article>>() {
            });
            logger.info("Saving Articles' data to DB.");
            articleRepository.saveAll(articles);
        } catch (IOException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }

    private void loadProductsData(String productFilePath) {
        try {
            logger.info("Start loading Products data from: " + productFilePath);
            FileReader reader = new FileReader(ResourceUtils.getFile(productFilePath));
            ArrayNode productsArrayNode = (ArrayNode) objectMapper.readTree(reader).get("products");
            productsArrayNode.forEach(productJsonNode -> {
                Product product = objectMapper.convertValue(productJsonNode, Product.class);
                logger.info("Saving Products' data to DB.");
                product = productRepository.save(product);
                ArrayNode productArticles = (ArrayNode) productJsonNode.get("contain_articles");
                saveProductArticle(product, productArticles);
            });

        } catch (IOException exception) {
            logger.error(exception.getMessage());
            throw new DataLoadException(exception.getMessage());
        }
    }

    private void saveProductArticle(Product product, ArrayNode productArticles) {
        logger.info("Saving ProductArticle data to DB.");
        try {
            productArticles.forEach(productArticle -> {
                long articleId = Long.parseLong(productArticle.get("art_id").asText());
                long amountOf = Long.parseLong(productArticle.get("amount_of").asText());

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
