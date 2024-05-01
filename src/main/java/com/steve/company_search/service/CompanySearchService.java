package com.steve.company_search.service;


import com.steve.company_search.model.Company;
import com.steve.company_search.model.Officer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class CompanySearchService {


    @Autowired
    private TrueProxyService trueProxyService;

    public List<Company> searchForCompanies(String apiKey, String companyName, String companyNo, boolean activeCompaniesOnly) throws ExecutionException, InterruptedException {

        List<Company>  companies = searchCompanies(apiKey,companyName,companyNo,activeCompaniesOnly);

        // proces all the officers call in Virtual threads and wait until they are all done

         List< Company> companyWithOfficers  =companies.stream()
                .map(company ->
                        CompletableFuture.supplyAsync(() ->
                                findOffices(apiKey,company)))
                .map(CompletableFuture::join).toList()
                 .stream().map(CompletableFuture::join)
                 .toList();



        return companyWithOfficers;

    }


    /**
     * Searched TrueProxy for all teh companies against a company name
     * it will then try and filter on active companies then filter to a specific company number
     *
     * @param apiKey shouldnt be used here, passed form authentation credentials for this demo.
     * @param CompanyName search all companies wih thsi name
     * @param companyNo filter to only this compnay number a value is passed
     * @param activeCompaniesOnly filter on  active companies
     * @return list of companies after the filtering
     */
    public List<Company> searchCompanies(String apiKey, String CompanyName,  String companyNo,boolean activeCompaniesOnly){

        List<Company> companies =trueProxyService.findAllCompanies(apiKey,CompanyName);
         //filters on acrive only company status
        if (activeCompaniesOnly){
            companies =companies.stream()
                    .filter(company -> company.getCompanyStatus().equalsIgnoreCase("active"))
                    .toList();
        }
        //reduce the companies if have number
        if (companyNo !=null && !companyNo.isBlank() ){
            companies =companies.stream()
                    .filter(company -> company.getCompanyNumber().equalsIgnoreCase(companyNo))
                    .toList();
        }


        return companies;

    }

    /**
     * Search TrueProxy for officers assciated with the company.
     * Any offices found are enriched to the company model
     * will only include officers that are active
     * @param apiKey shouldnt be used here, passed form authentation credentials for this demo.
     * @param company entoty to search offiers for this company
     * @return company enriched with officers
     */
    @Async
    public CompletableFuture<Company> findOffices(String apiKey, Company company){
        List<Officer> officers =trueProxyService.findAllOfficers(apiKey,company.getCompanyNumber());
        officers =officers.stream()
                .filter(officer -> officer.getResignedOn()==null)
                .toList();
        company.setOfficers(officers);

        return CompletableFuture.completedFuture(company);

    }




}
