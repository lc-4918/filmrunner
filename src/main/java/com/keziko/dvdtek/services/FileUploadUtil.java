package com.keziko.dvdtek.services;

/*
 * Nom de classe : FileUploadUtil
 * Description   :
 * Version       : 1.0
 * Date          : 27/12/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
import java.io.*;
import java.nio.file.*;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    public static void saveFile(String uploadDir, String fileName,
                                MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
            InputStream inputStream = multipartFile.getInputStream();

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void deleteFile(String pathFile) throws IOException {
        Path filePath = Paths.get(pathFile);
        Files.deleteIfExists(filePath);
    }
}