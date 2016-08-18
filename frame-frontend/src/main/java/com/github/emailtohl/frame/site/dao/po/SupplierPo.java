package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_supplier")
public class SupplierPo implements Serializable {
	private static final long serialVersionUID = -1642589988409475020L;

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

	@Orm(columnLabel = "mark_deleted", isMarkDeleted = true)
	protected Integer markDeleted;

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

	public Integer getMarkDeleted() {
		return markDeleted;
	}

	public void setMarkDeleted(Integer markDeleted) {
		this.markDeleted = markDeleted;
	}

	@Override
	public String toString() {
		return "SupplierPo [supplierId=" + supplierId + ", supplierName=" + supplierName + ", address=" + address
				+ ", description=" + description + ", tel=" + tel + ", email=" + email + ", rank=" + rank
				+ ", markDeleted=" + markDeleted + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((supplierName == null) ? 0 : supplierName.hashCode());
		result = prime * result + ((tel == null) ? 0 : tel.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPo other = (SupplierPo) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (supplierName == null) {
			if (other.supplierName != null)
				return false;
		} else if (!supplierName.equals(other.supplierName))
			return false;
		if (tel == null) {
			if (other.tel != null)
				return false;
		} else if (!tel.equals(other.tel))
			return false;
		return true;
	}

}
