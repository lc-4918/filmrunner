import { Component, OnInit } from '@angular/core';
import {ThemeDialog} from "./theme-dialog/theme-dialog.component";
import {MatDialog} from "@angular/material/dialog";

@Component({
  selector: 'app-theme-list',
  templateUrl: './theme-list.component.html',
  styleUrls: ['./theme-list.component.css']
})
export class ThemeListComponent implements OnInit {

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }
  open():void{
    this.dialog.open(ThemeDialog, {
    });
  }
}
