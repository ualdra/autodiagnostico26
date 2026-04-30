import { Component } from '@angular/core';

@Component({
  selector: 'app-mis-vehiculos-page',
  standalone: true,
  template: '<section class="page-shell"></section>',
  styles: [
    ':host { display: block; } .page-shell { min-height: 40vh; }'
  ]
})
export class MisVehiculosComponent {}
