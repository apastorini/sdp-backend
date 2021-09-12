package com.ude.sdp.dto;

import java.util.List;

public class FileDTO extends DTO {

	private long idFile;

	private String path;

	private String name;

	private boolean toAnalize;
	
	private boolean pdf;
	
	

	private boolean borrado;
	
	private String extension;

	private String size;
	
	private List<NWordsDTO> nwords;

	

	public List<NWordsDTO> getNwords() {
		return nwords;
	}

	public void setNwords(List<NWordsDTO> nwords) {
		this.nwords = nwords;
	}

	public long getIdFile() {
		return idFile;
	}

	public void setIdFile(long idFile) {
		this.idFile = idFile;
	}

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isToAnalize() {
		return toAnalize;
	}

	public void setToAnalize(boolean toAnalize) {
		this.toAnalize = toAnalize;
	}

	

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	

	public FileDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FileDTO(long id, String name, String extension, String size, String path, boolean toAnalize,boolean pdf) {
		super();
		this.idFile=id;
		this.name = name;
		this.extension = extension;
		this.size = size;
		this.path = path;
		this.toAnalize=toAnalize;
	    this.pdf=pdf;
	}
	

	

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	
	public FileDTO(String name) {
		super();
		this.name = name;
	}
	
	public boolean isBorrado() {
		return borrado;
	}

	public void setBorrado(boolean borrado) {
		this.borrado = borrado;
	}
	
	
	public boolean isPdf() {
		return pdf;
	}

	public void setPdf(boolean pdf) {
		this.pdf = pdf;
	}
	

}