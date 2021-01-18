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
  filmReceived: DvdForm |undefined;
  film: FilmDisplay | undefined;
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
        this.transformPays();
        const film: FilmDisplay = {
          titre: this.filmReceived.titre,
          titreVf: this.filmReceived.titreVf,
          annee: this.filmReceived.annee,
          duree: this.filmReceived.duree,
          description: this.filmReceived.description,
          tip: this.filmReceived.format? this.findLabelById(this.dvdTypeArray, this.filmReceived.format):undefined,
          support: this.filmReceived.support? this.findLabelById(this.dvdSupportArray, this.filmReceived.support):undefined,
          genre: this.filmReceived.type? this.findLabelById(this.dvdGenreArray, this.filmReceived.type):undefined,
          source: this.filmReceived.source? this.findLabelById(this.dvdSourceArray, this.filmReceived.source):undefined,
          norme: this.filmReceived.norme? this.findLabelById(this.dvdNormeArray, this.filmReceived.norme):undefined,
          details: this.filmReceived.details? this.parseDetails(this.filmReceived.details): undefined,
          pays: this.filmReceived.pays,
          subLangs: this.filmReceived.subLangs,
          themes: this.filmReceived.themes,
          realisateurs: this.filmReceived.realisateurs,
          shortfilms: this.filmReceived.shortfilms,
          imageUrl: this.filmReceived.imageUrl? this.filmReceived.imageUrl: this.dataService.imageDirectory+'default.jpg',
        }
        this.film = film;
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
          pays.push(this.dataService.findLabelByCodePays(this.filmReceived.pays[i]))
        }
      }
    }
  }

  close(): void {
    this.dialogRef.close();
  }

  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
