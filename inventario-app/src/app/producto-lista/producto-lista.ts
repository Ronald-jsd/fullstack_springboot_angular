import { Component, inject, OnInit } from '@angular/core';
import { Producto } from '../models/producto';
import { ProductoService } from '../servicios/ProductoService';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Page } from '../interfaces/page';

@Component({
  selector: 'app-producto-lista',
  imports: [CommonModule],
  templateUrl: './producto-lista.html',
  standalone: true,
  styleUrls: ['./producto-lista.css']
})

export class ProductoLista implements OnInit {
  productos: Producto[] = [];
  page: number = 0;
  totalPages: number = 0;
  totalElements: number = 0;
  size: number = 9;
  cargando: boolean = false;
  mensajeError: string = '';
  errorConexion: boolean = false;

  private productoServicio = inject(ProductoService);
  private enrutador = inject(Router);

  ngOnInit() {
    this.obtenerProductos();
  }

  obtenerProductos() {
    this.cargando = true;
    this.productoServicio.obtenerProductosPaginados(
      this.page,
      this.size,
      `${this.sortField},${this.sortDirection}`
    ).subscribe({
      next: (data: Page<Producto>) => {
        this.productos = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.cargando = false;
      },
      error: (error: any) => {
        this.cargando = false;
        this.errorConexion = true;
        this.mensajeError = 'Error al cargar los productos: ' + (error.error?.message || 'Error de conexión');
        console.error('Error al obtener productos', error);
      }
    });
  }

  editarProducto(id: number | undefined) {
    if (id !== undefined && id !== null) {
      this.enrutador.navigate(['editar-producto', id]);
    } else {
      console.error('ID de producto no válido');
      this.mensajeError = 'No se puede editar: ID de producto no válido';
    }
  }

  mostrarModalEliminar = false;
  productoIdAEliminar: number | null = null;

  eliminarProducto(idProducto: number) {
    this.productoIdAEliminar = idProducto;
    this.mostrarModalEliminar = true;
  }

  cerrarModal() {
    this.mostrarModalEliminar = false;
    this.productoIdAEliminar = null;
  }

  confirmarEliminar() {
    if (this.productoIdAEliminar) {
      this.productoServicio.eliminarProducto(this.productoIdAEliminar).subscribe({
        next: () => {
          this.obtenerProductos();
          this.cerrarModal();
        },
        error: (err) => {
          console.error('Error al eliminar', err);
        }
      });
    }
  }

  get rangoActual(): string {
    if (this.totalElements === 0) return '0 - 0';
    const inicio = this.page * this.size + 1;
    const fin = Math.min(this.page * this.size + this.productos.length, this.totalElements);
    return `${inicio} - ${fin}`;
  }

  paginaAnterior() {
    if (this.page > 0) {
      this.page--;
      this.obtenerProductos();
    }
  }

  paginaSiguiente() {
    if (this.page + 1 < this.totalPages) {
      this.page++;
      this.obtenerProductos();
    }
  }

  goToPage(pagina: number) {
    if (pagina >= 0 && pagina < this.totalPages && pagina !== this.page) {
      this.page = pagina;
      this.obtenerProductos();
    }
  }

  getPageNumbers(): number[] {
    const paginas: number[] = [];
    const maxPaginasMostradas = 5;

    if (this.totalPages <= maxPaginasMostradas) {
      for (let i = 0; i < this.totalPages; i++) {
        paginas.push(i);
      }
    } else {
      let inicio = Math.max(0, this.page - 2);
      let fin = Math.min(this.totalPages - 1, inicio + 4);

      if (fin - inicio < 4) {
        inicio = Math.max(0, fin - 4);
      }

      for (let i = inicio; i <= fin; i++) {
        paginas.push(i);
      }
    }

    return paginas;
  }

  trackById(index: number, item: Producto): number {
    return item.idProducto ?? index;
  }

  sortField: string = 'idProducto';
  sortDirection: string = 'asc';
  ordenarPor(campo: string) {
    if (this.sortField === campo) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = campo;
      this.sortDirection = 'asc';
    }
    this.obtenerProductos();
  }

}