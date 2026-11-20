import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { FuenteService } from '../../services/fuente/fuente';
import { ArchivoService } from '../../services/archivo/archivo';
import { BaseDeDatos } from '../../models/base_de_datos';
import { BaseDeDatosService } from '../../services/base_de_datos/base_de_datos';
import { Conexion } from '../../models/conexion';
import { ConexionService } from '../../services/conexion/conexion.service';
import { ServicioWeb } from '../../models/servicio_web';
import { ServicioWebService } from '../../services/servicio_web/servicio-web.service';

@Component({
  selector: 'app-modal-agregar-fuente',
  standalone: false,
  templateUrl: './modal-agregar-fuente.html',
  styleUrl: './modal-agregar-fuente.scss'
})
export class ModalAgregarFuenteComponent implements OnInit {
  @Output() cerrarModal: EventEmitter<void> = new EventEmitter<void>();
  @Input() tipoFuenteSeleccionada: string = '';
  @Input() IdDataset!: number;

  pasoActual: number = 1;
  tablasDisponibles: string[] = [];
  filasObtenidas: String[] = [];
  tipoSeleccionado: string = '';
  pestanaSeleccionada = 'archivo';
  tiposBasesDeDatosDisponibles: string[] = ['postgresql'];
  probandoConexion: boolean = false;
  conexionProbada: boolean = false;
  validationError = '';
  private errorTimeout?: ReturnType<typeof setTimeout>;
  conexionesGuardadas: Conexion[] = [];
  conexionIdSeleccionada: number = -1;
  esNuevaConexion: boolean = true;

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

  nuevaBaseDeDatos: BaseDeDatos = {
    id: 0,
    nombre: '',
    tipo: 'BASE_DE_DATOS',
    tablaObjetivo: '',
    querySql: '',
    modoExtraccion: '',
    columnaContenido: '',
    idConexion: 0,
    estadoIndexacion: 'PENDIENTE',
    crdate: '',
    idDataset: 0,
    idIndexacion: 0,
    idExternoVectorial: 0
  };

  nuevoServicioWeb: ServicioWeb = {
    id: 0,
    nombre: '',
    tipo: 'SERVICIO_WEB',
    urlEndpoint: '',
    metodoHttp: '',
    headersJson: '',
    bodyPayload: '',
    rutaTexto: '',
    rutasMetadatos: '',
    estadoIndexacion: 'PENDIENTE',
    crdate: '',
    idDataset: 0,
    idIndexacion: 0,
    idExternoVectorial: 0
  };

  nombreApi: string = '';
  valorApi: string = '';

  constructor(
    private fuenteService: FuenteService,
    private archivoService: ArchivoService,
    private baseDeDatosService: BaseDeDatosService,
    private conexionService: ConexionService,
    private servicioWebService: ServicioWebService
  ) {}

  ngOnInit() {
    this.getConexionesGuardadas();
    this.nuevaBaseDeDatos.idDataset = this.IdDataset;
  }
  seleccionarConexion(id: number) {
    const idNum = Number(id);

    if (idNum === -1) {
      this.esNuevaConexion = true;
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
      this.tipoSeleccionado = '';
    } else {
      const conexion = this.conexionesGuardadas.find(c => c.id === idNum);
      if (conexion) {
        this.esNuevaConexion = false;
        this.conexionSeleccionada = { ...conexion };
        this.conexionSeleccionada.motor = conexion.motor.toLowerCase();
        this.tipoSeleccionado = conexion.motor.toLowerCase();
      }
    }
  }

  apagarConexion() {
    this.probandoConexion = false;
  }

  validarPaso1(): boolean {
    this.clearError();
    if (this.tipoFuenteSeleccionada === 'BASE_DE_DATOS') {
      if (!this.conexionSeleccionada.motor) {
        this.showError('Debe seleccionar un tipo de base de datos.');
        return false;
      }
      const tipo = this.conexionSeleccionada.motor.toLowerCase();
      if (tipo === 'postgresql') {
        if (!this.conexionSeleccionada.host || !this.conexionSeleccionada.puerto || 
            !this.conexionSeleccionada.usuario || !this.conexionSeleccionada.password) {
          this.showError('Debe completar todos los campos de conexión.');
          return false;
        }
      }
      if (!this.conexionSeleccionada.nombre) {
        this.showError('Debe ingresar el nombre de la base de datos.');
        return false;
      }
      return true;
    }
    else if (this.tipoFuenteSeleccionada === 'SERVICIO_WEB') {
      if (!this.nuevoServicioWeb.urlEndpoint || !this.nuevoServicioWeb.metodoHttp) {
        this.showError('Debe completar la URL del endpoint y el método HTTP.');
        return false;
      }
      if (!this.nuevoServicioWeb.nombre) {
        this.showError('Debe ingresar el nombre del servicio web.');
        return false;
      }
      return true;
    }
      else {
      this.showError('Tipo de fuente no válido.');
      return false;
    }
  }

  validarPaso2(): boolean {
   this.clearError();
   if (this.tipoFuenteSeleccionada === 'BASE_DE_DATOS') {
      if (!this.nuevaBaseDeDatos.tablaObjetivo && !this.nuevaBaseDeDatos.querySql) {
        this.showError('Debe ingresar una tabla objetivo o una consulta SQL.');
        return false;
      }
      return true;  
    }
    else if (this.tipoFuenteSeleccionada === 'SERVICIO_WEB') {
      if (!this.nuevoServicioWeb.urlEndpoint || !this.nuevoServicioWeb.metodoHttp) {
        this.showError('Debe completar la URL del endpoint y el método HTTP.');
        return false;
      }
      return true;
    }
      else {
      this.showError('Tipo de fuente no válido.');
      return false;
    }  
  }


  siguientePaso() {
    this.clearError();
    if (this.validarPaso1()) {
      this.pasoActual++;
    }
  }

  goToStep(step: number) {
    this.clearError();
    this.pasoActual = step;
  }

  private showError(message: string): void {
    this.validationError = message;
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
    }
    this.errorTimeout = setTimeout(() => {
      this.validationError = '';
      this.errorTimeout = undefined;
    }, 5000);
  }

  private clearError(): void {
    this.validationError = '';
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
      this.errorTimeout = undefined;
    }
  }
  getTablas() {
    this.conexionService.getTablas(this.conexionSeleccionada).subscribe((response) => {
      this.tablasDisponibles = response.tablas;
    });
  }

  getColumns() {
    this.probandoConexion = true;
    this.nuevaBaseDeDatos.idConexion = this.conexionSeleccionada.id;
    this.baseDeDatosService.getFilas(this.nuevaBaseDeDatos).subscribe(
      (response: any) => {
        if (response && response.data) {
            this.filasObtenidas = response.data;
        } else {
            this.filasObtenidas = []; 
        }
        this.probandoConexion = false;
        this.conexionProbada = true;

      },
      (error) => {
        console.error('Error al obtener columnas:', error);
        this.probandoConexion = false;
        this.conexionProbada = false;
      
      }
    );
  }

  getConexionesGuardadas() {
    this.conexionService.getConexiones().subscribe((response) => {
      this.conexionesGuardadas = response;
    });
  }

  crearBaseDeDatos() {
    if (!this.validarPaso2()) {
      return;
    }
    if (!this.esNuevaConexion && this.conexionSeleccionada.id !== 0) {
      this.nuevaBaseDeDatos.idConexion = this.conexionSeleccionada.id;
    }
    this.nuevaBaseDeDatos.idDataset = this.IdDataset;
    this.nuevaBaseDeDatos.nombre = `${this.conexionSeleccionada.nombre}`;
    this.baseDeDatosService.createBaseDeDatos(this.nuevaBaseDeDatos, this.conexionSeleccionada).subscribe(
      (response) => {
        this.cerrarModalAgregarFuente();
      },
      (error) => {
        console.error('Error al crear la base de datos:', error);
      }
    );
    this.conexionProbada = false;
    this.probandoConexion = false;
  }

  crearApi() {
    if (!this.validarPaso2()) {
      return;
    }
    if (this.valorApi == '' || this.nombreApi == '') {
      this.nuevoServicioWeb.headersJson = '';
    }
    else{
      let headersObj = {
        "name": this.nombreApi,
        "value": this.valorApi
      }
      this.nuevoServicioWeb.headersJson = JSON.stringify(headersObj);
    }
    this.nuevoServicioWeb.idDataset = this.IdDataset;
    this.servicioWebService.createServicioWeb(this.nuevoServicioWeb).subscribe(
      (response) => {
        this.cerrarModalAgregarFuente();
      },
      (error) => {
        console.error('Error al crear el servicio web:', error);
      }
    );
  }

  cerrarModalAgregarFuente() {
    this.cerrarModal.emit();
  }
}
 