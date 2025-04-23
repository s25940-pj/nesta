package com.example.nesta.controller;

import com.example.nesta.exception.InvalidQueryParamException;

import java.util.Map;
import java.util.Set;

public abstract class AbstractSearchController {
    protected Set<String> allowedQueryParams;

    public AbstractSearchController(Set<String> allowedQueryParams) {
        this.allowedQueryParams = allowedQueryParams;
    }

    protected void validateRequestParams(Map<String, String> allParams, Set<String> allowedParams) {
        for (String param : allParams.keySet()) {
            if (!allowedParams.contains(param)) {
                throw new InvalidQueryParamException(param);
            }
        }
    }
}
