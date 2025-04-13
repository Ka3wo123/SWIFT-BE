package pl.ka3wo.swift.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class RestExceptionHandler {
  @ExceptionHandler(NoSwiftDataFound.class)
  public ProblemDetail handleNoSwiftDataFound(NoSwiftDataFound ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problem.setTitle("SWIFT data not found");
    problem.setDetail(ex.getLocalizedMessage());
    return problem;
  }

  @ExceptionHandler(DuplicateSwiftCodeException.class)
  public ProblemDetail handleDuplicateSwiftCode(DuplicateSwiftCodeException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problem.setTitle("SWIFT code conflict");
    problem.setDetail(ex.getLocalizedMessage());
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error -> {
              Map<String, Object> details = new HashMap<>();
              details.put("message", error.getDefaultMessage());
              details.put("rejectedValue", error.getRejectedValue());
              errors.put(error.getField(), details);
            });

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Invalid SWIFT data");
    problem.setProperty("errors", errors);
    return problem;
  }
}
