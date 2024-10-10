package com.barizi.ecommerce.barizi.DTOs.Response.AuthResponses;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private int status;
    private String message;
    private User user;
    private String jwt;
}
