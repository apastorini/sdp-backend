package com.ude.sdp.dto;

public class RoleDTO extends DTO {

	private long id;
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String desc;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	
	public RoleDTO(long id,String name, String desc) {
		super();
		this.id = id;
		this.desc = desc;
		this.name = name;
		
	}

	public RoleDTO() {
		super();
	}

}
