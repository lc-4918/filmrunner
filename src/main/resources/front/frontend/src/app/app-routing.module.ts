import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {DvdformComponent} from "./dvd-form/dvdform.component";
import {FilmListComponent} from "./film-list/film-list.component";
import {LoginComponent} from "./auth/login-component/login-component.component";
import {RegisterComponent} from "./auth/register/register.component";
import {ImportComponent} from "./import/import.component";

const routes: Routes = [
  { path: '', component: FilmListComponent },
  { path: 'add', component: DvdformComponent },
  { path: 'edit/:id', component: DvdformComponent },
  { path: 'login', component: LoginComponent },
  { path: 'director/:id', component: FilmListComponent},
  { path: 'register', component: RegisterComponent },
  { path: 'import', component: ImportComponent },
  { path: '', redirectTo: '', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
