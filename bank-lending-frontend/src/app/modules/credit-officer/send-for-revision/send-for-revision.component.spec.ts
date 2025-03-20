import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SendForRevisionComponent } from './send-for-revision.component';

describe('SendForRevisionComponent', () => {
  let component: SendForRevisionComponent;
  let fixture: ComponentFixture<SendForRevisionComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SendForRevisionComponent]
    });
    fixture = TestBed.createComponent(SendForRevisionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
