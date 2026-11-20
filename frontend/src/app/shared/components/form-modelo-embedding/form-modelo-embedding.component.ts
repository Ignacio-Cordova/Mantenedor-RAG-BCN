import { Component, Input } from '@angular/core';
import { Api } from '../../../features/apis/models/api';
import { ApiService } from '../../../features/apis/services/api/api';
import { ModeloEmbedding } from '../../../features/apis/models/modelo-embedding';

@Component({
  selector: 'app-form-modelo-embedding',
  standalone: false,
  templateUrl: './form-modelo-embedding.component.html',
  styleUrl: './form-modelo-embedding.component.scss'
})
export class FormModeloEmbeddingComponent {
  @Input() modeloEmbeddingSeleccionado: ModeloEmbedding = {
    id: 0,
    nombreTecnico: '',
    dimensiones: 0,
    proveedor: '',
    maxTokensInput: 0,
    idApi: 0,
    crdate: ''
  };

  apis: Api[] = [];

  constructor(private apiService: ApiService) {}

  ngOnInit() {
    this.apiService.getApis().subscribe({
      next: (apis: Api[]) => {
        this.apis = apis;
      },
      error: (error) => {
        console.error('Error al obtener las APIs:', error);
      }
    });
  }
}
