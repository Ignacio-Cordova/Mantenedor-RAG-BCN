import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable, throwError } from 'rxjs';
import { Api } from '../../models/api'; 

@Injectable({
  providedIn: 'root' 
})
export class ApiService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  constructor(private http: HttpClient) { }

  /** Obtiene proveedores de API con paginación. */
  getApisPaginadas(pageIndex: number, pageSize: number): Observable<{ data: Api[]; total: number }> {
    const offset = pageIndex * pageSize;
    const params = new HttpParams()
      .set('offset', offset.toString())
      .set('limit', pageSize.toString());
    return this.http.get<{data: Api[], total: number}>(`${this.apiUrl}/ObtenerApisPaginadas`, { params });
  }

  
  /** Lista todos los proveedores de API registrados. */
  getApis(): Observable<Api[]> {
    return this.http.get<Api[]>(`${this.apiUrl}/ObtenerApis`);
  }

  
  /** Envía un proveedor nuevo con credenciales y autenticación mediante parámetros de formulario. */
  createApi(api: Api): Observable<any> {

    if (!api.nombreProveedor || !api.baseUrl) {
      console.error('Todos los campos son obligatorios para crear una API.');
      return throwError(() => new Error('Todos los campos son obligatorios para crear una API.'));
    }
    
    let body = new HttpParams()
      .set('nombre_proveedor', api.nombreProveedor)
      .set('base_url', api.baseUrl)
      .set('tipo_autenticacion', api.tipoAutenticacion)
      .set('api_key_encriptada', api.apiKeyEncriptada)
      .set('organizacion_id', api.organizacionId)
      .set('crdate', new Date().toISOString());

    return this.http.post(`${this.apiUrl}/AgregarApi`, body);
  }

  deleteApi(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/EliminarApi`, body);
  }

  updateApi(api: Api): Observable<any> {
    let body = new HttpParams()
      .set('id', api.id)
      .set('nombre_proveedor', api.nombreProveedor)
      .set('base_url', api.baseUrl)
      .set('tipo_autenticacion', api.tipoAutenticacion)
      .set('api_key_encriptada', api.apiKeyEncriptada)
      .set('organizacion_id', api.organizacionId)
      .set('crdate', new Date().toISOString())

    return this.http.post(`${this.apiUrl}/ActualizarApi`, body);
  }
}
