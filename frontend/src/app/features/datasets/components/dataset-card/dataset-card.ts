import { Component, EventEmitter, Input, Output } from '@angular/core';

import { Dataset } from '../../models/dataset';

@Component({
  selector: 'app-dataset-card',
  templateUrl: './dataset-card.html',
  styleUrl: './dataset-card.scss',
  standalone: false,
})
export class DatasetCard {
  @Input() dataset!: Dataset;
  @Output() abrirAjustes = new EventEmitter<void>();
  @Output() abrirConfirmacion = new EventEmitter<void>();
  @Output() datasetSeleccionado = new EventEmitter<Dataset>();
  seleccionarDataset() {
    this.datasetSeleccionado.emit(this.dataset);
  }

  abrirModalAjustes() {
    this.abrirAjustes.emit();
  }

  abrirModalConfirmacion() {
    this.abrirConfirmacion.emit();
  }
  
  
  
}
