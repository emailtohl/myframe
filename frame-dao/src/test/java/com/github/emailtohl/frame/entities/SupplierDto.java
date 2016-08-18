package com.github.emailtohl.frame.entities;

public class SupplierDto extends SupplierPo {
	private static final long serialVersionUID = -4308259903850674450L;
	// 用于接收分页查询时的总行数
	private Long totalRowNum;
	private Long currentPage;// 查询页码
	private Integer maxRowNum;// 每页最大行数
	
	public Long getTotalRowNum() {
		return totalRowNum;
	}
	public void setTotalRowNum(Long totalRowNum) {
		this.totalRowNum = totalRowNum;
	}
	public Long getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Long currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getMaxRowNum() {
		return maxRowNum;
	}
	public void setMaxRowNum(Integer maxRowNum) {
		this.maxRowNum = maxRowNum;
	}
	
	@Override
	public String toString() {
		return "SupplierDto [totalRowNum=" + totalRowNum + ", currentPage=" + currentPage + ", maxRowNum=" + maxRowNum
				+ ", supplierId=" + supplierId + ", supplierName=" + supplierName + ", address=" + address
				+ ", description=" + description + ", tel=" + tel + ", email=" + email + ", rank=" + rank
				+ ", markDeleted=" + markDeleted + "]";
	}
	
}
