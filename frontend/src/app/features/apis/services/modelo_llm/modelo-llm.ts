import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable, throwError } from 'rxjs'; 
import { ModeloLlm } from '../../models/modelo-llm'; 

@Injectable({
  providedIn: 'root' 
})
export class ModeloLlmService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  constructor(private http: HttpClient) { }

  /** Lista los modelos LLM registrados. */
  getModelosLlm(): Observable<ModeloLlm[]> {
    return this.http.get<ModeloLlm[]>(this.apiUrl + '/ObtenerModelosLlm');
  }

  getModelosLLMPaginados(pageIndex: number, pageSize: number): Observable<{ data: ModeloLlm[]; total: number }> {
    const offset = pageIndex * pageSize;
    const params = new HttpParams()
      .set('offset', offset.toString())
      .set('limit', pageSize.toString());
    return this.http.get<{data: ModeloLlm[], total: number}>(`${this.apiUrl}/ObtenerModelosLlmPaginados`, { params });
  }

  
  /** Obtiene los modelos LLM asociados a una API por su identificador. */
  getModelosLlmByApiId(apiId: number): Observable<ModeloLlm[]> {
    return this.http.get<ModeloLlm[]>(`${this.apiUrl}/ObtenerModelosLlmPorApi?id=${apiId}`);  
  }

  createModeloLlm(modelo: ModeloLlm): Observable<any> {
    if (!modelo.nombre || !modelo.nombreTecnico || !modelo.endpoint || !modelo.plantilla_payload || !modelo.ruta_respuesta || !modelo.limiteTokens || !modelo.idApi) {
      console.error('Todos los campos son obligatorios para crear un Modelo LLM.');
      return throwError(() => new Error('Todos los campos son obligatorios para crear un Modelo LLM.'));
    }
    let body = new HttpParams()
      .set('nombre', modelo.nombre)
      .set('nombre_tecnico', modelo.nombreTecnico)
      .set('endpoint', modelo.endpoint)
      .set('plantilla_payload', modelo.plantilla_payload)
      .set('ruta_respuesta', modelo.ruta_respuesta)
      .set('limite_tokens', modelo.limiteTokens)
      .set('id_api', modelo.idApi)
      .set('crdate', new Date().toISOString());
      return this.http.post(`${this.apiUrl}/AgregarModeloLlm`, body);
  }

  getModeloLlmById(id: number): Observable<ModeloLlm> {
    return this.http.get<ModeloLlm>(`${this.apiUrl}/ObtenerModeloLlmPorId?id=${id}`);
  }

  updateModeloLlm(modelo: ModeloLlm): Observable<any> {
    let body = new HttpParams()
      .set('id', modelo.id)
      .set('nombre', modelo.nombre)
      .set('nombre_tecnico', modelo.nombreTecnico)
      .set('endpoint', modelo.endpoint)
      .set('plantilla_payload', modelo.plantilla_payload)
      .set('ruta_respuesta', modelo.ruta_respuesta)
      .set('limite_tokens', modelo.limiteTokens)
      .set('id_api', modelo.idApi)
      .set('crdate', new Date().toISOString());
    return this.http.post(`${this.apiUrl}/ActualizarModeloLlm`, body);
  }

  deleteModelo(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/EliminarModeloLlm`, body);
  }
}
