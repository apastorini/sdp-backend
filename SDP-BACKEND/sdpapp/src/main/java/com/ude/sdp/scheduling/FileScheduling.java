package com.ude.sdp.scheduling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ude.sdp.FileStorageProperties;
import com.ude.sdp.extractor.DocumentExtractor;
import com.ude.sdp.model.File;
import com.ude.sdp.model.User;
import com.ude.sdp.repository.sql.AnalisisRepository;
import com.ude.sdp.repository.sql.FileRepository;
import com.ude.sdp.repository.sql.NWordsRepository;
import com.ude.sdp.repository.sql.UserRepository;

@Component
public class FileScheduling {

	
	private static final Logger logger = LoggerFactory.getLogger(FileScheduling.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	NWordsRepository nwordsRepository;

	@Autowired
	AnalisisRepository analisisRepository;

	@Autowired
	FileRepository fileRepository;

	@Autowired
	FileStorageProperties fileStorageProperties;
	
	
	private Path fileStorageLocation;

	
	@PostConstruct
	public void init() {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

	}

	private static final Logger log = LoggerFactory.getLogger(FileScheduling.class);

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public final int N = 5;

		
	
	@Scheduled(initialDelay = 0, fixedDelay = 30 * 1000) // cada 30 segundos luego de finalizada la anterior
	@Transactional
	public void processDocumentToPdf() {
		log.info("Procesar Archivos {}", dateFormat.format(System.currentTimeMillis()));
		List<File> lista = fileRepository.findAllNotPdf();
		
		if (lista != null) {
			try {
				
				for (File f : lista) {
							try {
									System.out.println("Procesar archivos " + f.getName());
									ByteArrayInputStream pdfStream = null;	
									byte currentXMLBytes[] = f.getContent();
									ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
											currentXMLBytes);
									ByteArrayOutputStream outStream = DocumentExtractor
											.convertDoc2Pdf(f.getExtension().toUpperCase(), byteArrayInputStream);

									pdfStream = new ByteArrayInputStream(outStream.toByteArray());

									byte[] array = new byte[pdfStream.available()];
									if (array != null) {
										int read = pdfStream.read(array);
										if (read > 0) {
											f.setPdfContent(array);
											f.setToPdf(false);
											fileRepository.save(f);
										}
									}
								

							} catch (IOException e) {
								logger.trace(e.toString());
								f.setLog(e.toString());
								e.printStackTrace();
								fileRepository.save(f);
							}
						}
				
			} catch (Exception e) {

				logger.trace(e.toString());
				e.printStackTrace();
				
			}
		}

	}


	@Scheduled(initialDelay = 0, fixedDelay = 30 * 1000) // cada 30 segundos luego de finalizada la anterior
	@Transactional
	public void procesarArchivosEnDisco() {

		User user = userRepository.findByEmail("sdptestadm@gmail.com");
		String targetLocationPath = this.fileStorageLocation.toString();
		if (user != null) {
			try {
				java.io.File[] files = new java.io.File(targetLocationPath).listFiles();
				if (files != null) {
					for (java.io.File file : files) {
						if (file.isFile()) {
							File f = fileRepository
									.findByPath(targetLocationPath + java.io.File.separator + file.getName());
							if (f == null) {
								String fileName = file.getName();

								String fileExtension = FilenameUtils.getExtension(file.getName()).toUpperCase();

								ByteArrayInputStream stream = reteriveByteArrayInputStream(file);

								File fileObject = new File();
								fileObject.setName(fileName);

								byte[] array = new byte[stream.available()];
								int read = stream.read(array);
								if (read > 0) {
									fileObject.setContent(array);
									fileObject.setExtension(fileExtension);
									String numberAsString = String.format("%.2f", array.length / (double) 1024);
									fileObject.setSize(numberAsString + "Kb");

									if (user.getFiles() != null) {
										user.getFiles().add(fileObject);
									} else {
										List<File> fs = new ArrayList<File>();
										fs.add(fileObject);
										user.getFiles().addAll(fs);
									}
									fileObject.setToAnalize(true);
									if (!fileExtension.toUpperCase().equals("PDF")) {
										fileObject.setToPdf(true);
										

									} else {
										fileObject.setToPdf(false);
										fileObject.setPdfContent(array);
										/*
										java.io.File inputFile = new java.io.File(fileObject.getPath());
										java.io.File outputFile = new java.io.File("document.pdf");
							https://ranjeetgkp.wordpress.com/2012/05/08/converting-doc-to-pdf-using-jodconverter/
										// connect to an OpenOffice.org instance running on port 8100
										OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
										connection.connect();

										// convert
										DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
										converter.convert(inputFile, outputFile);

										// close the connection
										connection.disconnect();*/
										
									}
									fileObject.setPath(targetLocationPath + java.io.File.separator + file.getName());
									fileObject.setUser(user);
									
									fileObject = fileRepository.save(fileObject);
									userRepository.save(user);
								}

							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.trace(e.toString());
			}
		}
	}

	public static ByteArrayInputStream reteriveByteArrayInputStream(java.io.File file) throws IOException {
		return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
	}

	
}
