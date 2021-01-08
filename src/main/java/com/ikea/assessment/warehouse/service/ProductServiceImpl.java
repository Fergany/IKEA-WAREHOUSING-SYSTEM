package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.dto.ProductDTO;
import com.ikea.assessment.warehouse.entity.Article;
import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductArticle;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import com.ikea.assessment.warehouse.exception.InsufficientStockException;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import com.ikea.assessment.warehouse.repository.ArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ArticleRepository articleRepository;
    private final ProductArticleRepository productArticleRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ArticleRepository articleRepository, ProductArticleRepository productArticleRepository) {
        this.productRepository = productRepository;
        this.articleRepository = articleRepository;
        this.productArticleRepository = productArticleRepository;
    }

    @Override
    public List<ProductDTO> getNewProducts() {
        try {
            logger.info("get all New products");
            ProductStatus status = ProductStatus.NEW;
            List<Product> productList = this.productRepository.findAllByStatus(status)
                    .orElse(new ArrayList());
            return productList.stream().map(product -> {
                List<ProductArticle> productArticlesList = productArticleRepository.findByProduct(product)
                        .orElse(new ArrayList());

                final long availableQuantity = productArticlesList.size() == 0? 0 : getAvailableQuantity(productArticlesList);

                return new ProductDTO(product.getId(), product.getName(), availableQuantity);

            }).collect(Collectors.toList());
        } catch (RuntimeException exception) {
            logger.error(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    private long getAvailableQuantity(List<ProductArticle> productArticles) {
        return productArticles
                .stream()
                .map(productArticle -> productArticle.getArticle().getStock() / productArticle.getAmountOf())
                .min(Long::compare)
                .get();
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void sell(long id) {
        logger.info("Sell product: " + id);
        ProductStatus status = ProductStatus.NEW;
        Product product = productRepository.findByIdAndStatus(id, status)
                .orElseThrow(() -> new ObjectNotFoundException("Product", "(Id & Status)", "(" + String.valueOf(id) + " & " + status.toString() + ")"));

        List<ProductArticle> productArticleList = productArticleRepository.findByProduct(product)
                .orElseThrow(() -> new ObjectNotFoundException("ProductArticle", "Product", String.valueOf(id)));

        productArticleList.stream().forEach(productArticle -> {
            updateArticle(productArticle);
        });
        updateProduct(product);
    }

    private void updateArticle(ProductArticle productArticle) throws IllegalArgumentException, InsufficientStockException {
        Article article = productArticle.getArticle();
        if (article.getStock() >= productArticle.getAmountOf()) {
            article.setStock(article.getStock() - productArticle.getAmountOf());
            articleRepository.save(article);
        } else {
            long productId = productArticle.getProduct().getId();
            long articleId = productArticle.getArticle().getId();
            logger.error("Article " + articleId + " has insufficient stock for this product" + productId);
            throw new InsufficientStockException(articleId, productId);
        }
    }

    private void updateProduct(Product product) throws IllegalArgumentException {
        product.setStatus(ProductStatus.SOLD);
        this.productRepository.save(product);
    }
}
