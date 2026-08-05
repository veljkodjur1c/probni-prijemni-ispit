package rs.fon.probniispit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rs.fon.probniispit.dto.GreskaOdgovor;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalniObradjivacGresaka {

    @ExceptionHandler(NijePronadjenoException.class)
    public ResponseEntity<GreskaOdgovor> nijePronadjeno(NijePronadjenoException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new GreskaOdgovor(404, e.getMessage()));
    }

    @ExceptionHandler(NevalidanZahtevException.class)
    public ResponseEntity<GreskaOdgovor> nevalidanZahtev(NevalidanZahtevException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GreskaOdgovor(400, e.getMessage()));
    }

    @ExceptionHandler(KonfliktException.class)
    public ResponseEntity<GreskaOdgovor> konflikt(KonfliktException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new GreskaOdgovor(409, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GreskaOdgovor> validacija(MethodArgumentNotValidException e) {
        Map<String, String> greske = new HashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            greske.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new GreskaOdgovor(400, "Neispravni podaci", greske));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GreskaOdgovor> ostalo(Exception e) {
        e.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GreskaOdgovor(500, "Došlo je do greške na serveru"));
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<GreskaOdgovor> zabranjeno(org.springframework.security.authorization.AuthorizationDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new GreskaOdgovor(403, "Nemate dozvolu za ovu akciju"));
    }

    @ExceptionHandler(org.springframework.web.ErrorResponseException.class)
    public ResponseEntity<GreskaOdgovor> springGreske(org.springframework.web.ErrorResponseException e) {
        return ResponseEntity
                .status(e.getStatusCode())
                .body(new GreskaOdgovor(e.getStatusCode().value(), e.getMessage()));
    }
}