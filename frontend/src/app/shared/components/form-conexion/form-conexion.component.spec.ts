import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormConexionComponent } from './form-conexion.component';

describe('FormConexionComponent', () => {
  let component: FormConexionComponent;
  let fixture: ComponentFixture<FormConexionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormConexionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormConexionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should normalize the selected motor to the option value', () => {
    component.conexionSeleccionada = {
      id: 1,
      nombre: 'DB Test',
      host: 'localhost',
      puerto: 5432,
      usuario: 'user',
      password: 'pass',
      motor: 'POSTGRESQL',
      crdate: '2024-01-01'
    };

    expect(component.conexionSeleccionada?.motor).toBe('postgresql');
    expect(component.tiposBasesDeDatosDisponibles.includes(component.conexionSeleccionada!.motor)).toBeTrue();
  });
});
