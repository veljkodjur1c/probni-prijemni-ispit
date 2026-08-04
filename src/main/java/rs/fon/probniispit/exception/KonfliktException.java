package rs.fon.probniispit.exception;

public class KonfliktException extends RuntimeException {
    public KonfliktException(String poruka) {
        super(poruka);
    }
}