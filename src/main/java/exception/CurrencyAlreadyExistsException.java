package exception;

public class CurrencyAlreadyExistsException extends RuntimeException {
    public CurrencyAlreadyExistsException(String message, Throwable source) {
        super(message, source);
    }
}
