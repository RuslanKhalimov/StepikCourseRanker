public class RankerException extends Exception {
    RankerException() {
    }

    RankerException(String message) {
        super(message);
    }

    RankerException(Throwable cause) {
        super(cause);
    }

    RankerException(String message, Throwable cause) {
        super(message, cause);
    }
}
