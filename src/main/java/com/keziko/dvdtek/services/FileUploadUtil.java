package com.keziko.dvdtek.services;

import java.io.*;
import java.nio.file.*;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

    /**
     * Sauvegarde un fichier image dans le répertoire de stockage des images ("img")
     * @param uploadDir chemin absolu d'accès au dossier de stockage des images
     * @param fileName nom de l'image que portera le fichier
     * @param multipartFile fichier image issu du front
     * @throws IOException
     * @see com.keziko.dvdtek.controllers.IoController#importxls(MultipartFile)
     */
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

    /**
     * Supprime un fichier image stocké
     * @param pathFile chemin d'accès absolu au fichier image
     * @throws IOException
     */
    public static void deleteFile(String pathFile) throws IOException {
        Path filePath = Paths.get(pathFile);
        Files.deleteIfExists(filePath);
    }
    public static String getDbSubPath(){
        String os = System.getProperty("os.name").toLowerCase();;
        String subpath;
        if (os.contains("win")){
            subpath = "\\data\\";
        }else{
            subpath = "/data/";
        }
        return subpath;
    }
}