import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import *  as  enums from '../models/enums.json';
import *  as  codePays from '../models/codealpha2_fr.json';
import {RestService} from "../services/rest.service";
import {filter, takeWhile} from "rxjs/operators";
import {DvdForm} from "../models/dvd-form";
import {JsonItem} from "../models/json-item";
import {DataService} from "../services/data.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MatSlideToggleChange} from "@angular/material/slide-toggle";
import {FormArray, FormBuilder, FormControl} from "@angular/forms";
import {FilmListItem} from "../models/film-list-item";
import {Theme} from "../models/theme";

@Component({
  selector: 'app-dvdform',
  templateUrl: './dvdform.component.html',
  styleUrls: ['./dvdform.component.css']
})

export class DvdformComponent implements OnInit, OnDestroy {
  isEdit = false;
  dvdId: number |undefined;
  hasSubscription: boolean = false;
  dvdDetailArray: JsonItem[] = (enums as any).details as JsonItem[];
  dvdGenreArray: JsonItem[] = (enums as any).genres as JsonItem[];
  dvdSupportArray: JsonItem[] = (enums as any).supports as JsonItem[];
  dvdTypeArray: JsonItem[] = (enums as any).types as JsonItem[];
  dvdSourceArray: JsonItem[] = (enums as any).sources as JsonItem[];
  dvdNormeArray: JsonItem[] = (enums as any).normes as JsonItem[];
  paysArray: any = (codePays as any).default;
  pays: string[] = [];
  directors: string[] = [];
  themes: Theme[] | undefined;
  selectedThemes: Theme[] = [];
  dvdDetailMap: Map<number,boolean> = new Map<number,boolean>();
  hasDescription: boolean = false;
  descriptionToggleLabel = "Ajouter une description";
  hasShortfilm: boolean = false;
  hasCustomImage: boolean = false;
  selectedFiles: FileList | any;
  currentFile: File | any;
  uploadSuccess: boolean = false;
  chipTitles: string[] = ["Réalisateurs","Pays","Sous-titres"];
  chipPlaceholders: string[] = ["Nouveau réalisateur","Nouveau pays","Nouvelle langue"];
  badgeElement: any;
  componentDvd: DvdForm = {
    titre: '',
    pays: [],
    realisateurs: [],
    subLangs: [],
    shortfilms: [],
  };
  shortfilmForm = this.fb.group({
    shortfilmTab: this.fb.array([],{updateOn: 'blur'})
  });
  themesControl = new FormControl([]);

  constructor(private restService: RestService, private dataService: DataService,private router: Router, private route: ActivatedRoute, private fb: FormBuilder) {
    this.hasSubscription = true;
    for(let key in this.paysArray) {
      this.pays.push(key);
    }
    for (const detail of this.dvdDetailArray){
      this.dvdDetailMap.set(detail.id,false);
    }
    route.params.pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(val => {
      if (val.id){
        this.isEdit = true;
        this.dvdId = parseInt(val.id);
      }else{
        this.isEdit = false;
        this.dvdId = undefined;
      }
    });
  }

  ngOnInit(): void {
    if (this.dvdId){
      this.restService.findDvdById(this.dvdId).pipe(
        takeWhile(()=>this.hasSubscription)
      ).subscribe(
        res=>{
          this.cleanEmptyList(res);
          this.componentDvd = res;
          if (this.componentDvd.themes && this.componentDvd.themes.length>0){
            this.themesControl.setValue(this.componentDvd.themes);
            this.selectedThemes = this.componentDvd.themes;
          }
          this.dvdDetailMap = this.dataService.createDetailsMap(this.componentDvd);
          const desc = this.componentDvd.description;
          this.hasDescription = desc!==undefined && desc!==null && desc.length>0;
          const cm = this.componentDvd.shortfilms;
          this.hasShortfilm = cm!==undefined && cm!==null && cm.length>0;
          if (this.hasShortfilm){
            this.setFormShortfilms();
          }
/*          if (this.componentDvd.themes){
            for (const th of this.componentDvd.themes.entries()){
              this.themesControl.patchValue(th[1]);
            }
          }*/
          if (this.componentDvd.imageUrl){
            const length =this.componentDvd.imageUrl.split('\\').length;
            this.currentFile = this.componentDvd.imageUrl?.split('\\')[length-1];
          }
        },
        error=>{
          this.dataService.openSnackBar("erreur dans la récupération du film","error");
          console.log(error);
        }
      )
    }

    // Get all existing directors for autocomplete
    this.restService.getAllDirectorsName().pipe(
      filter(()=>!this.dataService.hasDirectorList()),
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.directors = res;
        this.dataService.setDirectorList(res);
      },
      error=>{
        console.log(error);
      }
    )

    // Get all existing themes for select
    this.restService.getAllThemes().pipe(
      filter(()=>!this.themes),
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.dataService.cleanEmptyThemes(res);
        this.themes = res;
        this.dataService.setThemeList(res);
      },
      error=>{
        console.log(error);
      }
    )
    if(this.dataService.hasThemeList()){
      this.themes = this.dataService.getThemeList();
    }

  }

  //DVD
  validForm():void{
    if (this.selectedFiles){
      this.currentFile = this.selectedFiles.item(0);
    }
    if (!this.currentFile){
      this.createOrUpdateDvd();
      return;
    }
    this.restService.upload(this.currentFile).then(
      res=> {
        this.componentDvd.imageUrl = res.message;
        if (this.selectedFiles){
          this.dataService.openSnackBar("image chargée","success");
        }
        this.createOrUpdateDvd();
        this.selectedFiles = undefined;
      }
    ).catch(error=>{
      this.uploadSuccess = false;
      this.currentFile = undefined;
      this.dataService.openSnackBar("échec de l'import de l'image","error");
    })
  }
  createOrUpdateDvd():void{
    const isUpdate: boolean = !!this.dvdId;
    this.componentDvd.details = this.dataService.convertDetailsMapToString(this.dvdDetailMap);
    this.componentDvd.description = this.hasDescription ? this.componentDvd.description : undefined;
    this.componentDvd.shortfilms = this.getFormShortfilms();
    this.componentDvd.themes = this.themesControl.value;
    if (this.themesControl.value && this.themesControl.value.length>0){
      const themesArray: Theme[] = [];
      for (const theme of this.themesControl.value){
        const themeDTO: Theme = {
          name: theme.name,
          color: theme.color
        }
        themesArray.push(themeDTO)
      }
      this.componentDvd.themes = themesArray;
    }
    if (isUpdate){
      this.restService.updateDvd(this.componentDvd).pipe(
        takeWhile(()=>this.hasSubscription),
      ).subscribe(
        res=>{
          this.dataService.addOrUpdateFilmFromList(res, this.dvdId);
          this.dataService.openSnackBar("dvd mis à jour", "success");
          this.goToDvdPage();
        },
        error => {
          this.dataService.openSnackBar("échec de la mise à jour", "error");
          console.log(error);
        }
      );
    }else{
      this.restService.createDvd(this.componentDvd).pipe(
        takeWhile(()=>this.hasSubscription)
      ).subscribe(
        res=>{
          this.dataService.addOrUpdateFilmFromList(res);
          this.dataService.openSnackBar("film ajouté", "success");
          this.goToDvdPage();
        },
        error => {
          this.dataService.openSnackBar("échec de la création du film", "error");
          console.log(error);
        }
      );
    }
  }
  deleteDvd(id: number):void{
    this.restService.deleteDvd(id).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        if (res){
          this.dataService.deleteFilmFromList(id);
          this.dataService.openSnackBar("dvd supprimé", "success");
          this.goToDvdPage();
        }
      },
      error => {
        console.log(error);
      }
    )
  }
  cleanEmptyList(film: DvdForm): void{
    if (film.pays?.length==1 && film.pays[0]==''){
      film.pays = [];
    }
    if (film.subLangs?.length==1 && film.subLangs[0]==''){
      film.subLangs = [];
    }
  }

  // DETAILS
  detailToggleChanged(event: any, id: number):void{
    const previousState = this.dvdDetailMap.get(id);
    const currentState = previousState==undefined ? true : !previousState;
    this.dvdDetailMap.set(id,currentState)
    const label = this.dvdDetailArray.filter(x=>x.id==id).map(x=>x.label)[0];
    if (label=='CM'){
      this.toggleShortfilms(currentState);
    }
  }

  // CHIPS
  setChipData(event: string[],category: string):void{
    switch (category){
      case 'realisateurs':
        this.componentDvd.realisateurs = event;
        break;
        case 'pays':
          this.componentDvd.pays = event;
        break;
        case 'langues':
          this.componentDvd.subLangs = event;
        break;
    }
  }

  // DESCRIPTION
  toggleDescTitle(event: MatSlideToggleChange):void{
    this.descriptionToggleLabel = this.hasDescription ? "":"Ajouter une description";
  }

  // COURT-METRAGE
  formValues = this.shortfilmForm.getRawValue();
  arrayValues = (this.shortfilmForm.get('shortfilmTab') as FormArray).getRawValue();
  addShortfilm():void{
    const group = this.fb.group({
      cmName: '',
    },{updateOn: 'blur'});
    (this.shortfilmForm.get('shortfilmTab') as FormArray).push(group);
  }
  deleteShortfilm(index: number):void{
    (this.shortfilmForm.get('shortfilmTab') as FormArray).removeAt(index);
  }
  toggleShortfilms(isCmChecked: boolean):void{
    const formArray = (this.shortfilmForm.get('shortfilmTab') as FormArray);
      this.hasShortfilm = isCmChecked;
      if (isCmChecked){
        if ((this.shortfilmForm.get('shortfilmTab') as FormArray).length==0){
          this.addShortfilm();
        }
      }else if (formArray && formArray.length==1 && formArray.at(0).value.length==0){
        (this.shortfilmForm.get('shortfilmTab') as FormArray).clear();
      }
    }
  get shortfilmTab() : FormArray {
    return this.shortfilmForm.get('shortfilmTab') as FormArray
  }
  getFormShortfilms():string[]{
    return ((this.shortfilmForm.get('shortfilmTab') as FormArray).getRawValue()).filter(x => x.cmName.length > 0).map(x => x.cmName);
  }
  setFormShortfilms():void{
    const groups = this.componentDvd.shortfilms.map(shortfilm => {
      return this.fb.group({cmName: this.fb.control(shortfilm)},{ updateOn: 'blur' });
    });
    const array = this.shortfilmForm.get('shortfilmTab') as FormArray;
    for(const group of groups){
      array.push(group);
    }
  }
  hasMoreShortfilms():string{
    const array = (this.shortfilmForm.get('shortfilmTab') as FormArray);
    const result = '';
    if (array && array.length>4){
      if (array.length>8){
        return 'row-detail-xlarge';
      }else{
        return 'row-detail-large';
      }
    }
    return result;
  }
  hasMoreThanOneShortfilm():boolean{
    const array = (this.shortfilmForm.get('shortfilmTab') as FormArray);
    return array && array.length>1;
  }

  // IMAGE
  selectFile(event: any):void {
    this.selectedFiles = event.target.files;
  }
  hasImage():string{
    const hasSelectedImage = this.selectedFiles && this.selectedFiles.length>0;
    const hasImageInDvd = this.componentDvd && this.componentDvd.imageUrl &&  this.componentDvd.imageUrl.length>0 && !this.componentDvd.imageUrl.includes('default-film.jpg');
    if (hasSelectedImage || hasImageInDvd){
      return 'image-wrapper-selected'
    }else{
      return '';
    }
  }
  deleteFile():void{
    if (this.componentDvd.imageUrl){
      this.componentDvd.imageUrl = undefined;
      if (this.dvdId){
        this.restService.deleteImage(this.dvdId).pipe(
          takeWhile(()=>this.hasSubscription)
        ).subscribe(
          res=>{
            if (res){
              this.selectedFiles = undefined;
              this.currentFile = undefined;
              this.dataService.openSnackBar("image supprimée","success");
            }else{
              this.dataService.openSnackBar("impossible de supprimer l'image","error");
            }
          },
          err=>{
            this.dataService.openSnackBar("impossible de supprimer l'image","error");
          }
        )
      }
    }else{
      this.currentFile = undefined;
      this.selectedFiles = undefined;
    }
  }
  compareFn(o1: any, o2: any) {
    return o1.name == o2.name;
  }

  // NAVIGATION
  goToDvdPage(): void{
    this.router.navigate(['']).then(r =>{
      if (r){
        console.log("navigation successful");
      }else{
        console.log("navigation has failed");
      }
    });
  }

  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
