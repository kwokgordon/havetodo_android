package com.kwokgordon.havetodo.sqlite.model;

public class Tasklists {
	
	int id;
	String name;
	String color;
	
	String created_at;
	String updated_at;
	
	// Constructor
	public Tasklists() {
	}
	
	public Tasklists(String name, String color) {
		this.name = name;
		this.color = color;
	}
	
	
	// Getters
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getColor() {
		return this.color;
	}
	
	public String getCreatedAt() {
		return this.created_at;
	}

	public String getUpdatedAt() {
		return this.updated_at;
	}
	
	
	// Setters
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
		
	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}
	
	public void setUpdatedAt(String updated_at) {
		this.updated_at = updated_at;
	}
	
}
