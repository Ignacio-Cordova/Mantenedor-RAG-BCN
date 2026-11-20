  import { Component, EventEmitter, Output, Input } from '@angular/core';
  import { Dataset } from '../../models/dataset'; 
  import { DatasetService } from '../../services/dataset/dataset';

  @Component({
    selector: 'app-dataset-list',
    templateUrl: './dataset-list.html',
    styleUrl: './dataset-list.scss',
    standalone: false,
  })
  export class DatasetList {
    @Output() abrirAjustes = new EventEmitter<void>();
    @Output() abrirConfirmacion = new EventEmitter<void>();
    @Output() datasetSeleccionado = new EventEmitter<Dataset>();
    @Input() datasetsActualizados !: () => void;

    datasets: Dataset[] = [];

    constructor(private datasetService: DatasetService) {
      this.cargarDatasets();
    }
    ngOnChanges() {
      this.cargarDatasets();
    }
    
    notificarAjustes() {
      this.abrirAjustes.emit();
    }

    notificarConfirmacion() {
      this.abrirConfirmacion.emit();
    }

    emitirDatasetSeleccionado(dataset: Dataset) {
      this.datasetSeleccionado.emit(dataset);
    }
    datasetsActualizadosHandler() {
      if (this.datasetsActualizados) {
        this.datasetsActualizados();
        this.cargarDatasets();
      }
    }
    cargarDatasets() {
      this.datasetService.getDatasets().subscribe({
        next: (data) => {
          this.datasets = data;
        },
        error: (err) => {
          console.error('Error al cargar datasets:', err);
        }
      });
    }

    seleccionarDataset(dataset: Dataset) {
      this.datasetService.getDataset(dataset.id).subscribe({
        next: (data) => {
        },
        error: (err) => {
          console.error('Error al seleccionar dataset:', err);
        }
      });
    }
  }
