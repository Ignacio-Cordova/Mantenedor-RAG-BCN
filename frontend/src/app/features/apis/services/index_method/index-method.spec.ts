import { TestBed } from '@angular/core/testing';

import { IndexMethodService } from './index-method';

describe('IndexMethodService', () => {
  let service: IndexMethodService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IndexMethodService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
