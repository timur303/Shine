package kg.kadyrbekov.exception;

public class IncorrectLoginException extends RuntimeException {
    public IncorrectLoginException(String messages) {
        super(messages);
    }
}
