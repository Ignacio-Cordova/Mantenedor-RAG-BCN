import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Api } from '../../../apis/models/api';
import { ApiService } from '../../../apis/services/api/api';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-ajustes-globales-apis',
  standalone: false,
  templateUrl: './ajustes-globales-apis.component.html',
  styleUrl: './ajustes-globales-apis.component.scss'
})
export class AjustesGlobalesApisComponent {

  @Output() eliminarApi = new EventEmitter<Api>();
  apis: Api[] = [];
  columnas: string[] = ['nombre', 'host', 'acciones'];
  pageIndex: number = 0;
  pageSize: number = 5;
  total: number = 0;
  apiSeleccionada: Api = {
    id: 0,
    nombreProveedor: '',
    baseUrl: '',
    organizacionId: '',
    tipoAutenticacion: '',
    apiKeyEncriptada: '',
    crdate: ''
  };
  modalApiAbierto: boolean = false;

  @Input() apiChange: (api: Api) => void = () => {};

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.cargarApis();
  }

  cargarApis() {
    this.apiService.getApisPaginadas(this.pageIndex, this.pageSize)
      .subscribe({
        next: (response: { data: Api[]; total: number }) => {
          this.apis = [...response.data];
          this.total = response.total;
        },
        error: (err) => console.error('Error cargando APIs:', err)
      });
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.cargarApis();
  }

  abrirModalApi(apiExistente?: Api) {
    if (apiExistente) {
      this.apiSeleccionada = { ...apiExistente };
    } else {
      this.apiSeleccionada = {
        id: 0,
        nombreProveedor: '',
        baseUrl: '',
        organizacionId: '',
        tipoAutenticacion: '',
        apiKeyEncriptada: '',
        crdate: ''
      };
    }
    this.modalApiAbierto = true;
  }

  onCerrarModal() {
    this.modalApiAbierto = false;
    this.apiSeleccionada = {
      id: 0,
      nombreProveedor: '',
      baseUrl: '',
      organizacionId: '',
      tipoAutenticacion: '',
      apiKeyEncriptada: '',
      crdate: ''
    };
    this.modalApiAbierto = false;
  }

  onGuardarCambios(apiModificada: Api) {
    if (apiModificada.id !== 0) {
      this.apiService.updateApi(apiModificada).subscribe({
        next: () => {
          this.cargarApis();
          this.modalApiAbierto = false;
          this.apiSeleccionada = {
            id: 0,
            nombreProveedor: '',
            baseUrl: '',
            organizacionId: '',
            tipoAutenticacion: '',
            apiKeyEncriptada: '',
            crdate: ''
          };
        },
        error: (error: any) => console.error('Error al guardar cambios:', error)
      });
    }
    else {
      this.apiService.createApi(apiModificada).subscribe({
        next: (Response: any) => {
          if (Response === null) {
            console.error('Error: No se pudo crear la API. Todos los campos son obligatorios.');
            return;
          }
          this.cargarApis();
          this.modalApiAbierto = false;
          this.apiSeleccionada = {
            id: 0,
            nombreProveedor: '',
            baseUrl: '',
            organizacionId: '',
            tipoAutenticacion: '',
            apiKeyEncriptada: '',
            crdate: ''
          };
        }
      });
    }
  }

  onEliminar(api: Api) {
    this.eliminarApi.emit(api);
  }

  recargarTabla() {
    this.modalApiAbierto = false;
    this.apiSeleccionada = {
      id: 0,
      nombreProveedor: '',
      baseUrl: '',
      organizacionId: '',
      tipoAutenticacion: '',
      apiKeyEncriptada: '',
      crdate: ''
    };
    this.cargarApis();
  }
}
