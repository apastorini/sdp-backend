package com.ude.sdp.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



@Entity
@Table(name = "NWORDS")
public class NWords implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FILEID", nullable = false)
	private File file;

	@Column(name = "ORD")
	private Integer ord;

	@Column(name = "PAGE_NUM")
	private Integer pageNum;

	@Column(name = "NWORDS")
	private Integer nWords;

	@Column(name = "CONTENT")
	private String content;

	@Column(name = "HASH")
	private String hash;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Integer getOrd() {
		return ord;
	}

	public void setOrd(Integer ord) {
		this.ord = ord;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getnWords() {
		return nWords;
	}

	public void setnWords(Integer nWords) {
		this.nWords = nWords;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	
	
	@Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (o==null || !(o instanceof NWords)) {
            return false;
        }
        NWords n = (NWords) o;
        return this.getHash().equals(n.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ord, pageNum,content, hash);
    }
    
    @Override
    public String toString() {
        return this.getContent();
    }

}