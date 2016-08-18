package com.github.emailtohl.frame.site.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.dao.myjdbctemplate.BeanAnnotationRowMapper;
import com.github.emailtohl.frame.dao.myjdbctemplate.RowMapper;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlAndArgs;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlBuilder;
import com.github.emailtohl.frame.site.dao.ISupplierDao;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.dto.SupplierDto;

public final class SupplierDao extends BaseDao implements ISupplierDao {
	private static SupplierDao supplierDao;
	private SqlBuilder sqlBuilder = new SqlBuilder();

	private SupplierDao(DataSource ds) {
		super(ds);
	}

	public synchronized static SupplierDao getSupplierDaoInstance() {
		if (supplierDao == null) {
			String configFilePath = Thread.currentThread().getContextClassLoader()
					.getResource("database.properties").getPath().substring(1);
			DataSource ds = BaseDao.getDataSourceByPropertyFile(configFilePath);
			supplierDao = new SupplierDao(ds);
		}
		return supplierDao;
	}

	@Override
	public List<SupplierDto> querySupplier(SupplierDto supplierDto) {
		return query(supplierDto);
	}

	@Override
	public long addSupplier(SupplierDto supplierDto) {
		return insert(supplierDto);
	}

	@Override
	public int updateSupplier(SupplierDto supplierDto) {
		SupplierDto condition = new SupplierDto();
		condition.setSupplierId(supplierDto.getSupplierId());
		return update(supplierDto, condition);
	}

	@Override
	public int deleteSupplier(SupplierDto condition) {
		return delete(condition);
	}

	@Override
	public Pager<SupplierDto> queryPage(SupplierDto supplierDto) {
		SqlAndArgs sa = sqlBuilder.getSimpleQueryStatement(supplierDto);
		RowMapper<SupplierDto> rowMapper = new BeanAnnotationRowMapper<SupplierDto>(SupplierDto.class);
		return pageForMySqlOrPostgresql(sa.getPreparedSQL(), sa.getParamValues(), rowMapper,
				supplierDto.getPageNum(), 5);
	}

	@Override
	public void syncData(List<SupplierPo> srcList) {
		super.syncData(srcList, SupplierPo.class);
	}

}
