import { Component, OnInit, Input, Output, EventEmitter, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { switchMap, takeWhile } from 'rxjs/operators';
import { PageEvent } from '@angular/material/paginator'; 

import { ArchivoService } from '../../services/archivo/archivo';
import { BaseDeDatosService } from '../../services/base_de_datos/base_de_datos';
import { FuenteService } from '../../services/fuente/fuente';
import { ServicioWebService } from '../../services/servicio_web/servicio-web.service';

@Component({
  selector: 'app-detalle-fuente',
  standalone: false,
  templateUrl: './detalle-fuente.html',
  styleUrl: './detalle-fuente.scss'
})
export class DetalleFuenteComponent implements OnInit, OnDestroy, OnChanges { 

  @Input() datasetId!: number; 
  @Input() tipoFuente = '';
  @Output() volver = new EventEmitter<void>();

  pollingSubscription!: Subscription;
  pageIndex: number = 0;
  pageSize: number = 4;
  cantidadFuentes: number = 0;
  fuentesActuales: any[] = []; 
  columnasDinamicas: string[] = [];
  fuenteSeleccionada: any = null;
  mostrarConfirmacion = false;

  get nombreTipoFuente(): string {
    const nombres: Record<string, string> = {
      ARCHIVO: 'archivos',
      BASE_DE_DATOS: 'bases de datos',
      SERVICIO_WEB: 'servicios web'
    };

    return nombres[this.tipoFuente] ?? this.tipoFuente?.toLowerCase() ?? 'fuentes';
  }

  constructor(
    private archivoService: ArchivoService, 
    private baseDeDatosService: BaseDeDatosService, 
    private fuenteService: FuenteService, 
    private servicioWebService: ServicioWebService
  ) {}

  ngOnInit() {
    this.cargarDatosYColumnas();
  }
  ngOnChanges(changes: SimpleChanges) {
    if (changes['tipoFuente'] && !changes['tipoFuente'].firstChange) {
      this.pageIndex = 0;
      this.cargarDatosYColumnas();
    }
  }

  ngOnDestroy() {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }
  }

  cargarDatosYColumnas() {
    if (!this.datasetId || !this.tipoFuente) return;

    if (this.tipoFuente === 'ARCHIVO') {
      this.columnasDinamicas = ['id', 'nombre', 'estado', 'acciones'];
      this.archivoService.getArchivosDeDatasetPaginados(this.datasetId, this.pageIndex, this.pageSize)
        .then(response => this.actualizarTabla(response.data, response.total))
        .catch(err => console.error("Error cargando archivos:", err));

    } else if (this.tipoFuente === 'BASE_DE_DATOS') {
      this.columnasDinamicas = ['id', 'nombre', 'modoExtraccion', 'estado', 'acciones'];
      this.baseDeDatosService.getBasesDeDatosDeDatasetPaginados(this.datasetId, this.pageIndex, this.pageSize)
        .then(response => this.actualizarTabla(response.data, response.total))
        .catch(err => console.error("Error cargando bases de datos:", err));

    } else if (this.tipoFuente === 'SERVICIO_WEB') {
      this.columnasDinamicas = ['id', 'nombre', 'metodoHttp', 'estado', 'acciones'];
      this.servicioWebService.getServiciosWebDeDatasetPaginados(this.datasetId, this.pageIndex, this.pageSize)
        .then(response => this.actualizarTabla(response.data, response.total))
        .catch(err => console.error("Error cargando servicios web:", err));
    }
  }

  private actualizarTabla(data: any[], total: number) {
    this.fuentesActuales = [...data]; 
    this.cantidadFuentes = total;
  }

  onPageChange(event: PageEvent) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.cargarDatosYColumnas();
  }

  onSincronizar(fuenteId: number) {
    this.fuenteService.reIndexarFuente(fuenteId).subscribe({
      next: () => {
        this.cargarDatosYColumnas();
        this.monitorearEstadoIndexacion(fuenteId);
      },
      error: (error) => console.error('Error al sincronizar la fuente:', error)
    });
  }

  onEliminar(fuenteId: number) {
    this.fuenteSeleccionada = this.fuentesActuales.find(fuente => fuente.id === fuenteId) ?? null;
    if (this.fuenteSeleccionada) {
      this.mostrarConfirmacion = true;
    }
  }

  monitorearEstadoIndexacion(idFuente: number) {
    if (this.pollingSubscription) {
      this.pollingSubscription.unsubscribe();
    }

    this.pollingSubscription = interval(3000).pipe(
      switchMap(() => this.fuenteService.getFuentePorId(idFuente)),
      takeWhile((fuenteActualizada: any) => fuenteActualizada.estadoIndexacion === 'INDEXANDO', true)
    ).subscribe({
      next: (fuenteActualizada) => {
        const index = this.fuentesActuales.findIndex(f => f.id === idFuente);
        if (index !== -1) {
          this.fuentesActuales[index].estadoIndexacion = fuenteActualizada.estadoIndexacion;
          this.fuentesActuales = [...this.fuentesActuales];
        }
      },
      error: (error) => console.error('Error al consultar el estado:', error)
    });
  }
}