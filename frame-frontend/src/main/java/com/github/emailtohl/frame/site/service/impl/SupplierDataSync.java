package com.github.emailtohl.frame.site.service.impl;

import java.util.List;

import com.github.emailtohl.frame.site.dao.IRemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.ISupplierDao;
import com.github.emailtohl.frame.site.dao.impl.RemoteSupplierDao;
import com.github.emailtohl.frame.site.dao.impl.SupplierDao;
import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;
import com.github.emailtohl.frame.site.dao.po.SupplierPo;
import com.github.emailtohl.frame.site.service.ISupplierDataSync;
import com.github.emailtohl.frame.util.BeanUtils;

public class SupplierDataSync implements ISupplierDataSync {
	private IRemoteSupplierDao remoteDao = RemoteSupplierDao.getRemoteSupplierDaoInstance();// 访问远程数据库
	private ISupplierDao supplierDao = SupplierDao.getSupplierDaoInstance();

	public void syncSupplierData() {
		List<RemoteSupplierPo> remoteSupplierList = remoteDao.querySupplier(new RemoteSupplierPo());
		List<SupplierPo> srcPoList = BeanUtils.copyList(remoteSupplierList, SupplierPo.class);
		supplierDao.syncData(srcPoList);
	}

/*	public static void main(String[] args) {
		ISupplierDataSync aSupplierDataSync = new SupplierDataSync();
		aSupplierDataSync.syncSupplierData();
	}*/
}
