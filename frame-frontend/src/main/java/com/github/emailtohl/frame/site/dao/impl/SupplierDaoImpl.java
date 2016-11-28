package com.github.emailtohl.frame.site.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.github.emailtohl.frame.cdi.Component;
import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.dao.myjdbctemplate.BeanAnnotationRowMapper;
import com.github.emailtohl.frame.dao.myjdbctemplate.RowMapper;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlAndArgs;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlBuilder;
import com.github.emailtohl.frame.site.dao.SupplierDao;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.dto.SupplierDto;

@Component
public class SupplierDaoImpl extends BaseDao implements SupplierDao {
	private SqlBuilder sqlBuilder = new SqlBuilder();

	@Inject
	public SupplierDaoImpl(@Named("local") DataSource ds) {
		super(ds);
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
