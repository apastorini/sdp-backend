package com.ude.sdp.dto;

import java.util.Date;
import java.util.List;

public class AnalisisDTO extends DTO {

	private long idAnalize;
	
	private UserAuthDTO credentials;	

	private String initialDate;

	private String finishDate;
	
	private long fileToAnalyze;
	
	private FileDTO fileToAnalize;
	
	private boolean terminado;
	
	private Integer cantidadCoincidencias;

	

	private List<FileDTO> filesDTOToCompare;

	private List<Long> filesToCompare;

	public long getIdAnalize() {
		return idAnalize;
	}

	public void setIdAnalize(long idAnalize) {
		this.idAnalize = idAnalize;
	}

	
	public String getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public UserAuthDTO getCredentials() {
		return credentials;
	}

	public void setCredentials(UserAuthDTO credentials) {
		this.credentials = credentials;
	}

	public long getFileToAnalyze() {
		return fileToAnalyze;
	}

	public void setFileToAnalyze(long fileToAnalyze) {
		this.fileToAnalyze = fileToAnalyze;
	}

	public List<Long> getFilesToCompare() {
		return filesToCompare;
	}

	public void setFilesToCompare(List<Long> filesToCompare) {
		this.filesToCompare = filesToCompare;
	}

	
	public boolean isTerminado() {
		return terminado;
	}

	public void setTerminado(boolean terminado) {
		this.terminado = terminado;
	}
	

	public List<FileDTO> getFilesDTOToCompare() {
		return filesDTOToCompare;
	}

	public void setFilesDTOToCompare(List<FileDTO> filesDTOToCompare) {
		this.filesDTOToCompare = filesDTOToCompare;
	}
	
	public FileDTO getFileToAnalize() {
		return fileToAnalize;
	}

	public void setFileToAnalize(FileDTO fileToAnalize) {
		this.fileToAnalize = fileToAnalize;
	}

	public Integer getCantidadCoincidencias() {
		return cantidadCoincidencias;
	}

	public void setCantidadCoincidencias(Integer cantidadCoincidencias) {
		this.cantidadCoincidencias = cantidadCoincidencias;
	}

}