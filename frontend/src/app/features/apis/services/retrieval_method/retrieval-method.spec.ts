import { TestBed } from '@angular/core/testing';

import { RetrievalMethodService } from './retrieval-method';

describe('RetrievalMethodService', () => {
  let service: RetrievalMethodService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RetrievalMethodService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
