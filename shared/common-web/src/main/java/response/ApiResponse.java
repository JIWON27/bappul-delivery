package response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {

  Boolean result;
  Error error;
  T message;

  public static <T> ApiResponse<T> success() {
    return success(null);
  }

  public static <T> ApiResponse<T> success(T message) {
    return ApiResponse.<T>builder()
        .result(true)
        .error(Error.empty())
        .message(message)
        .build();
  }

  public static <T> ApiResponse<T> error(T message) {
    return ApiResponse.<T>builder()
        .result(false)
        .error(Error.empty())
        .message(message)
        .build();
  }

  public static ApiResponse<Void> error(String code, String errorMessage) {
    return ApiResponse.<Void>builder()
        .result(false)
        .error(Error.of(code, errorMessage))
        .build();
  }

  public static ApiResponse<Void> badRequest(String code, String errorMessage) {
    return ApiResponse.<Void>builder()
        .result(false)
        .error(Error.of(code, errorMessage))
        .build();
  }

  public static ApiResponse<Void> serverError(String code, String errorMessage) {
    return ApiResponse.<Void>builder()
        .result(false)
        .error(Error.of(code, errorMessage))
        .build();
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public record Error(String errorCode, String errorMessage) {
    public static Error of(String errorCode, String errorMessage) {
      return new Error(errorCode, errorMessage);
    }
    public static Error empty(){
      return new Error(null, null);
    }
  }
}
