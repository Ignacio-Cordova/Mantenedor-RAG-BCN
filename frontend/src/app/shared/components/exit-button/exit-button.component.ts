import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-exit-button',
  standalone: false,
  templateUrl: './exit-button.component.html',
  styleUrl: './exit-button.component.scss'
})
export class ExitButtonComponent {

  @Output() cerrarModal = new EventEmitter<void>();

  cerrar(): void {
    this.cerrarModal.emit();
  }
}
