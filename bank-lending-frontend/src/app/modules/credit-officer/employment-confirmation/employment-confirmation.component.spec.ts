import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmploymentConfirmationComponent } from './employment-confirmation.component';

describe('EmploymentConfirmationComponent', () => {
  let component: EmploymentConfirmationComponent;
  let fixture: ComponentFixture<EmploymentConfirmationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EmploymentConfirmationComponent]
    });
    fixture = TestBed.createComponent(EmploymentConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
