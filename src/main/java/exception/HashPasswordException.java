package exception;

public class HashPasswordException extends RuntimeException {
    public HashPasswordException(String message, Exception e) {
        super(message, e);
    }
}
