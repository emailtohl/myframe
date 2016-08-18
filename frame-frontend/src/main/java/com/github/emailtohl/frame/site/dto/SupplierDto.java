package com.github.emailtohl.frame.site.dto;

import com.github.emailtohl.frame.site.dao.po.SupplierPo;

public class SupplierDto extends SupplierPo {
	private static final long serialVersionUID = -4308259903850674450L;
	// 用于接收分页查询时的总行数
	private Long totalRowNum;
	private Long pageNum;// 查询页码
	private Integer pageSize;// 每页最大行数
	
	public Long getTotalRowNum() {
		return totalRowNum;
	}
	public void setTotalRowNum(Long totalRowNum) {
		this.totalRowNum = totalRowNum;
	}
	public Long getPageNum() {
		return pageNum;
	}
	public void setPageNum(Long pageNum) {
		this.pageNum = pageNum;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	@Override
	public String toString() {
		return "SupplierDto [totalRowNum=" + totalRowNum + ", pageNum=" + pageNum + ", pageSize=" + pageSize
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", address=" + address
				+ ", description=" + description + ", tel=" + tel + ", email=" + email + ", rank=" + rank
				+ ", markDeleted=" + markDeleted + "]";
	}
}
