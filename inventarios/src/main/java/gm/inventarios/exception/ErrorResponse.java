package gm.inventarios.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private int statusCode;
    private String error;
    private Object message;
    private String path;
    private LocalDateTime timestamp;

}
