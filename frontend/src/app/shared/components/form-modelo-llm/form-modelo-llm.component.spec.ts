import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormModeloLlmComponent } from './form-modelo-llm.component';

describe('FormModeloLlmComponent', () => {
  let component: FormModeloLlmComponent;
  let fixture: ComponentFixture<FormModeloLlmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormModeloLlmComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormModeloLlmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
