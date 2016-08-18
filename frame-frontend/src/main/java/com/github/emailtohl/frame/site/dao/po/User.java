package com.github.emailtohl.frame.site.dao.po;

import java.io.Serializable;

import com.github.emailtohl.frame.dao.preparedstatementfactory.Orm;

@Orm(tableName = "t_user")
public class User implements Serializable {
	private static final long serialVersionUID = -39857136697838683L;
	
	@Orm(columnLabel = "id", isKey = true)
	private Long id;
	
	@Orm(columnLabel = "name")
	private String name;
	
	@Orm(columnLabel = "email")
	private String email;
	
	@Orm(columnLabel = "password")
	private String password;
	
	@Orm(columnLabel = "icon")
	private String icon;
	
	@Orm(columnLabel = "role_id")
	private Integer roleId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", icon=" + icon + ", roleId=" + roleId + "]";
	}
}
