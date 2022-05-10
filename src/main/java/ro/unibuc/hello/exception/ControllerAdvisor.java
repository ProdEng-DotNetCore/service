package ro.unibuc.hello.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    private final MeterRegistry metricsRegistry;

    private final Counter counter201;
//          = Counter.builder("product_201_responses_count")
//            .description("Number of requests returning 201 No Content")
//            .tags("endpoint", "Product")
//            .register(metricsRegistry);

    private final Counter counter400;
//        = Counter.builder("product_400_responses_count")
//            .description("Number of requests returning 400 Bad Request")
//            .tags("endpoint", "Product")
//            .register(metricsRegistry);

    private final Counter counter404;
//        = Counter.builder("product_404_responses_count")
//            .description("Number of requests returning 404 Not Found")
//            .tags("endpoint", "Product")
//            .register(metricsRegistry);

    public ControllerAdvisor(MeterRegistry metricsRegistry) {
        this.metricsRegistry = metricsRegistry;
        counter201 = Counter.builder("product_201_responses_count")
                .description("Number of requests returning 201 No Content")
                .tags("endpoint", "Product")
                .register(this.metricsRegistry);

        counter400 = Counter.builder("product_400_responses_count")
                .description("Number of requests returning 400 Bad Request")
                .tags("endpoint", "Product")
                .register(this.metricsRegistry);

        counter404 = Counter.builder("product_404_responses_count")
                .description("Number of requests returning 404 Not Found")
                .tags("endpoint", "Product")
                .register(this.metricsRegistry);
    }

    @ExceptionHandler(NotFoundException.class)
    @Counted(value = "product.notfound", description = "Times a request returned 404 Not Found")
    public ResponseEntity<Object> handleNotFoundException(
            NotFoundException ex, WebRequest request) {

        counter404.increment();

        metricsRegistry.counter("product_counter_404_manual", "endpoint", "Product").increment();

        var body = new HashMap<String, Object>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(
            BadRequestException ex, WebRequest request) {

        counter400.increment();

        metricsRegistry.counter("product_counter_400_manual", "endpoint", "Product").increment();

        var body = new HashMap<String, Object>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getProblems());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<Object> handleNoContentException(
            NoContentException ex, WebRequest request) {

        counter201.increment();

        metricsRegistry.counter("product_counter_201_manual", "endpoint", "Product").increment();

        var body = new HashMap<String, Object>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleInternalServerErrorException(
            NoContentException ex, WebRequest request) {

        metricsRegistry.counter("product_internal_server_error", "endpoint", "Product").increment();

        var body = new HashMap<String, Object>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);
    }
}
