package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.dto.ArticleDTO;
import com.ikea.assessment.warehouse.dto.ProductDTO;
import com.ikea.assessment.warehouse.entity.Article;
import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductArticle;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import com.ikea.assessment.warehouse.mapper.ArticleMapper;
import com.ikea.assessment.warehouse.repository.ArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ArticleRepository articleRepository;
    private final ProductArticleRepository productArticleRepository;

    public ProductServiceImpl(ProductRepository productRepository, ArticleRepository articleRepository, ProductArticleRepository productArticleRepository) {
        this.productRepository = productRepository;
        this.articleRepository = articleRepository;
        this.productArticleRepository = productArticleRepository;
    }

    @Override
    public List<ProductDTO> getNewProducts() {
        ProductStatus status = ProductStatus.NEW;
        List<Product> productList = this.productRepository.findAllByStatus(status)
                .orElseThrow(() -> new ObjectNotFoundException("Product", "Status", status.toString()));
        return productList.stream().map(product -> {
            List<ProductArticle> productArticlesList = productArticleRepository.findByProduct(product)
                    .orElseThrow(() -> new ObjectNotFoundException("ProductArticle", "Product", String.valueOf(product.getId())));

            List<ArticleDTO> articleDTOList = productArticlesList.stream().map(productArticle -> {
                return ArticleMapper.convertToDTO(productArticle.getArticle());
            }).collect(Collectors.toList());
            return new ProductDTO(product.getId(), product.getName(), articleDTOList);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void sell(long id) {
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

    private void updateArticle(ProductArticle productArticle) throws IllegalArgumentException {
        Article article = productArticle.getArticle();
        article.setStock(article.getStock() - productArticle.getAmountOf());
        articleRepository.save(article);
    }

    private void updateProduct(Product product) throws IllegalArgumentException {
        product.setStatus(ProductStatus.SOLD);
        this.productRepository.save(product);
    }
}
