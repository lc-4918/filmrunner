package com.keziko.dvdtek.services;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Nom de classe : ImportExportService
 * @version 18/01/2021
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Slf4j
@Service
public class IoService {

    private final DirectorRepository directorRepository;
    private final Dvdrepository dvdrepository;
    private final RoleRepository roleRepository;
    private final ThemeRepository themeRepository;
    private final ShortfilmRepository shortfilmRepository;
    private ResponseMessage intervalMessage;
    private final ResourceLoader resourceLoader;
    @Value("${film.app.excelFolder:/data}")
    private String excelFolder;

    @Value("${film.app.excelFilename:enums.json}")
    private String excelFilename;

    @Autowired
    public IoService(DirectorRepository directorRepository, Dvdrepository dvdrepository, RoleRepository roleRepository, ThemeRepository themeRepository, ShortfilmRepository shortfilmRepository, ResourceLoader resourceLoader) {
        this.directorRepository = directorRepository;
        this.dvdrepository = dvdrepository;
        this.roleRepository = roleRepository;
        this.themeRepository = themeRepository;
        this.shortfilmRepository = shortfilmRepository;
        this.resourceLoader = resourceLoader;
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
     * @see IoService#importXLS(List)
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

        // NORME
        String objectNorme = object.getNorme();
        int norme;
        if ("NTSC".equalsIgnoreCase(objectNorme)) {
            norme = 1;
        } else {
            norme = 2;
        }

        // SUPPORT
        String objectSupport = object.getSupport();
        int support;
        switch(objectSupport.toUpperCase()){
            case "XRIP":
                support = 1;
                break;
            case "720P":
                support = 2;
                break;
            case "1080":
                support = 3;
                break;
            case "DVD9":
                support = 5;
                break;
            case "BD-R":
                support = 6;
                break;
            default:
                support = 4; //DVD5
                break;
        }

        // SOURCE
        String objectSource = object.getSource();
        int source;
        switch (objectSource.toUpperCase()){
            case "VOD":
                source = 2;
                break;
            case "WEB":
                source = 3;
                break;
            case "TV":
                source = 4;
                break;
            case "VHS":
                source = 5;
                break;
            default:
                source = 1; //DVD
                break;
        }

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
        String objectSubLangs = object.getSub().isEmpty()? null:object.getSub().toUpperCase();
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
        Random r = new Random();
        for (String themeString : themesarray){
            if (!themeString.isEmpty()){
                Optional<Theme> optionalTheme = themeRepository.findThemeByName(themeString);
                if (optionalTheme.isPresent()){
                    themes.add(optionalTheme.get());
                }else{

                    Theme newTheme = themeRepository.save(new Theme(themeString,IoService.generateColor(r)));
                    themes.add(newTheme);
                }
            }
            // si subLangs.contains FR || details.contains XFR => theme vostfr
            if (Arrays.binarySearch(themesarray, "vostfr")<0 && isVostFr){
                Optional<Theme> optionalTheme = themeRepository.findThemeByName("vostfr");
                if (!optionalTheme.isPresent()){
                    Theme newTheme = themeRepository.save(new Theme("vostfr",IoService.generateColor(r)));
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
    public File createExcel(ListeObject listes) throws IOException, WriteException {
        File newFile = new File("temp.xls");
        log.info("writing excel : {}",newFile.getAbsolutePath());
        WritableWorkbook workbook = Workbook.createWorkbook(newFile);
        workbook.setColourRGB(Colour.LIGHT_ORANGE,253, 233, 217);
        workbook.setColourRGB(Colour.LIGHT_BLUE,229, 224, 236);
        WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
        defineHeaders(sheet);
        List<Dvd> dvds = dvdrepository.findAll();
        int index = 1;
        for (Dvd dvd : dvds){
            createXlsLine(index,listes, sheet, dvd);
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
        String directorIds = dvd.getDirectors().stream().map(director -> director.getId().toString()).collect(Collectors.joining("-"));
        cellLabel = new Label(4, lineIndex, directors, cellFormat);
        sheet.addCell(cellLabel);

        cellLabel = new Label(5, lineIndex, directorIds, cellFormat);
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

        WritableCellFormat cmCellFormat = new WritableCellFormat();
        if (lineIndex % 2 ==0){
            cmCellFormat.setBackground(Colour.LIGHT_BLUE);
        }else{
            cmCellFormat.setBackground(Colour.WHITE);
        }
        int colIndex = 15;
        Shortfilm[] shortfilms = new Shortfilm[5];
        int j = 0;
        for (Shortfilm sf:dvd.getShortfilms()){
            shortfilms[j]=sf;
            j++;
        }
        for (int i = 0; i<5; i++){
            String cm = Objects.nonNull(shortfilms[i]) && shortfilms[i].getBody().length()>0 ? shortfilms[i].getBody():null;
            cellLabel = new Label(i+colIndex, lineIndex, cm, cmCellFormat);
            sheet.addCell(cellLabel);
        }
    }

    private void setColumnView(int index, WritableSheet sheet){
        CellView cv = sheet.getColumnView(index);
        cv.setAutosize(true);
        sheet.setColumnView(index,cv);
    }

    private File getEmptyExcelFileOrCreateIfNotExists() throws IOException {
        File directory = new File(excelFolder);
        if (!directory.exists()){
            boolean dirCreated = directory.mkdirs();
            System.out.println("dossier "+ directory.getAbsolutePath() +" créé");
            if (!dirCreated){
                throw new IOException("impossible de créer le dossier "+excelFolder);
            }
        }
        Path filePath = Paths.get(excelFolder, excelFilename);
        File file = filePath.toFile();
        // print path
        System.out.println("chemin du fichier importé : "+file.getAbsolutePath());
        if (!file.exists()){
            Resource resource = resourceLoader.getResource("classpath:data/enums.json"); // data/enums.json
            InputStream is = resource.getInputStream();
            Files.copy(is, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            is.close();
        }
        return file;
    }
    private static String generateColor(Random r) {
        final char [] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        char [] s = new char[7];
        int     n = r.nextInt(0x1000000);

        s[0] = '#';
        for (int i=1;i<7;i++) {
            s[i] = hex[n & 0xf];
            n >>= 4;
        }
        return new String(s);
    }

    private void resetLineCounter(){
        this.intervalMessage.setCount(0);
    }
}
