import {Component, OnDestroy, OnInit} from '@angular/core';
import {DomSanitizer} from "@angular/platform-browser";
import {RestService} from "../../services/rest.service";
import {takeWhile} from "rxjs/operators";

@Component({
  selector: 'app-export',
  templateUrl: './export.component.html',
  styleUrls: ['./export.component.css']
})
export class ExportComponent implements OnInit, OnDestroy {
  hasSubscription: boolean;
  fileUrl: any;
  constructor(private sanitizer: DomSanitizer, private restService : RestService) {
    this.hasSubscription = true;
  }

  ngOnInit(): void {
    this.restService.downloadDb().pipe(
      takeWhile(()=> this.hasSubscription)
    ).subscribe(
      res=>{
        const blob = new Blob([res]);
        this.fileUrl = this.sanitizer.bypassSecurityTrustResourceUrl(window.URL.createObjectURL(blob))
      }
    )
  }

  ngOnDestroy():void {
    this.hasSubscription = false;
  }

}
