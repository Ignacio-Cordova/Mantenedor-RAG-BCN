import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Dataset } from '../../../datasets/models/dataset';
import { Api } from '../../../apis/models/api';
import { ModeloLlm } from '../../../apis/models/modelo-llm';

@Component({
  selector: 'app-ajustes-generales',
  templateUrl: './ajustes-generales.component.html',
  styleUrls: ['./ajustes-generales.component.scss'],
  standalone: false
})
export class AjustesGeneralesComponent {
  @Input() datasetModificado!: Dataset;
  @Input() apisDisponibles: Api[] = [];
  @Input() modelosLlmDisponibles: ModeloLlm[] = [];
  @Output() apiCambiada = new EventEmitter<number>();
  @Output() modeloCambiado = new EventEmitter<number>();
  @Output() guardar = new EventEmitter<void>();
  @Output() cerrar = new EventEmitter<void>();

  onApiChange(idApi: number) {
    this.apiCambiada.emit(idApi);
  }

  onModeloChange(idModelo: number) {
    this.modeloCambiado.emit(idModelo);
  }

  onGuardarCambios() {
    this.guardar.emit();
  }

  onCerrarModal() {
    this.cerrar.emit();
  }
}