package rs.fon.probniispit.exception;

public class NijePronadjenoException extends RuntimeException {
    public NijePronadjenoException(String poruka) {
        super(poruka);
    }
}