package org.example.account.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Обработчик исключений, которые выбрасываются в контроллере
 */
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value =
            {
                    AccountNotFoundException.class,
                    AccountAlreadyExistException.class
            }
    )
    public ResponseEntity<Object> handleApiRequestException(RuntimeException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                e.getMessage(),
                e.getClass(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }

    @ExceptionHandler(value =
            {
                    MethodArgumentNotValidException.class
            }
    )
    public ResponseEntity<Object> handleApiRequestValidException(MethodArgumentNotValidException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(
                Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(),
                e.getClass(),
                badRequest,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, badRequest);
    }
}
