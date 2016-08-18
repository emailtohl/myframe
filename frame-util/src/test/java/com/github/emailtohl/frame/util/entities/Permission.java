package com.github.emailtohl.frame.util.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_permission")
public class Permission implements Serializable {
	private static final long serialVersionUID = -2016506896694264888L;
	private Integer id;
	private String name;
	private transient Set<Role> roles = new HashSet<Role>();
	
	public Permission() {
		super();
	}
	
	public Permission(Integer id, String name, Set<Role> roles) {
		super();
		this.id = id;
		this.name = name;
		this.roles = roles;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	// 使用mappedBy将映射关系交到Role类的permissions属性上
	@ManyToMany(targetEntity = Role.class, mappedBy = "permissions", fetch = FetchType.LAZY)
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "Permission [id=" + id + ", name=" + name + "]";
	}
	
}
