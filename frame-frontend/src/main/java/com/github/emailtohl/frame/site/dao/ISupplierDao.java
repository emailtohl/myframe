package com.github.emailtohl.frame.site.dao;

import java.util.List;

import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.dto.SupplierDto;

public interface ISupplierDao {
	List<SupplierDto> querySupplier(SupplierDto supplierDto);

	Pager<SupplierDto> queryPage(SupplierDto supplierDto);

	long addSupplier(SupplierDto supplierDto);

	int updateSupplier(SupplierDto supplierDto);

	int deleteSupplier(SupplierDto condition);

	void syncData(List<SupplierPo> srcList);
}
