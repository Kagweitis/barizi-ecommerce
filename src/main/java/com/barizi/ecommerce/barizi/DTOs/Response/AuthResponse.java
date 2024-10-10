package com.barizi.ecommerce.barizi.DTOs.Response;

import com.barizi.ecommerce.barizi.Entities.UserEntity;
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
