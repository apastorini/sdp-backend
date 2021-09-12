package com.ude.sdp.service;



import org.springframework.core.io.Resource;

import com.ude.sdp.dto.AnalisisDTO;
import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.dto.UserDTO;

public interface SystemService {

	public boolean isUserID(String mail);

	public ResultDTO logout(UserAuthDTO dto);

	public ResultDTO login(UserAuthDTO dto);

	public ResultDTO sendPasswordByMail(String mail, String urlBase);


	public ResultDTO createUser(UserAuthDTO userAuthDTO, UserDTO userDTO);

	public ResultDTO modifyUser(UserAuthDTO userAuthDTO, UserDTO userDTO);
	
	public void iniciarAnalisis(AnalisisDTO analisisDTO,ResultDTO dto);
	public ResultDTO existeAnalisis(AnalisisDTO analisisDTO);
	public ResultDTO listarAnalisisPorUsuario(UserAuthDTO usuario);
	public ResultDTO listarUsuarios(UserAuthDTO usuario);
	public ResultDTO getDashBoard(UserAuthDTO usuario);
	
	public Resource reporteAnalisis(UserAuthDTO usuario, long idAnalisis,ResultDTO result);
	public ResultDTO changePassword(UserAuthDTO usuario);
	
	ResultDTO isLoggedIn(UserAuthDTO dto);

	public void recuperarContrasenia(UserDTO userDTO,ResultDTO resultoDTO);
	
}
