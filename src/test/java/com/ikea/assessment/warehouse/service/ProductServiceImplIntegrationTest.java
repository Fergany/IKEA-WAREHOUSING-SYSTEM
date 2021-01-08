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
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@NoArgsConstructor
public class ProductServiceImplIntegrationTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private DataLoadService dataLoadService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ProductArticleRepository productArticleRepository;
    @PersistenceContext
    private EntityManager entityManager;


    @Value("${products_file_path}")
    String productsFilePath;

    @Value("${inventory_file_path}")
    String inventoryFilePath;

    @Before
    public void setup() {
        dataLoadService.loadData(inventoryFilePath, productsFilePath);
    }

    @Test
    public void getNewProductsHappyPath() {
        List<ProductDTO> productList = productService.getNewProducts();
        int size = productRepository
                .findAllByStatus(ProductStatus.NEW)
                .map(List::size)
                .orElse(0);
        Assert.assertEquals(size, productList.size());
    }

    @Test
    public void sellProductHappyPath() {
        Product product = productRepository
                .findAllByStatus(ProductStatus.NEW)
                .get()
                .get(0);

        List<ProductArticle> productArticles = getProductArtiles(product);

        long productId = product.getId();
        productService.sell(productId);

        checkProductStatus(productId);
        checkArticleStock(productArticles);
    }

    private List<ProductArticle> getProductArtiles(Product product) {
        return productArticleRepository
                .findByProduct(product)
                .get();
    }

    private void checkProductStatus(long productId) {
        Product product = productRepository
                .findById(productId)
                .get();
        Assert.assertEquals(ProductStatus.SOLD, product.getStatus());
    }

    private void checkArticleStock(List<ProductArticle> productArticles) {
        productArticles
                .stream()
                .forEach(productArticle -> {
                    Article updatedArticle = articleRepository
                            .findById(productArticle.getArticle().getId())
                            .get();
                    Assert.assertEquals((productArticle.getArticle().getStock() - productArticle.getAmountOf())
                            , updatedArticle.getStock());
                });
    }

    @Test
    public void sellProductWithInsufficitArticleStock() {
        Product product = productRepository.save(new Product("Test Product", ProductStatus.NEW));
        long articleId = getNotExistingId(articleRepository);
        Article article = articleRepository.save(new Article(articleId, "Test Article", 3));
        ProductArticle productArticle = productArticleRepository.save(new ProductArticle(product, article, 10));
        long productId = product.getId();

        Assert.assertThrows(InsufficientStockException.class, () -> {
            productService.sell(productId);
        });
    }

    @Test
    public void sellProductNotExisting() {
        long productId = getNotExistingId(productRepository);
        Assert.assertThrows(ObjectNotFoundException.class, () -> {
            productService.sell(productId);
        });
    }

    private long getNotExistingId(JpaRepository repository) {
        long id = 123;
        while (repository.existsById(id)) {
            id += 10;
        }
        return id;
    }

}
