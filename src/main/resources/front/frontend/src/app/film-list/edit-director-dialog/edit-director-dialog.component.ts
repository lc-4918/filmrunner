import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DialogData} from "../delete-film-dialog/delete-film-dialog.component";

@Component({
  selector: 'app-edit-director-dialog',
  templateUrl: './edit-director-dialog.component.html',
  styleUrls: ['./edit-director-dialog.component.css']
})
export class EditDirectorDialogComponent implements OnInit, OnDestroy {
hasSubscription = false;
directorName: string | undefined;
  constructor(public dialogRef: MatDialogRef<EditDirectorDialogComponent>,
  @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
  ngOnDestroy():void {
    this.hasSubscription = false;
  }
}
