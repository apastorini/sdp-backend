package com.ude.sdp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "FILE")
public class File implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "FILEID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long fileID;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;
	
	
	@OneToMany(/*cascade = {CascadeType.PERSIST,CascadeType.REFRESH},*/ orphanRemoval = true)
	@Column(name = "ID_NWORD")
	private List<NWords> nWords = new ArrayList<>();
	

	

	@Lob
	@Column(name = "CONTENT")
	private byte[] content;
	
	
	@Lob
	@Column(name = "PDF_CONTENT")
	private byte[] pdfContent;

	

	@Column(name = "NAME")
	private String name;

	@Column(name = "TO_ANALIZE")
	private boolean toAnalize;
	
	@Column(name = "TO_PDF")
	private boolean toPdf;
	
	@Column(name = "BORRADO")
	private boolean borrado;

	
	

	@Column(name = "EXTENSION")
	private String extension;
	
	@Column(name = "PATH")
	private String path;

	@Column(name = "SIZE")
	private String size;
	
	@Lob
	@Column(name = "LOG")
	private String log;
	
	
	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getFileID() {
		return fileID;
	}

	public void setFileID(long fileID) {
		this.fileID = fileID;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	public byte[] getPdfContent() {
		return pdfContent;
	}

	public void setPdfContent(byte[] pdfContent) {
		this.pdfContent = pdfContent;
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

	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public boolean isToPdf() {
		return toPdf;
	}

	public void setToPdf(boolean toPdf) {
		this.toPdf = toPdf;
	}
	
	public List<NWords> getnWords() {
		return nWords;
	}

	public void setnWords(List<NWords> nWords) {
		this.nWords = nWords;
	}
	
	public boolean isBorrado() {
		return borrado;
	}

	public void setBorrado(boolean borrado) {
		this.borrado = borrado;
	}
}