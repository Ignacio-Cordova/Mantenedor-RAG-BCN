import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Api } from '../../../features/apis/models/api';

@Component({
  selector: 'app-form-api',
  standalone: false,
  templateUrl: './form-api.component.html',
  styleUrl: './form-api.component.scss'
})
export class FormApiComponent {
  @Input() apiSeleccionada: Api = {
    id: 0,
    nombreProveedor: '',
    baseUrl: '',
    organizacionId: '',
    tipoAutenticacion: '',
    apiKeyEncriptada: '',
    crdate: ''
  };

  @Output() apiChange: EventEmitter<Api> = new EventEmitter<Api>();

  onInputChange() {
    this.apiChange.emit(this.apiSeleccionada);
    if (!this.apiSeleccionada.nombreProveedor || !this.apiSeleccionada.baseUrl || !this.apiSeleccionada.tipoAutenticacion || !this.apiSeleccionada.apiKeyEncriptada || !this.apiSeleccionada.organizacionId) {
      console.error('Todos los campos son obligatorios para crear una API.');
    }
  }
}
