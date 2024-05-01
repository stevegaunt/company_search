package com.steve.company_search.service;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.steve.company_search.model.Company;
import com.steve.company_search.model.Officer;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrueProxyService {


    @Value("${trueproxy.base_url}")
    String base_url;

    @Value("${trueproxy.company_uri}")
    String company_search_uri;

    @Value("${trueproxy.officer_uri}")
    String officer_search_uri;


    private RestClient restClient;

    @PostConstruct
    public void postConstructRoutine() {
        restClient = RestClient.builder()
                .baseUrl(base_url)
                .build();
    }


    List<Company> findAllCompanies( String apiKey,String companyName) {

        CompaniesResponse allCompanies = restClient.get()

                .uri(uriBuilder -> uriBuilder.path(company_search_uri)
                        .queryParam( "Query",companyName).build())
                .header("x-api-key",apiKey)
                .retrieve().onStatus(new RestCleintResponseErrorHandler())
                .body(CompaniesResponse.class);

        return allCompanies !=null && allCompanies.getItems()!=null ?allCompanies.getItems() : new ArrayList<>();
    }


    List<Officer> findAllOfficers( String apiKey,String companyNo) {

        OfficersResponse officers= restClient.get()
                .uri(uriBuilder -> uriBuilder.path(officer_search_uri)
                        .queryParam( "CompanyNumber",companyNo).build())
                .header("x-api-key",apiKey)
                .retrieve().onStatus(new RestCleintResponseErrorHandler())
                .body(OfficersResponse.class);
        return officers !=null && officers.getItems()!=null?officers.getItems() : new ArrayList<>();
    }



    public static class RestCleintResponseErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
            return httpResponse.getStatusCode().is5xxServerError() ||
                    httpResponse.getStatusCode().is4xxClientError();
        }

        @Override
        public void handleError(ClientHttpResponse httpResponse) throws IOException {

                throw new HttpClientErrorException(httpResponse.getStatusCode());

        }
    }

    @Getter
    @Setter
//@Table(name = "Officer")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OfficersResponse {

        List<Officer> items;
    }

    @Getter
    @Setter
//@Table(name = "Officer")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CompaniesResponse {

        List<Company> items;
    }


}
