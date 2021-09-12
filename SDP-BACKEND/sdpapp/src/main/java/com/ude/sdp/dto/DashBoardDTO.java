package com.ude.sdp.dto;

public class DashBoardDTO extends DTO {
	//Datos Para tutor
	private String cantArchivos;
	private String cantAnalisis;
	private String cantAnalisisTerminados;
	private String Rol;
	private String porcentajeDePlagio;
	private String porcentajeDeArchivosPlagiados;
	private int analisisTerminadosConPlagio;
	private int analisisTerminadosSinPlagio;
	private int analisisNoTerminados;
	
	//Datos para Admin
	private String cantUsuariosDelSistema;
	private String cantArchivosCarpetaCompartida;
	private String cantArchivosEnElSistema;
	private int totalArchivos;
	private String listaTutoresCantArchivos;
	
	public int getAnalisisTerminadosConPlagio() {
		return analisisTerminadosConPlagio;
	}
	public void setAnalisisTerminadosConPlagio(int analisisTerminadosConPlagio) {
		this.analisisTerminadosConPlagio = analisisTerminadosConPlagio;
	}
	public int getAnalisisTerminadosSinPlagio() {
		return analisisTerminadosSinPlagio;
	}
	public void setAnalisisTerminadosSinPlagio(int analisisTerminadosSinPlagio) {
		this.analisisTerminadosSinPlagio = analisisTerminadosSinPlagio;
	}
	public int getAnalisisNoTerminados() {
		return analisisNoTerminados;
	}
	public void setAnalisisNoTerminados(int analisisNoTerminados) {
		this.analisisNoTerminados = analisisNoTerminados;
	}
	public int getTotalArchivos() {
		return totalArchivos;
	}
	public void setTotalArchivos(int totalArchivos) {
		this.totalArchivos = totalArchivos;
	}
	public String getListaTutoresCantArchivos() {
		return listaTutoresCantArchivos;
	}
	public void setListaTutoresCantArchivos(String listaTutoresCantArchivos) {
		this.listaTutoresCantArchivos = listaTutoresCantArchivos;
	}
	public String getCantUsuariosDelSistema() {
		return cantUsuariosDelSistema;
	}
	public void setCantUsuariosDelSistema(String cantUsuariosDelSistema) {
		this.cantUsuariosDelSistema = cantUsuariosDelSistema;
	}
	public String getCantArchivosCarpetaCompartida() {
		return cantArchivosCarpetaCompartida;
	}
	public void setCantArchivosCarpetaCompartida(String cantArchivosCarpetaCompartida) {
		this.cantArchivosCarpetaCompartida = cantArchivosCarpetaCompartida;
	}
	public String getCantArchivosEnElSistema() {
		return cantArchivosEnElSistema;
	}
	public void setCantArchivosEnElSistema(String cantArchivosEnElSistema) {
		this.cantArchivosEnElSistema = cantArchivosEnElSistema;
	}
	public String getCantArchivos() {
		return cantArchivos;
	}
	public void setCantArchivos(String cantArchivos) {
		this.cantArchivos = cantArchivos;
	}
	public String getCantAnalisis() {
		return cantAnalisis;
	}
	public void setCantAnalisis(String cantAnalisis) {
		this.cantAnalisis = cantAnalisis;
	}
	public String getCantAnalisisTerminados() {
		return cantAnalisisTerminados;
	}
	public void setCantAnalisisTerminados(String cantAnalisisTerminados) {
		this.cantAnalisisTerminados = cantAnalisisTerminados;
	}
	public String getRol() {
		return Rol;
	}
	public void setRol(String rol) {
		Rol = rol;
	}
	
	public String getPorcentajeDePlagio() {
		return porcentajeDePlagio;
	}
	public void setPorcentajeDePlagio(String porcentajeDePlagio) {
		this.porcentajeDePlagio = porcentajeDePlagio;
	}
	
	public String getPorcentajeDeArchivosPlagiados() {
		return porcentajeDeArchivosPlagiados;
	}
	public void setPorcentajeDeArchivosPlagiados(String porcentajeDeArchivosPlagiados) {
		this.porcentajeDeArchivosPlagiados = porcentajeDeArchivosPlagiados;
	}
	
	
}
