import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogData} from "../delete-film-dialog/delete-film-dialog.component";
import {takeWhile} from "rxjs/operators";
import {RestService} from "../../services/rest.service";
import {DvdForm} from "../../models/dvd-form";
import {DataService} from "../../services/data.service";
import *  as  enums from '../../models/enums.json';
import {JsonItem} from "../../models/json-item";
import {FilmDisplay} from "../../models/film-display";
import {Theme} from "../../models/theme";

@Component({
  selector: 'app-display-film-dialog',
  templateUrl: './display-film-dialog.component.html',
  styleUrls: ['./display-film-dialog.component.css']
})
export class DisplayFilmDialogComponent implements OnInit, OnDestroy {
  dvdDetailArray: JsonItem[] = (enums as any).details as JsonItem[];
  dvdGenreArray: JsonItem[] = (enums as any).genres as JsonItem[];
  dvdSupportArray: JsonItem[] = (enums as any).supports as JsonItem[];
  dvdTypeArray: JsonItem[] = (enums as any).types as JsonItem[];
  dvdSourceArray: JsonItem[] = (enums as any).sources as JsonItem[];
  dvdNormeArray: JsonItem[] = (enums as any).normes as JsonItem[];
  hasSubscription = false;
  realGroupNumber: number = 0;
  filmReceived: DvdForm |undefined;
  film: FilmDisplay | undefined;
  filmLabels: Map<String,String> = new Map<string, string>();
  listLabels: Map<string,string> = new Map<string, string>();
  constructor(
    public dialogRef: MatDialogRef<DisplayFilmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private restService: RestService,
    private dataService: DataService) {
    this.hasSubscription = true;
  }

  ngOnInit(): void {
    console.log(window.location.host);
    this.getFilm();
  }

  getFilm():void{
    this.restService.findDvdById(this.data.idFilm).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.filmReceived = res;
        //this.transformPays();
        this.createPaysMap();
        this.createListMap();
        this.transformThemes();
        const filmThemes = this.transformThemes();
        this.film = {
          titre: this.filmReceived.titre,
          titreVf: this.filmReceived.titrevf,
          annee: this.filmReceived.annee,
          duree: this.filmReceived.duree,
          description: this.filmReceived.description,
          tip: this.filmReceived.format ? this.findLabelById(this.dvdTypeArray, this.filmReceived.format) : undefined,
          support: this.filmReceived.support ? this.findLabelById(this.dvdSupportArray, this.filmReceived.support) : undefined,
          genre: this.filmReceived.type ? this.findLabelById(this.dvdGenreArray, this.filmReceived.type) : undefined,
          source: this.filmReceived.source ? this.findLabelById(this.dvdSourceArray, this.filmReceived.source) : undefined,
          categorie: this.filmReceived.support? this.dataService.createCategorie(this.filmReceived.support): undefined,
          norme: this.filmReceived.norme ? this.findLabelById(this.dvdNormeArray, this.filmReceived.norme) : undefined,
          details: this.filmReceived.details ? this.parseDetails(this.filmReceived.details) : undefined,
          pays: this.filmReceived.pays,
          subLangs: this.filmReceived.subLangs,
          themes: filmThemes,
          realisateurs: this.filmReceived.realisateurs,
          shortfilms: this.filmReceived.shortfilms,
          imageUrl: this.filmReceived.imageUrl ? this.filmReceived.imageUrl : this.dataService.imageDirectory + 'default.jpg',
        };
      },
      error => {
        console.log("impossible de lire le film : "+error);
      }
    )
  }


  parseDetails(details: string):string[]{
    const array: string[]=[];
    const inputArray = details.split(';');
    for (const input of inputArray){
      const label = this.findLabelById(this.dvdDetailArray,input);
      if (label){
        array.push(label);
      }
    }
    return array;
  }


  findLabelById(array: JsonItem[], id: string):string|undefined{
    const idNumber = parseInt(id);
    const item = array.find(x=>x.id==idNumber);
    if (item){
      return item.label;
    }else{
      return undefined;
    }
  }
  transformPays():void{
    if (this.filmReceived){
      if (this.filmReceived.pays && this.filmReceived.pays.length>0){
        let pays = [];
        for (let i=0; i<this.filmReceived.pays.length; i++){
          const p = this.dataService.findLabelByCodePays(this.filmReceived.pays[i]);
          if (p.length>0){
            pays.push(this.dataService.findLabelByCodePays(this.filmReceived.pays[i]));
          }
        }
        this.filmReceived.pays = pays;
      }
    }
  }
  createPaysMap():void{
    if (this.filmReceived){
      if (this.filmReceived.pays && this.filmReceived.pays.length>0){
        for (let i=0; i<this.filmReceived.pays.length; i++) {
          const p = this.dataService.findLabelByCodePays(this.filmReceived.pays[i]);
          this.filmLabels.set(this.filmReceived.pays[i],p);
        }
      }
      if (this.filmReceived.subLangs && this.filmReceived.subLangs.length>0){
        for (let i=0; i<this.filmReceived.subLangs.length; i++) {
          const p = this.dataService.findLabelByCodePays(this.filmReceived.subLangs[i]);
          this.filmLabels.set(this.filmReceived.subLangs[i],p);
        }
      }
    }
  }
  createListMap():void{
    if (this.filmReceived){
      this.listLabels = this.dataService.createListMap(this.filmReceived);
    }
  }

  transformThemes():Theme[]{
    let filmThemes:Theme[] = [];
    if (this.filmReceived){
      if (this.filmReceived.themes){
        filmThemes = this.filmReceived.themes;
      }
    }
    return this.cleanThemes(filmThemes);
  }

  getColor(theme: Theme):string{
    if (theme.color){
      const isLightBackground = this.dataService.isLight(theme.color);
      return isLightBackground? 'rgba(0,0,0,.87)':'rgb(255,255,255)';
    }else {
      return 'rgba(0,0,0,.87)';
    }
  }
  cleanThemes(themes: Theme[]):Theme[]{
    const theme = themes.find(x=>x.name=='RIP'.toLowerCase()||x.name=='ISO'.toLowerCase());
    const result:Theme[] = [];
    if (theme){
      const index = themes.indexOf(theme,0);
      themes.splice(index,1);
    }
    return themes;
  }

  close(): void {
    this.dialogRef.close();
  }

  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
