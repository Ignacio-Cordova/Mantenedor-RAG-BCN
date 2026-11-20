import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dataset } from '../../../features/datasets/models/dataset';
import { DatasetService } from '../../../features/datasets/services/dataset/dataset';
import { Fuente } from '../../../features/fuentes/models/fuente';
import { FuenteService } from '../../../features/fuentes/services/fuente/fuente';
import { switchMap } from 'rxjs';
import { Api } from '../../../features/apis/models/api';
import { ApiService } from '../../../features/apis/services/api/api';
import { Conexion } from '../../../features/fuentes/models/conexion';
import { ConexionService } from '../../../features/fuentes/services/conexion/conexion.service';
import { ModeloLlm } from '../../../features/apis/models/modelo-llm';
import { ModeloLlmService } from '../../../features/apis/services/modelo_llm/modelo-llm';
import { ModeloEmbedding } from '../../../features/apis/models/modelo-embedding';
import { ModeloEmbeddingService } from '../../../features/apis/services/modelo_embedding/modelo-embedding';
import { ArchivoService } from '../../../features/fuentes/services/archivo/archivo';
import { BaseDeDatosService } from '../../../features/fuentes/services/base_de_datos/base_de_datos';
import { ServicioWebService } from '../../../features/fuentes/services/servicio_web/servicio-web.service';

@Component({
  selector: 'app-modal-confirmacion',
  standalone: false,
  templateUrl: './modal-confirmacion.component.html',
  styleUrls: ['./modal-confirmacion.component.scss']
})
export class ModalConfirmacionComponent {
  @Input() dataset!: Dataset;
  @Input() fuente!: Fuente;
  @Input() api!: Api;
  @Input() conexion!: Conexion; 
  @Input() modeloLlm!: ModeloLlm;
  @Input() modeloEmbedding!: ModeloEmbedding;
  @Input() tipoFuente = '';
  @Input() tipoElemento: 'dataset' | 'fuente' | 'reindexar' | 'api' | 'conexion' | 'modelo_llm' | 'modelo_embedding' = 'dataset'; 
  mensajeError: string = '';
  @Output() cerrar = new EventEmitter<void>();
  @Output() datasetActualizado = new EventEmitter<void>();

  constructor(
    private fuenteService: FuenteService, 
    private datasetService: DatasetService,
    private apiService: ApiService,
    private conexionService: ConexionService,
    private modeloLlmService: ModeloLlmService,
    private modeloEmbeddingService: ModeloEmbeddingService,
    private archivoService: ArchivoService,
    private baseDeDatosService: BaseDeDatosService,
    private servicioWebService: ServicioWebService) {}
  
  public confirmar(): void {
    this.mensajeError = '';
    if (this.tipoElemento === 'fuente') {
      let deleteObservable;

      if (this.tipoFuente === 'ARCHIVO') {
        deleteObservable = this.archivoService.deleteArchivo(this.fuente.id);
      } else if (this.tipoFuente === 'BASE_DE_DATOS') {
        deleteObservable = this.baseDeDatosService.deleteBaseDeDatos(this.fuente.id);
      } else if (this.tipoFuente === 'SERVICIO_WEB') {
        deleteObservable = this.servicioWebService.deleteServicioWeb(this.fuente.id);
      } else {
        deleteObservable = this.fuenteService.deleteFuente(this.fuente.id);
      }

      deleteObservable.subscribe({
        next: (response: any) => {
          if (response?.error) {
            console.error('Error al eliminar la fuente:', response.error);
            return;
          }
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error) => {
          console.error('Error al eliminar la fuente:', error);
        }
      });
    }
    else if (this.tipoElemento === 'dataset') {
      this.datasetService.deleteDataset(this.dataset.id).subscribe({
        next: () => {
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error) => {
          console.error('Error al eliminar el dataset:', error);
        }
      });
    }
   else if (this.tipoElemento === 'reindexar') {
      this.datasetService.updateDataset(this.dataset).pipe(
        switchMap(() => this.datasetService.reIndexarDataset(this.dataset.id))
      ).subscribe({
        next: () => {
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error) => {
          console.error('Error en actualización/reindexación:', error);
        }
      });
    }
    else if (this.tipoElemento === 'api') {
      this.apiService.deleteApi(this.api.id).subscribe({
        next: (response: any) => {
          if (response?.error) {
            this.mensajeError = response.error;
            return;
          }
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error) => {
          console.error('Error al eliminar la API:', error);
        }
      });
    }
    else if (this.tipoElemento === 'conexion') {
      this.conexionService.deleteConexion(this.conexion.id).subscribe({
        next: (response: any) => {
          if (response?.error) {
            this.mensajeError = response.error;
            return;
          }
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error: any) => {
          console.error('Error al eliminar la conexión:', error);
        }
      });
    }
    else if (this.tipoElemento === 'modelo_llm') {
      this.modeloLlmService.deleteModelo(this.modeloLlm.id).subscribe({
        next: (response: any) => {
          if (response?.error) {
            this.mensajeError = response.error;
            return;
          }
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error) => {
          console.error('Error al eliminar el modelo LLM:', error);
        }
      });
    }
    else if (this.tipoElemento === 'modelo_embedding') {
      this.modeloEmbeddingService.deleteModeloEmbedding(this.modeloEmbedding.id).subscribe({
        next: (response: any) => {
          if (response?.error) {
            this.mensajeError = response.error;
            return;
          }
          this.cerrar.emit();
          this.datasetActualizado.emit();
        },
        error: (error) => {
          this.mensajeError = error?.error?.error || 'Error al eliminar el modelo Embedding.';
        }
      });
    }
  }

  public cerrarModal(): void {
    this.cerrar.emit();
  }
}
