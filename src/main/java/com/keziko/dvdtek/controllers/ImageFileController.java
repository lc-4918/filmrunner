package com.keziko.dvdtek.controllers;

import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.entities.Dvd;
import com.keziko.dvdtek.services.DvdService;
import com.keziko.dvdtek.services.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/*
 * Nom de classe : ImageFileController
 * Description   :
 * Version       : 1.0
 * Date          : 18/03/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class ImageFileController {

    private static final String UPLOAD_DIR = "\\img\\";
    private final DvdService dvdService;

    @Autowired
    public ImageFileController(DvdService dvdService) {
        this.dvdService = dvdService;
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = null;
        String userDirectory = new File("").getAbsolutePath();
        String fileDownloadFolder = userDirectory + UPLOAD_DIR;
        try {
            if (Objects.isNull(file)) {
                ResponseMessage response = new ResponseMessage(null);
                log.info("dvd sans image");
                return ResponseEntity.ok().body(response);
            }
            if (Objects.nonNull(file.getOriginalFilename())) {
                fileName = StringUtils.cleanPath(file.getOriginalFilename());
                String fileDownload = (fileDownloadFolder + fileName);
                FileUploadUtil.saveFile(fileDownloadFolder, fileName, file);
                ResponseMessage response = new ResponseMessage(fileDownload);
                log.info("image uploadée" + fileDownload);
                return ResponseEntity.ok().body(response);
            }
            log.info("image non importée");
            return ResponseEntity.badRequest().body(null);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            ResponseMessage response = new ResponseMessage(ioe.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("image/{id}")
    public ResponseEntity<Boolean> deleteImageByDvdId(@PathVariable("id") Long id) {
        Boolean result = dvdService.deleteImageByDvdId(id);
        return ResponseEntity.ok().body(result);
    }
}
