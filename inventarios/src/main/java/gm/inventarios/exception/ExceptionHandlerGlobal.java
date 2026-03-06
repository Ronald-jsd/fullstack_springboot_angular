package gm.inventarios.exception;


import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerGlobal {


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityNotFound(EntityNotFoundException e,
                                                                   HttpServletRequest request){
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Recurso no encontrado",
                e.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionResourceNotFound(ResourceNotFoundException e,
                                                       HttpServletRequest request){
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                e.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> exceptionValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ){

        Map<String, List<String>> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        error -> error.getField(),
                        Collectors.mapping(
                                error -> error.getDefaultMessage(),
                                Collectors.toList()
                        )
                ));

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Error de Validación",
                errors,
               request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionGeneral( Exception e ,
                                               HttpServletRequest request ){
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error Interno del Servidor",
                e.getMessage(),
                request.getRequestURI()
        );
    }


    public ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String error,
            Object message,
            String path
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(status.value())
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}
