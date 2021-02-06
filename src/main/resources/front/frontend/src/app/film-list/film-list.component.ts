import {Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
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
import {Theme} from "../models/theme";

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
  filterBox=false;
  realisateurId: number | undefined;
  resultsLength: number | undefined;
  superThemes: Theme[] = [];
  themes: Theme[] = [];
  themesSelected: string[] = [];
  columnsToDisplay: string[] = ['titre', 'realisateurs', 'annee', 'pays'];
  dataSource = new MatTableDataSource<FilmListItem>();
  elements: FilmListItem[] = [];

  @ViewChild(MatSort, {static: false}) set content(sort: MatSort) {
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

    // check if request has director id
    route.params.pipe(
      takeWhile(() => this.hasSubscription)
    ).subscribe(val => {
      if (val.id) {
        this.realisateurId = val.id;
        this.initializeRealisateurs(val.id);
      } else {
        this.dataService.directorSubject.next(false);
        this.isLoggedIn = !!this.tokenStorageService.getToken();
        this.initializeFilms();
      }
    });
  }

  ngOnInit(): void {
    // set logout action
    this.isLoggedIn = !!this.tokenStorageService.getToken();
    this.authService.loggedIn.pipe(
      takeWhile(() => this.hasSubscription)
    ).subscribe(
      res => {
        this.isLoggedIn = res;
        const actionIsPresent = this.columnsToDisplay.includes('actions');
        if (!this.isLoggedIn && actionIsPresent) {
          this.columnsToDisplay.pop();
        }
      }
    )
    // retrieve theme list
    this.restService.getAllThemes().pipe(
      filter(() => !this.dataService.hasThemeList()),
      takeWhile(() => this.hasSubscription)
    ).subscribe(
      (res) => {
        this.dataService.cleanEmptyThemes(res);
        this.dataService.setThemeList(res);
        this.parseThemes(res);
      },
      error => {
        console.log(error);
      }
    )
  }

  /** INITIALIZE WHOLE FILM LIST */
  initializeFilms(): void {
    if (this.isLaunching) {
      return;
    }
    this.isLoadingResults = true;
    console.log("initialize lancé");
    this.restService.findAllDvd().pipe(
      takeWhile(() => this.hasSubscription),
      catchError(() => {
        this.isLoadingResults = false;
        return observableOf([]);
      })).subscribe(
      (res: FilmListItem[]) => {
        if (res) {
          this.checkIfListIsEmpty(res);
          this.transformPays(res);
          this.elements = res;
          this.dataSource = new MatTableDataSource(this.elements);
          this.isLoadingResults = false;
          this.resultsLength = res.length;
          this.dataService.setFilmList(this.elements);
          this.dataSource.sort = this.content || null;
          const sortState: Sort = {active: 'name', direction: 'desc'};
          if (this.content) {
            this.content.active = sortState.active;
            this.content.direction = sortState.direction;
            this.content.sortChange.emit(sortState);
          }
          this.dataSource.filterPredicate = this.getFilterPredicate();
          this.toggleActionColumn();
        }
      }
    )
  }
  /** INITIALIZE DIRECTOR'S FILM LIST */
  initializeRealisateurs(id: number): void {
    this.isLoadingResults = true;
    this.restService.findDvdByDirectorId(id).pipe(
      //filter(()=>!this.dataService.hasFilm()),
      takeWhile(() => this.hasSubscription),
      catchError(() => {
        this.isLoadingResults = false;
        return observableOf([]);
      })).subscribe(
      (res: FilmListItem[]) => {
        if (res) {
          this.checkIfListIsEmpty(res);
          this.transformPays(res);
          this.elements = res;
          this.dataSource = new MatTableDataSource(this.elements);
          this.isLoadingResults = false;
          this.resultsLength = res.length;
          this.dataSource.sort = this.content || null;
          const sortState: Sort = {active: 'name', direction: 'desc'};
          if (this.content) {
            this.content.active = sortState.active;
            this.content.direction = sortState.direction;
            this.content.sortChange.emit(sortState);
          }
          // informe que l'on est sur une liste de films d'un réalisateur
          this.dataSource.filterPredicate = this.getFilterPredicate();
          this.dataService.directorSubject.next(true);
        }
      }
    )
  }

  /** BUILD PAYS */
  transformPays(res: FilmListItem[]): void {
    for (const film of res) {
      if (film.pays && film.pays.split(';').length > 0) {
        film.pays = this.dataService.transformCodesToPaysLabels(film.pays);
      }
    }
  }
  /** BUILD CATEGORIES */
  parseThemes(res: Theme[]):void {
    for (const theme of res) {
      (theme.name=='iso' || theme.name=='rip') ? this.superThemes.push(theme): this.themes.push(theme);
    }
  }

  /** FILTERING */
  applyFilter(event: Event): void {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.resultsLength = this.dataSource.filteredData.length;
  }
  filter(event: any, theme: string):void{
    if (this.themesSelected.includes(theme)) {
      const index = this.themesSelected.indexOf(theme, 0);
      this.themesSelected.splice(index, 1);
    } else {
      this.themesSelected.push(theme);
    }
    // create string of our searching values and split if by '$'
    const filterValue = this.themesSelected.join('$');
    this.dataSource.filter = filterValue.trim().toLowerCase();
    this.resultsLength = this.dataSource.filteredData.length;
  }
  getFilterPredicate():(row: FilmListItem, filters: string)=>boolean{
    return (row: FilmListItem, filters: string)=>{
      if(this.filterBox){
        return row.titre.includes(filters) || row.realisateurs?.includes(filters) || row.pays?.includes(filters) || row.annee?.toString().includes(filters);
      }else{
        this.filterBox = false;
        const matchFilter = [];
        const filterArray = this.themesSelected;
        if (row.themes){
          const filters = row.themes;
          for (const thString of filterArray){
            const customFilter = filters.includes(thString);
            matchFilter.push(customFilter);
          }
        }
        return matchFilter.every(Boolean);
      }
    };
  }

  /** DIALOGS */
  openDisplayFilmDialog(id: number): void {
    const dialogRef = this.dialog.open(DisplayFilmDialogComponent, {
      height: '700px',
      width: '1000px',
      data: {
        idFilm: id,
        name: null
      }
    });
  }
  openDeleteConfirmDialog(element: FilmListItem): void {
    const dialogRef = this.dialog.open(DeleteFilmDialogComponent, {
      width: '600px',
      data: {
        idFilm: element.id,
        name: element.titre
      }
    });
  }

/** AUTH ACTIONS */
  toggleActionColumn(): void {
    if (this.isLoggedIn) {
      this.columnsToDisplay.push('actions');
    } else {
      const index = this.columnsToDisplay.indexOf('actions', 0);
      if (index >= 0) {
        this.columnsToDisplay.splice(index, 1);
      }
    }
  }
  logout(): void {
    this.tokenStorageService.signOut();
    window.location.reload();
  }

  /** NAVIGATION */
  openFilmByRealisateur(realisateur: string): void {
    this.restService.getDirectorIdByName(realisateur).pipe(
      takeWhile(() => this.hasSubscription)
    ).subscribe(
      res => {
        this.realisateurId = parseInt(res);
        this.router.navigate(['/director', parseInt(res)], {relativeTo: this.route}).then();
      },
      error => {
        this.dataService.openSnackBar("impossible de lire les données du réalisateur", "error");
      }
    )
  }
  editRealisateur(id: number): void {
    this.router.navigate(['/director/edit/', id], {relativeTo: this.route}).then();
  }
  editFilm(id: number): void {
    this.router.navigate(['/edit/', id], {relativeTo: this.route}).then();
  }

  /** ALERT */
  checkIfListIsEmpty(res: FilmListItem[]):void{
    if (res.length === 0) {
      this.dataService.openSnackBar("aucun film", "info");
    }
  }

  ngOnDestroy(): void {
    this.hasSubscription = false;
  }


}
