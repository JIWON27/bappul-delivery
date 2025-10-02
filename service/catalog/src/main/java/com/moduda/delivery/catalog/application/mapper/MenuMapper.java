package com.moduda.delivery.catalog.application.mapper;

import com.moduda.delivery.catalog.domain.entity.Menu;
import com.moduda.delivery.catalog.domain.entity.MenuOptionValue;
import com.moduda.delivery.catalog.web.v1.response.menu.MenuOptionResponse;
import com.moduda.delivery.catalog.web.v1.response.menu.MenuOptionSetResponse;
import com.moduda.delivery.catalog.web.v1.response.menu.MenuOptionValueResponse;
import com.moduda.delivery.catalog.web.v1.response.menu.MenuResponse;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {

  MenuResponse toResponse(Menu menu, MenuOptionResponse  menuOption);
  MenuOptionValueResponse toMenuOptionValueResponse(MenuOptionValue menuOptionValue);
  MenuOptionSetResponse toMenuOptionSetResponse(String optionGroupName, List<MenuOptionValueResponse> optionValues);

}
