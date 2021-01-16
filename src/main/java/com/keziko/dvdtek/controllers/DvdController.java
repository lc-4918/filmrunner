package com.keziko.dvdtek.controllers;

import com.keziko.dvdtek.config.CsvReader;
import com.keziko.dvdtek.dtos.DvdForm;
import com.keziko.dvdtek.dtos.DvdListItem;
import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.dtos.XlsObject;
import com.keziko.dvdtek.entities.Director;
import com.keziko.dvdtek.entities.Dvd;
import com.keziko.dvdtek.entities.Shortfilm;
import com.keziko.dvdtek.entities.Theme;
import com.keziko.dvdtek.services.DvdService;
import com.keziko.dvdtek.services.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.NoResultException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Nom de classe : DvdController
 * @version 18/03/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class DvdController {
    private static final String UPLOAD_DIR = "\\xls\\";
    private final DvdService dvdService;

    @Autowired
    public DvdController(DvdService dvdService) {
        this.dvdService = dvdService;
    }

    /**
     *
     * @return
     */
    @GetMapping("/dvd")
    public ResponseEntity<List<DvdListItem>> getDtos() {
        try{
            List<DvdListItem> dtos = dvdService.findAllDto();
            if (Objects.isNull(dtos))
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.status(HttpStatus.OK).body(dtos);
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param formDto
     * @return
     */
    @PutMapping("/dvd")
    public ResponseEntity<DvdListItem> addDvd(@RequestBody DvdForm formDto) {
        try{
            Dvd dvd = dvdService.addDvd(formDto);
            DvdListItem dvdItem = dvdService.convertDvdToListDto(dvd);
            if (Objects.isNull(dvdItem))
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return ResponseEntity.status(HttpStatus.OK).body(dvdItem);
        }catch(Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param formDtos
     * @return
     */
    @PostMapping("/dvd")
    public ResponseEntity<List<DvdListItem>> addDvds(List<DvdForm> formDtos) {
        try{
            List<Dvd> dvds = dvdService.addDvds(formDtos);
            if (Objects.isNull(dvds)){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }else {
                List<DvdListItem> items = dvdService.convertDvdToListDtos(dvds);
                return ResponseEntity.status(HttpStatus.OK).body(items);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/dvd/{id}")
    public ResponseEntity<DvdForm> getDvd(@PathVariable("id") Long id) {
        try{
            Optional<Dvd> optionalDvd = dvdService.findDvdById(id);
            if (optionalDvd.isPresent()) {
                Dvd dvd = optionalDvd.get();
                DvdForm editDvd = dvdService.convertDvdToFormDto(dvd);
                return ResponseEntity.status(HttpStatus.OK).body(editDvd);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param dvdForm
     * @param id
     * @return
     */
    @PutMapping("/dvd/{id}")
    public ResponseEntity<DvdListItem> updateDvd(@RequestBody DvdForm dvdForm, @PathVariable("id") Long id) {
        try{
            Dvd dvd = dvdService.updateDvd(dvdForm,id);
            DvdListItem item = dvdService.convertDvdToListDto(dvd);
            return ResponseEntity.status(HttpStatus.OK).body(item);
        }catch(NoResultException e){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping("/dvd/{id}")
    public ResponseEntity<Boolean> deleteDvdById(@PathVariable("id") Long id) {
        try {
            dvdService.deleteDvdById(id);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @return
     */
    @GetMapping("/context")
    public ResponseEntity<ResponseMessage> getApplicationContext(){
        try{
            String userDirectory = new File("").getAbsolutePath();
            String imageDirectory = userDirectory+"\\img\\";
            ResponseMessage message = new ResponseMessage(imageDirectory);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @return
     */
    @PostMapping("import/execute")
    public ResponseEntity<ResponseMessage> importxls(@RequestParam("file") MultipartFile file){
        String userDirectory = new File("").getAbsolutePath();
        String fileDownloadFolder = userDirectory + UPLOAD_DIR;
        String fileLocation = null;
        try {
            if (Objects.nonNull(file.getOriginalFilename())) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());
                fileLocation = (fileDownloadFolder + fileName);
                FileUploadUtil.saveFile(fileDownloadFolder, fileName, file);
            }
            dvdService.initRolesAndUser();
            Object data = CsvReader.readJExcel(fileLocation);
            if (data instanceof String){
                ResponseMessage message = new ResponseMessage((String)data);
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }else {
                dvdService.importXLS((List<XlsObject>) data);
                ResponseMessage message = new ResponseMessage("Données importées",true);
                return ResponseEntity.status(HttpStatus.OK).body(message);
            }
        }catch (Exception e){
            ResponseMessage message = new ResponseMessage("Erreur dans l'import de données",false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
        }
    }

    @GetMapping("import/count")
    public ResponseEntity<ResponseMessage> getLineImportedCount(){
        ResponseMessage response = dvdService.getLineCounter();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
