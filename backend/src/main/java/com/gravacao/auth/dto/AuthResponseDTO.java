package com.gravacao.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String email;
    private String name;
    private List<String> roles;
}

