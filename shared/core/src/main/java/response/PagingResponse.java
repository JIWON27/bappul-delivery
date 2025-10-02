package response;

import java.util.List;
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
public class PagingResponse<T> {
  List<T> items;
  int page;
  int size;
  long totalElements;
  int totalPages;
  boolean last;

  public static <T> PagingResponse<T> of(List<T> items, int page, int size, long totalElements, int totalPages, boolean last){
    return PagingResponse.<T>builder()
        .items(items)
        .page(page)
        .size(size)
        .totalElements(totalElements)
        .totalPages(totalPages)
        .last(last)
        .build();
  }
}
