import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckCreditRequestComponent } from './check-credit-request.component';

describe('CheckCreditRequestComponent', () => {
  let component: CheckCreditRequestComponent;
  let fixture: ComponentFixture<CheckCreditRequestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CheckCreditRequestComponent]
    });
    fixture = TestBed.createComponent(CheckCreditRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
