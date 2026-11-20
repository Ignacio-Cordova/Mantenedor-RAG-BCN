import { Component, EventEmitter, Output } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { ModeloEmbedding } from '../../../apis/models/modelo-embedding';
import { ModeloEmbeddingService } from '../../../apis/services/modelo_embedding/modelo-embedding';

@Component({
  selector: 'app-ajustes-globales-modelos-embedding',
  standalone: false,
  templateUrl: './ajustes-globales-modelos-embedding.component.html',
  styleUrl: './ajustes-globales-modelos-embedding.component.scss'
})
export class AjustesGlobalesModelosEmbeddingComponent {
  @Output() eliminarModeloEmbedding = new EventEmitter<ModeloEmbedding>();

  modelos: ModeloEmbedding[] = [];
  modelosPaginados: ModeloEmbedding[] = [];
  columnas: string[] = ['nombreTecnico', 'dimensiones', 'maxTokensInput', 'proveedor', 'acciones'];

  pageIndex: number = 0;
  pageSize: number = 5;
  total: number = 0;

  mostrarModalFormulario: boolean = false;
  modeloEmbeddingSeleccionado: ModeloEmbedding = this.crearModeloVacio();

  constructor(private modeloEmbeddingService: ModeloEmbeddingService) {}

  ngOnInit() {
    this.cargarModelos();
  }

  cargarModelos() {
    this.modeloEmbeddingService.getModelosEmbedding().subscribe({
      next: (modelos: ModeloEmbedding[]) => {
        this.modelos = [...modelos];
        this.total = modelos.length;
        this.actualizarPagina();
      },
      error: (error) => console.error('Error cargando modelos Embedding:', error)
    });
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.actualizarPagina();
  }

  actualizarPagina() {
    const inicio = this.pageIndex * this.pageSize;
    this.modelosPaginados = this.modelos.slice(inicio, inicio + this.pageSize);
  }

  abrirModalModeloEmbedding(modeloExistente?: ModeloEmbedding) {
    this.modeloEmbeddingSeleccionado = modeloExistente
      ? { ...modeloExistente }
      : this.crearModeloVacio();
    this.mostrarModalFormulario = true;
  }

  onGuardarCambios(modelo: ModeloEmbedding) {
    const observable = modelo.id
      ? this.modeloEmbeddingService.updateModeloEmbedding(modelo)
      : this.modeloEmbeddingService.createModeloEmbedding(modelo);

    observable.subscribe({
      next: (response: any) => {
        if (response?.error) {
          console.error('Error al guardar modelo Embedding:', response.error);
          return;
        }
        this.cargarModelos();
        this.onCerrarModal();
      },
      error: (error) => console.error('Error al guardar modelo Embedding:', error)
    });
  }

  onCerrarModal() {
    this.mostrarModalFormulario = false;
    this.modeloEmbeddingSeleccionado = this.crearModeloVacio();
  }

  onEliminar(modelo: ModeloEmbedding) {
    this.eliminarModeloEmbedding.emit(modelo);
  }

  recargarTabla() {
    this.cargarModelos();
  }

  private crearModeloVacio(): ModeloEmbedding {
    return {
      id: 0,
      nombreTecnico: '',
      dimensiones: 0,
      proveedor: '',
      maxTokensInput: 0,
      idApi: 0,
      crdate: ''
    };
  }
}
