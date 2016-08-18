package com.github.emailtohl.frame.site.dto;

import java.util.Arrays;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;
import com.github.emailtohl.frame.site.dao.po.GoodsPo;

public class GoodsDto extends GoodsPo {
	private static final long serialVersionUID = -5265798743914506550L;
	
	@Orm(columnLabel="supplier_name")
	private String supplierName;
	// 用于接收分页查询时的总行数
	private Long totalRowNum;
	private Long pageNum;// 查询页码
	private Integer pageSize;// 每页最大行数
	private String[] types;
	
	private String startDate;
	private String endDate;
	
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
	public String[] getTypes() {
		return types;
	}
	public void setTypes(String[] types) {
		this.types = types;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	@Override
	public String toString() {
		return "GoodsDto [supplierName=" + supplierName + ", totalRowNum=" + totalRowNum + ", pageNum=" + pageNum
				+ ", pageSize=" + pageSize + ", types=" + Arrays.toString(types) + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", goodsId=" + goodsId + ", goodsName=" + goodsName + ", price=" + price
				+ ", description=" + description + ", amount=" + amount + ", supplierId=" + supplierId + ", createTime="
				+ createTime + ", type=" + type + "]";
	}
}
