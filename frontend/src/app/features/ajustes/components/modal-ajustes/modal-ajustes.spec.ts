import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalAjustes } from './modal-ajustes';

describe('ModalAjustes', () => {
  let component: ModalAjustes;
  let fixture: ComponentFixture<ModalAjustes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalAjustes]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModalAjustes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
