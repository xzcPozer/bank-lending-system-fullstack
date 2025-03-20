import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RefuseComponent } from './refuse.component';

describe('RefuseComponent', () => {
  let component: RefuseComponent;
  let fixture: ComponentFixture<RefuseComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RefuseComponent]
    });
    fixture = TestBed.createComponent(RefuseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
