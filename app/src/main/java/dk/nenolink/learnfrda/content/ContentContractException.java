package dk.nenolink.learnfrda.content;

public final class ContentContractException extends Exception {
    public ContentContractException(String message) {
        super(message);
    }

    public ContentContractException(String message, Throwable cause) {
        super(message, cause);
    }
}
