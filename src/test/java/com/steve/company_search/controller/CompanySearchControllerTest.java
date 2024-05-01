package com.steve.company_search.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.steve.company_search.model.Address;
import com.steve.company_search.model.Company;
import com.steve.company_search.model.Officer;
import com.steve.company_search.controller.request.CompanySearchRequest;
import com.steve.company_search.service.CompanySearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class CompanySearchControllerTest {


    @Autowired
    CompanySearchController companySearchController;
    @Mock
    CompanySearchService mockCompanySearchService;

    @Autowired
    private MockMvc mockMvc;

    @Value("${api.key:''}")
    private String apiKey;

    @Test
    void doPost() throws Exception {

        //inject mock...  using spring context
        companySearchController.companySearchService=mockCompanySearchService;
        final String testCompanyNumber = "testCompanyNo";
        final String testCompanyName = "testComanyName";

        List<Company> testCompanies = generateCompanyTestData(5);
        when(mockCompanySearchService.searchForCompanies(apiKey,testCompanyName,testCompanyNumber,true))
                .thenReturn(testCompanies);
        ObjectMapper om = new ObjectMapper();
        CompanySearchRequest request = new CompanySearchRequest(testCompanyName,testCompanyNumber);
        MvcResult result =  this.mockMvc.perform(post("/api/company/search?activeCompaniesOnly=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content( om.writeValueAsString(request))

                .header("x-api-key", apiKey)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_count").value("5"))
                .andExpect(jsonPath("$.items[0].company_number").value("test0"))
                .andExpect(jsonPath("$.items[0].title").value("title0"))
                .andExpect(jsonPath("$.items[0].company_status").value("active"))
                .andExpect(jsonPath("$.items[0].date_of_creation").value("2024-01-0"))
                .andExpect(jsonPath("$.items[0].address.locality").value("companylocality0"))
                .andExpect(jsonPath("$.items[0].officers[0].name").value("name0"))
                .andExpect(jsonPath("$.items[0].officers[0].officer_role").value("role0"))
                .andExpect(jsonPath("$.items[0].officers[0].address.locality").value("officerlocality0"))
                .andReturn();


        String content = result.getResponse().getContentAsString();


    }

    @Test
    void shouldReturnError_IfWrongApiKeyIsProvided() throws Exception {
        this.mockMvc.perform(post("/api/company/search?activeCompaniesOnly=true").header("x-api-key", "Inavlidfdsfs"))
                .andExpect(status().isUnauthorized());
    }




    List<Company> generateCompanyTestData(int number){
        List<Company> testCompanies = new ArrayList<>();

        for (int i = 0; i <number ; i++) {
            Company company = new Company();
            company.setCompanyType("testCompanyType"+i);
            company.setCompanyNumber("test"+i);

            company.setCompanyStatus( (i % 2) == 0 ?"active":"inactive");
            company.setTitle("title"+i);
            company.setDateOfCreation("2024-01-"+i);

            Address address = new Address();
            address.setAddressLine_1("companyaddressLineis"+i);
            address.setLocality("companylocality"+i);
            address.setCountry("companycountry"+i);
            address.setPremises("companypremises"+i);
            address.setPostalCode("companypostcode"+i);

            company.setAddress(address);

            company.setOfficers(generateOfficerTestData(3));
            testCompanies.add(company);


        }

        return testCompanies;

    }

    List<Officer> generateOfficerTestData(int number){
        List<Officer> testOfficers = new ArrayList<>();

        for (int i = 0; i <number ; i++) {
            Officer officer = new Officer();
            officer.setName("name"+i);
            officer.setOfficerRole("role"+i);
            officer.setAppointedOn("2024-01-"+i);
            officer.setResignedOn((i % 2) == 0 ?"2024-01-"+i:null);


            Address address = new Address();
            address.setAddressLine_1("officeraddressLineis"+i);
            address.setLocality("officerlocality"+i);
            address.setCountry("officercountry"+i);
            address.setPremises("officerpremises"+i);
            address.setPostalCode("officerpostcode"+i);

            officer.setAddress(address);
            testOfficers.add(officer);

        }

        return testOfficers;

    }
}

