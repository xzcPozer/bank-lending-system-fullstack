import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCreditRequestComponent } from './edit-credit-request.component';

describe('EditCreditRequestComponent', () => {
  let component: EditCreditRequestComponent;
  let fixture: ComponentFixture<EditCreditRequestComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EditCreditRequestComponent]
    });
    fixture = TestBed.createComponent(EditCreditRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
