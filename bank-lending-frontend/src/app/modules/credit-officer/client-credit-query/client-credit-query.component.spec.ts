import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientCreditQueryComponent } from './client-credit-query.component';

describe('ClientCreditQueryComponent', () => {
  let component: ClientCreditQueryComponent;
  let fixture: ComponentFixture<ClientCreditQueryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ClientCreditQueryComponent]
    });
    fixture = TestBed.createComponent(ClientCreditQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
