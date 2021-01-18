package com.keziko.dvdtek.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keziko.dvdtek.dtos.ResponseMessage;
import com.keziko.dvdtek.dtos.json.XlsObject;
import com.keziko.dvdtek.dtos.json.ListeObject;
import com.keziko.dvdtek.entities.*;
import com.keziko.dvdtek.enums.ERole;
import com.keziko.dvdtek.repositories.*;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.*;
import jxl.write.Number;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Nom de classe : ImportExportService
 * @version 18/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Service
public class IoService {

    private final DirectorRepository directorRepository;
    private final Dvdrepository dvdrepository;
    private final RoleRepository roleRepository;
    private final ThemeRepository themeRepository;
    private final ShortfilmRepository shortfilmRepository;
    private ResponseMessage intervalMessage;

    @Autowired
    public IoService(DirectorRepository directorRepository, Dvdrepository dvdrepository, RoleRepository roleRepository, ThemeRepository themeRepository, ShortfilmRepository shortfilmRepository) {
        this.directorRepository = directorRepository;
        this.dvdrepository = dvdrepository;
        this.roleRepository = roleRepository;
        this.themeRepository = themeRepository;
        this.shortfilmRepository = shortfilmRepository;
    }


    /**
     * Après lecture du fichier excel (XLS) avec la librairie jxl,<br>
     * transforme l'ensemble des lignes du fichier {@link XlsObject} en dvd, réalisateurs, court-métrage et thème
     * @param data liste de DTO de type {@link XlsObject} dont les propriétés nommées ont été importées depuis le contenu des cellules du fichier excel (XLS)
     * @see com.keziko.dvdtek.controllers.IoController#importxls(MultipartFile)
     * @see IoService#importXLSLine(XlsObject) (sous-méthode)
     */
    public void importXLS(List<XlsObject> data){
        int index = 0;
        for (XlsObject object : data){
            importXLSLine(object);
            this.intervalMessage = new ResponseMessage(object.getTitre(),index);
            index++;
        }
    }

    /**
     * Après lecture du fichier excel (XLS) avec la librairie jxl,<br>
     * transforme la ligne du fichier {@link XlsObject} en dvd, réalisateurs, court-métrage et thème
     * @param object DTO de type {@link XlsObject} dont les propriétés nommées ont été importées depuis le contenu des cellules du fichier excel (XLS)
     * @see com.keziko.dvdtek.config.CsvReader#readJExcel(String fileLocation)
     * @see IoService#importXLS(List XlsObjects)
     */
    @Transactional
    public void importXLSLine(XlsObject object){
        boolean isVostFr = false;
        // TITRE
        String titre = object.getTitre();
        // TITRE VF
        String titreVf = object.getTitreVf();
        // ANNEE
        Integer annee = Integer.parseInt(object.getAnnee());
        // DUREE
        Integer duree = Integer.parseInt(object.getDuree().replace("'",""));
        // FORMAT
        // le tip est devenu "format"
        String objectFormat = object.getFormat();
        int format;
        switch(objectFormat.toUpperCase()){
            case "LM":
                format = 1;
                break;
            case "CM":
                format = 2;
                break;
            case "MM":
                format = 3;
                break;
            default:
                format = 1; // LM
                break;
        }
        Integer tip = format;

        // TYPE
        // le genre est devenu "type"
        String objectType = object.getType();
        int type;
        switch(objectType.toUpperCase()){
            case "DOC":
                type = 1;
                break;
            case "ANI":
                type = 2;
                break;
            case "EXP":
                type = 4;
                break;
            default:
                type = 3; // FIC
        }
        Integer genre = type;

        // NORME
        String objectNorme = object.getNorme();
        int normeInt;
        if ("NTSC".equalsIgnoreCase(objectNorme)) {
            normeInt = 1;
        } else {
            normeInt = 2;
        }
        Integer norme = normeInt;

        // SUPPORT
        String objectSupport = object.getSupport();
        int supportInt;
        switch(objectSupport.toUpperCase()){
            case "XRIP":
                supportInt = 1;
                break;
            case "720P":
                supportInt = 2;
                break;
            case "1080":
                supportInt = 3;
                break;
            case "DVD9":
                supportInt = 5;
                break;
            case "BD-R":
                supportInt = 6;
                break;
            default:
                supportInt = 4; //DVD5
                break;
        }
        Integer support = supportInt;

        // SOURCE
        String objectSource = object.getSource();
        int sourceInt;
        switch (objectSource.toUpperCase()){
            case "VOD":
                sourceInt = 2;
                break;
            case "WEB":
                sourceInt = 3;
                break;
            case "TV":
                sourceInt = 4;
                break;
            case "VHS":
                sourceInt = 5;
                break;
            default:
                sourceInt = 1; //DVD
                break;
        }
        Integer source = sourceInt;

        // DETAILS
        String objectDetails = object.getDetails().toUpperCase();
        String[] detailsArray = objectDetails.split("-");
        List<String> detailsResult = new ArrayList<>();
        for (String detail : detailsArray){
            switch(detail){
                case "CM":
                    detailsResult.add("2");
                    break;
                case "VFR":
                    detailsResult.add("3");
                    break;
                case "SCR":
                    detailsResult.add("4");
                    break;
                case "TVR":
                    detailsResult.add("5");
                    break;
                case "XFR":
                    detailsResult.add("6");
                    isVostFr = true;
                    break;
                case "XES":
                    detailsResult.add("7");
                    break;
                case "XPT":
                    detailsResult.add("8");
                    break;
                case "XEN":
                    detailsResult.add("9");
                    break;
                case "XIT":
                    detailsResult.add("10");
                    break;
                case "X2P":
                    detailsResult.add("11");
                    break;
                case "X3P":
                    detailsResult.add("12");
                    break;
                case "X4P":
                    detailsResult.add("13");
                    break;
                case "LOG":
                    detailsResult.add("14");
                    break;
                case "VEN":
                    detailsResult.add("15");
                    break;
                default:
                    detailsResult.add("1"); // INC
                    break;
            }
        }
        String details = String.join(";", detailsResult);

        // COURT-METRAGES
        Set<Shortfilm> shortfilms = new HashSet<>();
        if (Objects.nonNull(object.getCm1()) && !object.getCm1().isEmpty()){
            Shortfilm shortfilm = new Shortfilm(object.getCm1());
            shortfilms.add(shortfilm);
        }
        if (Objects.nonNull(object.getCm2()) && !object.getCm2().isEmpty()){
            Shortfilm shortfilm = new Shortfilm(object.getCm2());
            shortfilms.add(shortfilm);
        }
        if (Objects.nonNull(object.getCm3()) && !object.getCm3().isEmpty()){
            Shortfilm shortfilm = new Shortfilm(object.getCm3());
            shortfilms.add(shortfilm);
        }
        if (Objects.nonNull(object.getCm4()) && !object.getCm4().isEmpty()){
            Shortfilm shortfilm = new Shortfilm(object.getCm4());
            shortfilms.add(shortfilm);
        }
        if (Objects.nonNull(object.getCm5()) && !object.getCm5().isEmpty()){
            Shortfilm shortfilm = new Shortfilm(object.getCm5());
            shortfilms.add(shortfilm);
        }

        // LANGUES
        String objectSubLangs = object.getSub().toUpperCase();
        String subLangs = objectSubLangs.replace("-",";");
        if (subLangs.contains("FR")){
            isVostFr = true;
        }

        // PAYS
        String objectPays = object.getPays().toUpperCase();
        String pays = objectPays.replace("-",";");

        // THEMES
        String objectThemes = object.getThemes();
        String[] themesarray = objectThemes.split("-");
        Set<Theme> themes = new HashSet<>();
        for (String themeString : themesarray){
            if (!themeString.isEmpty()){
                Optional<Theme> optionalTheme = themeRepository.findThemeByName(themeString);
                if (optionalTheme.isPresent()){
                    themes.add(optionalTheme.get());
                }else{
                    Theme newTheme = themeRepository.save(new Theme(themeString));
                    themes.add(newTheme);
                }
            }
            // si subLangs.contains FR || details.contains XFR => theme vostfr
            if (Arrays.binarySearch(themesarray, "vostfr")<0 && isVostFr){
                Optional<Theme> optionalTheme = themeRepository.findThemeByName("vostfr");
                if (!optionalTheme.isPresent()){
                    Theme newTheme = themeRepository.save(new Theme("vostfr"));
                    themes.add(newTheme);
                }else{
                    themes.add(optionalTheme.get());
                }
            }
        }

        // REALISATEURS
        String objectRealisateurs = object.getRealisateurs();
        String[] realisateursarray = objectRealisateurs.split(", ");
        Set<Director> directors = new HashSet<>();
        for (String directorString : realisateursarray){
            Optional<Director> optionalDirector = directorRepository.findByName(directorString);
            if (optionalDirector.isPresent()){
                directors.add(optionalDirector.get());
            }else{
                Director newDirector = directorRepository.save(new Director(directorString));
                directors.add(newDirector);
            }
        }
        Dvd dvd = new Dvd (directors,themes,shortfilms,null,subLangs,pays,titre,titreVf,null,details,annee,duree,format,type,norme,support,source);
        Dvd savedDvd = dvdrepository.save(dvd);
        for (Shortfilm cm: shortfilms){
            cm.setDvd(savedDvd);
            shortfilmRepository.save(cm);
        }
        //System.out.println("ligne "+object.getLigneFichier()+" traitée");
    }

    /**
     * Initie la base de données en insérant les rôles utilisés par l'application (Admin, User, Moderator)
     * @see com.keziko.dvdtek.controllers.IoController#importxls(MultipartFile file)
     */
    @Transactional
    public void initRolesAndUser(){
        Optional<Role> optAdmin = roleRepository.findByName(ERole.ROLE_ADMIN);
        if (!optAdmin.isPresent()){
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }
        Optional<Role> optUser = roleRepository.findByName(ERole.ROLE_USER);
        if (!optUser.isPresent()){
            roleRepository.save(new Role(ERole.ROLE_USER));
        }
        Optional<Role> optMod = roleRepository.findByName(ERole.ROLE_MODERATOR);
        if (!optMod.isPresent()){
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));
        }
    }

    /**
     * Créé un fichier excel des films de la base de données
     * Reproduit la mise en forme d'un fichier importé
     * @return fichier excel de type {@link File}
     * @throws IOException si la méthode {@link ObjectMapper#readValue(File, Class) échoue}
     * @throws WriteException si la sous-méthode {@link IoService#createXlsLine(int, ListeObject, WritableSheet, Dvd)}
     */
    public File createExcel() throws IOException, WriteException {
        File currDir = new File("");
        String fileLocation = currDir.getAbsolutePath() + "temp.xls";
        File newFile = new File(fileLocation);
        WritableWorkbook workbook = Workbook.createWorkbook(newFile);
        WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
        defineHeaders(sheet);
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        ListeObject listesObject = mapper.readValue(new File("src/main/resources/static/enums.json"), ListeObject.class);
        List<Dvd> dvds = dvdrepository.findAll();
        int index = 1;
        for (Dvd dvd : dvds){
            createXlsLine(index,listesObject, sheet, dvd);
            index++;
        }
        for (int i = 0; i<20; i++){
            setColumnView(i,sheet);
        }
        workbook.write();
        workbook.close();
        return newFile;
    }

    /**
     * Lors de l'import du fichier excel (XLS), récupère le nombre de lignes importées
     * @return l'objet {@link ResponseMessage} instancié dans le service
     * @see com.keziko.dvdtek.controllers.IoController#getLineImportedCount()
     */
    public ResponseMessage getLineCounter(){
        return this.intervalMessage;
    }

    private void defineHeaders(WritableSheet sheet) throws WriteException {
        WritableCellFormat headerFormat = new WritableCellFormat();
        WritableFont font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
        headerFormat.setFont(font);
        headerFormat.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
        headerFormat.setBackground(Colour.VERY_LIGHT_YELLOW);
        headerFormat.setWrap(true);

        Label headerLabel = new Label(0, 0, "FORMAT", headerFormat);
        sheet.setColumnView(0, 30);
        sheet.addCell(headerLabel);

        headerLabel = new Label(1, 0, "TYPE", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(2, 0, "TITRE", headerFormat);
        sheet.setColumnView(0, 80);
        sheet.addCell(headerLabel);

        headerLabel = new Label(3, 0, "ANNEE", headerFormat);
        sheet.setColumnView(0, 60);
        sheet.addCell(headerLabel);

        headerLabel = new Label(4, 0, "REALISATEURS", headerFormat);
        sheet.setColumnView(0, 80);
        sheet.addCell(headerLabel);

        headerLabel = new Label(5, 0, "ID_REA", headerFormat);
        sheet.setColumnView(0, 20);
        sheet.addCell(headerLabel);

        headerLabel = new Label(6, 0, "PAYS", headerFormat);
        sheet.setColumnView(0, 60);
        sheet.addCell(headerLabel);

        headerLabel = new Label(7, 0, "DUREE", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(8, 0, "SUB", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(9, 0, "SUPPORT", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(10, 0, "NORME", headerFormat);
        sheet.setColumnView(0, 60);
        sheet.addCell(headerLabel);

        headerLabel = new Label(11, 0, "DETAILS", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(12, 0, "SOURCE", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(13, 0, "TITRE_VF", headerFormat);
        sheet.setColumnView(0, 80);
        sheet.addCell(headerLabel);

        headerLabel = new Label(14, 0, "LABEL", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(15, 0, "CM1", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(16, 0, "CM2", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(17, 0, "CM3", headerFormat);
        sheet.setColumnView(0, 60);
        sheet.addCell(headerLabel);

        headerLabel = new Label(18, 0, "CM4", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);

        headerLabel = new Label(19, 0, "CM5", headerFormat);
        sheet.setColumnView(0, 40);
        sheet.addCell(headerLabel);
    }

    private void createXlsLine(int lineIndex, ListeObject listeObject, WritableSheet sheet, Dvd dvd) throws WriteException {
        WritableCellFormat cellFormat = new WritableCellFormat();
        if (lineIndex % 2 ==0){
            cellFormat.setBackground(Colour.LIGHT_ORANGE);
        }else{
            cellFormat.setBackground(Colour.WHITE);
        }

        String format = listeObject.getLabelById(dvd.getFormat(), listeObject.getTypes());
        Label cellLabel = new Label(0, lineIndex, format, cellFormat);
        sheet.addCell(cellLabel);

        String type;
        switch (dvd.getType()){
            case 1:
                type = "DOC";
                break;
            case 2:
                type = "ANI";
                break;
            case 3:
                type = "FIC";
                break;
            case 4:
                type = "EXP";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dvd.getType());
        }
        cellLabel = new Label(1, lineIndex, type, cellFormat);
        sheet.addCell(cellLabel);

        cellLabel = new Label(2, lineIndex, dvd.getTitre(), cellFormat);
        sheet.addCell(cellLabel);

        Number cellNumber = new Number(3, lineIndex, dvd.getAnnee(), cellFormat);
        sheet.addCell(cellNumber);

        String directors = dvd.getDirectors().stream().map(Director::getName).collect(Collectors.joining(", "));
        cellLabel = new Label(4, lineIndex, directors, cellFormat);
        sheet.addCell(cellLabel);

        cellLabel = new Label(5, lineIndex, null, cellFormat);
        sheet.addCell(cellLabel);

        String pays = dvd.getPays().replace(";","-");
        cellLabel = new Label(6, lineIndex, pays, cellFormat);
        sheet.addCell(cellLabel);

        cellNumber = new Number(7, lineIndex, dvd.getDuree(), cellFormat);
        sheet.addCell(cellNumber);

        String sub = dvd.getSublangs().replace(";","-");
        cellLabel = new Label(8, lineIndex, sub, cellFormat);
        sheet.addCell(cellLabel);

        String support = listeObject.getLabelById(dvd.getSupport(), listeObject.getSupports());
        cellLabel = new Label(9, lineIndex, support, cellFormat);
        sheet.addCell(cellLabel);

        String norme = listeObject.getLabelById(dvd.getNorme(), listeObject.getNormes());
        cellLabel = new Label(10, lineIndex, norme, cellFormat);
        sheet.addCell(cellLabel);

        List<String> details = new ArrayList<>();
        String[] dvdDetails = dvd.getDetails().split(";");
        for (String detailId : dvdDetails){
            String detail = listeObject.getLabelById(Integer.parseInt(detailId), listeObject.getDetails());
            details.add(detail);
        }
        String detail = String.join("-",details);
        cellLabel = new Label(11, lineIndex, detail, cellFormat);
        sheet.addCell(cellLabel);

        String source = listeObject.getLabelById(dvd.getSource(), listeObject.getSources());
        cellLabel = new Label(12, lineIndex, source, cellFormat);
        sheet.addCell(cellLabel);

        cellLabel = new Label(13, lineIndex, dvd.getTitreVf(), cellFormat);
        sheet.addCell(cellLabel);

        String themes = dvd.getThemes().stream().map(Theme::getName).collect(Collectors.joining(", "));
        cellLabel = new Label(14, lineIndex, themes, cellFormat);
        sheet.addCell(cellLabel);

        int colIndex = 15;
        for (Shortfilm sf : dvd.getShortfilms()){
            String cm = sf.getBody();
            cellLabel = new Label(colIndex, lineIndex, cm, cellFormat);
            sheet.addCell(cellLabel);
            colIndex++;
        }
    }

    private void setColumnView(int index, WritableSheet sheet){
        CellView cv = sheet.getColumnView(index);
        cv.setAutosize(true);
        sheet.setColumnView(index,cv);
    }

    private void resetLineCounter(){
        this.intervalMessage.setCount(0);
    }
}
