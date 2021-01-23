package com.keziko.dvdtek.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Nom de classe : ThemeDTO
 * Description   :
 * Version       : 1.0
 * Date          : 22/01/2021
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDTO {
    private String name;
    private String color;
}
