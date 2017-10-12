package com.github.emailtohl.frame.site.dao;

import java.util.List;

import com.github.emailtohl.frame.dao.Page;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.dto.SupplierDto;

public interface SupplierDao {
	List<SupplierDto> querySupplier(SupplierDto supplierDto);

	Page<SupplierDto> queryPage(SupplierDto supplierDto);

	long addSupplier(SupplierDto supplierDto);

	int updateSupplier(SupplierDto supplierDto);

	int deleteSupplier(SupplierDto condition);

	void syncData(List<SupplierPo> srcList);
}
