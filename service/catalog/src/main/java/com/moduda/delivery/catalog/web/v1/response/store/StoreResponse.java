package com.moduda.delivery.catalog.web.v1.response.store;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreResponse {
  Long ownerId;
  String category;
  String name;
  String phoneNumber;
  String address;
  String introduction;
}
