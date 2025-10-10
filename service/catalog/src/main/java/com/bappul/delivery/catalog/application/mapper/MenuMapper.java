package com.bappul.delivery.catalog.application.mapper;

import com.bappul.delivery.catalog.domain.entity.Menu;
import com.bappul.delivery.catalog.domain.entity.MenuOptionValue;
import com.bappul.delivery.catalog.domain.entity.Store;
import com.bappul.delivery.catalog.web.v1.request.menu.MenuRequest;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionSetResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionValueResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuSummaryResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuMapper {

  @Mapping(target = "sortOrder", expression = "java(0)")
  @Mapping(target = "photoUrl", source = "photoUrl")
  @Mapping(target = "name", source = "request.name")
  Menu toMenu(Store store, MenuRequest request, String photoUrl);

  MenuResponse toResponse(Menu menu, MenuOptionResponse  menuOption);
  MenuSummaryResponse toSummaryResponse(Menu menu);
  MenuOptionValueResponse toMenuOptionValueResponse(MenuOptionValue menuOptionValue);
  MenuOptionSetResponse toMenuOptionSetResponse(String optionGroupName, List<MenuOptionValueResponse> optionValues);

}
