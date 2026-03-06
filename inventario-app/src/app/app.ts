import { Component, signal } from '@angular/core';
import { ProductoLista } from "./producto-lista/producto-lista";
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [RouterModule],

  styleUrls: ['./app.css']
})
export class App {
  protected readonly title = signal('inventario-app');
}
