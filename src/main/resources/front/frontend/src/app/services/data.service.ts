import { Injectable } from '@angular/core';
import  *  as  codePays  from  '../models/codealpha2_fr.json';
import  *  as  enums  from  '../models/enums.json';
import { DvdForm } from '../models/dvd-form';
import {FilmListItem} from '../models/film-list-item';
import {JsonItem} from '../models/json-item';
import {MatSnackBar} from '@angular/material/snack-bar';
import {PaysItem} from '../models/pays-item';
import {BehaviorSubject} from 'rxjs';
import {Theme} from "../models/theme";
@Injectable({
  providedIn: 'root'
})
export class DataService {
  isFormPage: boolean | undefined;
  detailsObject: JsonItem[] = (enums as any).details as JsonItem[];
  json = (codePays as any).default;
  filmList: FilmListItem[] = [];
  directorList: string[] = [];
  themeList: Theme[] = [];
  paysList: PaysItem[] = [];
  imageDirectory: string |undefined;
  directorSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  constructor(private _snackBar: MatSnackBar) {
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

  findLabelByCodePays(code: string):string{
    const pays = this.paysList.find(p=>p.code==code);
    if (pays){
      return pays.label;
    }else{
      return "";
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
    const codeArray: string[] = listeString.split(';');
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
    const detailsArray = dvd.details && dvd.details.length>0? dvd.details?.split(';'): [];
    for (const detail of this.detailsObject){
      result.set(detail.id,detailsArray.indexOf(detail.id.toString())>-1);
    }
    return result;
  }

  // THEMES MANAGEMENT
  hasThemeList(): boolean{
    return this.themeList && this.themeList.length>0;
  }
  getThemeList(): Theme[]{
    return this.themeList;
  }
  setThemeList(themeList: Theme[]):void{
    this.themeList = themeList;
  }
  replaceTheme(oldValue: string, theme: Theme):void{
    const oldTheme = this.themeList.find(th=>th.name==oldValue);
    if (oldTheme){
      const index = this.themeList.indexOf(oldTheme,0);
      this.themeList[index]=theme;
    }
  }
  cleanEmptyThemes(res: Theme[]):void{
    let indexes = [];
    for (const th of res){
      if (th.name==""){
        const index = res.indexOf(th,0);
        indexes.push(index);
      }
    }
    for (const id of indexes){
      res.splice(id,1);
    }
  }
  convertMapThemeToTheme(themeMap: Map<string,string>):Theme[]{
    const result: Theme[] = [];
    for (const [key, value] of Object.entries(themeMap)) {
      const th: Theme = {
        name:key,
        color: value
      }
      result.push(th);
    }
    return result;
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
    return result.join(';');
  }

  isLight(colorValue: string):boolean {
    let result = false;
    // Check the format of the color, HEX or RGB?
    let r: number = 0;
    let g: number = 0;
    let b: number = 0;

    if (colorValue.match(/^rgb/)) {

      // If HEX --> store the red, green, blue values in separate variables
      const color = colorValue.match(/^rgba?\((\d+),\s*(\d+),\s*(\d+)(?:,\s*(\d+(?:\.\d+)?))?\)$/);
      if (color) {
        r = parseInt(color[1]);
        g = parseInt(color[2]);
        b = parseInt(color[3]);
      }
    }
    else {

      // If RGB --> Convert it to HEX: http://gist.github.com/983661
      // @ts-ignore
      const color = +("0x" + colorValue.slice(1).replace(colorValue.length < 5 && /./g, '$&$&'));
      r = color >> 16;
      g = color >> 8 & 255;
      b = color & 255;
    }

    // HSP (Highly Sensitive Poo) equation from http://alienryderflex.com/hsp.html
    if(r && g && b){
      const hsp = Math.sqrt( 0.299 * (r * r) + 0.587 * (g * g) + 0.114 * (b * b));
      // Using the HSP value, determine whether the color is light or dark
      if (hsp>127.5) {
        result = true;
      }
      else {
        result = false;
      }
    }
    return result;
  }
}
