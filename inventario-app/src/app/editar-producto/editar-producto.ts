import { Component, inject, OnInit } from '@angular/core';
import { Producto } from '../models/producto';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductoService } from '../servicios/ProductoService';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-editar-producto',
  imports: [FormsModule, CommonModule],
  templateUrl: './editar-producto.html',
  styleUrls: ['./editar-producto.css'],
  standalone: true,
})

export class EditarProducto implements OnInit {
  producto: Producto = new Producto();
  id!: number;
  mensajeError: string = '';
  erroresValidacion: any = {};
  cargando: boolean = false;
  cargandoDatos: boolean = true;

  private productoServicio = inject(ProductoService);
  private ruta = inject(ActivatedRoute);
  private enrutador = inject(Router);

  ngOnInit() {
    this.id = this.ruta.snapshot.params['id'];
    this.cargarProducto();
  }

  cargarProducto() {
    this.cargandoDatos = true;
    this.productoServicio.obtenerProductoPorId(this.id).subscribe({
      next: (datos) => {
        this.producto = datos;
        this.cargandoDatos = false;
      },
      error: (error: any) => {
        this.cargandoDatos = false;
        this.mensajeError = 'Error al cargar el producto: ' + (error.error?.message || 'Producto no encontrado');
        console.error('Error al cargar producto', error);
      }
    });
  }

  onSubmit() {
    this.guardarProducto();
  }

  guardarProducto() {
    if (!this.validarProducto()) {
      this.cargando = false;
      return;
    }

    this.cargando = true;
    this.mensajeError = '';
    this.erroresValidacion = {};

    this.productoServicio.editarProducto(this.id, this.producto).subscribe({
      next: () => {
        this.cargando = false;
        this.irProductoLista();
      },
      error: (error: any) => {
        this.cargando = false;
        this.procesarError(error);
        console.error('Error al editar producto', error);
      }
    });
  }

  validarProducto(): boolean {
    let valido = true;
    this.erroresValidacion = {};

    if (!this.producto.descripcion || this.producto.descripcion.trim() === '') {
      this.erroresValidacion.descripcion = 'La descripción es obligatoria';
      valido = false;
    }

    if (this.producto.precio === null || this.producto.precio === undefined || this.producto.precio < 0) {
      this.erroresValidacion.precio = 'El precio es obligatorio y debe ser mayor o igual a 0';
      valido = false;
    }

    if (this.producto.existencia === null || this.producto.existencia === undefined || this.producto.existencia < 0) {
      this.erroresValidacion.existencia = 'La existencia es obligatoria y debe ser mayor o igual a 0';
      valido = false;
    }

    return valido;
  }

  procesarError(error: any) {
    console.log('== ERROR COMPLETO ==');
    console.log('Status:', error.status);
    console.log('Headers:', error.headers);
    console.log('Error data:', error.error);

    this.erroresValidacion = {};
    this.mensajeError = '';

    if (error.status === 400) {
      if (error.error) {
        const errorData = error.error;
        console.log('Error data structure:', errorData);

        if (errorData.message && typeof errorData.message === 'object') {
          console.log('Errores por campo:', errorData.message);
          this.erroresValidacion = errorData.message;
          this.mensajeError = '';
        }
        else if (errorData.error) {
          console.log('Error general:', errorData.error);
          this.mensajeError = errorData.error;
        }
        else if (typeof errorData === 'string') {
          console.log('Error string:', errorData);
          this.mensajeError = errorData;
        }
        else {
          console.log('Estructura desconocida:', errorData);
          this.mensajeError = 'Error de validación en el formulario';
        }
      } else {
        this.mensajeError = 'Error de validación en el formulario';
      }
    } else {
      this.mensajeError = error.error?.message || error.message || 'Error inesperado al guardar el producto';
    }

    console.log('Estado final - mensajeError:', this.mensajeError);
    console.log('Estado final - erroresValidacion:', this.erroresValidacion);


    console.log('Errores procesados:', {
      mensajeError: this.mensajeError,
      erroresValidacion: this.erroresValidacion
    });
  }
  irProductoLista() {
    this.enrutador.navigate(['/productos']);
  }

  cancelar() {
    this.irProductoLista();
  }
}