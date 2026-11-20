import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable, throwError } from 'rxjs';
import { ModeloEmbedding } from '../../models/modelo-embedding'; 

@Injectable({
  providedIn: 'root' 
})
export class ModeloEmbeddingService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  constructor(private http: HttpClient) { }

  /** Lista los modelos Embedding disponibles sin modificar la configuración. */
  getModelosEmbedding(): Observable<ModeloEmbedding[]> {
    return this.http.get<ModeloEmbedding[]>(this.apiUrl + '/ObtenerModelosEmbedding');
  }

  getModeloEmbeddingById(id: number): Observable<ModeloEmbedding> {
    return this.http.get<ModeloEmbedding>(`${this.apiUrl}/ObtenerModeloEmbeddingPorId?id=${id}`);
  }

  createModeloEmbedding(modelo: ModeloEmbedding): Observable<any> {
    if (!modelo.nombreTecnico || !modelo.dimensiones || !modelo.maxTokensInput || !modelo.idApi) {
      return throwError(() => new Error('Todos los campos son obligatorios para crear un modelo Embedding.'));
    }
    const body = new HttpParams()
      .set('nombre_tecnico', modelo.nombreTecnico)
      .set('dimensiones', modelo.dimensiones)
      .set('max_tokens_input', modelo.maxTokensInput)
      .set('id_api', modelo.idApi);
    return this.http.post(`${this.apiUrl}/AgregarModeloEmbedding`, body);
  }

  updateModeloEmbedding(modelo: ModeloEmbedding): Observable<any> {
    const body = new HttpParams()
      .set('id', modelo.id)
      .set('nombre_tecnico', modelo.nombreTecnico)
      .set('dimensiones', modelo.dimensiones)
      .set('max_tokens_input', modelo.maxTokensInput)
      .set('id_api', modelo.idApi);
    return this.http.post(`${this.apiUrl}/ActualizarModeloEmbedding`, body);
  }

  deleteModeloEmbedding(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/EliminarModeloEmbedding`, body);
  }
}
