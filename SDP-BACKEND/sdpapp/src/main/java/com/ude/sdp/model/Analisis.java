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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;





@Entity
@Table(name = "Analisis")
public class Analisis implements Serializable {

	

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "USER_ID")
	private long idUser;

	@Column(name = "FILE_ID")
	private long idFile;
	
	@Column(name = "NUM_MATCHES")
	private long numMatches;
	
	@Column(name = "NWORDS_TOTALES")
	private long nwordsTotales;

	@Column(name = "EMAIL")
	private String email;
	
	@Lob 
	@Column(name = "JSON_RESULT"/*,columnDefinition = "TEXT"*/)
	private String jsonResult;
	
	

	@Column(name = "PROCESADO")
	private boolean procesado;
	
	@Column(name = "GENERADO_REPORT")
	private boolean generadoReport;
	

	@Column(name = "Fecha_Inicial")
	//@Temporal(TemporalType.TIMESTAMP)
	private String fechaInicial;

	@Column(name = "Fecha_Final")
	//@Temporal(TemporalType.TIMESTAMP)
	private String fechaFinal;

	
	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	@JoinTable(name = "Analisis_FILE", joinColumns = { @JoinColumn(name = "Analisis_ID",insertable=true,updatable = true) }, inverseJoinColumns = {
			@JoinColumn(name = "FILE_ID",insertable=true,updatable = true) })
	private List<File> filesToCompare = new ArrayList<>();

	@Lob
	@Column(name = "RESULTADO")
	private byte[] resultado;
	
	public byte[] getResultado() {
		return resultado;
	}

	public void setResultado(byte[] resultado) {
		this.resultado = resultado;
	}
	
	public long getNumMatches() {
		return numMatches;
	}

	public void setNumMatches(long numMatches) {
		this.numMatches = numMatches;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdUser() {
		return idUser;
	}

	public void setIdUser(long idUser) {
		this.idUser = idUser;
	}

	public long getIdFile() {
		return idFile;
	}

	public void setIdFile(long idFile) {
		this.idFile = idFile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(String fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public String getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(String fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public List<File> getFilesToCompare() {
		return filesToCompare;
	}

	public void setFilesToCompare(List<File> filesToCompare) {
		this.filesToCompare = filesToCompare;
	}
	
	public boolean isProcesado() {
		return procesado;
	}

	public void setProcesado(boolean procesado) {
		this.procesado = procesado;
	}

	public String getJsonResult() {
		return jsonResult;
	}

	public void setJsonResult(String jsonResult) {
		this.jsonResult = jsonResult;
	}

	public boolean isGeneradoReport() {
		return generadoReport;
	}

	public void setGeneradoReport(boolean generadoReport) {
		this.generadoReport = generadoReport;
	}
	
	public long getNwordsTotales() {
		return nwordsTotales;
	}

	public void setNwordsTotales(long nwordsTotales) {
		this.nwordsTotales = nwordsTotales;
	}

	
}