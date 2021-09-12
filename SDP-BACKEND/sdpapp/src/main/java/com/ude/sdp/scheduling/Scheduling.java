package com.ude.sdp.scheduling;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.ude.sdp.FileStorageProperties;
import com.ude.sdp.extractor.BreakIteratorSentenceSplitter;
import com.ude.sdp.model.Analisis;
import com.ude.sdp.model.File;
import com.ude.sdp.model.NWords;
import com.ude.sdp.model.User;
import com.ude.sdp.repository.sql.AnalisisRepository;
import com.ude.sdp.repository.sql.FileRepository;
import com.ude.sdp.repository.sql.NWordsRepository;
import com.ude.sdp.repository.sql.UserRepository;
import com.ude.sdp.service.MailSender;
import com.ude.sdp.util.GeneratePdfReport;

import net.partow.GeneralHashFunctionLibrary;

@Component
public class Scheduling {

	static final String URL_ADD_NWORDS = "http://localhost:8080/dynamodb/saveNWORDS";
	private static final Logger logger = LoggerFactory.getLogger(Scheduling.class);

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
	
	@Autowired
	@Qualifier("javaMailSender")
	public MailSender mailSender;

	private Path fileStorageLocation;

	@Value("${dynamo.cache}")
	private boolean dynamoCache;

	@PostConstruct
	public void init() {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

	}

	private static final Logger log = LoggerFactory.getLogger(Scheduling.class);

	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public final int N = 5;

	public void sendEmailToUser(String user, String idAnalisis,InputStream reporte, int largoBytes) {

		//
		try {
			
			// String from = "sdptestadmin@gmail.com";//lo envia desde
			// prototypeoneb@gmail.com
			// que esta en appliation properties parametro spring.mail.username
			String subject = "SDP Fin Analisis " + idAnalisis;
			String body = "Culminó el Analisis con id " + idAnalisis +". Puede descargar el reporte con los resultados ingresando al sistema."  ;
			mailSender.sendMail(user, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// @JsonSerialize(keyUsing = MapSerializer.class)
	Hashtable<NWords, List<NWords>> hash = null;

	@Scheduled(initialDelay = 0, fixedDelay = 60 * 60 * 1000) // cada una hora luego de finalizada la anterior
	@Transactional
	public void clearSession() {

		log.info("Verificacion de sesiones {}", dateFormat.format(System.currentTimeMillis()));
		List<User> lista = userRepository.findAll();
		if (lista != null) {
			for (User u : lista) {
				if (u.getSessionExpiredDate() != null) {
					Date now = new Date(System.currentTimeMillis());
					if (u.getSessionExpiredDate().compareTo(now) < 0) {
						u.setJwtToken(null);
						u.setSessionExpiredDate(null);
						userRepository.save(u);
						System.out.println("Limpiar Sesion - " + u.getEmail());
					}
				}
			}
		}

	}

	@SuppressWarnings("unused")
	public void extractNWordsSinPunto() {
		// System.out.println("Extraer palabras - " + System.currentTimeMillis() /
		// 1000);
		// log.info("Extraer palabras {}", dateFormat.format(new Date()));
		List<File> lista = fileRepository.findAll();
		int chunk_size = 1;
		String chunk = "";
		String previusChunk = "";
		if (lista != null) {
			for (File f : lista) {
				List<NWords> nwords = new ArrayList<NWords>();
				if (f.isToAnalize() == true && f.isToPdf() == false) {
					// esta para analizar y ya esta convertido a pdf
					try {
						try {

							if (f.getPdfContent() != null && f.getPdfContent().length > 0) {
								InputStream myInputStream = new ByteArrayInputStream(f.getPdfContent());
								PdfReader reader = new PdfReader(myInputStream);
								System.out.println("This PDF has " + reader.getNumberOfPages() + " pages.");
								int numPage = reader.getNumberOfPages();
								String page = null;
								for (int i = 0; i < numPage; i++) {
									int ord = 0;
									page = PdfTextExtractor.getTextFromPage(reader, i + 1);
									page = Normalizer.normalize(page, Form.NFD)
											.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
									// sacar puntos y comas dejar solo palabras
									//System.out.println("pagina sin tildes " + page);
									Pattern pattern = Pattern.compile("\\w+");
									Matcher matcher = pattern.matcher(page);
									chunk = "";
									previusChunk = "";
									chunk_size = 1;

									while (matcher.find()) {

										if (chunk_size == N) {

											NWords n = new NWords();
											n.setContent(chunk);
											n.setFile(f);
											n.setnWords(6);
											n.setPageNum(i + 1);
											n.setHash((new Long(GeneralHashFunctionLibrary.DJBHash(chunk))).toString());
											n.setOrd(ord);
											ord++;
											chunk = "";
											chunk_size = 1;
											nwords.add(n);
										} else {
											chunk = chunk + " " + matcher.group();
											chunk_size++;
										}

									}
									if (chunk_size > 0) {
										// puede quedar con menos palabras
										NWords n = new NWords();
										n.setContent(chunk);
										n.setFile(f);
										n.setnWords(6);
										n.setPageNum(i + 1);
										n.setHash((new Long(GeneralHashFunctionLibrary.DJBHash(chunk))).toString());
										n.setOrd(ord);
										ord++;
										chunk = "";
										chunk_size = 1;
										// wordsRepository.save(n);
										nwords.add(n);
									}
									// agrego todo junto

								}
								log.info("Termine de extrear palabras" + " " + f.getName(),
										dateFormat.format(new Date()));
								f.setToAnalize(false);
								f.setnWords(nwords);
								fileRepository.save(f);
								// GeneralHashFunctionLibrary.APHash(str);

							}

						} catch (IOException e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Scheduled(initialDelay = 0, fixedDelay = 30 * 1000) // cada 30 segundos luego de finalizada la anterior
	@Transactional
	public void extractNWords() {

		List<File> lista = fileRepository.findAllNotNWordsProcess();
		int chunk_size = 0;
		String chunk = "";

		if (lista != null) {

			for (File f : lista) {
				List<NWords> nwords = new ArrayList<NWords>();
				if (f.isToAnalize() == true && f.isToPdf() == false) {
					// esta para analizar y ya esta convertido a pdf
					try {

						if (f.getPdfContent() != null && f.getPdfContent().length > 0) {
							System.out.println("Extrayendo palabras  - " + f.getName()
									+ dateFormat.format(System.currentTimeMillis()));

							InputStream myInputStream = new ByteArrayInputStream(f.getPdfContent());
							PdfReader reader = new PdfReader(myInputStream);
							// System.out.println("This PDF has " + reader.getNumberOfPages() + " pages.");
							int cantPages = reader.getNumberOfPages();
							String page = null;
							for (int pageNum = 1; pageNum <= cantPages; pageNum++) {
								int ord = 0;
								page = PdfTextExtractor.getTextFromPage(reader, pageNum);
								// System.out.println("Page Content:\n\n" + page + "\n\n");
								BreakIteratorSentenceSplitter bt = new BreakIteratorSentenceSplitter();
								String[] lineas = bt.split(page);
								chunk = "";
								String previusChunk = "";
								chunk_size = 0;

								if (lineas != null) {
									for (String linea : lineas) {
										String[] values = linea.toUpperCase().replaceAll("^[.,\\s\n]+", "")
												.split("[.,\\s]+");
										if (values != null && values.length > 0) {
											for (int i = 0; i < values.length; i++) {
												String s = values[i];
												String[] myStringArray = { "A", "O", "U", "EL", "LA", "THE", "DE",
														"LOS", "LAS", "AT", "OR", "BY", "Y" };
												boolean found = Arrays.asList(myStringArray).contains(s);
												if (!found) {

													if (chunk_size == N) {
														NWords n = new NWords();
														//System.out.println("chunk " + chunk);
														n.setContent(chunk);
														n.setFile(f);
														n.setnWords(N);
														n.setPageNum(pageNum);
														n.setHash((new Long(GeneralHashFunctionLibrary.DJBHash(chunk)))
																.toString());
														n.setOrd(ord);
														n.setId(Long.valueOf(String.valueOf(f.getFileID())
																.concat(String.valueOf(pageNum))
																.concat(String.valueOf(ord))).longValue());
														
														ord++;
														previusChunk = chunk;
														chunk = s;
														chunk_size = 1;
														nwords.add(n);
														//f.getnWords().add(n);
														//
														try {
															nwordsRepository.save(n);
															//fileRepository.save(f);
														}catch(Exception e) {
															e.printStackTrace();
														}
													} else {
														// se arma la cadena
														if (chunk_size == 0) {
															chunk = s;// matcher.group();
															chunk_size++;
														} else {
															chunk = chunk + " " + s;// matcher.group();
															chunk_size++;
														}
													}
												}

											}

											if (chunk_size > 0) {
												// puede quedar con menos palabras
												if (previusChunk != null && previusChunk.length() > 0) {
													NWords n = new NWords();
													String[] c1 = chunk.split(" ");
													String[] c2 = previusChunk.split(" ");
													int faltan = N - c1.length;
													int count = 1;
													int largoc2 = c2.length;
													String nchunk = "";
													while (count <= largoc2 && faltan > 0) {
														nchunk = c2[N - count] + " " + nchunk;
														count++;
														faltan--;
													}
													chunk = nchunk + "" + chunk;
													n.setContent(chunk);
													n.setFile(f);
													n.setnWords(N);
													n.setPageNum(pageNum);
													n.setHash((new Long(GeneralHashFunctionLibrary.DJBHash(chunk)))
															.toString());
													n.setOrd(ord);
													n.setId(Long.valueOf(String.valueOf(f.getFileID())
															.concat(String.valueOf(pageNum))
															.concat(String.valueOf(ord))).longValue());
													
													ord++;
													chunk = "";
													chunk_size = 0;

													nwords.add(n);
													//f.getnWords().add(n);
													try {
														nwordsRepository.save(n);
														//fileRepository.save(f);
													}catch(Exception e) {
														e.printStackTrace();
													}
												}
											}
										}
									}
								}
								// agrego todo junto

							}
							log.info("Termine de extrear palabras" + " " + f.getName(), dateFormat.format(new Date()));
							f.setToAnalize(false);
							
							/*f.getnWords().clear(); 
							f.getnWords().addAll(nwords);
							*/
							
							/*if(nwords!=null && nwords.size()>0) { 
								for(NWords n:nwords) {
									f.getnWords().add(n); 
								}
							 
							}*/
														
							try {
								fileRepository.save(f);
							}catch(Exception e) {
								e.printStackTrace();
							}
							log.info("Termine de extrear palabras Guardando" + " " + f.getName(),
									dateFormat.format(new Date()));
							// GeneralHashFunctionLibrary.APHash(str);

						}

					} catch (Exception e) {
						e.printStackTrace();
						// logger.trace(e.toString());
						String stacktrace = ExceptionUtils.getStackTrace(e);
						f.setLog(stacktrace);
						fileRepository.save(f);
					}

				}

			}
		}
	}

	@SuppressWarnings("unused")
	private void addDynamo(NWords n) {
		if (dynamoCache) {
			// https://o7planning.org/en/11647/spring-boot-restful-client-with-resttemplate-example#a13889219
			// mando a dinamo
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			headers.add("Accept", MediaType.APPLICATION_XML_VALUE);
			headers.setContentType(MediaType.APPLICATION_XML);

			// Data attached to the request.
			HttpEntity<NWords> requestBody = new HttpEntity<>(n, headers);

			// Send request with POST method.
			NWords nw = restTemplate.postForObject(URL_ADD_NWORDS, requestBody, NWords.class);
			

			// dinamo
		}
	}

	/*@Scheduled(initialDelay = 0, fixedDelay = 30 * 1000) // cada 30 segundos luego de finalizada la anterior
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
									String pattern = "#####.#";
									DecimalFormat decimalFormat = new DecimalFormat(pattern);
									decimalFormat.setRoundingMode(RoundingMode.DOWN);
									decimalFormat.format(array.length / (double) 1024);
									fileObject.setSize(decimalFormat.toPattern() + "Kb");

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
*/
	public static ByteArrayInputStream reteriveByteArrayInputStream(java.io.File file) throws IOException {
		return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
	}

	/*
	 * @Scheduled(initialDelay = 0, fixedDelay = 30 * 1000) // cada 30 segundos
	 * luego de finalizada la anterior
	 * 
	 * @Transactional public void analizar() { // se procesan todos los archivos a
	 * analizar List<Analisis> analisis = analisisRepository.findByNoProcesado(); //
	 * hash es lo que termina guardando el resultado long numMatches = 0; JsonObject
	 * resultado = new JsonObject(); if (analisis != null && analisis.size() > 0) {
	 * try { Analisis a = analisis.get(0); resultado.addProperty("idAnalisis",
	 * a.getId());
	 * 
	 * a.setGeneradoReport(false); System.out.println("Realizando analisis" +
	 * System.currentTimeMillis()); List<NWords> concidencias = new
	 * ArrayList<NWords>(); List<NWords> ltmp = null; // for (Analisis a : analisis)
	 * { File f = fileRepository.findByFileID(a.getIdFile());
	 * resultado.addProperty("Archivo",f.getName()); List<NWords> ns =
	 * f.getnWords(); List<File> filesToCompare = a.getFilesToCompare(); hash = new
	 * Hashtable<NWords, List<NWords>>(); boolean todosProcesados = true; int size =
	 * filesToCompare.size(); int i = 0; JsonArray archivos = new JsonArray();
	 * archivos.add("Russian"); while (i < size && todosProcesados) { File f1 =
	 * a.getFilesToCompare().get(i); archivos.add(f1.getName());
	 * 
	 * if (f1.isToAnalize()) { todosProcesados = false; } i++; }
	 * resultado.add("Archivos",archivos); JsonArray nwordsPorArchivo = new
	 * JsonArray(); resultado.add("nwordsPorArchivo",nwordsPorArchivo);
	 * 
	 * if (!f.isToAnalize() && todosProcesados) { if (ns != null && filesToCompare
	 * != null) { for (File ft : filesToCompare) { //max_allowed_packet // Para cada
	 * arhivo que quiero comparar JsonArray porArchivo = new JsonArray(); JsonObject
	 * nwordsJ=null; JsonObject coincidenciasPorArchivo=new JsonObject();
	 * coincidenciasPorArchivo.addProperty("archivo", ft.getName()); int
	 * contPorArchivo=0; for (NWords n : ns) { //
	 * ltmp=nwordsRepository.findByCustomHash(n.getHash(),a.getIdFile()); ltmp =
	 * nwordsRepository.findByHashAndFile(n.getHash(), ft.getFileID()); if (ltmp !=
	 * null && ltmp.size() > 0) { // System.out.println("Concidencia");
	 * contPorArchivo++; nwordsJ=new JsonObject(); nwordsJ.addProperty("nword",
	 * n.getContent()); nwordsJ.addProperty("ord", n.getOrd());
	 * nwordsJ.addProperty("pagina documento original", n.getPageNum());
	 * porArchivo.add(nwordsJ); JsonArray coincidenciasDeNWordsEnArchivo = new
	 * JsonArray(); porArchivo.add(coincidenciasDeNWordsEnArchivo); for(int
	 * ix=0;ix<ltmp.size();ix++) { NWords nx=ltmp.get(ix); JsonObject jx=new
	 * JsonObject(); jx.addProperty("Pag", nx.getPageNum()); jx.addProperty("Ord",
	 * nx.getOrd()); jx.addProperty("Contenido", nx.getContent());
	 * coincidenciasDeNWordsEnArchivo.add(jx); } List<NWords> auxL = hash.get(n); if
	 * (auxL != null && auxL.size() > 0) { auxL.addAll(ltmp); hash.put(n, auxL); }
	 * else { // primera vez que aparece el hash hash.put(n, ltmp);
	 * 
	 * } concidencias.addAll(ltmp); numMatches++; } }
	 * coincidenciasPorArchivo.add("coincidencias", porArchivo);
	 * coincidenciasPorArchivo.addProperty("cantidad",contPorArchivo);
	 * nwordsPorArchivo.add(coincidenciasPorArchivo); }
	 * resultado.addProperty("CoincidenciasTotales", numMatches); if (concidencias
	 * != null) {
	 * 
	 * if(resultado!=null) { a.setJsonResult(resultado.toString()); }
	 * a.setFechaFinal(new Date(System.currentTimeMillis())); DateTime dt = new
	 * DateTime(); DateTimeFormatter fmt =
	 * DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"); String dtStr =
	 * fmt.print(dt);
	 * 
	 * GeneratePdfReport pdf = new GeneratePdfReport(); ByteArrayInputStream reporte
	 * = pdf.report(f.getName(), a.getId(), dtStr, hash); if (reporte != null) {
	 * byte[] array = new byte[reporte.available()]; if (array != null) { int read =
	 * reporte.read(array); if (read > 0) { a.setResultado(array);
	 * 
	 * } } a.setGeneradoReport(true); }
	 * 
	 * } } // fin de un analisis a.setProcesado(true); a.setNumMatches(numMatches);
	 * a.setFechaFinal(new Date(System.currentTimeMillis()));
	 * analisisRepository.save(a);
	 * 
	 * } // Si no estan todos procesados no se analisan ese analisis
	 * 
	 * // }for // fin de todos los analisis
	 * 
	 * } catch (Exception e) { logger.trace(e.toString()); e.printStackTrace(); } }
	 * }
	 */

	@SuppressWarnings("unused")
	@Scheduled(initialDelay = 0, fixedDelay = 30 * 1000) // cada 30 segundos luego de finalizada la anterior
	@Transactional
	public void analizar() {
		// se procesan de a uno los archivos a analizar
		List<Analisis> analisis = analisisRepository.findByNoProcesado();
		// hash es lo que termina guardando el resultado
		long numMatches = 0;
		JsonObject informeTotal = new JsonObject();
		DateTime dt = new DateTime();
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		String dtStr = dt.toString(fmt);
		if (analisis != null && analisis.size() > 0) {
			try {
				Analisis a = analisis.get(0);
				informeTotal.addProperty("id_Analisis", a.getId());
				System.out.println("Realizando analisis" + System.currentTimeMillis());
				File f = fileRepository.findByFileID(a.getIdFile());
				informeTotal.addProperty("Archivo_Principal", f.getName());
				informeTotal.addProperty("Fecha_Incial", a.getFechaInicial());

				List<File> filesToCompare = a.getFilesToCompare();
				boolean todosProcesados = true;
				int size = filesToCompare.size();
				int i = 0;
				JsonArray corpus = new JsonArray();
				// archivos.add(f1.getName());
				// veo si todos los archivos con los cuales tengo que
				// comparar se les estrayeron las nwords
				while (i < size && todosProcesados) {
					File f1 = a.getFilesToCompare().get(i);
					corpus.add(f1.getName());// si despues no se procesa no pasa nada
					if (f1.isToAnalize()) {
						todosProcesados = false;
					}
					i++;
				}
				informeTotal.add("Archivos_Comparar", corpus);
				if (!f.isToAnalize() && todosProcesados) {

					//List<NWords> ns = f.getnWords();
					List<NWords> ns = this.nwordsRepository.findByFileId(f.getFileID());
					
					JsonArray informesPorArchivo = new JsonArray();
					informeTotal.add("Informes_por_Archivo", informesPorArchivo);

					if (ns != null && filesToCompare != null) {
						for (File ft : filesToCompare) {
							// max_allowed_packet
							// Para cada arhivo que quiero comparar
							JsonObject infoPorArchivo = new JsonObject();
							informesPorArchivo.add(infoPorArchivo);
							infoPorArchivo.addProperty("Archivo", ft.getName());
							JsonArray frasesPorArchivo = new JsonArray();
							JsonObject frase = null;
							infoPorArchivo.add("Frases", frasesPorArchivo);

							int contPorArchivo = 0;

							for (NWords n : ns) {

								List<NWords> mismaFraseEnArchivo = nwordsRepository.findByHashAndFile(n.getHash(),
										ft.getFileID());
								if (mismaFraseEnArchivo != null && mismaFraseEnArchivo.size() > 0) {
									// coincidencia
									// n es el nwords del archivo a comparar
									contPorArchivo++;
									frase = new JsonObject();
									frase.addProperty("Fragmento", n.getContent());
									frase.addProperty("Pag_Original", n.getPageNum());
									JsonArray mismaFrase = new JsonArray();
									frase.add("Frases_Corpus", mismaFrase);
									frasesPorArchivo.add(frase);

									for (int ix = 0; ix < mismaFraseEnArchivo.size(); ix++) {
										// nx son los nwords de los archivos contra los que se compara
										NWords nx = mismaFraseEnArchivo.get(ix);
										JsonObject jx = new JsonObject();
										jx.addProperty("Pag_Corpus", nx.getPageNum());
										jx.addProperty("Contenido", nx.getContent());
										mismaFrase.add(jx);
										numMatches++;
									}

									
								}
							}

							infoPorArchivo.addProperty("Cantidad_Concidencias", contPorArchivo);

						}
						informeTotal.addProperty("Coincidencias_Totales", numMatches);

						dt = new DateTime();
						dtStr = fmt.print(dt);
						a.setProcesado(true);
						a.setNumMatches(numMatches);
						if(ns!=null) {
							a.setNwordsTotales(ns.size());
						}else {
							a.setNwordsTotales(0);
						}
						
						a.setFechaFinal(dtStr);
						informeTotal.addProperty("Fecha_Final", dtStr);

						if (informeTotal != null) {
							a.setJsonResult(informeTotal.toString());
						}

						try {
							GeneratePdfReport pdf = new GeneratePdfReport();
							ByteArrayInputStream reporte = pdf.reportFromJson(informeTotal);
							int largoByte=0;
							if (reporte != null) {
								byte[] array = new byte[reporte.available()];
								if (array != null) {
									int read = reporte.read(array);
									if (read > 0) {
										a.setResultado(array);

									}
									largoByte=array.length;
								}
								a.setGeneradoReport(true);
							}
							analisisRepository.save(a);
							//Enviar mail
							try {
								sendEmailToUser(a.getEmail(),String.valueOf(a.getId()),reporte,largoByte);
							}catch(Exception e) {
								e.printStackTrace();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

				} // Si no estan todos procesados no se hace analisis

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Transactional
	private boolean todosProcesados(List<File> fs) {
		boolean ret = true;
		if (fs != null) {
			for (File f : fs) {
				if (!f.isToAnalize()) {
					ret = true;
				} else {
					ret = false;
				}
			}
		}

		return ret;
	}

}
