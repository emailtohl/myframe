package com.github.emailtohl.frame.site.dao;

import java.util.List;

import com.github.emailtohl.frame.site.dao.po.RemoteSupplierPo;

public interface IRemoteSupplierDao {
	List<RemoteSupplierPo> querySupplier(RemoteSupplierPo remoteSupplierPo);

	long addSupplier(RemoteSupplierPo remoteSupplierPo);

	int updateSupplier(RemoteSupplierPo remoteSupplierPo);

	int deleteSupplier(RemoteSupplierPo condition);
}
