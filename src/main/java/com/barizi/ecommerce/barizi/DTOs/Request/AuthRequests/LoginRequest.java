package com.barizi.ecommerce.barizi.DTOs.Request.AuthRequests;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}
