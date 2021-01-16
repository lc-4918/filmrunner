import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DvdformComponent } from './dvdform.component';

describe('DvdformComponent', () => {
  let component: DvdformComponent;
  let fixture: ComponentFixture<DvdformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DvdformComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DvdformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
