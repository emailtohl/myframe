package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_supplier")
public class RemoteSupplierPo implements Serializable {
	private static final long serialVersionUID = 7479889149596403671L;

	@Orm(columnLabel = "id", isKey = true)
	protected Long supplierId;

	@Orm(columnLabel = "supplier_name")
	protected String supplierName;

	@Orm(columnLabel = "address")
	protected String address;

	@Orm(columnLabel = "description")
	protected String description;

	@Orm(columnLabel = "tel")
	protected String tel;

	@Orm(columnLabel = "email")
	protected String email;

	@Orm(columnLabel = "rank")
	protected Integer rank;

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "RemoteSupplierPo [supplierId=" + supplierId + ", supplierName=" + supplierName + ", address=" + address
				+ ", description=" + description + ", tel=" + tel + ", email=" + email + ", rank=" + rank + "]";
	}

}
