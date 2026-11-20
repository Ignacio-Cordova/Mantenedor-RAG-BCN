import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dataset } from '../../models/dataset';
import { ConexionService } from '../../../fuentes/services/conexion/conexion.service';
import { FuenteService } from '../../../fuentes/services/fuente/fuente';
import { Api } from '../../../apis/models/api';
import { ApiService } from '../../../apis/services/api/api';
import { ModeloEmbedding } from '../../../apis/models/modelo-embedding';
import { ModeloEmbeddingService} from '../../../apis/services/modelo_embedding/modelo-embedding';
import { ModeloLlm } from '../../../apis/models/modelo-llm';
import { ModeloLlmService } from '../../../apis/services/modelo_llm/modelo-llm';
import { RetrievalMethod } from '../../../apis/models/retriebal_method';
import { RetrievalMethodService } from '../../../apis/services/retrieval_method/retrieval-method';
import { IndexMethod } from '../../../apis/models/index_method';
import { IndexMethodService } from '../../../apis/services/index_method/index-method';
import { DatasetService } from '../../../datasets/services/dataset/dataset';

@Component({
  selector: 'app-modal-creacion',
  templateUrl: './modal-creacion.html',
  styleUrl: './modal-creacion.scss',
  standalone: false,
})
export class ModalCreacion  {
  @Output() cerrar = new EventEmitter<void>();
  @Output() datasetActualizado = new EventEmitter<Dataset>();
  @Input() datasetsExistentes: Dataset[] = [];

  apisDisponibles: Api[] = [];
  modelosEmbeddingDisponibles: ModeloEmbedding[] = [];
  modelosLlmDisponibles: ModeloLlm[] = [];
  tiposFuentesDisponibles: string[] = [];
  tiposBasesDeDatosDisponibles: string[] = this.getBasesDeDatos();
  tablasDisponibles: string[] = [];
  retrievalMethodsDisponibles: RetrievalMethod[] = [];
  indexMethodsDisponibles: IndexMethod[] = [];

  pasoActual: number = 1;
  pestanaSeleccionada: 'QUERY' | 'TABLA' = 'QUERY';
  tipoSeleccionado: string = '';
  probandoConexion: boolean = false;
  errorMessage = '';
  private errorTimeout?: ReturnType<typeof setTimeout>;

  nuevoDataset: Dataset = {
    id: this.datasetsExistentes.length + 1,
    nombre: '',
    tamanoChunk: 0,
    overlap: 0,
    collectionName: '',
    topK: 0,
    systemPrompt: 'Eres un asistente de la BCN. Responde basándote exclusivamente en el contexto recuperado.',
    idApi: 0,
    idModeloLlm: 0,
    idModeloEmbedding: 0,
    idRetrievalMethod: 0,
    idIndexMethod: 0,
    crdate: new Date().toISOString(),
  };

  constructor( private apiService: ApiService,
               private modeloEmbeddingService: ModeloEmbeddingService,
               private modeloLlmService: ModeloLlmService,
               private fuenteService: FuenteService,
               private conexionService: ConexionService,
               private retrievalMethodService: RetrievalMethodService,
               private indexMethodService: IndexMethodService,
               private datasetService: DatasetService
               ) {
                this.getModelosEmbedding();
                this.getApis();
                this.getTipoFuentes();
                this.getRetrievalMethods();
                this.getIndexMethods();
               }
  siguientePaso() {
    this.clearError();
    if (!this.validacionDeCampos()) {
      this.showError('Por favor completa todos los campos requeridos antes de continuar.');
      return;
    }
    this.pasoActual++;
  }

  anteriorPaso() {
    this.clearError();
    if (this.pasoActual > 1) {
      this.pasoActual--;
    }
  }
  
  goToStep(numero: number) {
    this.clearError();
    this.pasoActual = numero;
  }

  goToAgregarFuente(tipo: string) {
    switch (tipo) {
      case 'ARCHIVO':
        this.pasoActual = 3; 
        break;
      case 'BASE_DE_DATOS':
        this.pasoActual = 4; 
        break;
      case 'API':
        this.pasoActual = 7; 
        break;
    }
  }

  validacionDeCampos(): boolean {
    if (this.pasoActual === 1 && (this.nuevoDataset.nombre.trim() === '' || 
        this.nuevoDataset.idApi === 0 || 
        this.nuevoDataset.idModeloLlm === 0)) {
      return false;
    }
    else if (this.pasoActual === 2 && (this.nuevoDataset.tamanoChunk <= 0 ||
        this.nuevoDataset.idModeloEmbedding === 0 ||
        this.nuevoDataset.idRetrievalMethod === 0 ||
        this.nuevoDataset.idIndexMethod === 0 ||
        this.nuevoDataset.systemPrompt.trim() === '')) {
      return false;
    }
    return true;
  }
  
  apagarConexion() {
    this.probandoConexion = false;
  }
  crearDataset() {
    this.clearError();
    if (!this.validacionDeCampos()) {
      this.showError('Por favor completa todos los campos requeridos antes de crear el dataset.');
      return;
    }
    this.datasetService.createDataset(this.nuevoDataset).subscribe((response) => {
      this.datasetActualizado.emit(response);
      this.cerrarModal();
    }, (error) => {
      console.error('Error al crear el dataset:', error);
    });
  }

  private showError(message: string): void {
    this.errorMessage = message;
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
    }
    this.errorTimeout = setTimeout(() => {
      this.errorMessage = '';
      this.errorTimeout = undefined;
    }, 5000);
  }

  private clearError(): void {
    this.errorMessage = '';
    if (this.errorTimeout) {
      clearTimeout(this.errorTimeout);
      this.errorTimeout = undefined;
    }
  }

   getApis() {
    this.apiService.getApis().subscribe((apis) => {
      this.apisDisponibles = apis;
    });
  }

  getModelosEmbedding() {
    this.modeloEmbeddingService.getModelosEmbedding().subscribe((modelos) => {
      this.modelosEmbeddingDisponibles = modelos;
      if (this.nuevoDataset.idModeloEmbedding === 0 && modelos.length > 0) {
        this.nuevoDataset.idModeloEmbedding = modelos[0].id;
      }
    });
  }

  getModelosLlm() {
    this.modeloLlmService.getModelosLlm().subscribe((modelos) => {
      this.modelosLlmDisponibles = modelos;
    });
  }

  getRetrievalMethods() {
    this.retrievalMethodService.getRetrievalMethods().then((methods) => {
      this.retrievalMethodsDisponibles = methods;
    }).catch((error) => {
      console.error('Error al obtener Retrieval Methods:', error);
    });
  }

  getIndexMethods() {
    this.indexMethodService.getIndexMethods().subscribe((methods) => {
      this.indexMethodsDisponibles = methods;
    });
  }

  getModelosLlmByApiId(apiId: number) {
    this.modeloLlmService.getModelosLlmByApiId(apiId).subscribe((modelos) => {
      this.modelosLlmDisponibles = modelos;
    });
  }

  getTipoFuentes() {
    this.fuenteService.getTiposFuentes().subscribe((tipos) => {
      this.tiposFuentesDisponibles = tipos;
    });
  }

  getBasesDeDatos() {
    return ["PostgreSQL"];
  }
  cerrarModal() {
    this.cerrar.emit();
  }

  

}
