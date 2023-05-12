package subway.ui;

import java.sql.SQLException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import subway.ui.dto.response.ExceptionResponse;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ExceptionResponse> handleSQLException(final SQLException ex) {
        logger.error("SQLException : ", ex);

        return ResponseEntity.badRequest().body(new ExceptionResponse("중복된 이름이 존재합니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception ex) {
        logger.error("Exception : ", ex);

        return ResponseEntity.internalServerError().body(new ExceptionResponse("예상치 못한 예외가 발생했습니다."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(final IllegalArgumentException ex) {
        logger.error("IllegalArgumentException : ", ex);

        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body,
            final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        logger.error("plz : ", ex);

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }
}
