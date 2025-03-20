import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditQueryConfirmationComponent } from './credit-query-confirmation.component';

describe('CreditQueryConfirmationComponent', () => {
  let component: CreditQueryConfirmationComponent;
  let fixture: ComponentFixture<CreditQueryConfirmationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreditQueryConfirmationComponent]
    });
    fixture = TestBed.createComponent(CreditQueryConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
