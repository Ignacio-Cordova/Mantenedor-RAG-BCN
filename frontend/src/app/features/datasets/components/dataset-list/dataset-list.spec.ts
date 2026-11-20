import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatasetList } from './dataset-list';

describe('DatasetList', () => {
  let component: DatasetList;
  let fixture: ComponentFixture<DatasetList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DatasetList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DatasetList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
