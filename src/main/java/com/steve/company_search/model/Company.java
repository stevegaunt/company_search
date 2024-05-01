package com.steve.company_search.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

//@Entity
@Getter
@Setter
@EqualsAndHashCode
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

public class Company {

    private String companyNumber;

    private String companyType;

    private String title;

    private String companyStatus;

    private String dateOfCreation;

    private Address address;

    private List<Officer> officers;

}

