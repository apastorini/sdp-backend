package com.ude.sdp.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.service.DocumentService;
import com.ude.sdp.service.SystemService;

@RestController
@RequestMapping("/loginController")
@CrossOrigin
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private SystemService service;
	
	@Autowired
	DocumentService serviceDocument;

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> logout(@RequestBody UserAuthDTO userAuthDTO) {
		ResultDTO dto = null;
		try {
			dto = service.logout(userAuthDTO);

		} catch (Exception e) {
			//e.printStackTrace();
			logger.info(e.getMessage());
		}

		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> login(@RequestBody UserAuthDTO userAuthDTO) {
		ResultDTO dto = null;
		try {
			dto = service.login(userAuthDTO);

		} catch (Exception e) {
			logger.trace(e.toString());
			//e.printStackTrace();
		}

		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/isLoggedIn", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> isLoggedIn(@RequestBody UserAuthDTO userAuthDTO) {
		ResultDTO dto = null;
		try {
			dto = service.isLoggedIn(userAuthDTO);

		} catch (Exception e) {
			logger.trace(e.toString());
			//e.printStackTrace();
		}

		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value = "/sendPasswordByMail", method = RequestMethod.GET)
	public ResponseEntity<ResultDTO> sendPasswordByMail(HttpServletRequest request,@RequestParam("email") String email) {
		String url = request.getServerName() + ":" + request.getServerPort();
		ResultDTO dto = null;
		try {
			dto = service.sendPasswordByMail(email,url);
			
		} catch (Exception e) {
			logger.trace(e.toString());
			dto=new ResultDTO();
			dto.setCode(1);
			dto.setMessage("Ocurrió un error al eviar el correo");
		}

		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value = "/isUsedId", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isUsedId(@RequestParam("email") String email) {
		boolean isUserIDAvailable = false;
		try {
			isUserIDAvailable = service.isUserID(email);
		} catch (Exception e) {
			logger.trace(e.toString());
		}

		return new ResponseEntity<Boolean>(isUserIDAvailable, HttpStatus.OK);
	}
	
	

}
