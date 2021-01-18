import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DataService} from "../../services/data.service";
import {RestService} from "../../services/rest.service";
import {MatChipInputEvent} from "@angular/material/chips";
import {FormControl} from "@angular/forms";
import {MatAutocompleteSelectedEvent,MatAutocomplete} from "@angular/material/autocomplete";
import {map, startWith, takeWhile} from "rxjs/operators";
import {Observable} from "rxjs";
import {COMMA, ENTER} from "@angular/cdk/keycodes";

@Component({
  selector: 'app-theme-dialog',
  templateUrl: './theme-dialog.component.html',
  styleUrls: ['./theme-dialog.component.css']
})
export class ThemeDialog implements OnInit, OnDestroy {
  hasSubscription: boolean = false;
  themes: string[] = [];
  currentValue: string = '';
  oldValue: string = '';
  visible = true;
  addOnBlur = true;
  selectable = true;
  removable = true;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  filteredElements: Observable<string[]> | undefined;
  inputPlaceholder: string|null = "Nouveau thème..."

  @ViewChild('elementInput') elementInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild('auto') matAutocomplete: MatAutocomplete | undefined;

  constructor(private dataService: DataService, private  restService: RestService) {
    this.themes = this.dataService.getThemeList();
    this.hasSubscription = true;
  }
  elementCtrl = new FormControl();

  ngOnInit(): void {
    this.filteredElements = this.elementCtrl.valueChanges.pipe(
      takeWhile(()=>this.hasSubscription),
      startWith(null),
      map((element: string | null) => element ? this._filter(element) : this.themes.slice()));
  }

  selectedThemes: any[] = [];

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
        ()=>{
          this.themes.push(value.trim());
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
    this.selectedThemes[0] = this.currentValue;
    const oldValindex = this.dataService.themeList.indexOf(this.oldValue);
    if (oldValindex>=0){
      this.restService.updateTheme(this.oldValue,this.currentValue).pipe(
        takeWhile(()=>this.hasSubscription)
      ).subscribe(
        ()=>{
          this.themes = this.dataService.getThemeList();
          const index = this.themes.indexOf(this.oldValue);
          this.themes[index]=this.currentValue;
          this.selectedThemes.splice(0, 1);
          this.currentValue ="";
          this.dataService.openSnackBar("thème mis à jour","success");
        },
        ()=>{
          this.dataService.openSnackBar("échec de la mise à jour du thème","error");
        }
      )
    }
  }

  remove(element: string): void {
    const index = this.themes.indexOf(element);
    if (index >= 0) {
      this.restService.deleteTheme(element).pipe(
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
    this.themes.push(event.option.viewValue);
    // @ts-ignore
    this.elementInput.nativeElement.value = '';
    this.elementCtrl.setValue(null);
  }

  toggleTheme(theme: any): void {
    let index = this.selectedThemes.indexOf(theme);
    let indexList = this.dataService.themeList.indexOf(theme);
    if (index >= 0) {
      this.removable = true;
      this.selectedThemes.splice(index, 1);
      this.currentValue ="";
    } else {
      this.removable = false;
      this.oldValue = theme;
      this.currentValue = theme;
      if (this.selectedThemes.length==0){
        this.selectedThemes.push(theme);
      }else{
        this.selectedThemes.splice(0, 1);
        this.selectedThemes.push(theme);
      }
    }
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    // @ts-ignore
    return this.themes.filter(element => element.toLowerCase().indexOf(filterValue) === 0);
  }
  checkPlaceHolder():void {
    if (this.inputPlaceholder) {
      this.inputPlaceholder = null
      return;
    } else {
      this.inputPlaceholder = 'Nouveau thème...'
      return
    }
  }
  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
