package com.bappul.catalog.application.mapper;

import com.bappul.catalog.domain.entity.Menu;
import com.bappul.catalog.domain.entity.MenuOptionGroup;
import com.bappul.catalog.domain.entity.MenuOptionValue;
import com.bappul.catalog.domain.entity.Store;
import com.bappul.catalog.web.v1.request.menu.MenuRequest;
import com.bappul.catalog.web.v1.request.menu.OptionItemRequest;
import com.bappul.catalog.web.v1.request.menu.OptionValueRequest;
import com.bappul.catalog.web.v1.response.menu.MenuOptionResponse;
import com.bappul.catalog.web.v1.response.menu.MenuOptionSetResponse;
import com.bappul.catalog.web.v1.response.menu.MenuOptionValueResponse;
import com.bappul.catalog.web.v1.response.menu.MenuResponse;
import com.bappul.catalog.web.v1.response.menu.MenuSummaryResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {
  // TODO sortOrder 처리 로직 추가 현재는 임시값.

  @Mapping(target = "sortOrder", expression = "java(0)")
  @Mapping(target = "name", source = "request.name")
  Menu toMenu(Store store, MenuRequest request);

  @Mapping(target = "sortOrder", expression = "java(0)")
  @Mapping(target = "name", source = "request.groupName")
  MenuOptionGroup toMenuOptionGroup(OptionItemRequest request, Menu menu);

  @Mapping(target = "name", source = "request.name")
  MenuOptionValue toMenuOptionValue(OptionValueRequest request, MenuOptionGroup menuOptionGroup);

  MenuResponse toResponse(Menu menu, MenuOptionResponse menuOption, String thumbnail, List<String> imageUrls);
  MenuSummaryResponse toSummaryResponse(Menu menu, String imageUrl);
  MenuOptionValueResponse toMenuOptionValueResponse(MenuOptionValue menuOptionValue);
  MenuOptionSetResponse toMenuOptionSetResponse(String optionGroupName, List<MenuOptionValueResponse> optionValues);

}
