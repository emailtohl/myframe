package com.github.emailtohl.frame.site.service.impl;

import java.util.List;

import javax.inject.Inject;

import com.github.emailtohl.frame.ioc.Component;
import com.github.emailtohl.frame.site.dao.RemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.SupplierDao;
import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.service.SupplierDataSync;
import com.github.emailtohl.frame.util.BeanTools;

@Component
public class SupplierDataSyncImpl implements SupplierDataSync {
	private RemoteSupplierDao remoteDao;// 访问远程数据库
	private SupplierDao supplierDao;

	
	public RemoteSupplierDao getRemoteDao() {
		return remoteDao;
	}

	@Inject
	public void setRemoteDao(RemoteSupplierDao remoteDao) {
		this.remoteDao = remoteDao;
	}

	public SupplierDao getSupplierDao() {
		return supplierDao;
	}

	@Inject
	public void setSupplierDao(SupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	public void syncSupplierData() {
		List<RemoteSupplierPo> remoteSupplierList = remoteDao.querySupplier(new RemoteSupplierPo());
		List<SupplierPo> srcPoList = BeanTools.copyList(remoteSupplierList, SupplierPo.class);
		supplierDao.syncData(srcPoList);
	}

}
