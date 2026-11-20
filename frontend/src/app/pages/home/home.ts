import { Component, Input } from '@angular/core';
import { Dataset } from '../../features/datasets/models/dataset';

@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  styleUrl: './home.scss',
  standalone: false,
})
export class Home {
  @Input() datasetSeleccionado!: Dataset;
  @Input() actualizarDatasets!: () => void;
  modalVisible: 'creacion' | 'ajustes' | 'confirmacion' | 'ajustesGlobales' | null = null;

  notificarAjustes() {
    this.modalVisible = 'ajustes';
  }

  abrirCreacion() {
    this.modalVisible = 'creacion';
  }

  abrirAjustesGlobales() {
    this.modalVisible = 'ajustesGlobales';
  }

  notificarConfirmacion() {
    this.modalVisible = 'confirmacion';
  }

  cerrarModal() {
    this.modalVisible = null;
  }
}
