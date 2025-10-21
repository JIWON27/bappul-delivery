package exception;

public interface ServiceErrorCode {
  int getStatus();
  default String getCode() {
    return (this instanceof Enum<?> e) ? e.name() : "NOT_FOUND_SERVICE_CODE";
  }
  String getMessage();
}
