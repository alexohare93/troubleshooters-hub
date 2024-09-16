package hub.troubleshooters.soundlink.core;

/**
 * Represents the result of a core operation.
 * @param <T> The type of the result.
 * @param <E> The type of the error.
 */
public class CoreResult<T, E extends RuntimeException> {
    private final T result;
    private final E error;

    public CoreResult(T result) {
        this.result = result;
        this.error = null;
    }

    public CoreResult(E error) {
        this.result = null;
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public E getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == null;
    }
}
