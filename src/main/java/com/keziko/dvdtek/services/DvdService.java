package com.keziko.dvdtek.services;

import com.keziko.dvdtek.controllers.DvdController;
import com.keziko.dvdtek.controllers.ResetController;
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

/**
 * Nom de classe : DvdService
 * @version 16/03/2020
 * @author Luc CLÉMENT - lucclement38@gmail.com
 */
@Service
public class DvdService {
    private static final String UPLOAD_DIR = "\\img\\";
    private final Dvdrepository dvdrepository;
    private final DirectorRepository directorRepository;
    private final ThemeRepository themeRepository;
    private final ShortfilmRepository shortfilmRepository;


    @Autowired
    public DvdService(Dvdrepository dvdrepository, DirectorRepository directorRepository, ThemeRepository themeRepository, ShortfilmRepository shortfilmRepository) {
        this.dvdrepository = dvdrepository;
        this.directorRepository = directorRepository;
        this.themeRepository = themeRepository;
        this.shortfilmRepository = shortfilmRepository;
    }

    /**
     * Récupère toutes les entités persistentes film de la base de données
     * @return liste d'entités persitentes film de type {@link Dvd}
     * @see DvdService#findAllDto()
     */
    public List<Dvd> findAllDvd() {
        return (List<Dvd>) dvdrepository.findAll();
    }

    /**
     * Récupère une entité persistente film dont l'attribut est égal au paramètre
     * @param id attribut identifiant unique du film que l'on veut récupérer
     * @return une entité persistente film de type {@link Dvd}
     * @see DvdController#getDvd(Long)
     */
    public Optional<Dvd> findDvdById(Long id) {
        return dvdrepository.findById(id);
    }

    /**
     * Récupère la liste des films associés à un réalisateur précis
     * @param id attribut identifiant unique d'un réalisateur dont on veut récupérer tous les films
     * @return une liste de films de type {@link DvdListItem}
     * @throws NoResultException
     * @see com.keziko.dvdtek.controllers.DirectorController#getDtosByDirectorId(Long)
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
     * Récupère la liste des films associés à un thème précis
     * @param id attribut identifiant unique d'un thème dont on veut récupérer tous les films
     * @return une liste de films de type {@link DvdListItem}
     * @throws NoResultException
     * @see com.keziko.dvdtek.controllers.ThemeController#getDtosByThemeId(Long)
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
     * Construit une liste de DTO servant à alimenter la liste des films
     * @return liste de DTO de type {@link DvdListItem}
     * @see DvdController#getDtos()
     */
    public List<DvdListItem> findAllDto() {
        return findAllDvd().stream().map(this::convertDvdToListDto).collect(Collectors.toList());
    }

    /**
     * Ajout d'un nouveau film
     * Persistence d'une nouvelle entité de type {@link Dvd}
     * @param dto DTO renvoyé par le front de type {@link DvdForm} contenant les propriétés nécessaires pour la création d'un nouveau film
     * @return une entité persistente film de type {@link Dvd}
     */
    @Transactional
    public Dvd addDvd(DvdForm dto) {
        return createDvd(dto);
    }

    /**
     * Ajout de plusieurs films
     * @param dvdForms  liste d'objets de type {@link DvdForm}
     * @return liste d'entités persistentes film de type {@link Dvd}
     * @see DvdController#addDvds(List)
     */
    @Transactional
    public List<Dvd> addDvds(List<DvdForm> dvdForms) {
        List<Dvd> dvds = createDvds(dvdForms);
        return (List<Dvd>) dvdrepository.saveAll(dvds);
    }

    /**
     * Mise à jour d'un film
     * Persistence d'une entité de type {@link Dvd}
     * @param dvdForm  objet de type {@link DvdForm} renvoyé par le front depuis le formulaire d'édition d'un film ("/edit")
     * @param id attribut identifiant du film de type {@link Long} contenu dans l'URL
     * @return une entité persistente film de type {@link Dvd}
     * @throws NoResultException
     * @see DvdController#updateDvd(DvdForm, Long)
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
     * Supprime un film de la base de données
     * @param id attribut identifiant du film de type {@link Long} contenu dans l'URL
     * @throws NoResultException
     * @see DvdController#deleteDvdById(Long)
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
     * Ajout d'un nouveau film dans la base de données
     * @param dto objet de type {@link DvdForm} renvoyé par le front depuis le formulaire d'ajout d'un film ("/add")
     * @return entité persistente Dvd inséré en base de données
     * @throws IllegalArgumentException
     * @see DvdService#addDvd(DvdForm)
     * @see DvdService#createDvds(List)
     * @see DvdService#updateDvd(DvdForm, Long)
     */
    @Transactional
    public Dvd createDvd(DvdForm dto) throws IllegalArgumentException {

        String userDirectory = new File("").getAbsolutePath();
        String fileDownloadFolder = userDirectory+UPLOAD_DIR;
        String imageUrl = Objects.nonNull(dto.getImageUrl()) && !dto.getImageUrl().isEmpty() ? dto.getImageUrl() : null;

        String pays = Objects.nonNull(dto.getPays()) && !dto.getPays().isEmpty()? String.join(";",dto.getPays()):null;
        String subLangs = Objects.nonNull(dto.getSubLangs()) && !dto.getSubLangs().isEmpty()? String.join(";",dto.getSubLangs()):null;

        List<String> IRealisateurs = dto.getRealisateurs();
        Set<Director> realisateurs = new HashSet<>();
        if (Objects.nonNull(IRealisateurs) && !IRealisateurs.isEmpty())
            IRealisateurs.forEach(name -> {
                Optional<Director> optionalDirector = directorRepository.findByName(name);
                Director director = optionalDirector.orElseGet(() -> directorRepository.save(new Director(name)));
                realisateurs.add(director);
            });

        List<ThemeDTO> IThemes = dto.getThemes();
        Set<Theme> themes = new HashSet<>();
        if (Objects.nonNull(IThemes) && !IThemes.isEmpty())
            IThemes.forEach((themeDTO) -> {
                Optional<Theme> optionalTheme = themeRepository.findThemeByName(themeDTO.getName());
                Theme theme = optionalTheme.orElseGet(() -> themeRepository.save(new Theme(themeDTO.getName(),themeDTO.getColor())));
                themes.add(theme);
            });

        List<String> IShortfilms = dto.getShortfilms();
        Set<Shortfilm> shortfilms = new HashSet<>();
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
     * Transforme une entité de type {@link Dvd} en un DTO de type {@link DvdListItem} utilisé dans la liste de films
     * @param dvd entité persistente de type {@link Dvd}
     * @return une liste de DTO de type {@link DvdListItem} alimentant la liste de films
     * @see DvdController#addDvd(DvdForm)
     * @see DvdController#updateDvd(DvdForm, Long)
     * @see DvdService#convertDvdToListDtos(List)
     * @see DvdService#findAllDto()
     * @see DvdService#findDtosByDirectorId(Long)
     * @see DvdService#findDtosByThemeId(Long)
     */
    public DvdListItem convertDvdToListDto(Dvd dvd) {
        Set<Director> _directors = dvd.getDirectors();
        List<String> directors;
        directors = (Objects.nonNull(_directors) && !_directors.isEmpty()) ? _directors.stream().map(Director::getName).collect(Collectors.toCollection(ArrayList::new)) : null;
        Set<Theme> _themes = dvd.getThemes();
        List<String> themes;
        themes = (Objects.nonNull(_themes) && !_themes.isEmpty()) ? _themes.stream().map(Theme::getName).collect(Collectors.toCollection(ArrayList::new)) : null;
        return new DvdListItem(dvd.getId(), dvd.getTitre(), dvd.getTitreVf(), directors, dvd.getAnnee(), dvd.getPays(),dvd.getSublangs(), themes);
    }

    /**
     * Transforme une liste d'entité film de type {@link Dvd} en une liste de DTO de type {@link DvdListItem} utilisé dans la liste de films
     * @param dvds liste d'entités persistentes de type {@link Dvd}
     * @return une liste de DTO de type {@link DvdListItem} alimentant la liste de films
     * @see DvdController#addDvds(List)
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
     * Transforme une entité film de type {@link Dvd} en DTO de type {@link DvdForm} utilisé dans le formulaire d'édition d'un film
     * @param dvd entité persistante film de type {@link Dvd}
     * @return un DTO de type {@link DvdForm}
     * @see DvdController#getDvd(Long)
     * @see DvdService#deleteDvdById(Long) (sous-méthode)
     */
    public DvdForm convertDvdToFormDto(Dvd dvd){
        DvdForm dvdForm = new DvdForm();
        dvdForm.setId(dvd.getId());
        if (Objects.nonNull(dvd.getTitre())){
            dvdForm.setTitre(dvd.getTitre());
        }

        if (Objects.nonNull(dvd.getTitreVf())){
            dvdForm.setTitrevf(dvd.getTitreVf());
        }

        if (Objects.nonNull(dvd.getAnnee())){
            dvdForm.setAnnee(dvd.getAnnee());
        }

        if (Objects.nonNull(dvd.getDuree())){
            dvdForm.setDuree(dvd.getDuree());
        }

        if (Objects.nonNull(dvd.getDescription())){
            dvdForm.setDescription(dvd.getDescription());
        }

        if (Objects.nonNull(dvd.getImageUrl())){
            dvdForm.setImageUrl(dvd.getImageUrl());
        }

        if (Objects.nonNull(dvd.getSublangs())){
            dvdForm.setSubLangs(deleteListIfEmpty(Arrays.asList(dvd.getSublangs().split(";"))));
        }

        if (Objects.nonNull(dvd.getPays())){
            dvdForm.setPays(deleteListIfEmpty(Arrays.asList(dvd.getPays().split(";"))));
        }

        if (Objects.nonNull(dvd.getSupport())){
            dvdForm.setSupport(dvd.getSupport());
        }

        if (Objects.nonNull(dvd.getType())){
            dvdForm.setType(dvd.getType());
        }
        if (Objects.nonNull(dvd.getNorme())){
            dvdForm.setNorme(dvd.getNorme());
        }

        if (Objects.nonNull(dvd.getDetails())){
            dvdForm.setDetails(dvd.getDetails());
        }

        if (Objects.nonNull(dvd.getFormat())){
            dvdForm.setFormat(dvd.getFormat());
        }

        if (Objects.nonNull(dvd.getSource())){
            dvdForm.setSource(dvd.getSource());
        }

        if (Objects.nonNull(dvd.getDirectors())){
            List<String> directors = dvd.getDirectors()
                    .stream()
                    .map(Director::getName).collect(Collectors.toList());
            dvdForm.setRealisateurs(directors);
        }

        if (Objects.nonNull(dvd.getShortfilms())){
            List<String> shortFilms = dvd.getShortfilms()
                    .stream()
                    .map(Shortfilm::getBody).collect(Collectors.toList());
            dvdForm.setShortfilms(shortFilms);
        }

        if (Objects.nonNull(dvd.getThemes()) && !dvd.getThemes().isEmpty()){
            List<ThemeDTO> themesDTO = new ArrayList<>();
            for (Theme theme : dvd.getThemes()){
                ThemeDTO themeDTO = new ThemeDTO(theme.getName(),theme.getColor());
                themesDTO.add(themeDTO);
            }
            dvdForm.setThemes(themesDTO);
        }

        return dvdForm;
    }

    /**
     * Supprime l'attribut image du film
     * Efface l'attribut du film imageUrl sans supprimer l'image dans le dossier de stockage des images ("img")
     * @param id id id attribut identifiant du film de type {@link Long} contenu dans l'URL
     * @return true si la suppression est réussie
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
     * Supprime les films, réalisateurs, thèmes et court-métrages de la base de données
     * Effectue la ré-initialisation des données
     * @see ResetController#cleandb()
     */
    @Transactional
    public void deleteAllDvdsDirectorsThemesAndShortFilm(){
        shortfilmRepository.deleteAll();
        dvdrepository.deleteAll();
        themeRepository.deleteAll();
        directorRepository.deleteAll();

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
