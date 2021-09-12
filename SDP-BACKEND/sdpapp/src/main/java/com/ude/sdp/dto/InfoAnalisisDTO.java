package com.ude.sdp.dto;


import java.util.Date;
import java.util.List;

public class InfoAnalisisDTO extends DTO {

	private String nombreArchivoAComparar;
	private List<String> nombreArchivosCorpus;
	private boolean terminado;
	private String fechaPedidoAnalisis;
	private String fechaFinalAnalisis;
	private String resultado;
	private long idAnalisis;
	private long cantidadCoincidencias;
	
	public long getCantidadCoincidencias() {
		return cantidadCoincidencias;
	}
	public void setCantidadCoincidencias(long cantidadCoincidencias) {
		this.cantidadCoincidencias = cantidadCoincidencias;
	}
	public long getIdAnalisis() {
		return idAnalisis;
	}
	public void setIdAnalisis(long idAnalisis) {
		this.idAnalisis = idAnalisis;
	}
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public String getNombreArchivoAComparar() {
		return nombreArchivoAComparar;
	}
	public void setNombreArchivoAComparar(String nombreArchivoAComparar) {
		this.nombreArchivoAComparar = nombreArchivoAComparar;
	}
	public List<String> getNombreArchivosCorpus() {
		return nombreArchivosCorpus;
	}
	public void setNombreArchivosCorpus(List<String> nombreArchivosCorpus) {
		this.nombreArchivosCorpus = nombreArchivosCorpus;
	}
	public boolean isTerminado() {
		return terminado;
	}
	public void setTerminado(boolean terminado) {
		this.terminado = terminado;
	}
	public String getFechaPedidoAnalisis() {
		return fechaPedidoAnalisis;
	}
	public void setFechaPedidoAnalisis(String fechaPedidoAnalisis) {
		this.fechaPedidoAnalisis = fechaPedidoAnalisis;
	}
	public String getFechaFinaAnalisis() {
		return fechaFinalAnalisis;
	}
	public void setFechaFinalAnalisis(String fechaFinalAnalisis) {
		this.fechaFinalAnalisis = fechaFinalAnalisis;
	}
	
	
		
}