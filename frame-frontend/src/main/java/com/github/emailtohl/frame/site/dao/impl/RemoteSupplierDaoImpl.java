package com.github.emailtohl.frame.site.dao.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.ioc.Component;
import com.github.emailtohl.frame.site.dao.RemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;

@Component
public class RemoteSupplierDaoImpl extends BaseDao implements RemoteSupplierDao {

	@Inject
	public RemoteSupplierDaoImpl(@Named("remote") DataSource ds) {
		super(ds);
	}

	@Override
	public List<RemoteSupplierPo> querySupplier(RemoteSupplierPo remoteSupplierPo) {
		return query(remoteSupplierPo);
	}

	@Override
	public long addSupplier(RemoteSupplierPo remoteSupplierPo) {
		return insert(remoteSupplierPo);
	}

	@Override
	public int updateSupplier(RemoteSupplierPo remoteSupplierPo) {
		RemoteSupplierPo condition = new RemoteSupplierPo();
		condition.setSupplierId(remoteSupplierPo.getSupplierId());
		return update(remoteSupplierPo, condition);
	}

	@Override
	public int deleteSupplier(RemoteSupplierPo condition) {
		return delete(condition);
	}
}
