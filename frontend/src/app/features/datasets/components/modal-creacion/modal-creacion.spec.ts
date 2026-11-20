import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalCreacion } from './modal-creacion';

describe('ModalCreacion', () => {
  let component: ModalCreacion;
  let fixture: ComponentFixture<ModalCreacion>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalCreacion]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalCreacion);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
