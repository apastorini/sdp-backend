package com.ude.sdp.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.model.File;
import com.ude.sdp.service.DocumentService;
import com.ude.sdp.service.SystemService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/documentController")
public class DocumentController {

	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
	@Autowired
	DocumentService service;

	@Autowired
	SystemService systemService;

	// utf8mb4 var1 varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci
	// NOT NULL

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/addFileToUser", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> addFileToUser(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestParam("file") MultipartFile file) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);

		userAuthDTO.setToken(token);
		dto = service.addFileToUser(userAuthDTO, file);
		HttpHeaders headers = new HttpHeaders();
		// headers.add("Access-Control-Allow-Origin", "*");
		return new ResponseEntity<ResultDTO>(dto, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/addFileToSharedFolder", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> addFileToSharedFolder(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestParam("file") MultipartFile file) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);

		userAuthDTO.setToken(token);
		dto = service.addFileToSharedFolder(userAuthDTO, file);
		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value = "/listUserFiles", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> listUserFiles(@RequestParam("email") String email,
			@RequestParam("token") String token) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		dto = service.listUserFiles(userAuthDTO);
		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/existUserFile", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> existUserFile(@RequestParam("email") String email,
			@RequestParam("token") String token,@RequestParam("fileName") String fileName) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		dto = service.existUserFile(userAuthDTO,fileName);
		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value = "/listSharedFiles", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> listSharedFiles(@RequestParam("email") String email,
			@RequestParam("token") String token) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		dto = service.listSharedFiles(userAuthDTO);
		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	@RequestMapping(value = "/downloadSharedFiles", method = RequestMethod.POST)
	public ResponseEntity<Resource> downloadSharedFiles(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestParam("nameOfFile") String nameOfFile) {
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		Resource resource = service.downloadSharedFiles(userAuthDTO, nameOfFile);
		String contentType = "application/octet-stream";
		if (resource != null)
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		else
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "File Not Found").body(resource);
	}

	@RequestMapping(value = "/downloadUserFiles", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadUserFiles(@RequestParam ("email") String email,
			@RequestParam ("token") String token, @RequestParam ("fileID") long fileID) {

		String contentType = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);

		userAuthDTO.setToken(token);
		File file = service.downloadUserFiles(userAuthDTO, fileID);
		if (file != null && !file.isToPdf()) {
			Resource resource = new ByteArrayResource(file.getPdfContent());

			//if (file.getExtension().toUpperCase().equals("PDF")) {
			contentType = "application/pdf";
			//} 
			/*else {
				contentType = "application/msword";
			}*/
			// = "application/octet-stream";

			return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
					.body(resource);
		} else {
			contentType = "application/octet-stream";
			return ResponseEntity.noContent().build();
		}
	}

	

	// https://www.callicoder.com/spring-boot-file-upload-download-rest-api-example/
	@RequestMapping(value = "/download/{fileID:.+}/{email:.+}/{token:.+}", method = RequestMethod.GET, produces = "application/zip")
	public ResponseEntity<InputStreamResource> download(@PathVariable("fileID") String fileID,
			@PathVariable("email") String email, @PathVariable("token") String token)  {

		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);
		userAuthDTO.setToken(token);
		File file = service.downloadUserFiles(userAuthDTO, Long.valueOf(fileID));

		// File pdfFile = new File("c://andres//"+ fileName);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/zip"));
		headers.add("Content-Disposition", "attachment; filename=" + "pru.zip");
		// Arrays.copyOf(buf, count);
		// headers.setContentLength((int) file.getContent().length*1024);
		// andaba mal porque en length daba mal

		//System.out.println("Calling Download:-3 ");

		InputStream targetStream = new ByteArrayInputStream(file.getContent());
		ZipInputStream zis = new ZipInputStream(targetStream);
		ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(
				new InputStreamResource(zis), headers, HttpStatus.OK);

		return response;

	}

	@RequestMapping(value = "/removeFileFromUser", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> removeFileFromUser(@RequestParam("email") String email,
			@RequestParam("token") String token, @RequestParam("fileID") long fileID) {
		ResultDTO dto = null;
		UserAuthDTO userAuthDTO = new UserAuthDTO();
		userAuthDTO.setMail(email);

		userAuthDTO.setToken(token);
		dto = service.removeFileFromUser(userAuthDTO, fileID);
		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);
	}

	

	

	@RequestMapping(value = "/echo", method = RequestMethod.POST)
	public ResponseEntity<ResultDTO> echo(@RequestParam("email") String email) {
		ResultDTO dto = new ResultDTO();
		dto.setCode(0);
		dto.setMessage("Echo del email " + email);

		return new ResponseEntity<ResultDTO>(dto, HttpStatus.OK);

	}
}