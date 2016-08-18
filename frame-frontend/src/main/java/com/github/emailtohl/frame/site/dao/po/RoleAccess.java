package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_role_access")
public class RoleAccess implements Serializable {
	private static final long serialVersionUID = -9146748184462251854L;
	
	@Orm(columnLabel = "role_id")
	private Integer roleId;
	
	@Orm(columnLabel = "access_id")
	private Integer accessId;

	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	public Integer getAccessId() {
		return accessId;
	}
	public void setAccessId(Integer accessId) {
		this.accessId = accessId;
	}
}