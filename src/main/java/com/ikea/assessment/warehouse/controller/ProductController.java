package com.ikea.assessment.warehouse.controller;

import com.ikea.assessment.warehouse.dto.ProductDTO;
import com.ikea.assessment.warehouse.service.IProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = {"Products API"})
@RequestMapping("/api/v1/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService){
        this.productService = productService;
    }

    @ApiOperation(value = "View a list of NEW products")
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllNew(){
        return new ResponseEntity<List<ProductDTO>>(productService.getNewProducts(), HttpStatus.OK);
    }

    @ApiOperation(value = "Sell Product")
    @PutMapping("/{id}/sell")
    public ResponseEntity<Void> sell(@PathVariable long id){
        productService.sell(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
