package com.kwokgordon.havetodo.sqlite.model;

public class Tasks {

	int id;
	String name;
	String note;
	int priority;
	String due_date;
	String due_time;
	Boolean completed;
	String completed_date;
	int completed_user_id;
	String completed_user_name;
	
	String created_at;
	String updated_at;
	
	// Constructor
	public Tasks() {
	}
	
	public Tasks(String name) {
		this.name = name;
	}
	
	public Tasks(String name, String note) {
		this.name = name;
		this.note = note;
	}
	
	
	// Getters
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getNote() {
		return this.note;
	}
	
	public int getPriority() {
		return this.priority;
	}

	public String getDueDate() {
		return this.due_date;
	}

	public String getDueTime() {
		return this.due_time;
	}
	
	public Boolean getCompleted() {
		return this.completed;
	}
	
	public String getCompletedDate() {
		return this.completed_date;
	}
	
	public int getCompletedUserId() {
		return this.completed_user_id;
	}
	
	public String getCompletedUserName() {
		return this.completed_user_name;
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
	
	public void setNote(String note) {
		this.note = note;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public void setDueDate(String due_date) {
		this.due_date = due_date;
	}
	
	public void setDueTime(String due_time) {
		this.due_time = due_time;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
	public void setCompletedDate(String completed_date) {
		this.completed_date = completed_date;
	}
	
	public void setCompletedUser(int completed_user_id, String completed_user_name) {
		this.completed_user_id = completed_user_id;
		this.completed_user_name = completed_user_name;
	}
	
	public void setCreatedAt(String created_at) {
		this.created_at = created_at;
	}
	
	public void setUpdatedAt(String updated_at) {
		this.updated_at = updated_at;
	}
	
}
