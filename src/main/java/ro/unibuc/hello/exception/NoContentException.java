package ro.unibuc.hello.exception;

import java.util.HashMap;

public class NoContentException extends RuntimeException {

    public NoContentException() {
        super("No Content");
    }
}
