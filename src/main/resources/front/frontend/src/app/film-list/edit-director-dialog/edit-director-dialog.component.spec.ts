import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditDirectorDialogComponent } from './edit-director-dialog.component';

describe('EditDirectorDialogComponent', () => {
  let component: EditDirectorDialogComponent;
  let fixture: ComponentFixture<EditDirectorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditDirectorDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDirectorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
