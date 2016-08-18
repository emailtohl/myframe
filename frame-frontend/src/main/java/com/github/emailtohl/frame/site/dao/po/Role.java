package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_role")
public class Role implements Serializable {
	private static final long serialVersionUID = 8455919492034778744L;
	
	@Orm(columnLabel = "id", isKey = true)
	private Integer id;
	
	@Orm(columnLabel = "name")
	private String name;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
