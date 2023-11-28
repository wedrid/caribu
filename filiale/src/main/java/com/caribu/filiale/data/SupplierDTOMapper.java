package com.caribu.filiale_hib.data;

import com.caribu.filiale_hib.model.Supplier;
import com.caribu.filiale_hib.model.SupplierDTO;
import com.caribu.filiale_hib.model.SupplierList;

import java.util.Optional;
import java.util.function.Function;

class SupplierDTOMapper implements Function<Supplier, SupplierDTO> {
  @Override
  public SupplierDTO apply(Supplier supplier) {
    return new SupplierDTO(supplier.getId(), supplier.getUserId(), supplier.getName(), supplier.getSurname(), supplier.getDate());
  }
}
