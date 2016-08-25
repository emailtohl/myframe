package com.github.emailtohl.frame.site.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.site.dao.RemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;

public final class RemoteSupplierDaoImpl extends BaseDao implements RemoteSupplierDao {
	private static RemoteSupplierDaoImpl remoteDao;

	static {
		String configFilePath = Thread.currentThread().getContextClassLoader()
				.getResource("remoteDatabase.properties").getPath().substring(1);
		DataSource ds = BaseDao.getDataSourceByPropertyFile(configFilePath);
		remoteDao = new RemoteSupplierDaoImpl(ds);
	}

	private RemoteSupplierDaoImpl(DataSource ds) {
		super(ds);
	}

	public static RemoteSupplierDaoImpl getRemoteSupplierDaoInstance() {
		return remoteDao;
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
