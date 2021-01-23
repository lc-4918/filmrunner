import {Component, OnDestroy, OnInit} from '@angular/core';
import {RestService} from "../../services/rest.service";
import {takeWhile} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-reset',
  templateUrl: './reset.component.html',
  styleUrls: ['./reset.component.css']
})
export class ResetComponent implements OnInit, OnDestroy {
  toClean: boolean | undefined;
  hasSubscription: boolean | undefined;
  hasSucceed: boolean | undefined;
  messageResponse: string | undefined;
  constructor(private restService : RestService, private router : Router, private route : ActivatedRoute) {
    this.hasSubscription = true;
    this.toClean = false;
  }

  ngOnInit(): void {
  }

  resetAll():void{
    this.restService.cleanDb().pipe(
      // @ts-ignore
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        if (res!=null){
          this.hasSucceed = res.status;
          this.messageResponse = res.message;
        }
      }
    )
  }

  return():void{
    this.router.navigate([''], { relativeTo: this.route }).then();
  }

  ngOnDestroy():void{
    this.hasSubscription = false;
  }
}
