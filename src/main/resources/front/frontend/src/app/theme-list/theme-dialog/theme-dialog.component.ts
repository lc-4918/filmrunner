import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DataService} from "../../services/data.service";
import {RestService} from "../../services/rest.service";
import {MatChipInputEvent} from "@angular/material/chips";
import {FormControl} from "@angular/forms";
import {MatAutocompleteSelectedEvent,MatAutocomplete} from "@angular/material/autocomplete";
import {map, startWith, takeWhile} from "rxjs/operators";
import {Observable} from "rxjs";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {Theme} from "../../models/theme";

@Component({
  selector: 'app-theme-dialog',
  templateUrl: './theme-dialog.component.html',
  styleUrls: ['./theme-dialog.component.css']
})
export class ThemeDialog implements OnInit, OnDestroy {
  hasSubscription: boolean = false;
  themes: Theme[] = [];
  currentValue: Theme | undefined;
  oldValue: string | undefined;
  visible = true;
  addOnBlur = true;
  selectable = true;
  removable = true;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  filteredElements: Observable<Theme[]> | undefined;
  inputPlaceholder: string|undefined;

  @ViewChild('elementInput') elementInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild('auto') matAutocomplete: MatAutocomplete | undefined;

  constructor(private dataService: DataService, private  restService: RestService) {
    this.themes = this.dataService.getThemeList();
    this.hasSubscription = true;
  }
  elementCtrl = new FormControl();

  ngOnInit(): void {
/*    this.filteredElements = this.elementCtrl.valueChanges.pipe(
      takeWhile(()=>this.hasSubscription),
      startWith(null),
      map((element: Theme | null) => element ? this._filter(element) : this.themes.slice()));*/
  }

  selectedThemes: Theme[] = [];

  isSelected(theme: any): boolean {
    const index = this.selectedThemes.indexOf(theme);
    return index >= 0;
  }


  add(event: MatChipInputEvent): void {
    const input = event.input;
    const value = event.value;
    // Add our element
    if ((value || '').trim()) {
      this.restService.createTheme(value).pipe(
        takeWhile(()=>this.hasSubscription)
      ).subscribe(
        (res)=>{
          this.themes.push(res);
          this.dataService.setThemeList(this.themes);
          this.dataService.openSnackBar("thème créé","success");
        },
        ()=>{
          this.dataService.openSnackBar("échec de la création du thème","error");
        }
      );
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
    this.elementCtrl.setValue(null);
  }

  update():void{
    if (this.currentValue && this.oldValue){
      this.selectedThemes[0] = this.currentValue;

          this.restService.updateTheme(this.oldValue,this.currentValue.name).pipe(
            takeWhile(()=>this.hasSubscription)
          ).subscribe(
            (res)=>{
              this.dataService.openSnackBar("thème mis à jour","success");
              if (this.currentValue && this.oldValue){
                this.dataService.replaceTheme(this.oldValue,res);
                this.selectedThemes.splice(0, 1);
                this.currentValue =undefined;
              }
              this.dataService.openSnackBar("thème mis à jour","success");
            },
            ()=>{
              this.dataService.openSnackBar("échec de la mise à jour du thème","error");
            }
          )
    }
  }

  remove(element: Theme): void {
    const index = this.themes.indexOf(element);
    if (index >= 0) {
      this.restService.deleteTheme(element.name).pipe(
        takeWhile(()=>this.hasSubscription)
      ).subscribe(
        ()=>{
          this.themes.splice(index, 1);
          this.dataService.setThemeList(this.themes);
          this.dataService.openSnackBar("thème supprimé","success");
        },
        ()=>{
          this.dataService.openSnackBar("échec de la suppression du thème","error");
        }
      );
    }
  }
  selected(event: MatAutocompleteSelectedEvent): void {
    this.themes.push(event.option.value);
    // @ts-ignore
    this.elementInput.nativeElement.value = '';
    this.elementCtrl.setValue(null);
  }

  toggleTheme(theme: any): void {
    let index = this.selectedThemes.indexOf(theme);
    if (index >= 0) {
      this.removable = true;
      this.selectedThemes.splice(index, 1);
      this.currentValue =undefined;
    } else {
      this.removable = false;
      this.oldValue = theme.name;
      this.currentValue = theme;
      if (this.selectedThemes.length==0){
        this.selectedThemes.push(theme);
      }else{
        this.selectedThemes.splice(0, 1);
        this.selectedThemes.push(theme);
      }
    }
  }

  private _filter(value: Theme): string[] {
    const filterValue = value.name.toLowerCase();
    // @ts-ignore
    return this.themes.filter(element => element.name.toLowerCase().indexOf(filterValue) === 0);
  }
  checkPlaceHolder():void {
    if (this.inputPlaceholder) {
      this.inputPlaceholder = undefined
      return;
    } else {
      this.inputPlaceholder = 'Nouveau thème...'
      return
    }
  }
  getColor(theme: Theme):string{
    if (theme.color){
      const isLightBackground = this.dataService.isLight(theme.color);
      return isLightBackground? 'rgba(0,0,0,.87)':'rgb(255,255,255)';
    }else {
      return 'rgba(0,0,0,.87)';
    }
  }


  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
