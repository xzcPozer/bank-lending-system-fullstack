import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SolvencyConfirmationComponent } from './solvency-confirmation.component';

describe('SolvencyConfirmationComponent', () => {
  let component: SolvencyConfirmationComponent;
  let fixture: ComponentFixture<SolvencyConfirmationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SolvencyConfirmationComponent]
    });
    fixture = TestBed.createComponent(SolvencyConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
