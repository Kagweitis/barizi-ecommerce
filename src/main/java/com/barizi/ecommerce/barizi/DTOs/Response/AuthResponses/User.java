package com.barizi.ecommerce.barizi.DTOs.Response.AuthResponses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String email;
    private String phoneNumber;
    private String userName;
    private String firstName;
    private String lastName;
}

