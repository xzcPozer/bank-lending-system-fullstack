import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcessedCreditRequestsComponent } from './processed-credit-requests.component';

describe('ProcessedCreditRequestsComponent', () => {
  let component: ProcessedCreditRequestsComponent;
  let fixture: ComponentFixture<ProcessedCreditRequestsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProcessedCreditRequestsComponent]
    });
    fixture = TestBed.createComponent(ProcessedCreditRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
