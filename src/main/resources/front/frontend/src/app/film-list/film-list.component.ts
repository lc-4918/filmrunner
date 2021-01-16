import {
  Component,
  ElementRef,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import {MatTableDataSource} from "@angular/material/table";
import {RestService} from "../services/rest.service";
import {DataService} from "../services/data.service";
import {FilmListItem} from "../models/film-list-item";
import {of as observableOf} from "rxjs";
import {catchError, filter, takeWhile} from "rxjs/operators";
import {MatSort, Sort} from "@angular/material/sort";
import {ActivatedRoute, Router} from "@angular/router";
import {TokenStorageService} from "../services/token-storage.service";
import {AuthService} from "../services/auth.service";
import {MatDialog} from "@angular/material/dialog";
import {DeleteFilmDialogComponent} from "./delete-film-dialog/delete-film-dialog.component";
import {DisplayFilmDialogComponent} from "./display-film-dialog/display-film-dialog.component";

@Component({
  selector: 'app-film-list',
  templateUrl: './film-list.component.html',
  styleUrls: ['./film-list.component.css']
})

export class FilmListComponent implements OnInit, OnDestroy {
  hasSubscription = false;
  isLoadingResults = true;
  isLaunching = false;
  isLoggedIn = false;
  realisateurId: number | undefined;
  resultsLength: number | undefined;
  themes: string[] |undefined;
  themesSelected:string[] = [];
  columnsToDisplay: string[] = ['titre', 'realisateurs', 'annee', 'pays'];
  dataSource = new MatTableDataSource<FilmListItem>();
  elements: FilmListItem[] = [];
  @ViewChild(MatSort,{static:false})set content(sort: MatSort) {
    this.dataSource.sort = sort;
  }
  encapsulation: ViewEncapsulation.None | undefined

  constructor(
    private restService: RestService,
    private dataService: DataService,
    private authService: AuthService,
    private router: Router,
    private tokenStorageService: TokenStorageService,
    private route: ActivatedRoute,
    public dialog: MatDialog) {
    this.hasSubscription = true;
    route.params.subscribe(val => {
      if (val.id){
        this.realisateurId = val.id;
        this.initializeRealisateurs(val.id);
      }else {
        this.dataService.directorSubject.next(false);
        this.isLoggedIn = !!this.tokenStorageService.getToken();
        this.initializeFilms();
      }
    });
  }

  ngOnInit() {
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    // this.initializeFilms();
    this.authService.loggedIn.pipe().subscribe(
      res=>{
        this.isLoggedIn = res;
        const actionIsPresent =this.columnsToDisplay.includes('actions');
        if (!this.isLoggedIn && actionIsPresent){
          this.columnsToDisplay.pop();
        }
      }
    )
    this.restService.getAllThemesName().pipe(
      filter(()=>!this.dataService.hasThemeList()),
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        const index = res.indexOf("",0);
        if (index>=0){
          res.splice(index,1);
        }
        this.dataService.setThemeList(res);
        this.themes = res;
      }
    )
    this.themes = this.dataService.getThemeList();
  }

  initializeFilms(){
    if (this.isLaunching){
      return;
    }
    this.isLoadingResults = true;
    console.log("initialize lancé");
    this.restService.findAllDvd().pipe(
      //filter(()=>!this.dataService.hasFilm()),
      takeWhile(()=>this.hasSubscription),
      catchError(() => {
        this.isLoadingResults = false;
        return observableOf([]);
      })).subscribe(
      (res: FilmListItem[])=>{
        if (res){
          if (res.length===0){
            this.dataService.openSnackBar("aucun film","info");
          }
          this.transformPays(res);
          this.elements = res;
          this.dataSource = new MatTableDataSource(this.elements);
          this.isLoadingResults = false;
          this.resultsLength = res.length;
          this.dataService.setFilmList(this.elements);
          this.dataSource.sort = this.content || null;
          const sortState: Sort = {active: 'name', direction: 'desc'};
          if (this.content){
            this.content.active = sortState.active;
            this.content.direction = sortState.direction;
            this.content.sortChange.emit(sortState);
          }
          this.toggleActionColumn();
        }
      }
    )
  }
  initializeRealisateurs(id: number){
    this.isLoadingResults = true;
    this.restService.findDvdByDirectorId(id).pipe(
      //filter(()=>!this.dataService.hasFilm()),
      takeWhile(()=>this.hasSubscription),
      catchError(() => {
        this.isLoadingResults = false;
        return observableOf([]);
      })).subscribe(
      (res: FilmListItem[])=>{
        if (res){
          if (res.length===0){
            this.dataService.openSnackBar("aucun film","info");
          }
          this.transformPays(res);
          this.elements = res;
          this.dataSource = new MatTableDataSource(this.elements);
          this.isLoadingResults = false;
          this.resultsLength = res.length;
          this.dataSource.sort = this.content || null;
          const sortState: Sort = {active: 'name', direction: 'desc'};
          if (this.content){
            this.content.active = sortState.active;
            this.content.direction = sortState.direction;
            this.content.sortChange.emit(sortState);
          }
          // informe que l'on est sur une liste de films d'un réalisateur
          this.dataService.directorSubject.next(true);
        }
      }
    )
  }

  toggleActionColumn(){
    if (this.isLoggedIn){
      this.columnsToDisplay.push('actions');
    }else{
      const index = this.columnsToDisplay.indexOf('actions',0);
      if (index>=0){
        this.columnsToDisplay.splice(index,1);
      }
    }
  }
  transformPays(res: FilmListItem[]):void{
    for (const film of res){
      if (film.pays && film.pays.split(';').length>0){
        film.pays = this.dataService.transformCodesToPaysLabels(film.pays);
      }
    }
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.resultsLength = this.dataSource.filteredData.length ;
  }

  openFilmByRealisateur(realisateur: string):void{
    this.restService.getDirectorIdByName(realisateur).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res =>{
        this.realisateurId = parseInt(res);
        this.router.navigate(['/director',parseInt(res)], { relativeTo: this.route }).then();
      },
      error => {
        this.dataService.openSnackBar("impossible de lire les données du réalisateur","error");
      }
    )
  }

  filterByTheme(event: any, theme: string){
    if (this.themesSelected.includes(theme)){
      const index = this.themesSelected.indexOf(theme,0);
      this.themesSelected.splice(index,1);
      if (this.themesSelected.length==0){
        this.dataSource.filter = '';
        this.dataSource.data = this.elements;
        this.resultsLength = this.dataSource.data.length;
        return;
      }
    }else{
      this.themesSelected.push(theme);
    }
    let setFilms = new Set<FilmListItem>();
    for (const theme of this.themesSelected){
      const films: FilmListItem[] = this.dataService.getFilmList().filter(x=>x.themes?.includes(theme));
      for (const film of films){
        setFilms.add(film);
      }
    }
    const filmsFiltered = Array.from(setFilms);
    this.dataSource.data = filmsFiltered;
    this.resultsLength = this.dataSource.data.length;
  }

  openDisplayFilmDialog(id: number): void {
    const dialogRef = this.dialog.open(DisplayFilmDialogComponent, {
      width: '600px',
      data: {idFilm: id,
        name: null}
    });
  }

  openDeleteConfirmDialog(element: FilmListItem): void {
    const dialogRef = this.dialog.open(DeleteFilmDialogComponent, {
      width: '600px',
      data: {idFilm: element.id,
        name: element.titre}
    });
  }


  editRealisateur(id: number){
    this.router.navigate(['/director/edit/',id], { relativeTo: this.route }).then();
  }
  editFilm(id: number){
    this.router.navigate(['/edit/',id], { relativeTo: this.route }).then();
  }

  logout() {
    this.tokenStorageService.signOut();
    window.location.reload();
  }
  ngOnDestroy() {
    this.hasSubscription = false;
  }
}
