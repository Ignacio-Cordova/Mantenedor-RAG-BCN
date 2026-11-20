import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ArchivoService {

  constructor(private http: HttpClient) { }
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  /** Obtiene las fuentes ARCHIVO asociadas a un dataset con paginación. */
  getArchivosDeDatasetPaginados(datasetId: number, page: number, size: number) {
    const offset = page * size; 
    const url = `${this.apiUrl}/ObtenerArchivosPorDatasetPaginados?id=${datasetId}&offset=${offset}&limit=${size}`;
    
    return fetch(url)
      .then(response => {
        if (!response.ok) throw new Error('Error en el servidor');
        return response.json();
      });
  }
  
  
  /** Elimina la fuente y sus vectores asociados mediante un cuerpo form-urlencoded. */
  deleteArchivo(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded');

    return this.http.post(`${this.apiUrl}/EliminarArchivo`, body.toString(), { headers });
  }

  
  /** Envía el archivo multipart junto con dataset, nombre y extensión por query string. */
  crearArchivo(archivoData: FormData, idDataset: number, nombre: string, extension: string): Observable<any> {
    const parametros = [
      `id_dataset=${encodeURIComponent(idDataset.toString())}`,
      `nombre=${encodeURIComponent(nombre)}`,
      `extension=${encodeURIComponent(extension)}`
    ].join('&');
    const urlLimpia = `${this.apiUrl}/AgregarArchivo?${parametros}`;
    return this.http.post(urlLimpia, archivoData);
  }
}

