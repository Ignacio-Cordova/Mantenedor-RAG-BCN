import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { Dataset } from '../../../datasets/models/dataset';
import { Api } from '../../../apis/models/api';
import { ApiService } from '../../../apis/services/api/api';
import { Conexion } from '../../../fuentes/models/conexion';
import { ConexionService } from '../../../fuentes/services/conexion/conexion.service';
import { ModeloEmbedding } from '../../../apis/models/modelo-embedding';
import { ModeloEmbeddingService } from '../../../apis/services/modelo_embedding/modelo-embedding';
import { ModeloLlm } from '../../../apis/models/modelo-llm';
import { ModeloLlmService } from '../../../apis/services/modelo_llm/modelo-llm';
import { Fuente } from '../../../fuentes/models/fuente';
import { ViewChild } from '@angular/core';
import { AjustesGlobalesApisComponent } from '../ajustes-globales-apis/ajustes-globales-apis.component';
import { AjustesGlobalesConexionesComponent } from '../ajustes-globales-conexiones/ajustes-globales-conexiones.component';
import { AjustesGlobalesModelosComponent } from '../ajustes-globales-modelos/ajustes-globales-modelos.component';
import { AjustesGlobalesModelosEmbeddingComponent } from '../ajustes-globales-modelos-embedding/ajustes-globales-modelos-embedding.component';

@Component({
  selector: 'app-modal-ajustes-globales',
  templateUrl: './modal-ajustes-globales.html',
  styleUrl: './modal-ajustes-globales.scss',
  standalone: false,
})
export class ModalAjustesGlobales {
  @Output() cerrar = new EventEmitter<void>();
  @Output() datasetActualizado = new EventEmitter<void>();
  @Output() IdDataset = new EventEmitter<number>();
  @Input() dataset!: Dataset;
  @ViewChild(AjustesGlobalesApisComponent)
  apisComponent?: AjustesGlobalesApisComponent;
  @ViewChild(AjustesGlobalesConexionesComponent)
  conexionesComponent?:AjustesGlobalesConexionesComponent;
  @ViewChild(AjustesGlobalesModelosComponent)
  modelosLlmComponent?: AjustesGlobalesModelosComponent;
  @ViewChild(AjustesGlobalesModelosEmbeddingComponent)
  modelosEmbeddingComponent?: AjustesGlobalesModelosEmbeddingComponent;

  apisDisponibles: Api[] = [];
  modelosLlmDisponibles: ModeloLlm[] = [];
  conexionesDisponibles: Conexion[] = [];
  FuentesDelDataset: any[] = [];
  FuentesFiltradas: any[] = [];
  tipoFuenteSeleccionada: string = '';
  tipoElementoSeleccionado: 'dataset' | 'fuente' | 'reindexar' | 'api' | 'conexion' | 'modelo_llm' | 'modelo_embedding' = 'api';
  cantidadFuentes: number = 0;
  fuenteSeleccionada!: Fuente;
  apiSeleccionada!: Api;
  conexionSeleccionada!: Conexion;
  modeloLlmSeleccionado!: ModeloLlm
  modeloEmbeddingSeleccionado: ModeloEmbedding = {
    id: 0,
    nombreTecnico: '',
    dimensiones: 0,
    proveedor: '',
    maxTokensInput: 0,
    idApi: 0,
    crdate: ''
  };
  mostrarConfirmacion: boolean = false;
  mostrarModalAgregarFuente: boolean = false;
  tiposFuentes: string[] = ['ARCHIVO', 'BASE_DE_DATOS', 'SERVICIO_WEB'];
  seccionActual: 'Apis' | 'ModelosLLM' | 'ModelosEmbedding' | 'Conexiones' = 'Apis';

  datasetModificado: Dataset = { ...this.dataset };  

  constructor(
    private apiService: ApiService, 
    private modeloEmbeddingService: ModeloEmbeddingService, 
    private modeloLlmService: ModeloLlmService, 
    private conexionService: ConexionService
  ) {
    this.getApis();
    this.getConexiones();
    this.getModelosLlm();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['dataset'] && this.dataset) {
      this.datasetModificado = { ...this.dataset };
      this.getModelosLlmPorApi(this.dataset.idApi);

      this.getModeloEmbeddingSeleccionado(this.dataset.idModeloEmbedding);

      this.getModeloLlmSeleccionado(this.dataset.idModeloLlm);
    }
  }
  cambiarSeccion(seccion: 'Apis' | 'ModelosLLM' | 'ModelosEmbedding' | 'Conexiones') {
    this.seccionActual = seccion;
  }

  FiltrarFuentesPorTipo(tipoFuente: string) {
    this.FuentesFiltradas = this.FuentesDelDataset.filter(fuente => fuente.tipo === tipoFuente);
  }

  seleccionarFuente() {
  }

  recargarApis() {
  this.apisComponent?.cargarApis();
  }

  recargarConexiones() {
    this.conexionesComponent?.cargarConexiones();
  }

  recargarModelosLlm() {
    this.modelosLlmComponent?.cargarModelos();
  }

  recargarModelosEmbedding() {
    this.modelosEmbeddingComponent?.cargarModelos();
  }

  getApis() {
    this.apiService.getApis().subscribe((apis: Api[]) => {
      this.apisDisponibles = apis;
    });
  }

  getConexiones() {
    this.conexionService.getConexiones().subscribe((conexiones: Conexion[]) => {
      this.conexionesDisponibles = conexiones;
    });
  }

  getModelosLlm() {
    this.modeloLlmService.getModelosLlm().subscribe((modelosLlm: ModeloLlm[]) => {
      this.modelosLlmDisponibles = modelosLlm;
    });
  }


  getModeloEmbeddingSeleccionado(idModeloEmbedding: number) {
    this.modeloEmbeddingService.getModeloEmbeddingById(idModeloEmbedding).subscribe((modelo: ModeloEmbedding) => {
      this.datasetModificado.idModeloEmbedding = modelo.id;
    })
  }

  getModeloLlmSeleccionado(idModeloLlm: number) {
    this.modeloLlmService.getModeloLlmById(idModeloLlm).subscribe((modelo: ModeloLlm) => {
      this.datasetModificado.idModeloLlm = modelo.id;
    })
  }

  getModelosLlmPorApi(apiId: number = 0) {
    this.modeloLlmService.getModelosLlmByApiId(apiId).subscribe({
      next: (modelos: ModeloLlm[]) => {
        this.modelosLlmDisponibles = modelos;
      },
      error: (err) => {
        console.error('Error al obtener modelos de LLM:', err);
      }
    }); 
  }
  abrirModalConfirmacionIndexacion(tipo: 'dataset' | 'fuente' | 'reindexar' | 'api' | 'conexion' | 'modelo_llm') {
    this.mostrarConfirmacion = true;
    this.tipoElementoSeleccionado = tipo;  
    this.dataset = { ...this.datasetModificado };
  }

  abrirModalConfirmacion(fuente: Fuente) {
    this.fuenteSeleccionada = fuente;
    this.tipoElementoSeleccionado = 'fuente';
    this.mostrarConfirmacion = true;
  }

  abrirModalConfirmacionApi(api: Api) {
    this.apiSeleccionada = api;
    this.tipoElementoSeleccionado = 'api';
    this.mostrarConfirmacion = true;
  }

  abrirModalConfirmacionConexion(conexion: Conexion) {
    this.conexionSeleccionada = conexion;
    this.tipoElementoSeleccionado = 'conexion';
    this.mostrarConfirmacion = true;
  }

  abrirModalConfirmacionModeloLlm(modeloLlm: ModeloLlm) {
    this.modeloLlmSeleccionado = modeloLlm;
    this.tipoElementoSeleccionado = 'modelo_llm';
    this.mostrarConfirmacion = true;
  }

  abrirModalConfirmacionModeloEmbedding(modelo: ModeloEmbedding) {
    this.modeloEmbeddingSeleccionado = modelo;
    this.tipoElementoSeleccionado = 'modelo_embedding';
    this.mostrarConfirmacion = true;
  }
  
  cerrarConfirmacion() {
    this.mostrarConfirmacion = false;
    this.fuenteSeleccionada = undefined!;
    this.apiSeleccionada = undefined!;
    this.conexionSeleccionada = undefined!;
    this.modeloLlmSeleccionado = undefined!;
  }

  onConfirmacionExitosa() {
    this.cerrarConfirmacion();
  }

  cerrarModalAgregarFuente() {
    this.mostrarModalAgregarFuente = false;
    this.tipoFuenteSeleccionada = '';
  }

  cerrarModal() {
    this.cerrar.emit();
  }
}
