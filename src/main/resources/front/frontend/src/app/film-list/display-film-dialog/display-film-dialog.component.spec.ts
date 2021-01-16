import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayFilmDialogComponent } from './display-film-dialog.component';

describe('DisplayFilmDialogComponent', () => {
  let component: DisplayFilmDialogComponent;
  let fixture: ComponentFixture<DisplayFilmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DisplayFilmDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DisplayFilmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
