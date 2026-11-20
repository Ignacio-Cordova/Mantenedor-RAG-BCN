import { booleanAttribute, Component, Input } from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.html',
  styleUrl: './button.scss',
  standalone: false,
})
export class Button {
  @Input() text: string = 'Button';
  @Input() icon: string = '';
  @Input() tipo: string = 'default';
  @Input({ transform: booleanAttribute }) disabled = false;
}
