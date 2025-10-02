package com.moduda.delivery.catalog.web.v1.request.menu;

import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionRequest {
  List<OptionItemRequest> optionItemRequests;

}
