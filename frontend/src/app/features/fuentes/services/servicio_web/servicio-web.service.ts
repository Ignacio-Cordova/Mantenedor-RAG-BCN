import { Injectable } from '@angular/core';
import { ServicioWeb } from '../../models/servicio_web';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http'; 
import { Observable } from 'rxjs/internal/Observable';

@Injectable({
  providedIn: 'root'
})
export class ServicioWebService {

  constructor(private http: HttpClient) { }
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  /** Registra una fuente REST mediante application/x-www-form-urlencoded y conserva rutas de texto y metadatos. */
  createServicioWeb(servicioWebData: ServicioWeb): Observable<any> {
    let body = new HttpParams()
      .set('nombre', servicioWebData.nombre)
      .set('url_endpoint', servicioWebData.urlEndpoint)
      .set('metodo_http', servicioWebData.metodoHttp)
      .set('headers', servicioWebData.headersJson)
      .set('body', servicioWebData.bodyPayload)
      .set('ruta_texto', servicioWebData.rutaTexto)
      .set('rutas_metadatos', servicioWebData.rutasMetadatos)
      .set('estado_indexacion', servicioWebData.estadoIndexacion)
      .set('tipo', servicioWebData.tipo)
      .set('id_dataset', servicioWebData.idDataset ?? '')
      .set('id_indexacion', servicioWebData.idIndexacion ?? '')
      .set('id_externo_vectorial', servicioWebData.idExternoVectorial ?? '')
      .set('crdate', servicioWebData.crdate);
    
     const headers = new HttpHeaders()
      .set('Content-Type', 'application/x-www-form-urlencoded');
    return this.http.post(`${this.apiUrl}/AgregarServicioWeb`, body.toString(), { headers });
  }

  getServiciosWebDeDatasetPaginados(datasetId: number, page: number, size: number) {
    const offset = page * size; 
    const url = `${this.apiUrl}/ObtenerServiciosWebPorDatasetPaginados?id=${datasetId}&offset=${offset}&limit=${size}`;
    return fetch(url)
      .then(response => {
        if (!response.ok) throw new Error('Error en el servidor');
        return response.json();
      });
  }
  
  deleteServicioWeb(id: number): Observable<any> {
    const body = new HttpParams().set('id', id);
    return this.http.post(`${this.apiUrl}/EliminarServicioWeb`, body);
  }
}
