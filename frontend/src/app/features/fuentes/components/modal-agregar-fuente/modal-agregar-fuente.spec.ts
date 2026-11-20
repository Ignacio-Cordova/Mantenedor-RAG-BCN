import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAgregarFuenteComponent } from './modal-agregar-fuente';

describe('ModalAgregarFuenteComponent', () => {
  let component: ModalAgregarFuenteComponent;
  let fixture: ComponentFixture<ModalAgregarFuenteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAgregarFuenteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalAgregarFuenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
