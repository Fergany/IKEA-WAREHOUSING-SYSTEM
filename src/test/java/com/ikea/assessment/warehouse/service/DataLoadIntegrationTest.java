package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.exception.DataLoadException;
import com.ikea.assessment.warehouse.repository.ArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductArticleRepository;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DataLoadIntegrationTest {
    @Autowired
    private IDataLoadService dataLoadService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ProductArticleRepository productArticleRepository;

    @Value("${products_file_path}")
    String productsFilePath;

    @Value("${inventory_file_path}")
    String inventoryFilePath;

    @Test
    public void loadDataHappyPath(){
        dataLoadService.loadData(inventoryFilePath, productsFilePath);
        Assert.assertEquals(2, productRepository.findAll().size());
        Assert.assertEquals(4, articleRepository.findAll().size());
        Assert.assertEquals(6, productArticleRepository.findAll().size());
    }

    @Test
    public void loadDataFromNotExistingFile(){
        String notExistingFile1 = "not_real_path";
        String notExistingFile2 = "not_real_path";
        Assert.assertThrows(DataLoadException.class, () -> {
            dataLoadService.loadData(notExistingFile1, notExistingFile2);
        });
    }
}
