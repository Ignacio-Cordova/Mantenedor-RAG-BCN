import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http'; 
import { Observable, throwError } from 'rxjs'; 
import { Conexion } from '../../models/conexion';

interface RespuestaTablas {
  tablas: string[];
}

@Injectable({
  providedIn: 'root'
})

export class ConexionService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 
  
  constructor(private http: HttpClient) { }

  /** Obtiene tablas PostgreSQL visibles mediante las credenciales suministradas. */
  getTablas(payload: Conexion): Observable<RespuestaTablas> {
    let body = new HttpParams();
    
    body = body.set('host', payload.host);
    body = body.set('puerto', payload.puerto);
    body = body.set('usuario', payload.usuario);
    body = body.set('password', payload.password);
    body = body.set('motor', payload.motor.toUpperCase());
    body = body.set('nombre', payload.nombre); 
    return this.http.post<RespuestaTablas>(`${this.apiUrl}/TestConnection`, body);
  }

  
  /** Lista todas las conexiones JDBC registradas. */
  getConexiones(): Observable<Conexion[]> {
    return this.http.get<Conexion[]>(`${this.apiUrl}/ObtenerConexiones`);
  }

  getConexionesPaginadas(pageIndex: number, pageSize: number) {
    const offset = pageIndex * pageSize;
    const params = new HttpParams()
      .set('offset', offset.toString())
      .set('limit', pageSize.toString());
    return this.http.get<{data: Conexion[], total: number}>(`${this.apiUrl}/ObtenerConexionesPaginadas`, { params });
  }
  
  
  /** Registra una conexión PostgreSQL mediante parámetros de formulario. */
  addConexion(conexion: Conexion): Observable<any> {
    if (!conexion.nombre || !conexion.host || !conexion.puerto || !conexion.usuario || !conexion.password || !conexion.motor) {
      console.error('Todos los campos son obligatorios para crear una conexión.');
      return throwError(() => new Error('Todos los campos son obligatorios para crear una conexión.'));
    }
    let body = new HttpParams()
      .set('nombre', conexion.nombre)
      .set('host', conexion.host)
      .set('puerto', conexion.puerto)
      .set('usuario', conexion.usuario)
      .set('password', conexion.password)
      .set('motor', conexion.motor.toUpperCase())
      .set('crdate', new Date().toISOString());
    return this.http.post(`${this.apiUrl}/AgregarConexion`, body);
  }

  
  /** Actualiza una conexión PostgreSQL existente. */
  updateConexion(conexion: Conexion): Observable<any> {
    let body = new HttpParams()
      .set('id', conexion.id)
      .set('nombre', conexion.nombre)
      .set('host', conexion.host)
      .set('puerto', conexion.puerto)
      .set('usuario', conexion.usuario)
      .set('password', conexion.password)
      .set('motor', conexion.motor.toUpperCase())
      .set('crdate', new Date().toISOString());

    return this.http.post(`${this.apiUrl}/ActualizarConexion`, body);
  }
  
  deleteConexion(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/EliminarConexion`, body);
  }

}
