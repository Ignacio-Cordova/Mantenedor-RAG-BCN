import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable, of } from 'rxjs';
import { Fuente } from '../../models/fuente'; 

@Injectable({
  providedIn: 'root' 
})
export class FuenteService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  constructor(private http: HttpClient) { }

  /** Lista las fuentes asociadas a un dataset. */
  getFuentesDeDataset(datasetId: number): Observable<Fuente[]> {
    return this.http.get<Fuente[]>(`${this.apiUrl}/ObtenerFuentesPorDataset?id=${datasetId}`);
  }

  getFuentePorId(id: number): Observable<Fuente> {
    return this.http.get<Fuente>(`${this.apiUrl}/ObtenerFuentePorId?id=${id}`);
  }

  getTiposFuentes(): Observable<string[]> {
    return of(['ARCHIVO', 'BASE_DE_DATOS', 'SERVICIO_WEB']);
  }

  reIndexarFuente(id: number): Observable<any> {
    const params = new HttpParams().set('id', id);
    return this.http.get(`${this.apiUrl}/ReIndexarFuente`, { params });
}

  indexarFuente(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/IndexarFuente`, body);
  }

  deleteFuente(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/EliminarFuente`, body);
  }
}