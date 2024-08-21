package hub.troubleshooters.soundlink.core;

public class CoreResult<T, E extends Exception> {
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
