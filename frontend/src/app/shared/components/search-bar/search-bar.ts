import { booleanAttribute, Component, Input } from '@angular/core';

@Component({
  selector: 'app-search-bar',
  templateUrl: './search-bar.html',
  styleUrl: './search-bar.scss',
  standalone: false,
})
export class SearchBar {

  @Input({ transform: booleanAttribute }) disabled = false;
}
