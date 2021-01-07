package com.ikea.assessment.warehouse.service;

public interface IProductService {
    public List<ProductDTO> getAll();
    public void sell(long id);
}
