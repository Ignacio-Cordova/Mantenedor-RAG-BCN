import { Component, EventEmitter, Input, Output, SimpleChanges, OnChanges } from '@angular/core';
import { Dataset } from '../../../datasets/models/dataset';
import { DatasetService } from '../../../datasets/services/dataset/dataset';
import { Api } from '../../../apis/models/api';
import { ApiService } from '../../../apis/services/api/api';
import { ModeloEmbedding } from '../../../apis/models/modelo-embedding';
import { ModeloEmbeddingService } from '../../../apis/services/modelo_embedding/modelo-embedding';
import { ModeloLlm } from '../../../apis/models/modelo-llm';
import { ModeloLlmService } from '../../../apis/services/modelo_llm/modelo-llm';
import { Fuente } from '../../../fuentes/models/fuente';
import { RetrievalMethod } from '../../../apis/models/retriebal_method';
import { RetrievalMethodService } from '../../../apis/services/retrieval_method/retrieval-method';
import { IndexMethod } from '../../../apis/models/index_method';
import { IndexMethodService } from '../../../apis/services/index_method/index-method';

@Component({
  selector: 'app-modal-ajustes',
  templateUrl: './modal-ajustes.html',
  styleUrl: './modal-ajustes.scss',
  standalone: false,
})
export class ModalAjustes implements OnChanges {
  @Output() cerrar = new EventEmitter<void>();
  @Output() datasetActualizado = new EventEmitter<void>();
  @Input() dataset!: Dataset;
  apisDisponibles: Api[] = [];
  modelosLlmDisponibles: ModeloLlm[] = [];
  modelosEmbeddingDisponibles: ModeloEmbedding[] = [];
  metodosRecuperacionDisponibles: RetrievalMethod[] = [];
  metodosIndexacionDisponibles: IndexMethod[] = [];
  tiposFuentes: string[] = ['ARCHIVO', 'BASE_DE_DATOS', 'SERVICIO_WEB'];
  seccionActual: 'generales' | 'fuentes' | 'visorFuentes' | 'agregarFuente' | 'indexacion' = 'generales';
  tipoFuenteSeleccionada: string = '';
  mostrarConfirmacion: boolean = false;
  tipoElementoSeleccionado: 'dataset' | 'fuente' | 'reindexar' = 'fuente';
  fuenteSeleccionada!: Fuente;

  datasetModificado!: Dataset;  

  constructor(
    private apiService: ApiService, 
    private modeloEmbeddingService: ModeloEmbeddingService, 
    private modeloLlmService: ModeloLlmService, 
    private datasetService: DatasetService, 
    private indexMethodService: IndexMethodService, 
    private retrievalMethodService: RetrievalMethodService
  ) {
    this.cargarListasDesplegables();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['dataset'] && this.dataset) {
      this.datasetModificado = { ...this.dataset };
      this.getModelosLlmPorApi(this.dataset.idApi);
    }
  }
  private cargarListasDesplegables() {
    this.apiService.getApis().subscribe(apis => this.apisDisponibles = apis);
    this.modeloEmbeddingService.getModelosEmbedding().subscribe(modelos => this.modelosEmbeddingDisponibles = modelos);
    this.retrievalMethodService.getRetrievalMethods().then(metodos => this.metodosRecuperacionDisponibles = metodos);
    this.indexMethodService.getIndexMethods().subscribe(metodos => this.metodosIndexacionDisponibles = metodos);
  }
  getModelosLlmPorApi(apiId: number = 0) {
    this.modeloLlmService.getModelosLlmByApiId(apiId).subscribe({
      next: (modelos) => this.modelosLlmDisponibles = modelos,
      error: (err) => console.error('Error al obtener modelos de LLM:', err)
    }); 
  }

  getModeloLlmSeleccionado(idModeloLlm: number) {
    this.modeloLlmService.getModeloLlmById(idModeloLlm).subscribe((modelo: ModeloLlm) => {
      this.datasetModificado.idModeloLlm = modelo.id;
    });
  }
  guardarCambios() {
    const requiereReindexar = 
      this.datasetModificado.overlap !== this.dataset.overlap ||
      this.datasetModificado.tamanoChunk !== this.dataset.tamanoChunk;

    if (requiereReindexar) {
      this.abrirModalConfirmacionIndexacion();
    } else {
      this.ejecutarGuardado();
    }
  }

  private ejecutarGuardado() {
    this.datasetService.updateDataset(this.datasetModificado).subscribe({
      next: () => {
        this.datasetActualizado.emit();
        this.cerrarModal();
      },
      error: (error) => console.error('Error al actualizar el dataset:', error)
    });
  }

  indexarFuentes() {
    this.datasetService.indexarDataset(this.dataset.id).subscribe({
      next: () => {},
      error: (error) => console.error('Error al iniciar la indexación:', error)
    }); 
  } 
  verFuentes(tipoFuente: string) {
    this.tipoFuenteSeleccionada = tipoFuente;
    this.seccionActual = 'visorFuentes';
  }

  abrirModalAgregarFuente(tipoFuente: string) {
    this.tipoFuenteSeleccionada = tipoFuente;
    this.seccionActual = 'agregarFuente';
  }

  abrirModalConfirmacionIndexacion() {
    this.mostrarConfirmacion = true;
    this.tipoElementoSeleccionado = 'reindexar';  
    this.dataset = { ...this.datasetModificado };
  }

  cerrarModal() {
    this.cerrar.emit();
  }
}