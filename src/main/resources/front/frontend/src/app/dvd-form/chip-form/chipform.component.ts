import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {Component, ElementRef, Input, OnInit, Output, ViewChild, EventEmitter, OnDestroy} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatAutocompleteSelectedEvent, MatAutocomplete} from '@angular/material/autocomplete';
import {MatChipInputEvent} from '@angular/material/chips';
import {CdkDragDrop, moveItemInArray} from '@angular/cdk/drag-drop';
import {Observable} from 'rxjs';
import {map, startWith, takeWhile} from 'rxjs/operators';
import {DataService} from "../../services/data.service";

/**
 * @title Chips Autocomplete
 */
@Component({
  selector: 'chip-form-component',
  templateUrl: 'chipform.component.html',
  styleUrls: ['chipform.component.css'],
})
export class ChipformComponent implements OnInit, OnDestroy{
  @Input() title: string | undefined;
  @Input() placeholder: string | undefined;
  @Input() listOfElements: any[];
  @Input() elements: string[] = [];
  @Input() limitedChoice: boolean | undefined;
  @Output() elementsEmitter = new EventEmitter<string[]>();
  hasSubscription = false;
  panelClass = "panel-class";
  visible = true;
  selectable = true;
  removable = true;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  elementCtrl = new FormControl();
  filteredElements: Observable<string[]> | undefined;

  @ViewChild('elementInput') elementInput: ElementRef<HTMLInputElement> | undefined;
  @ViewChild('auto') matAutocomplete: MatAutocomplete | undefined;

  constructor(public dataService: DataService) {
    this.listOfElements=[];
    this.hasSubscription = true;
  }

  ngOnInit():void {
    this.filteredElements = this.elementCtrl.valueChanges.pipe(
      startWith(null),
      takeWhile(()=>this.hasSubscription),
      map((element: string | null) => element ? this._filter(element) : this.listOfElements.slice()));
  }

  add(event: MatChipInputEvent): void {
    const input = event.input;
    let value = event.value;
    // Add our element
    if ((value || '').trim()) {
      if (this.limitedChoice){
        if (this.dataService.isPaysExists(value.trim().toUpperCase())){
          this.elements.push(value.trim().toUpperCase());
          // emit after add
          this.elementsEmitter.emit(this.elements);
        }
      }else{
          this.elements.push(value.trim());
          // emit after add
          this.elementsEmitter.emit(this.elements);
      }
    }
    // Reset the input value
    if (input) {
      input.value = '';
    }
    this.elementCtrl.setValue(null);
  }

  remove(element: string): void {
    const index = this.elements.indexOf(element);
    if (index >= 0) {
      this.elements.splice(index, 1);
      // emit after remove
      this.elementsEmitter.emit(this.elements);
    }
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    let value = event.option.value;
    const valueArray = event.option.value.split(' ');
    if (valueArray.length>1){
      value = valueArray[0];
    }
    this.elements.push(value);
    // @ts-ignore
    this.elementInput.nativeElement.value = '';
    this.elementCtrl.setValue(null);
  }

  drop(event: CdkDragDrop<string[]>):void {
    moveItemInArray(this.elements, event.previousIndex, event.currentIndex);
    // emit after drop
    this.elementsEmitter.emit(this.elements);
  }

  private _filter(value: string): string[] {
    const filterValue = value.toLowerCase();
    if (this.limitedChoice){
      const searchByLabel = this.dataService.findLabelsContaining(filterValue)
      const searchByCode = this.listOfElements.filter(element => element.toLowerCase().indexOf(filterValue) === 0);
      const wholeSearch =  [...searchByLabel, ...searchByCode];
      const set = new Set(wholeSearch);
      return Array.from(set);
    }else{
      return this.listOfElements.filter(element => element.toLowerCase().includes(filterValue));
    }
  }
  ngOnDestroy():void {
    this.hasSubscription=false;
  }
}
