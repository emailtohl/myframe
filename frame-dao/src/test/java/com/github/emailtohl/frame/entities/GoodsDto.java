package com.github.emailtohl.frame.entities;

import java.util.Arrays;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

public class GoodsDto extends GoodsPo {
	private static final long serialVersionUID = -5265798743914506550L;
	
	@Orm(columnLabel="supplier_name")
	private String supplierName;
	// 用于接收分页查询时的总行数
	private Long totalRowNum;
	private Long currentPage;// 查询页码
	private Integer maxRowNum;// 每页最大行数
	private String[] types;
	
	private Long startTimeLong;
	private Long endTimeLong;
	
	public String getSupplierName() {
		return supplierName;
	}
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
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
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public Long getStartTimeLong() {
		return startTimeLong;
	}
	public void setStartTimeLong(Long startTimeLong) {
		this.startTimeLong = startTimeLong;
	}
	public Long getEndTimeLong() {
		return endTimeLong;
	}
	public void setEndTimeLong(Long endTimeLong) {
		this.endTimeLong = endTimeLong;
	}
	@Override
	public String toString() {
		return "GoodsDto [supplierName=" + supplierName + ", totalRowNum=" + totalRowNum + ", currentPage="
				+ currentPage + ", maxRowNum=" + maxRowNum + ", types=" + Arrays.toString(types) + ", startTimeLong="
				+ startTimeLong + ", endTimeLong=" + endTimeLong + ", goodsId=" + goodsId + ", goodsName=" + goodsName
				+ ", price=" + price + ", description=" + description + ", amount=" + amount + ", supplierId="
				+ supplierId + ", createTime=" + createTime + ", type=" + type + "]";
	}
}
