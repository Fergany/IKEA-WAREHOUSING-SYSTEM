package com.ikea.assessment.warehouse.service;

import com.ikea.assessment.warehouse.dto.ProductDTO;

import java.util.List;

public interface IProductService {
    public List<ProductDTO> getNewProducts();
    public void sell(long id);
}
