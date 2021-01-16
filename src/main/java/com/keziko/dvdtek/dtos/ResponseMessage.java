package com.keziko.dvdtek.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Nom de classe : ResponseMessage
 * Description   :
 * Version       : 1.0
 * Date          : 29/12/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Data
@NoArgsConstructor
public class ResponseMessage {
    private String message;
    private boolean status;
    private Integer count;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public ResponseMessage(String message, boolean status) {
        this.message = message;
        this.status = status;
    }
    public ResponseMessage(String message, Integer count) {
        this.message = message;
        this.count = count;
    }
}
