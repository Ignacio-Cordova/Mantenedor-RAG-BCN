import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatasetCard } from './dataset-card';

describe('DatasetCard', () => {
  let component: DatasetCard;
  let fixture: ComponentFixture<DatasetCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DatasetCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DatasetCard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
