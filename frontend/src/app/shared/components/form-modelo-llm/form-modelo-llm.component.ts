import { Component, Input } from '@angular/core';
import { ModeloLlm } from '../../../features/apis/models/modelo-llm';
import { Api } from '../../../features/apis/models/api';
import { ApiService } from '../../../features/apis/services/api/api';

@Component({
  selector: 'app-form-modelo-llm',
  standalone: false,
  templateUrl: './form-modelo-llm.component.html',
  styleUrl: './form-modelo-llm.component.scss'
})

export class FormModeloLlmComponent {
  ejemploPlantilla: string = 'Ej: {\n  "model": "{{nombre_modelo}}",\n  "prompt": "{{prompt_ensamblado}}"\n}';
  
 @Input() modeloLlmSeleccionado: ModeloLlm = {
  id: 0,
  nombre: '',
  nombreTecnico: '',
  endpoint: '',
  plantilla_payload: '',
  ruta_respuesta: '',
  limiteTokens: 0,
  idApi: 0,
  crdate: ''
 }

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

};
