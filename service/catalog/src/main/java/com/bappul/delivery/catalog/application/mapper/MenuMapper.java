package com.bappul.delivery.catalog.application.mapper;

import com.bappul.delivery.catalog.domain.entity.Menu;
import com.bappul.delivery.catalog.domain.entity.MenuOptionValue;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionSetResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuOptionValueResponse;
import com.bappul.delivery.catalog.web.v1.response.menu.MenuResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {

  MenuResponse toResponse(Menu menu, MenuOptionResponse  menuOption);
  MenuOptionValueResponse toMenuOptionValueResponse(MenuOptionValue menuOptionValue);
  MenuOptionSetResponse toMenuOptionSetResponse(String optionGroupName, List<MenuOptionValueResponse> optionValues);

}
