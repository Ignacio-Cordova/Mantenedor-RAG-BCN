import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs'; 
import { BaseDeDatos } from '../../models/base_de_datos';
import { Conexion } from '../../models/conexion';

@Injectable({
  providedIn: 'root',
})
export class BaseDeDatosService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  constructor(private http: HttpClient) {}  

  /** Registra una fuente PostgreSQL y reutiliza o crea la conexión indicada. */
  createBaseDeDatos(baseDeDatos: BaseDeDatos, conexion: Conexion): Observable<any> {
    let body = new HttpParams()
      .set('nombre', baseDeDatos.nombre)
      .set('tipo', baseDeDatos.tipo)
      .set('id_dataset', baseDeDatos.idDataset || 0)
      .set('id_externo_vectorial', baseDeDatos.idExternoVectorial || 0)
      .set('id_indexacion', baseDeDatos.idIndexacion || 0)
      .set('tabla_objetivo', baseDeDatos.tablaObjetivo || '')
      .set('query_sql', baseDeDatos.querySql || '')
      .set('modo_extraccion', baseDeDatos.modoExtraccion || '')
      .set('columna_contenido', baseDeDatos.columnaContenido || '')
      .set('id_conexion', baseDeDatos.idConexion || 0)
      .set('nombre_conexion', conexion.nombre || '')
      .set('host', conexion.host || '')
      .set('puerto', conexion.puerto || 0)
      .set('usuario', conexion.usuario || '')
      .set('password', conexion.password || '')
      .set('motor', conexion.motor || '');
    return this.http.post(`${this.apiUrl}/AgregarBaseDeDatos`, body);
  }

  
  getTiposBasesDeDatos(): Observable<string[]> {
    return of(['PostgreSQL']);
  }

  
  /** Ejecuta una consulta de previsualización limitada por el backend. */
  getFilas(bd: BaseDeDatos): Observable<any[]> {
    let body = new HttpParams()
      .set('nombre', bd.idConexion ? '' : '')
      .set('query', bd.querySql || '')
      .set('columna_contenido', bd.columnaContenido || '')
      .set('id_conexion', bd.idConexion ? bd.idConexion.toString() : '0')
      .set('tabla_objetivo', bd.tablaObjetivo || '')
      .set('tipo', bd.tipo || '')
      .set('modo_extraccion', bd.modoExtraccion || '');
    return this.http.post<any[]>(`${this.apiUrl}/ObtenerFilas`, body);
  }

  getBasesDeDatosDeDatasetPaginados(datasetId: number, page: number, size: number) {
    const offset = page * size; 
    
    const url = `${this.apiUrl}/ObtenerBDPorDatasetPaginados?id=${datasetId}&offset=${offset}&limit=${size}`;
    
    return fetch(url)
      .then(response => {
        if (!response.ok) throw new Error('Error en el servidor');
        return response.json();
      });
    }

  deleteBaseDeDatos(id: number): Observable<any> {
    const body = { id: id };
    return this.http.post(`${this.apiUrl}/EliminarBaseDeDatos`, body);
  }

}
