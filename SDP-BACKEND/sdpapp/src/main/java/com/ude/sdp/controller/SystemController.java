package com.ude.sdp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ude.sdp.dto.AnalisisDTO;
import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.dto.UserDTO;
import com.ude.sdp.service.SystemService;

@RestController
@RequestMapping("/systemController")
@CrossOrigin
public class SystemController {

	// https://spring.io/guides/gs/rest-service-cors/ ejemplo invocar jquery
	// https://stackoverflow.com/questions/32319396/cors-with-spring-boot-and-angularjs-not-working
	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);
	@Autowired
	SystemService service;

	@RequestMapping(value = "/createUser", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> createUser(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestBody UserDTO userDTO) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);

		userAuthDTO.setToken(token);
		dto = service.createUser(userAuthDTO, userDTO);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/recuperarContrasenia", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> recuperarContrasenia(@RequestBody UserDTO userDTO) {
		ResultDTO resultDTO = new ResultDTO();
		service.recuperarContrasenia(userDTO,resultDTO);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(resultDTO,headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/modifyUser", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> modifyUser(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestBody UserDTO userDTO) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);

		userAuthDTO.setToken(token);
		dto = service.modifyUser(userAuthDTO, userDTO);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> changePassword(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestParam("password") String nuevoPass) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		userAuthDTO.setPassword(nuevoPass);
		dto = service.changePassword(userAuthDTO);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/iniciarAnalisis", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> iniciarAnalisis(@RequestBody AnalisisDTO analisisDTO) {
		ResultDTO dto = new ResultDTO();
		try {

			service.iniciarAnalisis(analisisDTO,dto);

		} catch (Exception e) {
			e.printStackTrace();
			//logger.trace(e.toString());
		}
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/existeAnalisis", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> existeAnalisis(@RequestBody AnalisisDTO analisisDTO) {
		ResultDTO dto = null;
		try {

			dto = service.existeAnalisis(analisisDTO);

		} catch (Exception e) {
			e.printStackTrace();
			//logger.trace(e.toString());
		}
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);
	}
	
	
	
	

	@SuppressWarnings({ "finally", "null" })
	@RequestMapping(value = "/listarAnalisisPorUsuario", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> listarAnalisisPorUsuario(@RequestBody UserAuthDTO user) {
		ResultDTO dto = new ResultDTO();

		try {
			dto = service.listarAnalisisPorUsuario(user);

		} catch (Exception e) {
			dto.setCode(1);
			dto.setMessage("Ocurrió un error.");
			e.printStackTrace();
			//logger.trace(e.toString());
		}
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);

	}

	@SuppressWarnings({ "finally", "null" })
	@RequestMapping(value = "/listarUsuarios", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> listarUsuarios(@RequestBody UserAuthDTO user) {
		ResultDTO dto = new ResultDTO();

		dto.setMessage("Lista de Usuarios del sistema.");

		try {
			dto = service.listarUsuarios(user);

		} catch (Exception e) {
			dto.setMessage("Ocurrio un error.");
			// e.printStackTrace();
			logger.trace(e.toString());
		}
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);

	}

	@RequestMapping(value = "/reporteAnalisis", method = RequestMethod.GET)
	public ResponseEntity<Resource> reporteAnalisis(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestParam("idAnalisis") long idAnalisis) {

		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		ResultDTO dto=new ResultDTO();
		Resource resource = service.reporteAnalisis(userAuthDTO, idAnalisis,dto);
		

		String contentType = "application/pdf";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "reporte-"+ idAnalisis +".pdf" + "\"")
				.body(resource);

	}
	
	@RequestMapping(value = "/getDashBoard", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> getDashBoard(@RequestParam("email") String email,
			@RequestParam("token") String token) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		dto = service.getDashBoard(userAuthDTO);
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<ResultDTO>(dto,headers, HttpStatus.OK);
	}

}
