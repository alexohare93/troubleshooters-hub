package hub.troubleshooters.soundlink.core;

/**
 * Represents the result of a core operation.
 * @param <T> The type of the result.
 * @param <E> The type of the error.
 */
public class CoreResult<T, E extends RuntimeException> {
    private final T result;
    private final E error;

    /**
     * Creates a successful CoreResult with the given result.
     *
     * @param result The result of the operation.
     */
    public CoreResult(T result) {
        this.result = result;
        this.error = null;
    }

    /**
     * Creates an unsuccessful CoreResult with the given error.
     *
     * @param error The error that occurred during the operation.
     */
    public CoreResult(E error) {
        this.result = null;
        this.error = error;
    }

    /**
     * Returns the result of the operation if it was successful.
     *
     * @return The result, or {@code null} if the operation failed.
     */
    public T getResult() {
        return result;
    }

    /**
     * Returns the error that occurred during the operation.
     *
     * @return The error, or {@code null} if the operation was successful.
     */
    public E getError() {
        return error;
    }

    /**
     * Determines whether the operation was successful.
     *
     * @return {@code true} if the operation was successful (i.e., there is no error), {@code false} otherwise.
     */
    public boolean isSuccess() {
        return error == null;
    }
}
