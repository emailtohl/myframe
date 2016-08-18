package com.github.emailtohl.frame.util.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "t_person")
// 指定继承的映射策略，所有继承树上的实体共用一张表：SINGLE_TABLE，这是默认值
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// 定义辨别者列的列名为“person_type”，列类型是字符串
@DiscriminatorColumn(name = "person_type", discriminatorType = DiscriminatorType.STRING)
// 指定Person实体对应的记录在辨别者列的值是“person”
@DiscriminatorValue("person")
@Access(AccessType.PROPERTY) // 默认的配置
public class Person implements Serializable {
	private static final long serialVersionUID = -3812238179649646182L;
	
	public enum Gender {
		MALE, FEMALE, UNSPECIFIED
	}
	
	protected Long id;
	protected String name;
	protected Date birthday;
	protected String icon;
	protected Integer age;
	protected Gender gender;
	protected Contact contact;
	protected transient byte[] pic;
	protected String description;
	
	public Person() {
		super();
	}
	
	public Person(Long id, String name, Date birthday, String icon, Integer age, Gender gender, Contact contact,
			byte[] pic, String description) {
		super();
		this.id = id;
		this.name = name;
		this.birthday = birthday;
		this.icon = icon;
		this.age = age;
		this.gender = gender;
		this.contact = contact;
		this.pic = pic;
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name")
	@Basic(optional = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Temporal(TemporalType.DATE)
	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	@Column(name = "icon")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@Column(name = "age")
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	@Enumerated(EnumType.STRING)
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	@Embedded
	/*嵌入属性的映射可在嵌入实体中声明，不必要在此覆盖
	@AttributeOverrides({
		@AttributeOverride(name = "address", column = @Column(name = "contact_address")),
		@AttributeOverride(name = "email", column = @Column(name = "contact_email")),
		@AttributeOverride(name = "telephone", column = @Column(name = "contact_telephone"))
	})*/
	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Lob
	public byte[] getPic() {
		return pic;
	}

	public void setPic(byte[] pic) {
		this.pic = pic;
	}

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", birthday=" + birthday + ", icon=" + icon + ", age=" + age
				+ ", gender=" + gender + ", contact=" + contact + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
