import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dataset } from '../../../features/datasets/models/dataset';
import { DatasetService } from '../../../features/datasets/services/dataset/dataset';

@Component({
  selector: 'app-ui-modal',
  templateUrl: './ui-modal.html',
  styleUrl: './ui-modal.scss',
  standalone: false,
})
export class UiModal {
  @Input() visible: 'creacion' | 'ajustes' | 'ajustesGlobales' | 'confirmacion' | null = null;
  @Input() dataset: Dataset = {} as Dataset;
  @Output() close = new EventEmitter<void>();
  @Output() datasetActualizado = new EventEmitter<Dataset>();

  constructor(private datasetService: DatasetService) {}

  ngOnInit() {
  }

  actualizarDataset(dataset: Dataset) {
    this.datasetService.getDataset(this.dataset.id).subscribe({
      next: (datasetActualizado) => {
        this.dataset = datasetActualizado;
        this.datasetActualizado.emit(datasetActualizado);
      },
      error: (err) => {
        console.error('Error al recargar dataset:', err);
        this.datasetActualizado.emit(dataset);
      }
    });
    this.cerrar();
  }

  cerrar() {
    this.close.emit();
  }
}
