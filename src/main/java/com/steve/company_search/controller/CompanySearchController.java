package com.steve.company_search.controller;

import com.steve.company_search.controller.request.CompanySearchRequest;
import com.steve.company_search.controller.response.CompanytSearchResponse;
import com.steve.company_search.service.CompanySearchService;
import com.steve.company_search.config.ApiKeyAuth;
import com.steve.company_search.model.Company;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/company")
public class CompanySearchController {
    Logger logger = LoggerFactory.getLogger(CompanySearchController.class);

    @Autowired
    CompanySearchService companySearchService;


    @ResponseStatus(HttpStatus.OK)
    @PostMapping("search")
    public CompanytSearchResponse doPost(ApiKeyAuth authentication, @RequestParam("activeCompaniesOnly") boolean activeCompaniesOnly,
                                         @RequestBody CompanySearchRequest request) throws ExecutionException, InterruptedException {

        var companyName = request.companyName();
        var companyNo = request.companyNumber();
//        logger.debug(STR." Compnany Name \{companyName}  ");
        logger.debug("Compnany Name {} , company {} ", companyName, companyNo);

        List<Company> companies = companySearchService.searchForCompanies(authentication.getApiKey(), companyName, companyNo, activeCompaniesOnly);


        return CompanytSearchResponse.from(companies);
    }
}
