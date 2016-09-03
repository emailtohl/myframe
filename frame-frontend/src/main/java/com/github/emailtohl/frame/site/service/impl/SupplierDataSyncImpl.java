package com.github.emailtohl.frame.site.service.impl;

import java.util.List;

import com.github.emailtohl.frame.site.dao.RemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.SupplierDao;
import com.github.emailtohl.frame.site.dao.impl.RemoteSupplierDaoImpl;
import com.github.emailtohl.frame.site.dao.impl.SupplierDaoImpl;
import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.service.SupplierDataSync;
import com.github.emailtohl.frame.util.BeanTools;

public class SupplierDataSyncImpl implements SupplierDataSync {
	private RemoteSupplierDao remoteDao = RemoteSupplierDaoImpl.getRemoteSupplierDaoInstance();// 访问远程数据库
	private SupplierDao supplierDao = SupplierDaoImpl.getSupplierDaoInstance();

	public void syncSupplierData() {
		List<RemoteSupplierPo> remoteSupplierList = remoteDao.querySupplier(new RemoteSupplierPo());
		List<SupplierPo> srcPoList = BeanTools.copyList(remoteSupplierList, SupplierPo.class);
		supplierDao.syncData(srcPoList);
	}

/*	public static void main(String[] args) {
		ISupplierDataSync aSupplierDataSync = new SupplierDataSync();
		aSupplierDataSync.syncSupplierData();
	}*/
}
