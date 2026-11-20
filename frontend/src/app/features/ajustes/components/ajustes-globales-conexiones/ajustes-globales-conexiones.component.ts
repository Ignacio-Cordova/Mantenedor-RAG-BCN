import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Conexion } from '../../../fuentes/models/conexion'; 
import { ConexionService } from '../../../fuentes/services/conexion/conexion.service'; 

@Component({
  selector: 'app-ajustes-globales-conexiones',
  templateUrl: './ajustes-globales-conexiones.component.html',
  styleUrls: ['./ajustes-globales-conexiones.component.scss'],
  standalone: false
})
export class AjustesGlobalesConexionesComponent implements OnInit {
  
  @Output() eliminarConexion = new EventEmitter<Conexion>();
  conexiones: Conexion[] = [];
  columnas: string[] = ['nombre', 'host', 'acciones'];
  pageIndex: number = 0;
  pageSize: number = 5;
  total: number = 0;
  mostrarModalFormulario: boolean = false;
  conexionSeleccionada: Conexion = {
    id: 0,
    nombre: '',
    host: '',
    puerto: 0,
    usuario: '',
    password: '',
    motor: '',
    crdate: ''
  };
  constructor(private conexionService: ConexionService) {}

  ngOnInit() {
    this.cargarConexiones();
  }

  cargarConexiones() {
    this.conexionService.getConexionesPaginadas(this.pageIndex, this.pageSize)
      .subscribe({
        next: (response: { data: Conexion[]; total: number }) => {
          this.conexiones = [...response.data];
          this.total = response.total;
        },
        error: (err) => console.error('Error cargando conexiones:', err)
      });
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.cargarConexiones();
  }

  abrirModalConexion(conexionExistente?: Conexion) {
    if (conexionExistente) {
      this.conexionSeleccionada = { ...conexionExistente };
      this.mostrarModalFormulario = true;
    } else {
      this.conexionSeleccionada = {
        id: 0,
        nombre: '',
        host: '',
        puerto: 0,
        usuario: '',
        password: '',
        motor: '',
        crdate: ''
      };
    }
    this.mostrarModalFormulario = true;
  }

  onEliminar(conexion: Conexion) {
    this.eliminarConexion.emit(conexion);
  }

  onGuardarCambios(conexionModificada: Conexion) {
    if (conexionModificada.id !== 0) {
      this.conexionService.updateConexion(conexionModificada).subscribe({
        next: (Response: any) => {
          if (Response && Response.error) {
            console.error('Error al actualizar la conexión:', Response.error);
            return;
          }
          this.cargarConexiones();
          this.mostrarModalFormulario = false;
          this.conexionSeleccionada = {
            id: 0,
            nombre: '',
            host: '',
            puerto: 0,
            usuario: '',
            password: '',
            motor: '',
            crdate: ''
          };
        }
        ,
        error: (error: any) => console.error('Error al guardar cambios:', error)
      });
    }
    else{
      this.conexionService.addConexion(conexionModificada).subscribe({
        next: () => {
          this.cargarConexiones();
          this.mostrarModalFormulario = false;
          this.conexionSeleccionada = {
            id: 0,
            nombre: '',
            host: '',
            puerto: 0,
            usuario: '',
            password: '',
            motor: '',
            crdate: ''
          };
        },
        error: (error: any) => console.error('Error al crear la conexión:', error)
      });
    }
  }

  recargarTabla() {
    this.mostrarModalFormulario = false;
    this.conexionSeleccionada = {
      id: 0,
      nombre: '',
      host: '',
      puerto: 0,
      usuario: '',
      password: '',
      motor: '',
      crdate: ''
    };
  }
}