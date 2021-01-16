import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../services/rest.service";
import {takeWhile} from "rxjs/operators";
import {ActivatedRoute, Router} from "@angular/router";
import {DataService} from "../../services/data.service";

export interface DialogData {
  idFilm: number;
  name: string;
}

@Component({
  selector: 'app-delete-film-dialog',
  templateUrl: './delete-film-dialog.component.html',
  styleUrls: ['./delete-film-dialog.component.css']
})
export class DeleteFilmDialogComponent implements OnInit, OnDestroy {
hasSubscription = false;
  constructor(
    public dialogRef: MatDialogRef<DeleteFilmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private restService: RestService,
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService) {
    this.hasSubscription = true;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

  confirmDeleteFilm(){
    this.restService.deleteDvd(this.data.idFilm).pipe(
      takeWhile(()=>this.hasSubscription)
    ).subscribe(
      res=>{
        this.dialogRef.close();
        this.router.navigate([''], { relativeTo: this.route }).then();
        if (res){
          this.dataService.openSnackBar("film \"" + this.data.name + "\" supprimé!","success");
        }
      },
      error => {
        console.log("impossible de supprimer le film : "+error);
      }
    )
  }

  ngOnDestroy() {
    this.hasSubscription = false;
  }

}
