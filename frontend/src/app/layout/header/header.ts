import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.html',
  styleUrl: './header.scss',
  standalone: false,
})
export class Header {
  @Output() abrirCreacion = new EventEmitter<void>();
  @Output() abrirAjustesGlobales = new EventEmitter<void>();

  abrirModalCreacion() {
    this.abrirCreacion.emit();
  }
  abrirModalAjustesGlobales() {
    this.abrirAjustesGlobales.emit();
  }
}
