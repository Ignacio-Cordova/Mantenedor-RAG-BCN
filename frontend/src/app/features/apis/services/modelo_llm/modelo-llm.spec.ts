import { TestBed } from '@angular/core/testing';

import { ModeloLlmService } from './modelo-llm';

describe('ModeloLlmService', () => {
  let service: ModeloLlmService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModeloLlmService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
