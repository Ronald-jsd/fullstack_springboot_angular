package gm.inventarios.model;

import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoDto {

    private Integer idProducto;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(min = 3,
            max = 100,
            message = "La descripción debe tener entre 3 y 100 caracteres")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private Double precio;

    @NotNull(message = "La existencia es obligatorio")
    @Min(value = 6, message = "El stock mínimo permitido es 6")
    private Integer existencia;

}
