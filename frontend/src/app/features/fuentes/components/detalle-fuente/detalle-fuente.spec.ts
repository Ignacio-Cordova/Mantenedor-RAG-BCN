import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleFuenteComponent } from './detalle-fuente';

describe('DetalleFuenteComponent', () => {
  let component: DetalleFuenteComponent;
  let fixture: ComponentFixture<DetalleFuenteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleFuenteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleFuenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
