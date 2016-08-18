package com.github.emailtohl.frame.util.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Contact implements Serializable {
	private static final long serialVersionUID = 5845839322141957452L;
	private String address;
	private String email;
	private String telephone;
	
	public Contact() {
		super();
	}
	
	public Contact(String address, String email, String telephone) {
		super();
		this.address = address;
		this.email = email;
		this.telephone = telephone;
	}

	@Column(name = "contact_address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@Column(name = "contact_email")
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(name = "contact_telephone")
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
}
