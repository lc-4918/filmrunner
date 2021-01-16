package com.keziko.dvdtek.services;

import com.keziko.dvdtek.dtos.*;
import com.keziko.dvdtek.entities.*;
import com.keziko.dvdtek.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Nom de classe : DvdService
 * Description   :
 * Version       : 1.0
 * Date          : 16/03/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Service
public class DvdService {
    private static final String UPLOAD_DIR = "\\img\\";
    private final Dvdrepository dvdrepository;
    private final DirectorRepository directorRepository;
    private final ThemeRepository themeRepository;
    private final ShortfilmRepository shortfilmRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private ResponseMessage intervalMessage;

    @Autowired
    public DvdService(Dvdrepository dvdrepository, DirectorRepository directorRepository, ThemeRepository themeRepository, ShortfilmRepository shortfilmRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.dvdrepository = dvdrepository;
        this.directorRepository = directorRepository;
        this.themeRepository = themeRepository;
        this.shortfilmRepository = shortfilmRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     *
     * @return
     */
    public List<Dvd> findAllDvd() {
        return (List<Dvd>) dvdrepository.findAll();
    }

    /**
     *
     * @param id
     * @return
     */
    public Optional<Dvd> findDvdById(Long id) {
        return dvdrepository.findById(id);
    }

    /**
     *
     * @param id
     * @return
     * @throws NoResultException
     */
    public List<DvdListItem> findDtosByDirectorId(Long id) throws NoResultException {
        List<Dvd> result = dvdrepository.findAllByDirectorId(id);
        if (Objects.isNull(result) || result.isEmpty()) {
            throw new NoResultException();
        } else {
            return dvdrepository.findAllByDirectorId(id).stream().map(this::convertDvdToListDto).collect(Collectors.toList());
        }
    }

    /**
     *
     * @param id
     * @return
     * @throws NoResultException
     */
    public List<DvdListItem> findDtosByThemeId(Long id) throws NoResultException {
        List<Dvd> result = dvdrepository.findAllByThemeId(id);
        if (Objects.isNull(result) || result.isEmpty()) {
            throw new NoResultException();
        } else {
            return dvdrepository.findAllByThemeId(id).stream().map(this::convertDvdToListDto).collect(Collectors.toList());
        }
    }

    /**
     *
     * @return
     */
    public List<DvdListItem> findAllDto() {
        return findAllDvd().stream().map(this::convertDvdToListDto).collect(Collectors.toList());
    }

    /**
     *
     * @param dto
     * @return
     */
    @Transactional
    public Dvd addDvd(DvdForm dto) {
        return createDvd(dto);
    }

    /**
     *
     * @param dvdForms
     * @return
     */
    @Transactional
    public List<Dvd> addDvds(List<DvdForm> dvdForms) {
        List<Dvd> dvds = createDvds(dvdForms);
        return (List<Dvd>) dvdrepository.saveAll(dvds);
    }

    /**
     *
     * @param dvdForm
     * @param id
     * @return
     * @throws NoResultException
     */
    @Transactional
    public Dvd updateDvd(DvdForm dvdForm,Long id) throws NoResultException {
        Optional<Dvd> _optionalDvd = dvdrepository.findById(id);
        if (_optionalDvd.isPresent()) {
            return createDvd(dvdForm);
        } else {
            throw new NoResultException();
        }
    }

    /**
     *
     * @param id
     * @throws NoResultException
     */
    @Transactional
    public void deleteDvdById(Long id) throws NoResultException {
        if (dvdrepository.findById(id).isPresent()) {
            dvdrepository.deleteById(id);
        } else {
            throw new NoResultException();
        }
    }

    /**
     *
     * @param dto
     * @return
     * @throws IllegalArgumentException
     */
    @Transactional
    public Dvd createDvd(DvdForm dto) throws IllegalArgumentException {

        String userDirectory = new File("").getAbsolutePath();
        String fileDownloadFolder = userDirectory+UPLOAD_DIR;
        String imageUrl = Objects.nonNull(dto.getImageUrl()) ? dto.getImageUrl() : null;

        String pays = String.join(";",dto.getPays());
        String subLangs = String.join(";",dto.getSubLangs());

        List<String> IRealisateurs = dto.getRealisateurs();
        List<Director> realisateurs = new ArrayList<>();
        if (Objects.nonNull(IRealisateurs) && !IRealisateurs.isEmpty())
            IRealisateurs.forEach(name -> {
                Optional<Director> optionalDirector = directorRepository.findByName(name);
                Director director = optionalDirector.orElseGet(() -> directorRepository.save(new Director(name)));
                realisateurs.add(director);
            });

        List<String> IThemes = dto.getThemes();
        List<Theme> themes = new ArrayList<>();
        if (Objects.nonNull(IThemes) && !IThemes.isEmpty())
            IThemes.forEach(name -> {
                Optional<Theme> optionalTheme = themeRepository.findThemeByName(name);
                Theme theme = optionalTheme.orElseGet(() -> themeRepository.save(new Theme(name)));
                themes.add(theme);
            });

        List<String> IShortfilms = dto.getShortfilms();
        List<Shortfilm> shortfilms = new ArrayList<>();
        if (Objects.nonNull(IShortfilms) && !IShortfilms.isEmpty())
            IShortfilms.forEach(name -> {
                Shortfilm shortfilm = null;
                if (Objects.nonNull(dto.getId())){
                    List<Shortfilm> dvdShortfilms = shortfilmRepository.findByDvdId(dto.getId());
                    for (Shortfilm dvdShortFilm : dvdShortfilms){
                        if (name.equals(dvdShortFilm.getBody())){
                            shortfilm = dvdShortFilm;
                            break;
                        }
                    }
                    if (Objects.isNull(shortfilm)){
                        shortfilm = new Shortfilm(name);
                    }
                }else{
                    shortfilm = new Shortfilm(name);
                }
                shortfilms.add(shortfilm);
            });
        String description = Objects.nonNull(dto.getDescription()) ? dto.getDescription() : null;
        String titreVf = Objects.nonNull(dto.getTitrevf()) ? dto.getTitrevf() : null;
        String titre = Objects.nonNull(dto.getTitre()) ? dto.getTitre() : null;
        String details = Objects.nonNull(dto.getDetails()) ? dto.getDetails() : null;
        Integer annee = Objects.nonNull(dto.getAnnee()) ? dto.getAnnee() : null;
        Integer duree = Objects.nonNull(dto.getDuree()) ? dto.getDuree() : null;
        Integer format = Objects.nonNull(dto.getFormat()) ? dto.getFormat() : null;
        Integer type = Objects.nonNull(dto.getType()) ? dto.getType() : null;
        Integer norme = Objects.nonNull(dto.getNorme()) ? dto.getNorme() : null;
        Integer support = Objects.nonNull(dto.getSupport()) ? dto.getSupport() : null;
        Integer source = Objects.nonNull(dto.getSource()) ? dto.getSource() : null;
        Dvd dvd = new Dvd(realisateurs,themes,shortfilms,imageUrl,subLangs,pays,titre,titreVf,description,details,annee,duree,format,type,norme,support,source);
        if (Objects.nonNull(dto.getId())) {
            Long id = dto.getId();
            dvd.setId(id);
        }
        Dvd persistedDvd = dvdrepository.save(dvd);
        // must set the shortfilm's dvd before saving the shortfilm
        if (!dvd.getShortfilms().isEmpty()){
            for (Shortfilm shortfilm: dvd.getShortfilms()){
                shortfilm.setDvd(persistedDvd);
                shortfilmRepository.save(shortfilm);
            }
        }
        return persistedDvd;
    }

    /**
     *
     * @param dvd
     * @return
     */
    public DvdListItem convertDvdToListDto(Dvd dvd) {
        List<Director> _directors = dvd.getDirectors();
        List<String> directors;
        directors = (Objects.nonNull(_directors) && !_directors.isEmpty()) ? _directors.stream().map(Director::getName).collect(Collectors.toCollection(ArrayList::new)) : null;
        List<Theme> _themes = dvd.getThemes();
        List<String> themes;
        themes = (Objects.nonNull(_themes) && !_themes.isEmpty()) ? _themes.stream().map(Theme::getName).collect(Collectors.toCollection(ArrayList::new)) : null;
        return new DvdListItem(dvd.getId(), dvd.getTitre(), dvd.getTitreVf(), directors, dvd.getAnnee(), dvd.getPays(),dvd.getSublangs(), themes);
    }

    /**
     *
     * @param dvds
     * @return
     */
    public List<DvdListItem> convertDvdToListDtos(List<Dvd> dvds){
        List<DvdListItem> items = new ArrayList<>();
        if (!dvds.isEmpty()){
            for (Dvd dvd : dvds){
                DvdListItem item = convertDvdToListDto(dvd);
                items.add(item);
            }
        }
        return items;
    }

    /**
     *
     * @param dvd
     * @return
     */
    public DvdForm convertDvdToFormDto(Dvd dvd){
        DvdForm dvdForm = new DvdForm();
        dvdForm.setId(dvd.getId());
        dvdForm.setTitre(dvd.getTitre());
        dvdForm.setAnnee(dvd.getAnnee());
        dvdForm.setDuree(dvd.getDuree());
        dvdForm.setDescription(dvd.getDescription());
        dvdForm.setImageUrl(dvd.getImageUrl());
        dvdForm.setSubLangs(deleteListIfEmpty(Arrays.asList(dvd.getSublangs().split(";"))));
        dvdForm.setPays(deleteListIfEmpty(Arrays.asList(dvd.getPays().split(";"))));
        dvdForm.setSupport(dvd.getSupport());
        dvdForm.setType(dvd.getType());
        dvdForm.setNorme(dvd.getNorme());
        dvdForm.setDetails(dvd.getDetails());
        dvdForm.setFormat(dvd.getFormat());
        dvdForm.setSource(dvd.getSource());
        List<String> directors = dvd.getDirectors()
                .stream()
                .map(Director::getName).collect(Collectors.toList());
        dvdForm.setRealisateurs(directors);
        List<String> shortFilms = dvd.getShortfilms()
                .stream()
                .map(Shortfilm::getBody).collect(Collectors.toList());
        dvdForm.setShortfilms(shortFilms);
        List<String> themes = dvd.getThemes()
                .stream()
                .map(Theme::getName).collect(Collectors.toList());
        dvdForm.setThemes(themes);
        return dvdForm;
    }

    /**
     *
     * @param id
     * @return
     */
    @Transactional
    public Boolean deleteImageByDvdId(Long id){
        boolean result = false;
        Optional<Dvd> optdvd = dvdrepository.findById(id);
        if (optdvd.isPresent()){
            Dvd dvd = optdvd.get();
            String imageUrl = dvd.getImageUrl();
            List<Dvd> dvdsWithImage = dvdrepository.findAllByImageUrl(imageUrl);
            if (Objects.nonNull(dvdsWithImage) && !dvdsWithImage.isEmpty()){
                result = true;
            }else{
                try {
                    FileUploadUtil.deleteFile(imageUrl);
                    dvd.setImageUrl(null);
                    dvdrepository.save(dvd);
                    result = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }

    /**
     *
     * @param data
     */

    public void importXLS(List<XlsObject> data){
        int index = 0;
        for (XlsObject object : data){
            importXLSLine(object);
            this.intervalMessage = new ResponseMessage(object.getTitre(),index);
            index++;
        }
    }

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
        List<Shortfilm> shortfilms = new ArrayList<>();
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
        List<Theme> themes = new ArrayList<>();
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
        List<Director> directors = new ArrayList<>();
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
        System.out.println("ligne "+object.getLigneFichier()+" traitée");
    }

    /**
     *
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
        Optional<Role> optionalAdminRole = roleRepository.findByName(ERole.ROLE_ADMIN);
        Optional<User> optionalUser = userRepository.findByUsername("admin");
        if (optionalAdminRole.isPresent() && !optionalUser.isPresent()){
            Role adminRole = optionalAdminRole.get();
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            userRepository.save(new User("admin","admin@admin.com","password", roles));
        }
    }

    public ResponseMessage getLineCounter(){
        return this.intervalMessage;
    }
    public void resetLineCounter(){
        this.intervalMessage.setCount(0);
    }

    private List<Dvd> createDvds(List<DvdForm> dtos) {
        List<Dvd> dvds = new ArrayList<>();
        for (DvdForm dvdForm : dtos) {
            Dvd dvd = createDvd(dvdForm);
            dvds.add(dvd);
        }
        return dvds;
    }
    private List<String> deleteListIfEmpty(List<String> array){
        if (array.size()==0){
            return null;
        }else{
            return array;
        }
    }


}
