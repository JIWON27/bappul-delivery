package exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceException extends RuntimeException {
  int status;
  String code;
  String message;

  public ServiceException(String message) {
    super(message);
  }

  public ServiceException(ServiceErrorCode code) {
    super(code.getMessage());
    this.status = code.getStatus();
    this.code = code.getCode();
    this.message = code.getMessage();
  }

  @Override
  public String getMessage() {
    return message;
  }
}
