import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ThemeDialog } from './theme-dialog.component';

describe('ThemeDialogComponent', () => {
  let component: ThemeDialog;
  let fixture: ComponentFixture<ThemeDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThemeDialog ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThemeDialog);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
