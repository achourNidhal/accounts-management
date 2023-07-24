package com.socgen.accountsmanagement.domain;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Client {

    private UUID id;
    private String firstName;
    private String lastName;
}