package com.caribu.filiale_hib.data;

import com.caribu.filiale_hib.model.Supplier;
import com.caribu.filiale_hib.model.SupplierDTO;

import java.util.function.Function;

class SupplierEntityMapper implements Function<SupplierDTO, Supplier> {
  
  @Override
  public Supplier apply(SupplierDTO supplierDTO) {
    Supplier supplier = new Supplier();
    supplier.setId(supplierDTO.getId());
    supplier.setUserId(supplierDTO.getUserId());
    supplier.setName(supplierDTO.getName());
    supplier.setSurname(supplierDTO.getSurname());
    supplier.setDate(supplierDTO.getDate());
    
    return supplier;
  }
}
