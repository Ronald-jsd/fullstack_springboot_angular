import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Producto } from '../models/producto';
import { ProductoService } from '../servicios/ProductoService';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-agregar-producto',
  imports: [FormsModule, CommonModule],
  templateUrl: './agregar-producto.html', // Cambiado: sin .component
  styleUrls: ['./agregar-producto.css'], // Cambiado: sin .component
  standalone: true,
})
export class AgregarProducto {
  private productoService = inject(ProductoService);
  private enrutador = inject(Router);

  producto: Producto = new Producto();
  mensajeError: string | null = null;
  erroresValidacion: any = {};
  cargando: boolean = false;



  onSubmit() {
    this.guardarProducto();
  }


  guardarProducto() {
    this.validarProducto();

    this.cargando = true;
    this.mensajeError = '';


    const productoParaEnviar = {
      ...this.producto,
      precio: Number(this.producto.precio),
      existencia: Number(this.producto.existencia)
    };

    console.log('Enviando producto:', productoParaEnviar);

    this.productoService.agregarProducto(productoParaEnviar).subscribe({
      next: () => {
        this.cargando = false;
        this.irProductoLista();
      },
      error: (error: any) => {
        this.cargando = false;
        this.procesarError(error); 
        console.error("Error al guardar el producto", error);
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
    console.log('Error data:', error.error);

    this.mensajeError = '';

    if (error.status === 400) {
      if (error.error) {
        const errorData = error.error;
        console.log('Error data structure:', errorData);

        if (errorData.message && typeof errorData.message === 'object') {
          console.log('ERRORES DEL BACKEND POR CAMPO:', errorData.message);
          this.erroresValidacion = errorData.message; // Asigna los errores del backend
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

    console.log('MENSAJES FINALES:');
    console.log('- mensajeError:', this.mensajeError);
    console.log('- erroresValidacion (BACKEND):', this.erroresValidacion);
  }

  irProductoLista() {
    this.enrutador.navigate(['/productos']);
  }

  cancelar() {
    this.irProductoLista();
  }
}

