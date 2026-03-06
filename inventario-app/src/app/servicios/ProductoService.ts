import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto'; 
import { Page } from '../interfaces/page';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class ProductoService {

private urlBase = environment.apiUrl;  
private clienteHttp = inject(HttpClient);

  obtenerProductosLista(): Observable<Producto[]>{
    return this.clienteHttp.get<Producto[]>(this.urlBase);   
  }

  obtenerProductosPaginados(page: number, size: number, sort?:string): Observable<Page<Producto>> {
    let url =  `${this.urlBase}?page=${page}&size=${size}`;
    if( sort ) url+=`&sort=${sort}`;
    console.log('URL completa:', url);

    return this.clienteHttp.get<Page<Producto>>(url);
  }

   agregarProducto(producto: Producto): Observable<Object> {
    return this.clienteHttp.post(this.urlBase, producto);
  }

  obtenerProductoPorId(id: number){
    return this.clienteHttp.get<Producto>(`${this.urlBase}/${id}`);
  }

  editarProducto(id: number, producto: Producto) {
    return this.clienteHttp.put(`${this.urlBase}/${id}`, producto);
  }

  eliminarProducto(id: number): Observable<Object>{
    return this.clienteHttp.delete(`${this.urlBase}/${id}`);
  }
}
