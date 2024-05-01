package com.steve.company_search.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.steve.company_search.model.Company;
import com.steve.company_search.model.Officer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.net.URL;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static com.steve.company_search.service.TrueProxyService.CompaniesResponse;
import static com.steve.company_search.service.TrueProxyService.OfficersResponse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@WireMockTest(httpPort = 8081)

class TrueProxyServiceTest {



    @Autowired
    TrueProxyService trueProxyService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void getfindAllCompanies(WireMockRuntimeInfo wireMockRuntimeInfo) {


        stubFor(get(urlPathMatching("/TruProxyAPI/rest/Companies/v1/Search")).
                withQueryParam("Query",equalTo("test"))

                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("truProxyCompanySearchResponse.json")));


       List<Company> companies =trueProxyService.findAllCompanies("dsf","test");
        assertThat(companies.size()).isEqualTo(20);
        assertThat(companies.getFirst().getCompanyNumber()).isEqualTo("06500244");
        assertThat(companies.getFirst().getTitle()).isEqualTo("BBC LIMITED");
        assertThat(companies.getFirst().getAddress()).isNotNull();
        assertThat(companies.getFirst().getAddress().getPostalCode()).isEqualTo("DN22 0AD");
        assertThat(companies.getFirst().getAddress().getPremises()).isEqualTo("Boswell Cottage Main Street");
        assertThat(companies.getFirst().getAddress().getLocality()).isEqualTo("Retford");
        assertThat(companies.getFirst().getAddress().getAddressLine_1()).isEqualTo("North Leverton");


        assertDoesNotThrow(() -> {
            URL resource = TrueProxyServiceTest.class.getClassLoader().getResource("__files/truProxyCompanySearchResponse.json");
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            CompaniesResponse expectedcompanies = om.readValue( resource,CompaniesResponse.class );
            assertThat( expectedcompanies.items).containsAll(companies);
        });
    }

    @Test
    void findAllOfficers(WireMockRuntimeInfo wireMockRuntimeInfo) {

        final String testCompanyNumber ="06500244";
        stubFor(get(urlPathMatching("/TruProxyAPI/rest/Companies/v1/Officers")).
                withQueryParam("CompanyNumber",equalTo(testCompanyNumber))

                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("truProxyCompanyOfficerResponse.json")));


        List<Officer> officers =trueProxyService.findAllOfficers("dsf",testCompanyNumber);
        assertThat(officers.size()).isEqualTo(4);
        assertThat(officers.getFirst().getOfficerRole()).isEqualTo("secretary");
        assertThat(officers.getFirst().getName()).isEqualTo("BOXALL, Sarah Victoria");
        assertThat(officers.getFirst().getAppointedOn()).isEqualTo("2008-02-11");
        assertThat(officers.getFirst().getResignedOn()).isNull();
        assertThat(officers.getFirst().getAddress()).isNotNull();
        assertThat(officers.getFirst().getAddress().getPostalCode()).isEqualTo("SW20 0DP");
        assertThat(officers.getFirst().getAddress().getPremises()).isEqualTo("5");
        assertThat(officers.getFirst().getAddress().getLocality()).isEqualTo("London");
        assertThat(officers.getFirst().getAddress().getAddressLine_1()).isEqualTo("Cranford Close");


        assertDoesNotThrow(() -> {
            URL resource = TrueProxyServiceTest.class.getClassLoader().getResource("__files/truProxyCompanyOfficerResponse.json");
            ObjectMapper om = new ObjectMapper();
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            OfficersResponse expectedOfficers = om.readValue( resource, OfficersResponse.class );
            assertThat( expectedOfficers.items).containsAll(officers);
        });
    }



    @Test
    void getfindAllCompaniesException(WireMockRuntimeInfo wireMockRuntimeInfo) {

        stubFor(get(urlPathMatching("/TruProxyAPI/rest/Companies/v1/Search")).
                withQueryParam("Query",equalTo("test"))

                .willReturn(aResponse()
                        .withStatus(403).withStatusMessage("forbidden")));
        assertThrows(HttpClientErrorException.class, () -> {
          trueProxyService.findAllCompanies("dsf", "test");
        });
    }


    @Test
    void getfindAllOfficersException(WireMockRuntimeInfo wireMockRuntimeInfo) {
        final String testCompanyNumber ="06500244";
        stubFor(get(urlPathMatching("/TruProxyAPI/rest/Companies/v1/Officers")).
                withQueryParam("CompanyNumber",equalTo(testCompanyNumber))

                .willReturn(aResponse()
                        .withStatus(403).withStatusMessage("forbidden")));
        assertThrows(HttpClientErrorException.class, () -> {
             trueProxyService.findAllOfficers("dsf",testCompanyNumber);
        });
    }
}