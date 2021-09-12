package com.ude.sdp.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.ude.sdp.FileStorageProperties;
import com.ude.sdp.dto.FileDTO;
import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.exceptions.FileNotFoundException;
import com.ude.sdp.model.File;
import com.ude.sdp.model.User;
import com.ude.sdp.repository.sql.FileRepository;
import com.ude.sdp.repository.sql.RoleRepository;
import com.ude.sdp.repository.sql.UserRepository;

@Service
public class DocumentServiceImpl implements DocumentService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
	@Autowired
	UserRepository repository;

	@Autowired
	RoleRepository roleRepositoy;

	@Autowired
	FileRepository fileRepository;

	@Autowired
	@Qualifier("javaMailSender")
	public MailSender mailSender;

	@Autowired
	FileStorageProperties fileStorageProperties;

	@Autowired
	UtilService utilService;

	private Path fileStorageLocation;

	@PostConstruct
	public void init() {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			// ex.printStackTrace();
			logger.trace(ex.toString());
		}
	}

	@Override
	public ResultDTO addFileToUser(UserAuthDTO userAuthDTO, MultipartFile file) {
		User user = repository.findByEmail(userAuthDTO.getMail());
		ResultDTO resultDTO = new ResultDTO();

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		try {
			if (utilService.authenticate(userAuthDTO, result, roles)) {
				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				if (!this.existUserFile(userAuthDTO.getMail(), fileName)) {

					String fileExtension = FilenameUtils.getExtension(fileName).toUpperCase();

					if (fileExtension.toUpperCase().equals("PDF") || fileExtension.toUpperCase().equals("DOC")
							|| fileExtension.toUpperCase().equals("DOCX")) {
						ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());

						File fileObject = new File();
						fileObject.setName(fileName);
						//fileObject.setContent(IOUtils.toString(stream, "UTF-8"));
						byte[] array = new byte[stream.available()];
						int read = stream.read(array);
						//fileObject.setContent(IOUtils.toByteArray(new StringReader(stream), "UTF-8"));
						if (read > 0) {
							fileObject.setContent(array);
							fileObject.setExtension(fileExtension);
							// DecimalFormat decimalFormat = new DecimalFormat(pattern);
							// decimalFormat.setRoundingMode(RoundingMode.DOWN);
							// decimalFormat.format(file.getBytes().length / (double) 1024);
							String numberAsString = String.format("%.2f", file.getBytes().length / (double) 1024);
							fileObject.setSize(numberAsString + "Kb");

							user.getFiles().add(fileObject);
							fileObject.setToAnalize(true);
							fileObject.setBorrado(false);
							if (!fileExtension.toUpperCase().equals("PDF")) {
								fileObject.setToPdf(true);

							} else {
								fileObject.setToPdf(false);
								fileObject.setPdfContent(array);
							}
							fileObject.setPath(null);
							fileObject.setUser(user);
							fileObject = fileRepository.save(fileObject);
							user = repository.save(user);
							resultDTO.setCode(0);
							resultDTO.setMessage("Se ha guardado el archivo");
							FileDTO fileDTO = new FileDTO(fileObject.getFileID(), fileObject.getName(),
									fileObject.getExtension(), fileObject.getSize(), null, false,!fileObject.isToPdf());
							resultDTO.getResult().add(fileDTO);
						}
					} else {
						resultDTO.setCode(1);
						resultDTO.setMessage("Solo se pueden subir archivos con formato PDF,DOC,DOCX");
					}

				} else {
					resultDTO.setCode(1);
					resultDTO.setMessage("Ya existe un archivo con el mismo nombre.");
				}
			} else {
				resultDTO.setCode(1);
				resultDTO.setMessage("No se pudo autenticar.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrio un error.");

		}
		return resultDTO;
	}

	@Override
	public ResultDTO addFileToSharedFolder(UserAuthDTO userAuthDTO, MultipartFile file) {
		User user = repository.findByEmail(userAuthDTO.getMail());
		ResultDTO resultDTO = new ResultDTO();

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		try {
			if (utilService.authenticate(userAuthDTO, result, roles)) {

				String fileName = StringUtils.cleanPath(file.getOriginalFilename());
				Path targetLocation = this.fileStorageLocation.resolve(fileName);
				Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
				File fileObject = new File();
				fileObject.setName(fileName);
				String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/uploads/")
						.path(fileName).toUriString();
				fileObject.setPath(fileDownloadUri);
				fileObject.setExtension(FilenameUtils.getExtension(fileName));

				String numberAsString = String.format("%.2f", file.getBytes().length / (double) 1024);
				fileObject.setSize(numberAsString + "Kb");

				fileObject.setBorrado(false);
				fileObject.setToAnalize(true);
				user.getFiles().add(fileObject);
				fileRepository.save(fileObject);
				repository.save(user);
				resultDTO.setCode(0);
				resultDTO.setMessage("File has been stored");
				FileDTO fileDTO = new FileDTO(fileObject.getFileID(), fileObject.getName(), fileObject.getExtension(),
						fileObject.getSize(), fileDownloadUri, false,FilenameUtils.getExtension(fileName).equals("PDF"));
				resultDTO.getResult().add(fileDTO);

			}

		} catch (IOException e) {
			// e.printStackTrace();
			logger.trace(e.toString());
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrio un error.");
		}
		return resultDTO;
	}

	@Override
	public ResultDTO listUserFiles(UserAuthDTO userAuthDTO) {
		User user = repository.findByEmail(userAuthDTO.getMail());
		ResultDTO resultDTO = new ResultDTO();

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		roles.add(UtilService.ADMIN);
		try {
			if (utilService.authenticate(userAuthDTO, result, roles)) {
				resultDTO.setCode(0);
				resultDTO.setMessage("Se devuelven todos los archivos para el usuario " + user.getName());
				if (user.getFiles() != null && user.getFiles().size() > 0) {
					for (File file : user.getFiles()) {

						if (!file.isBorrado()) {
							resultDTO.getResult().add(new FileDTO(file.getFileID(), file.getName(), file.getExtension(),
									file.getSize(), null, false,!file.isToPdf()));
						}

					}
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.trace(e.toString());
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrio un error.");
		}
		return resultDTO;
	}

	private boolean existUserFile(String email, String fileName) {
		User user = repository.findByEmail(email);
		boolean encontre = false;

		try {

			if (user.getFiles() != null && user.getFiles().size() > 0) {

				int i = 0;
				while (!encontre && i < user.getFiles().size()) {
					if (user.getFiles().get(i).getName().toUpperCase().equals(fileName.toUpperCase())) {
						encontre = true;

					}
					i++;
				}

			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.trace(e.toString());

		}
		return encontre;

	}

	@Override
	public ResultDTO existUserFile(UserAuthDTO userAuthDTO, String fileName) {
		User user = repository.findByEmail(userAuthDTO.getMail());
		ResultDTO resultDTO = new ResultDTO();

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		roles.add(UtilService.ADMIN);
		try {
			if (utilService.authenticate(userAuthDTO, result, roles)) {
				resultDTO.setCode(0);
				resultDTO.setMessage("NO hay archivo con ese nombre: " + user.getName());

				if (user.getFiles() != null && user.getFiles().size() > 0) {
					boolean encontre = false;
					int i = 0;
					while (!encontre && i < user.getFiles().size()) {
						if (user.getFiles().get(i).getName().equals(fileName)) {
							encontre = true;
							resultDTO.setCode(1);
							resultDTO.setMessage("Ya existe un archivo con ese nombre: " + user.getName());
						}
						i++;
					}

				} else {
					resultDTO.setMessage("NO hay archivos con ese nombre");
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.trace(e.toString());
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrio un error.");
		}
		return resultDTO;
	}

	@Override
	public ResultDTO listSharedFiles(UserAuthDTO userAuthDTO) {
		User userAdm = repository.findByEmail("sdptestadm@gmail.com");

		ResultDTO resultDTO = new ResultDTO();

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		roles.add(UtilService.ADMIN);
		try {
			if (utilService.authenticate(userAuthDTO, result, roles)) {

				resultDTO.setCode(0);
				resultDTO.setMessage("Listado de archivos compartidos");
				// los archivos comparetidos son propiedad de admin
				if (userAdm.getFiles() != null && userAdm.getFiles().size() > 0) {
					for (File file : userAdm.getFiles()) {
						if (file.getPath() != null && !file.isBorrado()) {
							resultDTO.getResult().add(new FileDTO(file.getFileID(), file.getName(), file.getExtension(),
									file.getSize(), file.getPath(), false,!file.isToPdf()));
						}
					}
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			logger.trace(e.toString());
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrio un error.");
		}
		return resultDTO;
	}

	@Override
	public Resource downloadSharedFiles(UserAuthDTO userAuthDTO, String nameOfFile) {

		Resource resource = null;
		User user = repository.findByEmail(userAuthDTO.getMail());
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		roles.add(UtilService.ADMIN);
		ResultDTO r = new ResultDTO();
		try {
			if (utilService.authenticate(userAuthDTO, r, roles)) {
				resource = loadFileAsResource(nameOfFile);
			}

		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.trace(e.toString());
		}
		return resource;
	}

	@Override
	public ResultDTO removeFileFromUser(UserAuthDTO userAuthDTO, long fileID) {
		User user = null;
		user = repository.findByEmail(userAuthDTO.getMail());
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		ResultDTO r = new ResultDTO();
		boolean borrado = false;
		try {
			if (utilService.authenticate(userAuthDTO, r, roles)) {
				for (File file : user.getFiles()) {
					if (file.getFileID() == fileID) {
						r.setCode(0);
						file.setBorrado(true);
						fileRepository.save(file);
						r.setMessage("Archivo Borrado");
						borrado = true;
						break;
					}
				}
			}
			if (!borrado) {
				r.setCode(1);
				r.setMessage("No se encontró el archivo");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.trace(e.toString());
			r.setCode(1);
			r.setMessage("Ocurrio un error y no se pudo ejecutar la operación");
		}
		return r;
	}

	@Override
	public File downloadUserFiles(UserAuthDTO userAuthDTO, long fileID) {

		File file = null;
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.TUTOR);
		ResultDTO r = new ResultDTO();
		try {
			if (utilService.authenticate(userAuthDTO, r, roles)) {
				file = fileRepository.findByFileID(fileID);
				if (null != file) {
					return file;
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//logger.trace(e.toString());
		}
		return file;

	}

	public Resource loadFileAsResource(String fileName) throws Exception {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new Exception();
		}
	}

}
