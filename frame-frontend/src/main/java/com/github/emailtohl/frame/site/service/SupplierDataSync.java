package com.github.emailtohl.frame.site.service;

import javax.transaction.Transactional;

@Transactional
public interface SupplierDataSync {
	public void syncSupplierData();
}
