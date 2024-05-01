package com.steve.company_search.controller.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.steve.company_search.model.Company;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CompanytSearchResponse(int totalCount, List<Company> items) {


    public static CompanytSearchResponse from(List<Company> items) {
        return
                new CompanytSearchResponse(items.size(), items);
    }

}
