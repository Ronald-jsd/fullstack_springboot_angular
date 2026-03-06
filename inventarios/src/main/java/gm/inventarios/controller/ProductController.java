package gm.inventarios.controller;

import gm.inventarios.model.ProductoDto;
import gm.inventarios.service.IProductService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping("inventario-app/productos")
@CrossOrigin(origins = {"http://localhost", "http://localhost:80"})
public class ProductController {

    private final IProductService iProductService;


    public ProductController(IProductService iProductService) {
        this.iProductService = iProductService;
    }

    @GetMapping
    public ResponseEntity<Page<ProductoDto>> getProducts(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "idProducto",
                    direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductoDto> productos = this.iProductService.listProducts(pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDto> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(iProductService.searchProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductoDto> addProduct(
            @RequestBody
            @Valid ProductoDto producto) {

        log.info("Agregando producto: " + producto.toString());
        return ResponseEntity.created(URI
                        .create("inventario-app/productos/"))
                .body(this.iProductService.saveProduct(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDto> updateproduct(@PathVariable Integer id,
                                                     @RequestBody
                                                     @Valid ProductoDto productoDto) {
        return ResponseEntity.ok(this.iProductService
                .updateProductById(id, productoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        iProductService.deleteProductById(id);
        return ResponseEntity.noContent().build(); // 404
    }

}

