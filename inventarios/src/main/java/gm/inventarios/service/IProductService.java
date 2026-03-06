package gm.inventarios.service;

import gm.inventarios.model.ProductoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService {

    Page<ProductoDto> listProducts(Pageable pageable);

    ProductoDto searchProductById(Integer idProducto);

    ProductoDto saveProduct(ProductoDto producto);

    public ProductoDto updateProductById(Integer id, ProductoDto dto);

    void deleteProductById(Integer idProducto);

}
