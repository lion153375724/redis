package com.learn.redismybits.demo.top;

import java.io.Serializable;

public class MemberBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8346003761035649335L;
	private Long id;
	private String name;
	private int age;
	private Integer score;
	private String tag;

	public MemberBean(){
		
	}
	
	public MemberBean(String name,int age,Integer score,String tag){
		this.name = name;
		this.age = age;
		this.score = score;
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

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

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

}
