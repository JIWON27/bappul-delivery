package response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CursorResponse<T> {
  List<T> contents;
  String nextCursor;
  int size;
  boolean hasNext;

  public static <T> CursorResponse<T> of(List<T> contents, String nextCursor, int size, boolean hasNext) {
    return CursorResponse.<T>builder()
        .contents(contents)
        .nextCursor(nextCursor)
        .size(size)
        .hasNext(hasNext)
        .build();

  }
}
