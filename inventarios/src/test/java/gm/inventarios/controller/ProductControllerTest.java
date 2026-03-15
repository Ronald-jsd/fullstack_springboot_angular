package gm.inventarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gm.inventarios.exception.ResourceNotFoundException;
import gm.inventarios.model.ProductoDto;
import gm.inventarios.service.IProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Profile("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("GET /productos")
    class GetProductsTest {

        @Test
        void shouldReturnProductPage() throws Exception {

            ProductoDto producto = ProductoDto.builder()
                    .idProducto(1)
                    .precio(5000.00)
                    .descripcion("Laptop Lenovo")
                    .existencia(200)
                    .build();

            List<ProductoDto> list = Arrays.asList(producto);
            Page<ProductoDto> page = new PageImpl<>(list);

            when(productService.listProducts(any(PageRequest.class)))
                    .thenReturn(page);

            mockMvc.perform(
                            get("/inventario-app/productos")
                                    .param("page", "0")
                                    .param("size", "10")
                                    .param("direction", "Sort.Direction.ASC")
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].precio", is(5000.00)))
                    .andExpect(jsonPath("$.content[0].descripcion", is("Laptop Lenovo")));
        }
    }

    @Nested
    @DisplayName("GET /productos/{id}")
    class GetProductByIdTest {

        @Test
        void shouldReturnProduct() throws Exception {

            ProductoDto producto = ProductoDto.builder()
                    .idProducto(1)
                    .precio(5000.00)
                    .descripcion("Laptop Lenovo")
                    .existencia(200)
                    .build();

            when(productService.searchProductById(99))
                    .thenReturn(producto);

            mockMvc.perform(get("/inventario-app/productos/99"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.descripcion").value("Laptop Lenovo"))
                    .andExpect(jsonPath("$.precio").value(5000))
                    .andExpect(jsonPath("$.existencia").value(200));
        }
    }

    @Nested
    @DisplayName("POST /productos")
    class SaveProductTest {

        @Test
        void shouldSaveProduct() throws Exception {

            ProductoDto producto = ProductoDto.builder()
                    .precio(5000.00)
                    .descripcion("Laptop Lenovo")
                    .existencia(200)
                    .build();

            when(productService.saveProduct(any(ProductoDto.class)))
                    .thenReturn(producto);

            mockMvc.perform(
                            post("/inventario-app/productos")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsBytes(producto))
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.descripcion").value("Laptop Lenovo"))
                    .andExpect(jsonPath("$.precio").value(5000.00))
                    .andExpect(jsonPath("$.existencia").value(200));
        }

        @Test
        void shouldReturnValidationError() throws Exception {

            String invalidJson = """
                    {
                        "precio": -10,
                        "descripcion": "",
                        "existencia": -5
                    }
                    """;

            mockMvc.perform(post("/inventario-app/productos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error", is("Error de Validación")))
                    .andExpect(jsonPath("$.path", is("/inventario-app/productos")))
                    .andExpect(jsonPath("$.message.descripcion", hasSize(2)))
                    .andExpect(jsonPath("$.message.precio", hasSize(1)))
                    .andExpect(jsonPath("$.message.existencia", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("PUT /productos/{id}")
    class UpdateProductTest {

        @Test
        void shouldUpdateProduct() throws Exception {

            ProductoDto producto = ProductoDto.builder()
                    .precio(5000.00)
                    .descripcion("Laptop Lenovo")
                    .existencia(200)
                    .build();

            when(productService.updateProductById(anyInt(), any(ProductoDto.class)))
                    .thenReturn(producto);

            mockMvc.perform(
                            put("/inventario-app/productos/{id}", 1)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(producto))
                                    .accept(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.descripcion").value("Laptop Lenovo"))
                    .andExpect(jsonPath("$.precio").value(5000.00))
                    .andExpect(jsonPath("$.existencia").value(200));
        }
    }

    @Nested
    @DisplayName("DELETE /productos/{id}")
    class DeleteProductTest {

        @Test
        void shouldDeleteProduct() throws Exception {

            mockMvc.perform(delete("/inventario-app/productos/{id}", 1))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("Exception Handling Tests")
    class ExceptionTests {

        @Test
        void shouldHandleEntityNotFoundException() throws Exception {

            when(productService.searchProductById(99))
                    .thenThrow(new EntityNotFoundException("Producto no encontrado"));

            mockMvc.perform(get("/inventario-app/productos/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.statusCode").value(404))
                    .andExpect(jsonPath("$.error").value("Recurso no encontrado"))
                    .andExpect(jsonPath("$.message").value("Producto no encontrado"))
                    .andExpect(jsonPath("$.path").value("/inventario-app/productos/99"));
        }

        @Test
        void shouldHandleResourceNotFoundException() throws Exception {

            when(productService.searchProductById(99))
                    .thenThrow(new ResourceNotFoundException("Producto no encontrado"));

            mockMvc.perform(get("/inventario-app/productos/99"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.statusCode").value(400))
                    .andExpect(jsonPath("$.error").value("Bad Request"))
                    .andExpect(jsonPath("$.message").value("Producto no encontrado"))
                    .andExpect(jsonPath("$.path").value("/inventario-app/productos/99"));
        }

        @Test
        void shouldHandleGeneralException() throws Exception {

            when(productService.searchProductById(1))
                    .thenThrow(new RuntimeException("Error inesperado"));

            mockMvc.perform(get("/inventario-app/productos/1"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.statusCode").value(500))
                    .andExpect(jsonPath("$.error").value("Error Interno del Servidor"))
                    .andExpect(jsonPath("$.message").value("Error inesperado"))
                    .andExpect(jsonPath("$.path").value("/inventario-app/productos/1"));
        }
    }
}