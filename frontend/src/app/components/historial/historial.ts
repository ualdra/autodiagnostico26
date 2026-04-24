import { Component } from '@angular/core';

@Component({
  selector: 'app-historial-page',
  standalone: true,
  template: '<section class="page-shell"></section>',
  styles: [
    ':host { display: block; } .page-shell { min-height: 40vh; }'
  ]
})
export class HistorialComponent {}
