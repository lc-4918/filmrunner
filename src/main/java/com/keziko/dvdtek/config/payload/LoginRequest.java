package com.keziko.dvdtek.config.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Nom de classe : LoginRequest
 * Description   :
 * Version       : 1.0
 * Date          : 07/01/2021
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
}
