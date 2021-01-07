package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.dto.ArticleDTO;
import com.ikea.assessment.warehouse.dto.ProductDTO;
import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductArticle;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import com.ikea.assessment.warehouse.exception.ObjectNotFoundException;
import com.ikea.assessment.warehouse.mapper.ArticleMapper;
import com.ikea.assessment.warehouse.repository.ArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final ArticleRepository articleRepository;
    private final ProductArticleRepository productArticleRepository;

    public ProductService(ProductRepository productRepository, ArticleRepository articleRepository, ProductArticleRepository productArticleRepository) {
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
            List<ProductArticle> productArticlesList = productArticleRepository.findByProductAndStatus(product, status)
                    .orElseThrow(() -> new ObjectNotFoundException("ProductArticle", "(Product & Status)", "(" + product.getId() + " & " + status.toString() + ")"));

            List<ArticleDTO> articleDTOList = productArticlesList.stream().map(productArticle -> {
                return ArticleMapper.convertToDTO(productArticle.getArticle());
            }).collect(Collectors.toList());
            return new ProductDTO(product.getName(), articleDTOList);
        }).collect(Collectors.toList());
    }

    @Override
    public void sell(long id) {

    }
}
