package com.keziko.dvdtek.config;

import com.keziko.dvdtek.dtos.json.XlsObject;
import com.keziko.dvdtek.repositories.DirectorRepository;
import com.keziko.dvdtek.repositories.Dvdrepository;
import com.keziko.dvdtek.repositories.ThemeRepository;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Null;
import java.io.File;
import java.io.IOException;
import java.util.*;


/**
 * Nom de classe : CsvReader
 * @version 10/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
public class CsvReader {

    @Autowired
    private static ThemeRepository themeRepository;
    @Autowired
    private static DirectorRepository directorRepository;
    @Autowired
    private static Dvdrepository dvdrepository;

    /**
     * Lecture du fichier excel (XLS) déposé avec jxl
     * La lecture est effectuée sur les colonnes définies dans la méthode et sur toutes les lignes.
     * La lecture s'arrête à la première ligne dont la cellule est vide (cellule C*: titre)
     * @param fileLocation chemin d'accès absolu au fichier excel (XLS)
     * @return un objet de type {@link Object} dont les pri
     */
    public static Object readJExcel(String fileLocation) {

        List<XlsObject> data = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        settings.setGCDisabled(true);
        settings.setEncoding("ISO8859_1");
        Workbook workbook = null;
        try{
            File file = new File(fileLocation);
            workbook = Workbook.getWorkbook(file,settings);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            System.out.println("NOMBRE DE LIGNES: " + rows);

            // démarre à 1 pour exclure la ligne d'en-têtes
            for (int i = 1; i < rows; i++) {
                String titreCell = sheet.getCell(2, i).getContents();
                // parmi les lignes on ne traite que celles qui ont la cellule titre remplie
                if (!titreCell.isEmpty()){
                    String format = sheet.getCell(0, i).getContents();
                    String type = sheet.getCell(1, i).getContents();
                    String titre = sheet.getCell(2, i).getContents();
                    String annee = sheet.getCell(3, i).getContents();
                    String realisateurs = sheet.getCell(4, i).getContents();
                    String realisateursId = sheet.getCell(5, i).getContents();
                    String pays = sheet.getCell(6, i).getContents();
                    String duree = sheet.getCell(7, i).getContents();
                    String sub = sheet.getCell(8, i).getContents();
                    if ("0".equals(sub)){
                        sub="";
                    }
                    String support = sheet.getCell(9, i).getContents();
                    String norme = sheet.getCell(10, i).getContents();
                    String details = sheet.getCell(11, i).getContents();
                    String source = sheet.getCell(12, i).getContents();
                    String titreVf = sheet.getCell(13, i).getContents();
                    String label = sheet.getCell(14, i).getContents();
                    String cm1 = sheet.getCell(15, i).getContents();
                    String cm2 = sheet.getCell(16, i).getContents();
                    String cm3 = sheet.getCell(17, i).getContents();
                    String cm4 = sheet.getCell(18, i).getContents();
                    String cm5 = sheet.getCell(19, i).getContents();
                    Integer ligne = i+1;
                    XlsObject line = new XlsObject(format,type,titre,annee,realisateurs,realisateursId,pays,duree,sub,support,norme,details,source,titreVf,label,cm1,cm2,cm3,cm4,cm5,ligne);
                    System.out.println("XLS OBJECT ligne " + ligne + " : " + line.toString());
                    if (!line.getTitre().isEmpty()){
                        data.add(line);
                    }
                }
            }
        }catch (IOException | BiffException | NullPointerException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return data;
    }

    public static Integer getLineCount(String fileLocation){
        WorkbookSettings settings = new WorkbookSettings();
        settings.setGCDisabled(true);
        settings.setEncoding("ISO8859_1");
        Workbook workbook = null;
        int count = 0;
        try {
            workbook = Workbook.getWorkbook(new File(fileLocation), settings);
            Sheet sheet = workbook.getSheet(0);
            int rows = sheet.getRows();
            for (int i = 1; i < rows; i++) {
                String titreCell = sheet.getCell(2, i).getContents();
                if (titreCell.isEmpty()){
                    count = i;
                }
                i++;
            }
        }catch (IOException | BiffException e){
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return count;
    }
}
