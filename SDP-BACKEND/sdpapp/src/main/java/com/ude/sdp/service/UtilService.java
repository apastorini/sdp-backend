package com.ude.sdp.service;



import java.util.ArrayList;

import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.dto.UserDTO;


public interface UtilService {
	public static String ADMIN="ADMIN";
	public static String TUTOR="TUTOR";
	public boolean authenticate(UserAuthDTO userAuthDTO, ResultDTO result,ArrayList<String> rolesPermitidos);
	public void mensajePersonalizado(int code, String message, UserDTO userDTO,ResultDTO resultDTO);
	

}
