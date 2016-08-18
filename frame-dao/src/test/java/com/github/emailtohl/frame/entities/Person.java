package com.github.emailtohl.frame.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Person implements Serializable {
	private static final long serialVersionUID = 1842877219125267849L;
	private String name;
	private Integer age;
	private Gender gender;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
	@Column
	public Gender getGender() {
		return gender;
	}
	@Column
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public static enum Gender {
		MAN, WOMAN, UNKNOWN
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", gender=" + gender + "]";
	}
	
}
