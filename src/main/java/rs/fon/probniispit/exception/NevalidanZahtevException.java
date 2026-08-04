package rs.fon.probniispit.exception;

public class NevalidanZahtevException extends RuntimeException {
    public NevalidanZahtevException(String poruka) {
        super(poruka);
    }
}