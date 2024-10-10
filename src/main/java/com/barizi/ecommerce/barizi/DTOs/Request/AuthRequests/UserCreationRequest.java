package com.barizi.ecommerce.barizi.DTOs.Request.AuthRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationRequest {


    private String firstName;
    private String lastName;
    private String userName;
    private String phoneNumber;
    private String email;
    private String password;
    private String role;
}
