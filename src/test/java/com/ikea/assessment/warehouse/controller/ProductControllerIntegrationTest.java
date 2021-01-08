package com.ikea.assessment.warehouse.controller;


import com.ikea.assessment.warehouse.entity.Product;
import com.ikea.assessment.warehouse.entity.ProductStatus;
import com.ikea.assessment.warehouse.repository.ProductRepository;
import com.ikea.assessment.warehouse.service.DataLoadService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DataLoadService dataLoadService;

    @Autowired
    private MockMvc mockMvc;

    @Value("${products_file_path}")
    private String productsFilePath;

    @Value("${inventory_file_path}")
    private String inventoryFilePath;

    @Before
    public void setup() {
        dataLoadService.loadData(inventoryFilePath, productsFilePath);
    }

    @Test
    public void findAllProductHappyPath() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/products")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void sellProductHappyPath() throws Exception {
        Product product = productRepository
                .findAllByStatus(ProductStatus.NEW)
                .get()
                .get(0);
        long productId = product.getId();
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/products/" + productId + "/sell"))
                .andExpect(MockMvcResultMatchers.status().is(204));
    }

    @Test
    public void sellProductNotExist() throws Exception {
        long id = 123;
        while (productRepository.existsById(id)) {
            id += 10;
        }
        long productId = id;
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/products/" + productId + "/sell"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

}
