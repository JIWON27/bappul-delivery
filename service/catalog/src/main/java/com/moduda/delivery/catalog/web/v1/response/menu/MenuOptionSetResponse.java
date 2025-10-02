package com.moduda.delivery.catalog.web.v1.response.menu;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuOptionSetResponse {
  String optionGroupName;
  List<MenuOptionValueResponse> optionValues;
}
