package com.github.emailtohl.frame.entities;

public class Employee extends User {
	private static final long serialVersionUID = 5112854822525348776L;
	private Integer id;
	private Integer empNo;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getEmpNo() {
		return empNo;
	}
	public void setEmpNo(Integer empNo) {
		this.empNo = empNo;
	}
	
}
