import {Component, OnDestroy, OnInit} from '@angular/core';
import {RestService} from "../../services/rest.service";
import {filter, takeWhile} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";
import {DataService} from "../../services/data.service";

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

  constructor(private restService: RestService,private router: Router,private route: ActivatedRoute, private dataService: DataService) {
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
        clearInterval(this.interval);
        this.dataService.openSnackBar("impossible d'importer le fichier excel","error");
      }
    )
  }

  getCounterByPeriod():void {
    this.interval = setInterval(()=>this.getCounter(),10);
  }

  getCounter():void{
    this.restService.getImportedLineCount().pipe(
      takeWhile(()=>this.hasSubscription && this.isProcessing)
    ).subscribe(
      res=>{
        if (res){
          if (res.count){
            this.linesImported = res.count;
          }
          if (res.message){
            this.importedTitle = res.message;
          }
        }
      },
      (error)=>{
        clearInterval(this.interval);
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
