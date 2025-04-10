package pl.ka3wo.swift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.ka3wo.swift.model.dto.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@ControllerAdvice
public class RestExceptionHandler {
  @ExceptionHandler(NoSwiftDataFound.class)
  public ResponseEntity<ErrorResponse> handleNoSwiftDataFound(NoSwiftDataFound ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateSwiftCodeException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateSwiftCode(DuplicateSwiftCodeException ex) {
    ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach(e -> {
      String fieldName = ((FieldError) e).getField();
      String errorMessage = e.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return errors;
  }

}
