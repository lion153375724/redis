package com.learn.redismybits.dto;

import javax.persistence.Table;

@Table(name = "order_base")
public class Order extends BaseEntity{
	
	private int uid;
	private Double price;
	private String state;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	
}
