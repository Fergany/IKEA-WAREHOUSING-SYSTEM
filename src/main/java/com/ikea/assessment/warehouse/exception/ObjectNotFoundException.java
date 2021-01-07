package com.ikea.assessment.warehouse.exception;

public class ObjectNotFoundException extends RuntimeException{
    public ObjectNotFoundException(String objectName, String attribute, String value){
        super("Cannot find Object " + objectName + " by " + attribute + " with value/s " + value);
    }
}
