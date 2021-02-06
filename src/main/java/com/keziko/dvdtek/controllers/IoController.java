package com.keziko.dvdtek.controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keziko.dvdtek.config.CsvReader;
import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.dtos.json.ListeObject;
import com.keziko.dvdtek.dtos.json.XlsObject;
import com.keziko.dvdtek.services.FileUploadUtil;
import com.keziko.dvdtek.services.IoService;
import jxl.write.WriteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Nom de classe : IOController
 * @version 18/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class IoController {
    private static final String UPLOAD_DIR = "\\xls\\";
    private final IoService iOService;

    @Autowired
    public IoController(IoService iOService) {
        this.iOService = iOService;
    }

    /**
     * Lecture du fichier XLS déposé et intégration des lignes dans la base de données
     * méthode POST {RestService#executeimport(file: File | undefined)}
     * @param file fichier excel (XLS) renvoyé par le front.<br>
     * Le fichier excel doit avoir les pré-requis suivants:
     * <ul>
     *  <li>
     *      données dans la 1ère feuille du classeur ou fichier mono-feuille
     *  </li>
     *  <li>
     *      ordre des colonnes conforme au fichier initial (A: format, B: type...)
     *  </li>
     *  <li>
     *      réalisateurs séparés par ", " (caractères [virgule] et [espace])
     *  </li>
     *  <li>
     *      pays et langues séparés par "-" (caractère [trait-d'union])
     *  </li>
     * </ul>
     * @return responseEntity contenant un objet {@link ResponseMessage} avec un statut true si l'import a été réussi
     */
    @PostMapping("import/execute")
    public ResponseEntity<ResponseMessage> importxls(@RequestParam("file") MultipartFile file) {
        String userDirectory = new File("").getAbsolutePath();
        String fileDownloadFolder = userDirectory + UPLOAD_DIR;
        String fileName = null;
        try {
            if (Objects.nonNull(file.getOriginalFilename())) {
                fileName = StringUtils.cleanPath(file.getOriginalFilename());
                // fileLocation = (fileDownloadFolder + fileName);
                FileUploadUtil.saveFile(null, fileName, file);
            }
            Object data = CsvReader.readJExcel(fileName);
            if (data instanceof String) {
                ResponseMessage message = new ResponseMessage((String) data);
                return ResponseEntity.status(HttpStatus.OK).body(message);
            } else {
                iOService.importXLS((List<XlsObject>) data);
                ResponseMessage message = new ResponseMessage("Données importées", true);
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }
        } catch (Exception e) {
            ResponseMessage message = new ResponseMessage("Erreur dans l'import de données", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    /**
     * Alimente la page d'import excel ("/import")
     * méthode GET {RestService#getImportedLineCount()}
     * @return responseEntity contenant un objet {@link ResponseMessage} ayant pour attribut count le nombre de lignes déjà importées
     */
    @GetMapping("import/count")
    public ResponseEntity<ResponseMessage> getLineImportedCount() {
        log.info("GET /import/count");
        ResponseMessage response = iOService.getLineCounter();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Téléchargement de la base de données H2 "films.mv.db"
     * @return le fichier de la base de données
     */
    @PostMapping("/db/download")
    public ResponseEntity<InputStreamResource> download(@RequestBody String list){
        File file;
        try {
            ObjectMapper om = new ObjectMapper();
            om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            ListeObject listes = om.readValue(list, ListeObject.class);
            log.info("PUT /db/download");
            file = iOService.createExcel(listes);
            FileInputStream fis = new FileInputStream(file);
            InputStreamResource isr = new InputStreamResource(fis);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(isr);
        } catch (IOException | WriteException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
