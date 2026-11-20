import { TestBed } from '@angular/core/testing';

import { BaseDeDatosService } from './base_de_datos';

describe('BaseDeDatosService', () => {
  let service: BaseDeDatosService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BaseDeDatosService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
