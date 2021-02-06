package com.keziko.dvdtek.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nom de classe : ThemeDTO
 * @version 22/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThemeDTO {
    private String name;
    private String color;
}
