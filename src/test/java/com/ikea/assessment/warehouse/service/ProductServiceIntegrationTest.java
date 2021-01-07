package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.dto.ProductDTO;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ProductServiceIntegrationTest {

    @Autowired
    private IProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataLoadService dataLoadService;

    @Value("${products_file_path}")
    String productsFilePath;

    @Value("${inventory_file_path}")
    String inventoryFilePath;

    @Before
    public void setup() {
        dataLoadService.loadData(inventoryFilePath ,productsFilePath);
    }

    @Test
    public void getNewProductsHappyPath() {
        List<ProductDTO> productList = productService.getNewProducts();
        int size = productRepository.findAllByStatus(ProductStatus.NEW).map(List::size).orElse(0);
        Assert.assertEquals(size, productList.size());
    }

}
