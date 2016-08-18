package com.github.emailtohl.frame.util.entities;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Access(AccessType.PROPERTY) // 默认的配置
@Table(uniqueConstraints = { @UniqueConstraint(name = "User_nickname", columnNames = { "nickname" }) })
public class User extends Person implements Principal, Cloneable {
	private static final long serialVersionUID = 2416129519451072287L;
	private String nickname;
	private transient String password;
	private Boolean enabled;
	private List<String> authority = new ArrayList<String>();
	private Set<Role> roles = new HashSet<Role>();
	
	public User() {
		super();
	}

	public User(String nickname, String password, Boolean enabled, List<String> authority, Set<Role> roles) {
		super();
		this.nickname = nickname;
		this.password = password;
		this.enabled = enabled;
		this.authority = authority;
		this.roles = roles;
	}

	@Column(name = "nickname")
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Column(name = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "enabled")
	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	// 此属性暂时为目前配置的spring security提供服务
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	// @CollectionTable是可选项，若不做注解，JPA提供者则会根据自动生成连接表的表名以及对应的列名
	@CollectionTable(name = "t_user_authority"
	, joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") })
	public List<String> getAuthority() {
		return authority;
	}

	public void setAuthority(List<String> authority) {
		this.authority = authority;
	}

	@ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "t_user_role"
	, joinColumns = { @JoinColumn(name = "person_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "User [nickname=" + nickname + ", password=" + password + ", enabled=" + enabled + ", authority="
				+ authority + ", id=" + id + ", name=" + name + ", birthday=" + birthday + ", icon=" + icon + ", age="
				+ age + ", gender=" + gender + ", contact=" + contact + ", description=" + description + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		return true;
	}

}
