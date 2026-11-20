import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable } from 'rxjs';  
import { Dataset } from '../../models/dataset'; 

@Injectable({
  providedIn: 'root' 
})

export class DatasetService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  constructor(private http: HttpClient) { }

  /** Obtiene la configuración completa de un dataset por su identificador. */
  getDataset(id: number): Observable<Dataset> {
    return this.http.get<Dataset>(`${this.apiUrl}/ObtenerDatasetPorId?id=${id}`);
  }

  
  /** Lista todos los datasets disponibles. */
  getDatasets(): Observable<Dataset[]> {
    return this.http.get<Dataset[]>(`${this.apiUrl}/ObtenerDatasets`);
  }
    
  
  /** Crea un dataset mediante formulario; el backend genera su colección y persiste los parámetros RAG. */
  createDataset(dataset: Dataset): Observable<any> {
    let body = new HttpParams()
      .set('nombre', dataset.nombre)
      .set('tamano_chunk', dataset.tamanoChunk ?? '')
      .set('overlap', dataset.overlap ?? '')
      .set('collection_name', dataset.collectionName ?? '')
      .set('top_k', dataset.topK ?? '')
      .set('system_prompt', dataset.systemPrompt ?? '')
      .set('id_api', dataset.idApi ?? '')
      .set('id_modelo_llm', dataset.idModeloLlm ?? '')
      .set('id_modelo_embedding', dataset.idModeloEmbedding ?? '')
      .set('id_retrieval_method', dataset.idRetrievalMethod ?? '')
      .set('id_index_method', dataset.idIndexMethod ?? '')

    return this.http.post(`${this.apiUrl}/AgregarDataset`, body);
  }

  
  /** Actualiza los parámetros RAG y la configuración operativa del dataset. */
  updateDataset(dataset: Dataset): Observable<any> {
    let body = new HttpParams()
      .set('id', dataset.id)
      .set('nombre', dataset.nombre)
      .set('tamano_chunk', dataset.tamanoChunk ?? '')
      .set('overlap', dataset.overlap ?? '')
      .set('collection_name', dataset.collectionName ?? '')
      .set('top_k', dataset.topK ?? '')
      .set('system_prompt', dataset.systemPrompt ?? '')
      .set('id_api', dataset.idApi ?? '')
      .set('id_modelo_llm', dataset.idModeloLlm ?? '')
      .set('id_modelo_embedding', dataset.idModeloEmbedding ?? '')
      .set('id_retrieval_method', dataset.idRetrievalMethod ?? '')
      .set('id_index_method', dataset.idIndexMethod ?? '')  

    return this.http.post(`${this.apiUrl}/ActualizarDataset`, body);
  }

  
  /** Inicia la indexación de todas las fuentes pendientes del dataset. */
  indexarDataset(id: number): Observable<any> {
    const body = new HttpParams().set('id', id); 
    return this.http.post(`${this.apiUrl}/IndexarFuentes`, body);
  }

  
  /** Vacía y reconstruye el índice completo del dataset; es una operación potencialmente costosa. */
  reIndexarDataset(id: number): Observable<any> {
    const body = { id: id };

    return this.http.post(`${this.apiUrl}/ReIndexarDataset`, body);
  }

  deleteDataset(id: number): Observable<any> {
    const body = new HttpParams().set('id', id); 
    return this.http.post(`${this.apiUrl}/EliminarDataset`, body);
  }
}
