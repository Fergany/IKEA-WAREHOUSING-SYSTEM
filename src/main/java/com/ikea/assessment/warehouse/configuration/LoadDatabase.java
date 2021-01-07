package com.ikea.assessment.warehouse.configuration;

import com.ikea.assessment.warehouse.service.IDataLoadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@Configuration
public class LoadDatabase {

    @Value("${inventory_file_path}")
    String inventoryFilePath;

    @Value("${products_file_path}")
    String productsFilePath;

    @Bean
    CommandLineRunner initDatabase(IDataLoadService dataLoadService) {
        return args -> {
            dataLoadService.loadData(inventoryFilePath, productsFilePath);
        };
    }

}
