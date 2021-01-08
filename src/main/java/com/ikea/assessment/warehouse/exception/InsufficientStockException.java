package com.ikea.assessment.warehouse.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(long artcileId, long productId){
        super("Article " + artcileId + " don't have sufficient stock for this product " + productId);
    }
}
