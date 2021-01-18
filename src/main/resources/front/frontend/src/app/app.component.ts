import {Component, OnDestroy, OnInit} from '@angular/core';
import {TokenStorageService} from "./services/token-storage.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "./services/auth.service";
import {filter, takeWhile} from "rxjs/operators";
import {DataService} from "./services/data.service";
import {RestService} from "./services/rest.service";
import {MatDialog} from "@angular/material/dialog";
import {EditDirectorDialogComponent} from "./film-list/edit-director-dialog/edit-director-dialog.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy{
  isLoggedIn = false;
  isDirectorList = false;
  showAdminArea = false;
  hasSubscription = false;
  directorName: string |undefined;
  username: string = "";
  roles: string[] = [];
  title = 'frontend';
  constructor(
    private tokenStorageService: TokenStorageService,
    private dataService: DataService,
    private restService: RestService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
    public dialog: MatDialog) {
    this.hasSubscription = true;
  }

  ngOnInit():void{
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    if (this.isLoggedIn) {
      const user = this.tokenStorageService.getUser();
      this.roles = user.roles;
      this.showAdminArea = this.roles.includes('ROLE_ADMIN');

      this.username = user.username;
    }
    this.authService.loggedIn.pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.isLoggedIn = res;
      }
    )
    this.restService.getImageDirectory().pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      (res: any)=>{
        this.dataService.imageDirectory = res.message;
        console.log("répertoire des images : " + this.dataService.imageDirectory);
      }
    )
    this.dataService.directorSubject.pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.isDirectorList = res;
      }
    )
  }

  home():void{
    this.router.navigate([''], {relativeTo: this.route}).then(r =>
      ()=>{
        console.log("login");
      }) ;
  }
  admin():void{
    this.router.navigate(['/admin'], {relativeTo: this.route}).then(r =>
      ()=>{
        console.log("import");
      }) ;
  }
  login():void{
    this.router.navigate(['/login'], {relativeTo: this.route}).then(r =>
      ()=>{
      console.log("login");
      }) ;
  }
  logout():void {
    this.tokenStorageService.signOut();
    this.authService.loggedIn.next(false);
    this.router.navigate([''], {relativeTo: this.route}).then(r =>
      ()=>{
        console.log("logout");
      }) ;
  }
  addFilm():void{
    this.router.navigate(['/add'], {relativeTo: this.route}).then(r =>
      ()=>{
        console.log("ajout de film");
      }) ;
  }
  getDirectorNameOpenDialog():void {
    const id = this.getId();
    this.restService.getDirectorNamebyId(id).pipe(
      takeWhile(() => this.hasSubscription)
    ).subscribe(
      (res:any) => {
        this.directorName = res.message;
        this.openDialog(id);
      },
      error => {
        this.dataService.openSnackBar("impossible de récupérer le nom du réalisateur", "error");
      }
    )
  }

  openDialog(id: number):void{
    const dialogRef = this.dialog.open(EditDirectorDialogComponent, {
      width: '800px',
      data: {idFilm: id, name: this.directorName}
    });
    dialogRef.afterClosed().pipe(
      filter(x=>x!==null && x!==undefined)
    ).subscribe(result => {
      console.log('The dialog was closed');
      this.updateDirector(id,result)
    });
  }

  updateDirector(id: number, name: string):void{
    this.restService.updateDirector(id,name).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        if (res){
          window.location.reload();
        }
      },
      error => {
        this.dataService.openSnackBar("impossible de modifier le nom du réalisateur","error");
      }
    )
  }



  getId(): number{
    const url = window.location.href.split('/');
    return parseInt(url[url.length-1]);
  }

  ngOnDestroy():void {
    this.hasSubscription = false;
  }
}
