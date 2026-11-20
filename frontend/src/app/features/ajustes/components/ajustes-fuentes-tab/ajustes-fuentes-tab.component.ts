import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-ajustes-fuentes-tab',
  templateUrl: './ajustes-fuentes-tab.component.html',
  styleUrls: ['./ajustes-fuentes-tab.component.scss'],
  standalone: false
})
export class AjustesFuentesTabComponent {
  @Input() tiposFuentes: string[] = [];
  @Output() verFuentes = new EventEmitter<string>();
  @Output() abrirArchivo = new EventEmitter<void>();
  @Output() abrirConexion = new EventEmitter<string>();
  @Output() indexar = new EventEmitter<void>();

  onVerFuentes(tipo: string) {
    this.verFuentes.emit(tipo);
  }

  onAbrirArchivo() {
    this.abrirArchivo.emit();
  }

  onAbrirConexion(tipo: string) {
    this.abrirConexion.emit(tipo);
  }

  onIndexarTodo() {
    this.indexar.emit();
  }
}