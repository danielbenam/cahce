package com.danielbenami.cahce.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BondDto {

    @NotBlank(message = "bondId shouldn't be empty")
    private String bondId;

    @NotBlank(message = "exchange shouldn't be empty")
    private String exchange;

    @NotBlank(message = "name shouldn't be empty")
    private String name;

    @NotBlank(message = "securityType shouldn't be empty")
    private String securityType;

    @NotBlank(message = "description shouldn't be empty")
    private String description;

    @NotBlank(message = "currency shouldn't be empty")
    private String currency;

    @NotBlank(message = "country shouldn't be empty")
    private String country;









}
