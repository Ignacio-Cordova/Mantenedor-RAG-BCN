import { Injectable } from '@angular/core';
import { IndexMethod } from '../../models/index_method';
import { Observable } from 'rxjs/internal/Observable';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class IndexMethodService {

  constructor(private http: HttpClient) { }
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  /** Lista los métodos de fragmentación disponibles para el dataset. */
  getIndexMethods(): Observable<IndexMethod[]> {
      return this.http.get<IndexMethod[]>(this.apiUrl + '/ObtenerIndexMethods');
    }

  /** Obtiene un método de fragmentación por su identificador. */
  getIndexMethodById(id: number): Observable<IndexMethod> {
    return this.http.get<IndexMethod>(`${this.apiUrl}/ObtenerIndexMethodPorId?id=${id}`);
  }
}
