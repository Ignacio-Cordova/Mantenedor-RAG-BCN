import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RetrievalMethodService {
  
  private apiUrl = 'http://localhost:8080/MantenedorRAG/servicio'; 

  /** Consulta por fetch el catálogo de métodos de recuperación disponibles. */
  getRetrievalMethods() {
    return fetch(this.apiUrl + '/ObtenerRetrievalMethods')
      .then(response => {
        if (!response.ok) throw new Error('Error en el servidor');
        return response.json();
      });
  }
}
