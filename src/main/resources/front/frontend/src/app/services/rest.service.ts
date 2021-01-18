import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {DvdForm} from '../models/dvd-form';
import {FilmListItem} from '../models/film-list-item';
import {environment} from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})

export class RestService {
  httpHeader = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
  httpHeaderXls = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }

  baseUrl = environment.baseUrl;

  constructor(private  http: HttpClient) {
    console.log(this.baseUrl);
  }

  // LOGIN
  login(username: string, password: string): Observable<boolean>{
    return this.http.post<boolean>(this.baseUrl+'auth/login', {
      username: username,
      password: password
    });
  }

  // DVD MANAGEMENT
  findDvdById(id: number): Observable<DvdForm>{
    return this.http.get<DvdForm>(this.baseUrl+'dvd/'+id);
  }
  findAllDvd(): Observable<FilmListItem[]>{
    return this.http.get<FilmListItem[]>(this.baseUrl+'dvd');
  }
  createDvd(dvdForm: DvdForm): Observable<FilmListItem>{
    return this.http.put<FilmListItem>(this.baseUrl+'dvd', dvdForm );
  }
  updateDvd(dvdForm: DvdForm): Observable<FilmListItem>{
    return this.http.put<FilmListItem>(this.baseUrl+'dvd/'+dvdForm.id, dvdForm );
  }
  deleteDvd(dvdId: number): Observable<boolean>{
    return this.http.delete<boolean>(this.baseUrl+'dvd/'+dvdId);
  }


  // DIRECTORS MANAGEMENT
  getAllDirectorsName(): Observable<string[]>{
    return this.http.get<string[]>(this.baseUrl+'director');
  }
  findDvdByDirectorId(directorId: number):Observable<FilmListItem[]>{
    return this.http.get<FilmListItem[]>(this.baseUrl+'director/'+directorId);
  }
  getDirectorIdByName(name: string): Observable<string>{
    const params = new HttpParams().set('name',name);
    return this.http.get<string>(this.baseUrl+'director/',{params: params});
  }
  getDirectorNamebyId(id: number): Observable<string>{
    return this.http.get<string>(this.baseUrl+'director/id/'+id);
  }
  updateDirector(id: number, name: string): Observable<boolean>{
    return this.http.patch<boolean>(this.baseUrl+'director/'+id,name);
  }


  // THEME MANAGEMENT
  getAllThemesName(): Observable<string[]>{
    return this.http.get<string[]>(this.baseUrl+'theme');
  }
  createTheme(name: string):Observable<string>{
    return this.http.put<string>(this.baseUrl+'theme', name)
  }
  updateTheme(oldValue: string, newValue: string):Observable<string>{
    return this.http.put<string>(this.baseUrl+'theme/'+oldValue,newValue);
  }
  deleteTheme(name: string):Observable<boolean>{
    return this.http.delete<boolean>(this.baseUrl+'theme/'+name);
  }


  // IMAGE MANAGEMENT
  upload(file: File): Promise<any> {
    const formData: FormData = new FormData();
    formData.append('file', file);
    return this.http.post(this.baseUrl+'uploadFile',formData).toPromise();
  }
  getFiles(): Observable<any> {
    return this.http.get(this.baseUrl+'files');
  }
  deleteImage(dvdId: number):Observable<boolean>{
    return this.http.get<boolean>(this.baseUrl+'image/'+dvdId);
  }

  // IMPORT
  getImportedLineCount(): Observable<any>{
    return this.http.get<any>(this.baseUrl+'import/count')
  }
  executeimport(file: File | undefined): Observable<any>{
    const formData: FormData = new FormData();
    if (file){
      formData.append('file', file);
    }
    return this.http.post<any>(this.baseUrl+"import/execute", formData);
  }

  // RESET
  cleanDb():Observable<any>{
    return this.http.get<any>(this.baseUrl+"reset/cleandb");
  }

  // DOWNLOAD DB
  downloadDb():Observable<any>{
   return this.http.request("GET",this.baseUrl+"db/download",{ responseType: 'blob' })
  }

  // CONTEXT
  getImageDirectory(): Observable<any>{
    return this.http.get<any>(this.baseUrl+'context');
  }

  private handleError = (error: HttpErrorResponse, method: string, object: any) => {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
    }
    // Return an observable with a user-facing error message.
    return throwError(
      'Something bad happened; please try again later.');
  };
}
