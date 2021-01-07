package com.ikea.assessment.warehouse.controller;

import com.ikea.assessment.warehouse.dto.ProductDTO;
import com.ikea.assessment.warehouse.service.IProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllNew(){
        return new ResponseEntity<List<ProductDTO>>(productService.getNewProducts(), HttpStatus.OK);
    }

    @PutMapping("/{id}/sell")
    public ResponseEntity sell(@PathVariable long id){
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
