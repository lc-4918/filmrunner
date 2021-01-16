import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ChipformComponent } from './dvd-form/chip-form/chipform.component';
import {MaterialModule} from "./material/material.module";
import {ReactiveFormsModule} from "@angular/forms";
import {FormsModule} from "@angular/forms";
import { DvdformComponent } from './dvd-form/dvdform.component';
import { NumericDirective } from './numeric.directive';
import { ThemeListComponent } from './theme-list/theme-list.component';
import { ThemeDialog } from './theme-list/theme-dialog/theme-dialog.component';
import {MAT_DIALOG_DEFAULT_OPTIONS} from "@angular/material/dialog";
import { FilmListComponent } from './film-list/film-list.component';
import { LoginComponent } from './auth/login-component/login-component.component';
import { RegisterComponent } from './auth/register/register.component';
import {authInterceptorProviders} from "./helpers/auth.interceptor";
import { DeleteFilmDialogComponent } from './film-list/delete-film-dialog/delete-film-dialog.component';
import { DisplayFilmDialogComponent } from './film-list/display-film-dialog/display-film-dialog.component';
import { EditDirectorDialogComponent } from './film-list/edit-director-dialog/edit-director-dialog.component';
import { ImportComponent } from './import/import.component';
import { DragDropDirective } from './directives/drag-drop.directive';

@NgModule({
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MaterialModule,
    ReactiveFormsModule,
    FormsModule

  ],
  declarations: [
    AppComponent,
    ChipformComponent,
    DvdformComponent,
    NumericDirective,
    ThemeListComponent,
    ThemeDialog,
    FilmListComponent,
    LoginComponent,
    RegisterComponent,
    DeleteFilmDialogComponent,
    DisplayFilmDialogComponent,
    EditDirectorDialogComponent,
    ImportComponent,
    DragDropDirective
  ],
  providers: [
    {provide: MAT_DIALOG_DEFAULT_OPTIONS, useValue: {hasBackdrop: false}},
    authInterceptorProviders,
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
