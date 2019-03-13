package ysq.fall;

public class FallException extends RuntimeException {

    /**
     *
     */
    public FallException() {
        super();
    }

    /**
     *
     * @param message
     */
    public FallException(String message) {
        super(message);
    }

    /**
     *
     * @param cause
     */
    public FallException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public FallException(String message, Throwable cause) {
        super(message, cause);
    }

}