package gm.inventarios.mapper;

import gm.inventarios.model.Producto;
import gm.inventarios.model.ProductoDto;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Producto  toEntity(ProductoDto productoDto);
    ProductoDto toProductoDto(Producto producto);
    List<ProductoDto> toProductoDtoList(List<Producto> productos);
}
