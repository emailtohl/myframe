package com.github.emailtohl.frame.site.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.ioc.Component;
import com.github.emailtohl.frame.site.dao.SupplierDao;
import com.github.emailtohl.frame.site.dto.SupplierDto;
import com.github.emailtohl.frame.site.filter.AuthenticationFilter;
import com.github.emailtohl.frame.site.service.SupplierService;
import com.github.emailtohl.frame.util.BeanTools;

@Component
public class SupplierServiceImpl implements SupplierService {
	private static final Logger logger = Logger.getLogger(SupplierServiceImpl.class.getName());
	
	@Inject
	private SupplierDao supplierDao;

	@Override
	public List<SupplierDto> querySupplier(SupplierDto supplierDto) {
		return supplierDao.querySupplier(supplierDto);
	}
	
	@Override
	public SupplierDto getSupplierById(Long id) {
		SupplierDto supplierDto = new SupplierDto();
		supplierDto.setSupplierId(id);
		List<SupplierDto> ls = querySupplier(supplierDto);
		if (ls != null && !ls.isEmpty()) {
			return ls.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public Pager<SupplierDto> queryPage(SupplierDto supplierDto) {
		return supplierDao.queryPage(supplierDto);
	}

	@Override
	public synchronized int updateSupplier(SupplierDto supplierDto) {
		SupplierDto before = getSupplierById(supplierDto.getSupplierId());
		int row = supplierDao.updateSupplier(supplierDto);
		if (row > 0) {
			updateRecord(before, supplierDto);
		}
		return row;
	}

	@Override
	public synchronized int deleteSupplier(SupplierDto supplierDto) {
		int row = supplierDao.deleteSupplier(supplierDto);
		if (row > 0) {
			deleteRecord(supplierDto);
		}
		return row;
	}

	@Override
	public long addSupplier(SupplierDto supplierDto) {
		long id = supplierDao.addSupplier(supplierDto);
		if (id > 0) {
			addRecord(supplierDto);
		}
		return id;
	}

	private void addRecord(SupplierDto afterDto) {
		logger.info("user: " + AuthenticationFilter.CURRENT_USER.get() +  "  新增项：" + afterDto);
	}

	private void updateRecord(SupplierDto preDto, SupplierDto afterDto) {
		Map<String, Object> map = BeanTools.getModifiedField(preDto, afterDto);
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if (entry.getValue() == null) {
				it.remove();
			}
		}
		logger.info("user: " + AuthenticationFilter.CURRENT_USER.get() +  "  修改项：" + map);
	}

	private void deleteRecord(SupplierDto afterDto) {
		logger.info("user: " + AuthenticationFilter.CURRENT_USER.get() +  "  删除id：" + afterDto.getSupplierId());
	}

}
