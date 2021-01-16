import { Directive, Output, EventEmitter, HostBinding, HostListener } from '@angular/core';
import { DomSanitizer} from '@angular/platform-browser';

@Directive({
  selector: '[appDragDrop]'
})
export class DragDropDirective {

  @Output() files: EventEmitter<File | Event> = new EventEmitter();

  @HostBinding('style.background-color') private background = '#f5fcff'
  @HostBinding('style.opacity') private opacity = '0.8'

  constructor(private sanitizer: DomSanitizer) { }
  //Dragover listener
  @HostListener('dragover', ['$event']) onDragOver(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#3f51b5';
    this.opacity = '0.4'
  }

  //Dragleave listener
  @HostListener('dragleave', ['$event']) public onDragLeave(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#f5fcff'
    this.opacity = '1'
  }

  //Drop listener
  @HostListener('drop', ['$event']) public ondrop(evt: DragEvent) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#f5fcff'
    this.opacity = '1'
    if (evt.dataTransfer && evt.dataTransfer.files[0]) {
      this.files.emit(evt.dataTransfer.files[0]);
    }
  }

}
