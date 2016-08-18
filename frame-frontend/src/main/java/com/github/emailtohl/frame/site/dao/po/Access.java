package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_access")
public class Access implements Serializable {
	private static final long serialVersionUID = -6725185172485970429L;
	
	@Orm(columnLabel = "id", isKey = true)
	private Integer id;
	
	@Orm(columnLabel = "access")
	private String access;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
	}
}
