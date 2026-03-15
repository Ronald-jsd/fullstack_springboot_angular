package gm.inventarios.service;

import gm.inventarios.mapper.ProductMapper;
import gm.inventarios.model.Producto;
import gm.inventarios.model.ProductoDto;
import gm.inventarios.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Profile("test")
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductServiceImpl productService;


    @Nested
    @DisplayName("List Products Tests")
    class ListProductsTests {

        @Test
        @DisplayName("Debe retornar una página con productos")
        void shouldReturnProductPage() {

            Producto producto = Producto.builder()
                    .idProducto(1)
                    .descripcion("TV Led 4k")
                    .precio(999.99)
                    .existencia(250)
                    .build();

            ProductoDto dto = ProductoDto.builder()
                    .idProducto(1)
                    .descripcion("TV Led 4k")
                    .precio(999.99)
                    .existencia(250)
                    .build();

            Page<Producto> page =
                    new PageImpl<>(Collections.singletonList(producto));

            when(productRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(productMapper.toProductoDto(producto)).thenReturn(dto);

            Page<ProductoDto> result =
                    productService.listProducts(PageRequest.of(0, 10));

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            verify(productRepository).findAll(any(Pageable.class));
            verify(productMapper).toProductoDto(producto);
        }
    }


    @Nested
    @DisplayName("Search Product Tests")
    class SearchProductTests {

        @Test
        @DisplayName("Debe encontrar producto por ID")
        void shouldReturnProduct() {

            Producto producto = Producto.builder()
                    .idProducto(1)
                    .descripcion("Laptop Lenovo")
                    .existencia(100)
                    .precio(2900.00)
                    .build();

            ProductoDto dto = ProductoDto.builder()
                    .idProducto(1)
                    .descripcion("Laptop Lenovo")
                    .existencia(100)
                    .precio(2900.00)
                    .build();

            when(productRepository.findById(any())).thenReturn(Optional.of(producto));
            when(productMapper.toProductoDto(producto)).thenReturn(dto);

            ProductoDto result = productService.searchProductById(1);

            assertNotNull(result);
            assertEquals(1, result.getIdProducto());

            verify(productRepository).findById(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el producto no existe")
        void shouldThrowException() {

            when(productRepository.findById(any())).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> productService.searchProductById(1));
        }
    }

    @Nested
    @DisplayName("Save Product Tests")
    class SaveProductTests {

        @Test
        @DisplayName("Debe guardar producto")
        void shouldSaveProduct() {

            Producto producto = Producto.builder()
                    .idProducto(1)
                    .descripcion("Laptop Lenovo")
                    .existencia(100)
                    .precio(2900.00)
                    .build();

            ProductoDto dto = ProductoDto.builder()
                    .idProducto(1)
                    .descripcion("Laptop Lenovo")
                    .existencia(100)
                    .precio(2900.00)
                    .build();

            when(productMapper.toEntity(any())).thenReturn(producto);
            when(productRepository.save(any())).thenReturn(producto);
            when(productMapper.toProductoDto(producto)).thenReturn(dto);

            ProductoDto result = productService.saveProduct(dto);

            assertNotNull(result);

            verify(productMapper).toEntity(dto);
            verify(productRepository).save(producto);
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Debe actualizar producto")
        void shouldUpdateProduct() {

            Producto producto = Producto.builder()
                    .idProducto(1)
                    .descripcion("Laptop Lenovo")
                    .existencia(200)
                    .precio(2500.00)
                    .build();

            ProductoDto dto = ProductoDto.builder()
                    .idProducto(1)
                    .descripcion("Laptop Lenovo X")
                    .existencia(100)
                    .precio(3000.00)
                    .build();

            when(productRepository.findById(any())).thenReturn(Optional.of(producto));
            when(productRepository.save(any())).thenReturn(producto);
            when(productMapper.toProductoDto(any())).thenReturn(dto);

            productService.updateProductById(1, dto);

            ArgumentCaptor<Producto> captor =
                    ArgumentCaptor.forClass(Producto.class);

            verify(productRepository).save(captor.capture());

            Producto saved = captor.getValue();

            assertEquals("Laptop Lenovo X", saved.getDescripcion());
            assertEquals(100, saved.getExistencia());
        }

        @Test
        @DisplayName("Debe lanzar excepción si el producto no existe")
        void shouldThrowException() {

            when(productRepository.findById(any())).thenReturn(Optional.empty());

            ProductoDto dto = new ProductoDto();

            assertThrows(EntityNotFoundException.class,
                    () -> productService.updateProductById(1, dto));
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Debe eliminar producto")
        void shouldDeleteProduct() {

            when(productRepository.existsById(any())).thenReturn(true);

            productService.deleteProductById(1);

            verify(productRepository).deleteById(any());
        }

        @Test
        @DisplayName("Debe lanzar excepción si no existe")
        void shouldThrowException() {

            when(productRepository.existsById(any())).thenReturn(false);

            assertThrows(EntityNotFoundException.class,
                    () -> productService.deleteProductById(1));
        }
    }
}