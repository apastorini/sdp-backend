package com.ude.sdp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.RoleDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.dto.UserDTO;
import com.ude.sdp.model.Role;
import com.ude.sdp.model.User;
import com.ude.sdp.repository.sql.RoleRepository;
import com.ude.sdp.repository.sql.UserRepository;

@Service
public class UtilServiceImpl implements UtilService {

	private static final Logger logger = LoggerFactory.getLogger(UtilServiceImpl.class);
	@Autowired
	UserRepository repositoy;

	@Autowired
	RoleRepository roleRepositoy;

	public boolean authenticate(UserAuthDTO userAuthDTO, ResultDTO result, ArrayList<String> rolesPermitidos) {
		boolean ret = false;
		try {
			User user = repositoy.findByEmail(userAuthDTO.getMail());
			UserDTO udto = new UserDTO();
			if (null != user) {
				boolean tieneRol = false;
				int i = 0;
				while (i < user.getRoles().size() && !tieneRol) {
					Role role = user.getRoles().get(i);
					i++;
					if (rolesPermitidos.contains(role.getRoleType().name())) {
						tieneRol = true;
						if ((userAuthDTO.getToken() != null || !userAuthDTO.getToken().trim().equals(""))
								&& (userAuthDTO.getToken().equalsIgnoreCase(user.getJwtToken()))) {
							if (user.getSessionExpiredDate()==null || new Date(System.currentTimeMillis()).compareTo(user.getSessionExpiredDate()) < 0) {
								ret = true;
								udto.setEmail(user.getEmail());
								udto.setName(user.getName());
								udto.setRoles(rolesToRolesDTO(user.getRoles()));
								mensajePersonalizado(0, "Autenticación Exitosa.", udto, result);

							} else {
								result.setCode(1);
								result.setMessage("La sesión expiró. ");
							}
						} else {
							result.setCode(1);
							result.setMessage("Token Incorrecto. ");
						}
					} else {

						result.setCode(1);
						result.setMessage("La operación no se pudo realizar.No dispone del rol adecuado. "
								+ userAuthDTO.getMail());
					}
				}
			} else {
				result.setCode(1);
				result.setMessage("No existe el usuario pasado como parametro: " + userAuthDTO.getMail());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.trace(e.toString());
			result.setCode(1);
			result.setMessage("Ocurrió un error");
		}
		return ret;

	}

	public void mensajePersonalizado(int code, String message, UserDTO userDTO, ResultDTO resultDTO) {

		resultDTO.setCode(code);
		resultDTO.setMessage(message);
		resultDTO.getResult().add(userDTO);

	}

	public List<RoleDTO> rolesToRolesDTO(List<Role> roles) {
		ArrayList<RoleDTO> ret = new ArrayList<RoleDTO>();
		if (roles != null) {
			for (Role r : roles) {
				RoleDTO roldto = new RoleDTO();
				roldto.setDesc(r.getDescription());
				roldto.setId(r.getRoleID());
				roldto.setName(r.getName());
				ret.add(roldto);
			}
		}
		return ret;

	}
}
