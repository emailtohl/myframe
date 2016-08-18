package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_goods")
public class GoodsPo implements Serializable {
	private static final long serialVersionUID = -4645124731131549211L;

	@Orm(columnLabel = "id", isKey = true)
	protected Long goodsId;

	@Orm(columnLabel = "goods_name")
	protected String goodsName;

	@Orm(columnLabel = "price")
	protected BigDecimal price;

	@Orm(columnLabel = "description")
	protected String description;

	@Orm(columnLabel = "amount")
	protected Integer amount;

	@Orm(columnLabel = "supplier_id")
	protected Long supplierId;

	@Orm(columnLabel = "create_time")
	protected Timestamp createTime;
	
	@Orm(columnLabel = "goods_type")
	protected String type;

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "GoodsPo [goodsId=" + goodsId + ", goodsName=" + goodsName + ", price=" + price + ", description="
				+ description + ", amount=" + amount + ", supplierId=" + supplierId + ", createTime=" + createTime
				+ ", type=" + type + "]";
	}

}
