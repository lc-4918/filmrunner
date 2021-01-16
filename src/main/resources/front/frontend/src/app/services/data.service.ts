import { Injectable } from '@angular/core';
import  *  as  codePays  from  '../models/codealpha2_fr.json';
import  *  as  enums  from  '../models/enums.json';
import { DvdForm } from '../models/dvd-form';
import {FilmListItem} from "../models/film-list-item";
import {JsonItem} from "../models/json-item";
import {MatSnackBar} from "@angular/material/snack-bar";
import {PaysItem} from "../models/pays-item";
import {DomSanitizer} from "@angular/platform-browser";
import {BehaviorSubject, Subject} from "rxjs";
@Injectable({
  providedIn: 'root'
})
export class DataService {
  isFormPage: boolean | undefined;
  detailsObject: JsonItem[] = (enums as any).details as JsonItem[];
  json = (codePays as any).default;
  filmList: FilmListItem[] = [];
  directorList: string[] = [];
  themeList: string[] = [];
  paysList: PaysItem[] = [];
  imageDirectory: string |undefined;
  directorSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private _snackBar: MatSnackBar, private sanitized: DomSanitizer) {
    for (const property in this.json) {
      const code = property;
      const label = (this.json)[property];
      const pays: PaysItem = {code,label}
      this.paysList.push(pays);
    }
  }

  isPaysExists(code: string):boolean{
    let result = false;
    const pays = this.paysList.find(p=>p.code==code);
    if (pays){
      result = true;
    }
    return result;
  }

  findLabelByCodePays(code: string):string | null{
    const pays = this.paysList.find(p=>p.code==code);
    if (pays){
      return pays.label;
    }else{
      return null;
    }
  }
  findLabelsContaining(code: string):string[]{
    const paysFound: PaysItem[] = this.paysList.filter(p=>p.label.toLowerCase().includes(code));
    const paysLabels: string[] = [];
    if (paysFound){
      for (const pays of paysFound){
        paysLabels.push(pays.code)
      }
    }
    return paysLabels;
  }

  // DVD-LIST MANAGEMENT
  getFilmList():FilmListItem[]{
    return this.filmList;
  }
  setFilmList(filmList: FilmListItem[]){
    this.filmList = filmList;
  }
  hasFilm(): boolean{
    return this.filmList && this.filmList.length>0;
  }
  deleteFilmFromList(id: number):void{
    const filmItemToDelete: FilmListItem | undefined = this.filmList.find(item=> item.id == id);
    if (filmItemToDelete){
      const index = this.filmList.indexOf(filmItemToDelete,0);
      this.filmList.splice(index);
    }
  }
  addOrUpdateFilmFromList(dvd: FilmListItem, id?: number,):void{
    if (id){
      const dvdItemToUpdate: FilmListItem | undefined = this.filmList.find(item=> item.id == id);
      if (dvdItemToUpdate){
        const index = this.filmList.indexOf(dvdItemToUpdate,0);
        this.filmList[index]=dvd;
      }
    }else{
      this.filmList.push(dvd);
    }
  }
  // DIRECTORS MANAGEMENT
  hasDirectorList(): boolean{
    return this.directorList && this.directorList.length>0;
  }
  getDirectorList(): string[]{
    return this.directorList;
  }
  setDirectorList(directorList: string[]):void{
    this.directorList = directorList;
  }

  // CODE-PAYS MANAGEMENT
  transformCodesToPaysLabels(listeString: string):string{
    const codeArray: string[] = listeString.split(";");
    const array: string[] = [];
    for (const code of codeArray){
      const label = this.findLabelByCodePays(code);
      if (label){
        array.push(label);
      }
    }
    let result = array[0];
    if (array.length>=1 && array[0]==''){
      array.splice(0,1);
    }
    if (array.length>1){
      for (let i=1; i<array.length; i++){
        result = result + ', ' + array[i];
      }
    }else{
      result = array[0];
    }
    return result;
  }

  // DETAILS MANAGEMENT
  createDetailsMap(dvd: DvdForm):Map<number,boolean>{
    let result : Map<number,boolean> = new Map<number,boolean>();
    const detailsArray = dvd.details && dvd.details.length>0? dvd.details?.split(";"): [];
    for (const detail of this.detailsObject){
      result.set(detail.id,detailsArray.indexOf(detail.id.toString())>-1);
    }
    return result;
  }

  // THEMES MANAGEMENT
  hasThemeList(): boolean{
    return this.themeList && this.themeList.length>0;
  }
  getThemeList(): string[]{
    return this.themeList;
  }
  setThemeList(themeList: string[]):void{
    this.themeList = themeList;
  }

  // LOG MANAGEMENT
  openSnackBar(message: string, level: string):void {
    const levelClass = level.concat('-snackbar');
    this._snackBar.open(message, undefined,{
      duration: 2000,
      panelClass: [levelClass]
    });
  }

  // MISCELLANEOUS
  convertDetailsMapToString(details: Map<number,boolean>):string{
    const result: number[] = [];
    details.forEach((v,k)=>{
      if (v){
        result.push(k);
      }
    });
    return result.join(";");
  }
}
