import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAjustesGlobales } from './modal-ajustes-globales';

describe('ModalAjustesGlobales', () => {
  let component: ModalAjustesGlobales;
  let fixture: ComponentFixture<ModalAjustesGlobales>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAjustesGlobales]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalAjustesGlobales);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
