package com.github.emailtohl.frame.site.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.site.dao.IRemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;

public final class RemoteSupplierDao extends BaseDao implements IRemoteSupplierDao {
	private static RemoteSupplierDao remoteDao;

	static {
		String configFilePath = Thread.currentThread().getContextClassLoader()
				.getResource("remoteDatabase.properties").getPath().substring(1);
		DataSource ds = BaseDao.getDataSourceByPropertyFile(configFilePath);
		remoteDao = new RemoteSupplierDao(ds);
	}

	private RemoteSupplierDao(DataSource ds) {
		super(ds);
	}

	public static RemoteSupplierDao getRemoteSupplierDaoInstance() {
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
