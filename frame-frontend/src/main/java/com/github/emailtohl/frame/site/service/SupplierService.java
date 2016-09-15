package com.github.emailtohl.frame.site.service;

import java.util.List;

import javax.transaction.Transactional;

import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.site.dto.SupplierDto;

@Transactional
public interface SupplierService {
	List<SupplierDto> querySupplier(SupplierDto supplierDto);
	
	SupplierDto getSupplierById(Long id);

	Pager<SupplierDto> queryPage(SupplierDto supplierDto);

	int updateSupplier(SupplierDto supplierDto);

	int deleteSupplier(SupplierDto supplierDto);

	long addSupplier(SupplierDto supplierDto);
}
