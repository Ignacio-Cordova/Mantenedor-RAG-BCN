import { TestBed } from '@angular/core/testing';

import { ServicioWebService } from './servicio-web.service';

describe('ServicioWebService', () => {
  let service: ServicioWebService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServicioWebService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
