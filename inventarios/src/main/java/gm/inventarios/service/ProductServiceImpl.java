package gm.inventarios.service;

import gm.inventarios.mapper.ProductMapper;
import gm.inventarios.model.Producto;
import gm.inventarios.model.ProductoDto;
import gm.inventarios.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductoDto> listProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable)
                .map(productMapper::toProductoDto);
    }

    @Override
    public ProductoDto searchProductById(Integer idProduct) {
        Producto producto = productRepository.findById(idProduct)
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada con id: " + idProduct));
        return productMapper.toProductoDto(producto);
    }

    @Override
    public ProductoDto saveProduct(ProductoDto dto) {
        Producto producto = this.productRepository
                .save(productMapper.toEntity(dto));

        return productMapper.toProductoDto(producto);
    }

    @Override
    public ProductoDto updateProductById(
            Integer id, ProductoDto dto) {

        Producto encontrado = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidad no encontrada con id: " + id));

        if (dto.getPrecio() != null)
            encontrado.setPrecio(dto.getPrecio());

        if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank())
            encontrado.setDescripcion(dto.getDescripcion());

        if (dto.getExistencia() != null)
            encontrado.setExistencia(dto.getExistencia());

        Producto productSaved = productRepository.save(encontrado);
        return productMapper.toProductoDto(productSaved);
    }

    @Override
    public void deleteProductById(Integer idProduct) {
        if(!this.productRepository.existsById(idProduct)){
            throw new EntityNotFoundException("Producto no encontrado con id: " + idProduct);
        }
        this.productRepository.deleteById(idProduct);
    }
}
