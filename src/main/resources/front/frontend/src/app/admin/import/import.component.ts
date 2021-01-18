import {Component, OnDestroy, OnInit} from '@angular/core';
import {RestService} from "../../services/rest.service";
import {filter, takeWhile} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-import',
  templateUrl: './import.component.html',
  styleUrls: ['./import.component.css']
})
export class ImportComponent implements OnInit, OnDestroy {
  importedTitle: string|undefined;
  linesImported: number = 0;
  messageResult: String | undefined;
  isSucceed: boolean | undefined;
  isProcessing = false;
  hasSubscription = false;
  interval: any|undefined;
  file: File | undefined;
  isNotExcel: boolean | undefined;
  hasExcelFile: boolean | undefined;

  constructor(private restService: RestService,private router: Router,private route: ActivatedRoute) {
    this.hasSubscription = true;
  }

  uploadFile(event: any):void {
    this.isNotExcel = false;
    if (event instanceof File && event.type =='application/vnd.ms-excel'){
        this.file = event;
    }else{
      this.isNotExcel = true;
    }
  }

  deleteAttachment():void {
    this.file = undefined;
  }
  ngOnInit(): void {
  }
  import():void{
    this.hasExcelFile = true;
    this.isProcessing= true;
    this.getCounterByPeriod();
    this.restService.executeimport(this.file).pipe(
      filter(()=>this.file!=null)
    ).subscribe(
      (res: any)=>{
        this.isSucceed = res.status;
        this.messageResult = res.message;
        clearInterval(this.interval);
        this.isProcessing= false;
      },
      error => {
        this.isProcessing= false;
      }
    )
  }

  getCounterByPeriod():void {
    this.interval = setInterval(()=>this.getCounter(),10);
  }

  getCounter():void{
    this.restService.getImportedLineCount().pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.linesImported = res.count;
        this.importedTitle = res.message;
      }
    )
  }

  return():void{
    this.router.navigate([''], { relativeTo: this.route }).then();
  }


  ngOnDestroy():void {
    this.hasSubscription = false;

  }
}
