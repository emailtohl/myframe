package com.github.emailtohl.frame.site.service;

import java.util.List;

import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.site.dto.SupplierDto;

public interface ISupplierService {
	List<SupplierDto> querySupplier(SupplierDto supplierDto);
	
	SupplierDto getSupplierById(Long id);

	Pager<SupplierDto> queryPage(SupplierDto supplierDto);

	int updateSupplier(SupplierDto supplierDto);

	int deleteSupplier(SupplierDto supplierDto);

	long addSupplier(SupplierDto supplierDto);
}
