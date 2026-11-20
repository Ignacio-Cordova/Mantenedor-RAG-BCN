import { TestBed } from '@angular/core/testing';

import { ModeloEmbeddingService } from './modelo-embedding';

describe('ModeloEmbedding', () => {
  let service: ModeloEmbeddingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModeloEmbeddingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
