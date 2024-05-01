package com.steve.company_search.service;

import com.steve.company_search.model.Address;
import com.steve.company_search.model.Company;
import com.steve.company_search.model.Officer;
import com.steve.company_search.service.CompanySearchService;
import com.steve.company_search.service.TrueProxyService;
import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanySearchServiceTest {


    @InjectMocks
    CompanySearchService sut;

    @Mock
    TrueProxyService trueProxyService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @SneakyThrows
    @Test
    void searchForCompaniesWithCompanyNo() {

        String testApikey="testAPIKey";
        String testCompanyName = "test";

        String testCompanyNo = "test6";
        boolean testActiveCopmaniesOnly=true;

        List<Company> testComanies = generateCompanyTestData(20);
        List<Officer> testOfficers = generateOfficerTestData(20);
        when(trueProxyService.findAllCompanies(testApikey,testCompanyName)).thenReturn(testComanies);
        when(trueProxyService.findAllOfficers(any(String.class),any(String.class))).thenReturn(testOfficers);

        assertDoesNotThrow(() -> {
            List<Company> companies= sut.searchForCompanies(testApikey, testCompanyName, testCompanyNo, testActiveCopmaniesOnly);
            assertEquals(1,companies.size());
            assertEquals (testCompanyNo,companies.getFirst().getCompanyNumber());
            assertEquals ("title6",companies.getFirst().getTitle());
            //modus the test data to only set half of 20 to be active
            assertEquals(10,companies.getFirst().getOfficers().size());

            Address compAddress = companies.getFirst().getAddress();
            assertEquals("companyaddressLineis6",compAddress.getAddressLine_1());
            assertEquals("companylocality6",compAddress.getLocality());
            assertEquals("companypremises6",compAddress.getPremises());
            assertEquals("companycountry6",compAddress.getCountry());
            assertEquals("companypostcode6",compAddress.getPostalCode());



        });

    }

    @SneakyThrows
    @Test
    void searchForCompaniesWithoutCompanyNo() {

        String testApikey="testAPIKey";
        String testCompanyName = "test";

        String testCompanyNo = null;
        boolean testActiveCopmaniesOnly=true;

        List<Company> testComanies = generateCompanyTestData(20);
        List<Officer> testOfficers = generateOfficerTestData(20);
        when(trueProxyService.findAllCompanies(testApikey,testCompanyName)).thenReturn(testComanies);
        when(trueProxyService.findAllOfficers(any(String.class),any(String.class))).thenReturn(testOfficers);

        assertDoesNotThrow(() -> {
            List<Company> companies= sut.searchForCompanies(testApikey, testCompanyName, testCompanyNo, testActiveCopmaniesOnly);
            //modus the test data to only set half of 20 to be active
            assertEquals(10,companies.size());
            assertEquals ("test0",companies.getFirst().getCompanyNumber());
            assertEquals ("title0",companies.getFirst().getTitle());
            //modus the test data to only set half of 20 to be active
            assertEquals(10,companies.getFirst().getOfficers().size());

            Address compAddress = companies.getFirst().getAddress();
            assertEquals("companyaddressLineis0",compAddress.getAddressLine_1());
            assertEquals("companylocality0",compAddress.getLocality());
            assertEquals("companypremises0",compAddress.getPremises());
            assertEquals("companycountry0",compAddress.getCountry());
            assertEquals("companypostcode0",compAddress.getPostalCode());



            companies= sut.searchForCompanies(testApikey, testCompanyName, testCompanyNo, false);
            //not checking on acrive copmanies
            assertEquals(20,companies.size());
            assertEquals ("test19",companies.getLast().getCompanyNumber());
            assertEquals ("title19",companies.getLast().getTitle());
            //modus the test data to only set half of 20 to be active
            assertEquals(10,companies.getLast().getOfficers().size());

             compAddress = companies.getLast().getAddress();
            assertEquals("companyaddressLineis19",compAddress.getAddressLine_1());
            assertEquals("companylocality19",compAddress.getLocality());
            assertEquals("companypremises19",compAddress.getPremises());
            assertEquals("companycountry19",compAddress.getCountry());
            assertEquals("companypostcode19",compAddress.getPostalCode());

        });

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

            officer.setAddress(address);
            testOfficers.add(officer);

        }

        return testOfficers;

    }
}
