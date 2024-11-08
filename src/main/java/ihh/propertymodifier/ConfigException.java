package ihh.propertymodifier;

public class ConfigException extends IllegalArgumentException {
    public ConfigException() {
    }

    public ConfigException(String s) {
        super(s);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigException(Throwable cause) {
        super(cause);
    }
}
