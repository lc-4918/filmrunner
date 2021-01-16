package com.keziko.dvdtek.services;

import com.keziko.dvdtek.entities.Director;
import com.keziko.dvdtek.entities.Dvd;
import com.keziko.dvdtek.repositories.DirectorRepository;
import com.keziko.dvdtek.repositories.Dvdrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.*;

/*
 * Nom de classe : DirectorService
 * Description   :
 * Version       : 1.0
 * Date          : 16/03/2020
 * Copyright     : Luc CLÉMENT - lucclement38@gmail.com
 */
@Service
public class DirectorService {

    private final DirectorRepository directorRepository;
    private final Dvdrepository dvdrepository;

    @Autowired
    public DirectorService(DirectorRepository directorRepository, Dvdrepository dvdrepository) {
        this.directorRepository = directorRepository;
        this.dvdrepository = dvdrepository;
    }

    /**
     * Mise à jour du nom du réalisateur et fusionne les listes de films en lorsqu'un autre réalisateur porte déjà le même nom
     * Supprime l'éventuel réalisateur en doublon
     * @param name nouveau nom du réalisateur à assigner
     * @param id identifiant du réalisateur dont on veut modifier le nom
     * @return le réalisateur mis à jour de type {@link Director}
     * @throws NoResultException
     * @see #findDirectorIdByName(String)
     */
    @Transactional
    public Director updateDirector(String name, Long id) throws NoResultException{
        Director result = null;
        Optional<Director> optionalDirector = directorRepository.findById(id);
        if (optionalDirector.isPresent()){
            Director director = optionalDirector.get();
            director.setName(name);
            Director updatedDirector = directorRepository.save(director);
            List<Long> idList = directorRepository.getDirectorIdByName(name);
            if (idList.size()>1){
                for (Long idRealisateur : idList){
                    if (!idRealisateur.equals(updatedDirector.getId())){
                        Optional<Director> optionalDoublon = directorRepository.findById(idRealisateur);
                        if (optionalDoublon.isPresent()){
                            Director doublon = optionalDoublon.get();
                            List<Dvd> doublonDirectorDvds = doublon.getDvds();
                            // supression des références au doublons dans tous les dvds
                            for (Dvd dvdOfDoublon : doublonDirectorDvds){
                                List<Director> dvdDirectors = dvdOfDoublon.getDirectors();
                                dvdDirectors.remove(doublon);
                                dvdOfDoublon.setDirectors(dvdDirectors);
                                dvdrepository.save(dvdOfDoublon);
                            }
                            Set<Dvd> directorFilms = new HashSet<>(doublonDirectorDvds);
                            directorFilms.addAll(doublon.getDvds());
                            director.setDvds(new ArrayList<>(directorFilms));
                            result = directorRepository.save(director);
                            directorRepository.deleteById(idRealisateur);
                        }
                    }
                }
            }else{
                result = updatedDirector;
            }
            return result;
        }else{
            throw new NoResultException();
        }
    }

    /**
     * Alimente la liste du réalisateurs du formulaire d'ajout et de modification d'un film
     * @return la liste des noms de réalisateurs
     */
    public List<String> findAllDirectorNames(){
        return directorRepository.getDirectorNames();
    }

    /**
     * Permet de naviguer vers la page du réalisateur en constituant une partie de l'URL
     * @param name nom {@link String} nom du réalisateur dans la liste de films sur lequel il a été cliqué
     * @return attribut identifiant (id) du réalisateur de type {@link Long}
     * @throws NoResultException généré par {@link #getMergedDirectorId(List)}
     * @see #updateDirector(String, Long)
     */
    public Long findDirectorIdByName(String name) throws NoResultException {
        List<Long> directorIds = directorRepository.getDirectorIdByName(name);
        if (directorIds.size() == 0) {
            throw new NoResultException();
        }else if(directorIds.size()==1){
            return directorIds.get(0);
        }else {
            return getMergedDirectorId(directorIds);
        }
    }

    /**
     * Alimente le modal de modification du nom du réalisateur
     * @see com.keziko.dvdtek.controllers.DirectorController#findDirectorNameById(Long)
     * @param id attribut unique du réalisateur {@link Director}
     * @return nom du réalisateur
     * @throws NoResultException quand le paramètre ne permet pas de récupérer un réalisateur
     */
    public String findDirectorNameById(Long id) throws NoResultException{
        if (directorRepository.getDirectorNameById(id).isPresent()){
            return directorRepository.getDirectorNameById(id).get();
        }else{
            throw new NoResultException();
        }
    }

    /**
     * Fusionne les réalisateurs ayant un nom doublon en fusionnant les listes de films<br>
     * Prévient les erreurs de fusion en cas de nom existant lors de la modification du nom du réalisateur si <code>updateDirector</code> n'a pas supprimé les éventuels doublons
     * @see #findDirectorIdByName(String)
     * @see #updateDirector(String, Long)
     * @param ids liste des attributs ids des réalisateurs trouvés par le répository
     * @return l'attribut id du réalisateur enregistré
     */
    @Transactional
    public Long getMergedDirectorId(List<Long> ids) throws NoResultException{
        Director director = null;
        Set<Dvd> filmsSet = new HashSet<>();
        for (Long id : ids) {
            Optional<Director> optionalDirector = directorRepository.findById(id);
            if (optionalDirector.isPresent()) {
                if (director == null) {
                    director = optionalDirector.get();
                    filmsSet.addAll(director.getDvds());
                } else {
                    Director duplicatedDirector = optionalDirector.get();
                    filmsSet.addAll(duplicatedDirector.getDvds());
                    directorRepository.delete(duplicatedDirector);
                }
            }else{
                throw new NoResultException();
            }
        }
        List<Dvd> filmList = new ArrayList<>();
        if (filmsSet.size() > 0) {
            filmList = new ArrayList<>(filmsSet);
        }
        (Objects.requireNonNull(director)).setDvds(filmList);
        director = directorRepository.save(director);
        return director.getId();
    }

    @Transactional
    public void deleteDirectorById(Long id) throws NoResultException {
        if (directorRepository.findById(id).isPresent()) {
            directorRepository.deleteById(id);
        } else {
            throw new NoResultException();
        }
    }
    public Optional<Director> getOptionalDirectorById(Long id){
        return directorRepository.findById(id);
    }
    public List<Director> findAllDirectors(){
        return (List<Director>) directorRepository.findAll();
    }
}
