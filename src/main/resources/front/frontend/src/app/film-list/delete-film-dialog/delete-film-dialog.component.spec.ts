import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteFilmDialogComponent } from './delete-film-dialog.component';

describe('DeleteFilmDialogComponent', () => {
  let component: DeleteFilmDialogComponent;
  let fixture: ComponentFixture<DeleteFilmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeleteFilmDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteFilmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
