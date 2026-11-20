import { Component, Input } from '@angular/core';
import { Conexion } from '../../../features/fuentes/models/conexion';
import { ConexionService } from '../../../features/fuentes/services/conexion/conexion.service';

@Component({
  selector: 'app-form-conexion',
  standalone: false,
  templateUrl: './form-conexion.component.html',
  styleUrl: './form-conexion.component.scss'
})
export class FormConexionComponent {
  @Input() set conexionSeleccionada(value: Conexion | null) {
    this._conexionSeleccionada = value ? { ...value, motor: value.motor?.toLowerCase() ?? '' } : null;
  }

  get conexionSeleccionada(): Conexion | null {
    return this._conexionSeleccionada;
  }

  private _conexionSeleccionada: Conexion | null = null;

  constructor (private conexionService: ConexionService) {}

  tiposBasesDeDatosDisponibles: string[] = ['postgresql'];

  crearConexion() {
    this.conexionService.getConexiones().subscribe(conexiones => {
      if (!this.conexionSeleccionada) {
        console.error('No hay conexión seleccionada para crear.');
        return;
      }
      const nuevaConexion: Conexion = {
        id: conexiones.length + 1,
        nombre: this.conexionSeleccionada.nombre,
        host: this.conexionSeleccionada.host,
        puerto: this.conexionSeleccionada.puerto,
        usuario: this.conexionSeleccionada.usuario,
        password: this.conexionSeleccionada.password,
        motor: this.conexionSeleccionada.motor,
        crdate: new Date().toISOString()
      };
      this.conexionService.addConexion(nuevaConexion).subscribe(() => {
      });
    });
  }
}
