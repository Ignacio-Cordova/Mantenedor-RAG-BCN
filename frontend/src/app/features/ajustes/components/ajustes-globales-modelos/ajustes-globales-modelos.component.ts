import { Component, EventEmitter, Output } from '@angular/core';
import { ModeloLlmService } from '../../../apis/services/modelo_llm/modelo-llm';
import { ModeloLlm } from '../../../apis/models/modelo-llm';
import { PageEvent } from '@angular/material/paginator';
@Component({
  selector: 'app-ajustes-globales-modelos',
  standalone: false,
  templateUrl: './ajustes-globales-modelos.component.html',
  styleUrl: './ajustes-globales-modelos.component.scss'
})
export class AjustesGlobalesModelosComponent {

  @Output() eliminarModelo = new EventEmitter<ModeloLlm>();
  modelos: ModeloLlm[] = [];
  columnas: string[] = ['nombre', 'host', 'acciones'];
  pageIndex: number = 0;
  pageSize: number = 5;
  total: number = 0;
  mostrarModalFormulario: boolean = false;
  modeloLlmSeleccionado: ModeloLlm = {
    id: 0,
    nombre: '',
    nombreTecnico: '',
    endpoint: '',
    plantilla_payload: '',
    ruta_respuesta: '',
    limiteTokens: 0,
    idApi: 0,
    crdate: ''
  };

  constructor(private modeloService: ModeloLlmService) {}

  ngOnInit() {
    this.cargarModelos();
  }

  cargarModelos() {
    this.modeloService.getModelosLLMPaginados(this.pageIndex, this.pageSize)
      .subscribe({
        next: (response: { data: ModeloLlm[]; total: number }) => {
          this.modelos = [...response.data]; 
          this.total = response.total;
        },
        error: (err) => console.error('Error cargando modelos:', err)
      });
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.cargarModelos();
  }

  abrirModalModeloLlm(modeloExistente?: ModeloLlm) {
    if (modeloExistente) {
      this.modeloLlmSeleccionado = { ...modeloExistente };
    } else {
      this.modeloLlmSeleccionado = {
        id: 0,
        nombre: '',
        nombreTecnico: '',
        endpoint: '',
        plantilla_payload: '',
        ruta_respuesta: '',
        limiteTokens: 0,
        idApi: 0,
        crdate: ''
      };
    }
    
    this.mostrarModalFormulario = true;
  }

  onGuardarCambios(modeloLlmModificado: ModeloLlm) {
    if (modeloLlmModificado.id !== 0) {
      this.modeloService.updateModeloLlm(modeloLlmModificado).subscribe({
        next: () => {
          this.cargarModelos();
          this.mostrarModalFormulario = false;
          this.modeloLlmSeleccionado = {
            id: 0,
            nombre: '',
            nombreTecnico: '',
            endpoint: '',
            plantilla_payload: '',
            ruta_respuesta: '',
            limiteTokens: 0,
            idApi: 0,
            crdate: ''
          };
        },
        error: (error: any) => console.error('Error al guardar cambios:', error)
      });
    }
    else {
      this.modeloService.createModeloLlm(modeloLlmModificado).subscribe({
        next: (Response: any) => {
          if (Response && Response.error) {
            console.error('Error al crear el modelo:', Response.error);
            return;
          }
          this.cargarModelos();
          this.mostrarModalFormulario = false;
          this.modeloLlmSeleccionado = {
            id: 0,
            nombre: '',
            nombreTecnico: '',
            endpoint: '',
            plantilla_payload: '',
            ruta_respuesta: '',
            limiteTokens: 0,
            idApi: 0,
            crdate: ''
          };
        },
        error: (error: any) => console.error('Error al crear modelo:', error)
      });
    }
  }

  onCerrarModal() {
    this.mostrarModalFormulario = false;
    this.modeloLlmSeleccionado = {
      id: 0,
      nombre: '',
      nombreTecnico: '',
      endpoint: '',
      plantilla_payload: '',
      ruta_respuesta: '',
      limiteTokens: 0,
      idApi: 0,
      crdate: ''
    };
  }

  onEliminar(modelo: ModeloLlm) {
    this.eliminarModelo.emit(modelo);
  }

  recargarTabla() {
    this.mostrarModalFormulario = false;
    this.modeloLlmSeleccionado =  {
      id: 0,
      nombre: '',
      nombreTecnico: '',
      endpoint: '',
      plantilla_payload: '',
      ruta_respuesta: '',
      limiteTokens: 0,
      idApi: 0,
      crdate: ''
    };
  }
}

