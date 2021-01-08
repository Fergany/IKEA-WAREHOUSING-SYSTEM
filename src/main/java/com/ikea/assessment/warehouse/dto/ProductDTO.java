package com.ikea.assessment.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProductDTO {
    Long id;
    String name;
    Long availableQuantity;
}
