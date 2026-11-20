import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dataset } from '../../../datasets/models/dataset';
import { ModeloEmbedding } from '../../../apis/models/modelo-embedding';
import { RetrievalMethod } from '../../../apis/models/retriebal_method';
import { IndexMethod } from '../../../apis/models/index_method';

@Component({
  selector: 'app-ajustes-indexacion',
  templateUrl: './ajustes-indexacion.component.html',
  styleUrls: ['./ajustes-indexacion.component.scss'],
  standalone: false
})
export class AjustesIndexacionComponent {
  @Input() datasetModificado!: Dataset;
  @Input() modelosEmbeddingDisponibles: ModeloEmbedding[] = [];
  @Input() metodosRecuperacionDisponibles: RetrievalMethod[] = [];
  @Input() metodosIndexacionDisponibles: IndexMethod[] = [];
  @Output() guardar = new EventEmitter<void>();
  @Output() cerrar = new EventEmitter<void>();

  onGuardarCambios() {
    this.guardar.emit();
  }

  onCerrarModal() {
    this.cerrar.emit();
  }
}