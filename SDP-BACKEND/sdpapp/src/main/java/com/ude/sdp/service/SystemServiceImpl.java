package com.ude.sdp.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ude.sdp.FileStorageProperties;
import com.ude.sdp.dto.AnalisisDTO;
import com.ude.sdp.dto.DTO;
import com.ude.sdp.dto.DashBoardDTO;
import com.ude.sdp.dto.InfoAnalisisDTO;
import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.RoleDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.dto.UserDTO;
import com.ude.sdp.model.Analisis;
import com.ude.sdp.model.File;
import com.ude.sdp.model.Role;
import com.ude.sdp.model.User;
import com.ude.sdp.repository.sql.AnalisisRepository;
import com.ude.sdp.repository.sql.FileRepository;
import com.ude.sdp.repository.sql.RoleRepository;
import com.ude.sdp.repository.sql.UserRepository;

@Service
public class SystemServiceImpl implements SystemService {

	private static final Logger logger = LoggerFactory.getLogger(SystemService.class);
	private static final String MAIL_ADMIN = "sdptestadm@gmail.com";
	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepositoy;

	@Autowired
	FileRepository fileRepository;

	@Autowired
	AnalisisRepository analisisRepository;

	@Autowired
	@Qualifier("javaMailSender")
	public MailSender mailSender;

	@Autowired
	UtilService utilService;

	@Autowired
	FileStorageProperties fileStorageProperties;

	private static final Random random = new Random();
	private static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890";

	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Override
	public boolean isUserID(String mail) {
		User user = userRepository.findByEmail(mail);
		if (null != user)
			return true;
		else
			return false;
	}

	@Override
	@SuppressWarnings("unused")
	public ResultDTO logout(UserAuthDTO dto) {
		User user = null;
		UserDTO userDTO = null;
		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.ADMIN);
		roles.add(UtilService.TUTOR);
		if (utilService.authenticate(dto, result, roles)) {
			user = userRepository.findByEmail(dto.getMail());
			if (user != null) {
				user.setJwtToken(null);
				user.setLastAccess(new Date(System.currentTimeMillis()));
				user.setSessionExpiredDate(null);
				userRepository.save(user);
				userDTO = new UserDTO(user.getEmail(), user.getJwtToken());
				result.setCode(0);
				result.setMessage("Se ha cerrado la sesión.");
			} else {
				result.setCode(1);
				result.setMessage("No existe usuario.");
			}
		} else {
			result.setCode(1);
			result.setMessage("Autenticación fallida.");
		}
		return result;
		// Base64.getEncoder().encodeToString(dto.getPassword().getBytes()

	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ResultDTO login(UserAuthDTO dto) {
		User user = null;
		ResultDTO result = new ResultDTO();
		user = userRepository.findByEmail(dto.getMail());
		if (user != null) {
			if (user.getJwtToken() != null && user.getJwtToken().length() > 0) {

				if (dto.getPassword() != null && !dto.getPassword().equals("") && !dto.getPassword().equals("null")) {

					if (Base64.getEncoder().encodeToString(dto.getPassword().getBytes()).equals(user.getPassword())) {
						UserDTO userDTO = new UserDTO(user.getEmail(), user.getJwtToken());
						List<Role> roles = user.getRoles();
						userDTO.setRoles(toRolDTO(roles));
						utilService.mensajePersonalizado(0, "Usuario logueado con éxito.", userDTO, result);
						return result;
					} else {
						UserDTO userDTO = new UserDTO(dto.getMail(), dto.getToken());
						utilService.mensajePersonalizado(1, "Contraseña Incorrecta.", userDTO, result);
						return result;
					}
				} else {
					UserDTO userDTO = new UserDTO(user.getEmail(), user.getJwtToken());
					List<Role> roles = user.getRoles();
					userDTO.setRoles(toRolDTO(roles));
					utilService.mensajePersonalizado(0, "El usuario ya está logueado", userDTO, result);
					return result;
				}

			} else {// token null, se va a loguear

				if (dto.getPassword() != null && !dto.getPassword().equals("") && !dto.getPassword().equals("null")) {
					// withoutPadding()
					if (Base64.getEncoder().encodeToString(dto.getPassword().getBytes()).equals(user.getPassword())) {
						user.setJwtToken(getToken(5));
						logger.info("Fecha login : " + new Date(System.currentTimeMillis()));
						user.setLastAccess(new Date(System.currentTimeMillis()));
						// 5 horas màs
						logger.info("La Sesion Expira : " + new Date(System.currentTimeMillis() + 5 * 3600 * 1000));
						user.setSessionExpiredDate(new Date(System.currentTimeMillis() + 5 * 3600 * 1000));
						userRepository.save(user);
						UserDTO userDTO = new UserDTO(user.getEmail(), user.getJwtToken());
						List<Role> roles = user.getRoles();
						userDTO.setRoles(toRolDTO(roles));

						utilService.mensajePersonalizado(0, "Usuario logueado con éxito.", userDTO, result);
						return result;
					} else {
						UserDTO userDTO = new UserDTO(dto.getMail(), dto.getToken());
						utilService.mensajePersonalizado(1, "Contraseña Incorrecta", userDTO, result);
						return result;
					}

				} else {
					UserDTO userDTO = new UserDTO(dto.getMail(), dto.getToken());
					utilService.mensajePersonalizado(1, "Falta Contraseña", userDTO, result);
					return result;
				}
			}
		} else {
			UserDTO userDTO = new UserDTO(dto.getMail(), dto.getToken());
			utilService.mensajePersonalizado(1, "No existe usuario.", userDTO, result);
			return result;
		}

	}

	@Override
	public ResultDTO isLoggedIn(UserAuthDTO dto) {
		User user = null;
		ResultDTO result = new ResultDTO();
		user = userRepository.findByEmail(dto.getMail());
		if (user != null) {
			if (user.getJwtToken() != null && user.getJwtToken().length() > 0) {
				UserDTO userDTO = new UserDTO(user.getEmail(), user.getJwtToken());
				List<Role> roles = user.getRoles();
				userDTO.setRoles(toRolDTO(roles));
				utilService.mensajePersonalizado(0, "El usuario ya está logueado", userDTO, result);

			} else {
				result.setCode(1);
				result.setMessage("Usuario no logueado.");

			}
		} else {
			result.setCode(1);
			result.setMessage("Usuario no logueado.");
		}
		return result;
	}

	public ResultDTO changePassword(UserAuthDTO usuario) {
		ResultDTO result = new ResultDTO();
		User userObject = userRepository.findByEmail(usuario.getMail());
		if (userObject != null) {
			if (usuario.getPassword() != null && usuario.getPassword().length() > 0) {
				userObject.setPassword(Base64.getEncoder().encodeToString(usuario.getPassword().getBytes()));
				userRepository.save(userObject);
				result.setCode(0);
				result.setMessage("Contraseña modificada con éxito.");
			} else {
				result.setCode(1);
				result.setMessage("Password Incorrecto.");
			}
		} else {
			result.setCode(1);
			result.setMessage("No existe usuario.");

		}
		return result;
	}

	@Override
	public ResultDTO sendPasswordByMail(String mail, String urlBase) {
		User user = userRepository.findByEmail(mail);
		ResultDTO resultDTO = new ResultDTO();
		if (null != user) {

			try {
				user.setPasswordToken(getToken(5));
				user.setPasswordExpiredDate(new Date(System.currentTimeMillis()));
				userRepository.save(user);
				sendEmailToRegisterUser(user, urlBase);
				resultDTO.setCode(0);
				resultDTO.setMessage("Correo enviado con éxito");
				UserDTO userDTO = new UserDTO(mail, user.getJwtToken());
				resultDTO.getResult().add(userDTO);
				return resultDTO;
			} catch (Exception e) {
				e.printStackTrace();
				// logger.trace(e.toString());
				resultDTO.setCode(1);
				resultDTO.setMessage("Ocurrió un erro al enviar el Correo.");
			}

		} else {
			resultDTO.setCode(1);
			resultDTO.setMessage("El usuario no existe.");
		}
		UserDTO userDTO = new UserDTO(mail);
		resultDTO.getResult().add(userDTO);
		return resultDTO;

	}

	public void sendEmailToRegisterUser(User user, String urlBase) {

		try {
			byte[] decodedBytes = Base64.getDecoder().decode(user.getPassword());
			String password = new String(decodedBytes);

			// String from = "sdptestadmin@gmail.com";//lo envia desde
			// prototypeoneb@gmail.com
			// que esta en appliation properties parametro spring.mail.username
			// http://sdp-defensa.herokuapp.com/login
			String subject = "SDP Recuperar Contraseña";
			// String body = "La contraseña de " + user.getEmail() + " es : " + password;
			String body = "Acceda al siguiente enlace para cambiar la contraseña: " + "http://" + "localhost:4200"
					+ "/new-password?email=" + user.getEmail() + "&token=" + user.getPasswordToken();

			mailSender.sendMail(user.getEmail(), subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendEmailNewUser(String mail, String pass) {

		try {
			String subject = "SDP Nuevo Usuario";
			String body = "Su Usuario es " + mail + " su contraseña es " + pass + "\n No olvide cambiarla.";
			mailSender.sendMail(mail, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ResultDTO createUser(UserAuthDTO userAuthDTO, UserDTO userDTO) {

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.ADMIN);
		if (utilService.authenticate(userAuthDTO, result, roles)) {

			User userObject = new User();
			userObject.setName(userDTO.getName());
			userObject.setSecondName(userDTO.getSecondName());
			userObject.setEnable(true);
			userObject.setEmail(userDTO.getEmail());
			// Pasword auto generado
			String password = getToken(6);
			userDTO.setPassword(password);
			userObject.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
			// Obtener Roles
			if (userDTO.getRoles() != null) {
				List<Role> rolesN = new ArrayList<Role>();
				for (RoleDTO rol : userDTO.getRoles()) {
					Role rolb = roleRepositoy.findByName(rol.getName());
					rolesN.add(rolb);

				}
				userObject.setRoles(rolesN);
			}
			//

			sendEmailNewUser(userDTO.getEmail(), password);
			userRepository.save(userObject);
			result.setCode(0);
			result.setMessage("Usuario creado con éxito.");
			List<DTO> dto = new ArrayList<DTO>();
			userDTO.setEnable(true);
			dto.add(userDTO);

		}
		return result;

	}

	public static String getToken(int length) {
		StringBuilder token = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			token.append(CHARS.charAt(random.nextInt(CHARS.length())));
		}
		return token.toString();
	}

	@Override
	public ResultDTO modifyUser(UserAuthDTO userAuthDTO, UserDTO userDTO) {

		ResultDTO result = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.ADMIN);
		roles.add(UtilService.TUTOR);

		if (utilService.authenticate(userAuthDTO, result, roles)) {
			User userObject = userRepository.findByEmail(userDTO.getEmail());
			if (userDTO.getName() != null && userDTO.getName().length() > 0) {
				userObject.setName(userDTO.getName());
			}
			if (userDTO.getSecondName() != null && userDTO.getSecondName().length() > 0) {
				userObject.setSecondName(userDTO.getSecondName());
			}
			userObject.setEnable(true);
			if (userDTO.getPassword() != null && userDTO.getPassword().length() > 0) {
				userObject.setPassword(Base64.getEncoder().encodeToString(userDTO.getPassword().getBytes()));
			}

			// Obtener Roles
			if (userDTO.getRoles() != null) {
				List<Role> rolesN = new ArrayList<Role>();
				for (RoleDTO rol : userDTO.getRoles()) {
					Role rolb = roleRepositoy.findByName(rol.getName());
					rolesN.add(rolb);

				}
				userObject.setRoles(rolesN);
			}
			userRepository.save(userObject);
			result.setCode(0);
			result.setMessage("Usuario modificado con éxito.");
		}
		return result;
	}

	@Transactional
	private boolean existeAnalisisBoolean(AnalisisDTO analisisDTO, Long idAnalisis) {
		boolean ret = false;

		List<Analisis> lista = analisisRepository.findByEmail(analisisDTO.getCredentials().getMail());
		if (lista != null && lista.size() > 0) {
			for (Analisis a : lista) {

				if (a.getIdFile() == analisisDTO.getIdAnalize() && !ret) {

					if (a.getFilesToCompare() != null) {
						boolean tmpRet = true;
						for (File fs : a.getFilesToCompare()) {
							if (tmpRet) {
								List<Long> l = analisisDTO.getFilesToCompare();
								if (!l.contains(Long.valueOf(fs.getFileID()))) {
									tmpRet = false;
								}

							}
						}
						ret = tmpRet;
						if (ret) {
							idAnalisis = a.getId();
							// encontre uno igual
							break;
						}
					}

				}

			}

		} else {
			ret = false;
		}
		return ret;

	}

	public ResultDTO existeAnalisis(AnalisisDTO analisisDTO) {
		Long idAnalisis = (long) 0;
		boolean ret = existeAnalisisBoolean(analisisDTO, idAnalisis);
		ResultDTO result = new ResultDTO();
		if (ret) {
			result.setCode(2);
			result.setMessage("Ya existe un analisis con las mismas carateristicas");

		} else {
			result.setCode(0);
			result.setMessage("No existe un analisis con las mismas carateristicas");

		}
		return result;
	}

	public void iniciarAnalisis(AnalisisDTO analisisDTO, ResultDTO result) {

		try {
			ArrayList<String> roles = new ArrayList<String>();
			roles.add(UtilService.ADMIN);
			roles.add(UtilService.TUTOR);
			Long idAnalisis = (long) 0;
			if (utilService.authenticate(analisisDTO.getCredentials(), result, roles)) {
				if (analisisDTO.getFilesToCompare() != null && analisisDTO.getFilesToCompare().size() > 0) {
					if (!existeAnalisisBoolean(analisisDTO, idAnalisis)) {
						Analisis a = new Analisis();
						User user = userRepository.findByEmail(analisisDTO.getCredentials().getMail());
						a.setEmail(analisisDTO.getCredentials().getMail());
						a.setIdFile(analisisDTO.getFileToAnalyze());
						DateTime dt = new DateTime();
						DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
						String dtStr = dt.toString(fmt);
						a.setFechaInicial(dtStr);// (new Date(System.currentTimeMillis()));
						a.setFechaFinal(null);
						a.setIdUser(user.getId());
						a.setProcesado(false);
						a.setGeneradoReport(false);
						a.setJsonResult(null);
						List<File> lista = new ArrayList<File>();
						a.getFilesToCompare().clear();
						boolean ok = true;
						for (long l : analisisDTO.getFilesToCompare()) {
							File f = fileRepository.findByFileID(l);
							if (f != null) {
								if (f.getUser().getEmail().equals(a.getEmail())
										|| f.getUser().getEmail().equals(MAIL_ADMIN)) {
									// para agregar solo los archivos del usuarios tutor logueado o del
									// adminsitrardor, es decir carpeta compartida
									lista.add(f);
								}
							} else {
								result.setCode(1);
								result.setMessage("No se pudo inicializar el analisis falta archivo con id " + l);
								ok = false;
								break;
							}
						}
						if (ok) {
							a.getFilesToCompare().addAll(lista);
							analisisRepository.save(a);
							result.setCode(0);
							result.setMessage("El analisis esta pronto para iniciar. Id analisis " + a.getId());
						}
					} else {
						result.setCode(1);
						result.setMessage(
								"Ya se realizó un analisis con las mismas caracteristicas, id Analisis " + idAnalisis);
					}
				} else {
					result.setCode(1);
					result.setMessage("Faltan parámetros.");
				}
			} else {
				result.setCode(1);
				result.setMessage("No se pudo autenticar.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.setCode(1);
			result.setMessage(e.toString());
		}

	}

	public ResultDTO listarAnalisisPorUsuario(UserAuthDTO usuario) {
		User user = userRepository.findByEmail(usuario.getMail());
		ResultDTO resultDTO = new ResultDTO();
		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.ADMIN);
		roles.add(UtilService.TUTOR);
		List<DTO> listaDto = new ArrayList<>();

		if (utilService.authenticate(usuario, resultDTO, roles)) {
			resultDTO.setCode(0);
			resultDTO.setMessage("Lista de Analisis para el usuario " + user.getEmail());

			List<Analisis> lista = analisisRepository.findByEmail(usuario.getMail());
			if (lista != null) {
				for (Analisis a : lista) {
					File f = fileRepository.findByFileID(a.getIdFile());
					InfoAnalisisDTO info = new InfoAnalisisDTO();
					info.setNombreArchivoAComparar(f.getName());
					if (a.getFechaFinal() == null)
						info.setFechaFinalAnalisis("-");
					else
						info.setFechaFinalAnalisis(a.getFechaFinal());
					info.setFechaPedidoAnalisis(a.getFechaInicial());
					info.setTerminado(a.isGeneradoReport());
					info.setIdAnalisis(a.getId());
					info.setCantidadCoincidencias(a.getNumMatches());
					info.setCantidadCoincidencias(a.getNumMatches());
					if (a.getFilesToCompare() != null) {
						List<String> archivos = new ArrayList<String>();
						for (File fs : a.getFilesToCompare()) {
							archivos.add(fs.getName());
						}
						info.setNombreArchivosCorpus(archivos);
					}
					listaDto.add(info);
				}

				resultDTO.setResult(listaDto);
			}

		}

		return resultDTO;

	}

	public Resource reporteAnalisis(UserAuthDTO usuario, long idAnalisis, ResultDTO result) {

		ArrayList<String> roles = new ArrayList<String>();
		roles.add(UtilService.ADMIN);
		roles.add(UtilService.TUTOR);
		Resource resource = null;
		if (utilService.authenticate(usuario, result, roles)) {
			Analisis a = analisisRepository.findById(idAnalisis);
			if (a != null) {

				if (a.isProcesado()) {
					result.setCode(0);
					resource = new ByteArrayResource(a.getResultado());
				} else {
					result.setCode(1);
				}
			} else {
				result.setCode(1);
			}

		} else {
			result.setCode(1);
		}

		return resource;

	}

	public List<RoleDTO> toRolDTO(List<Role> roles) {
		List<RoleDTO> rolesDTO = new ArrayList<RoleDTO>();
		if (roles != null) {
			for (Role r : roles) {
				RoleDTO rt = new RoleDTO();
				rt.setDesc(r.getDescription());
				rt.setName(r.getName());
				rt.setId(r.getRoleID());
				rolesDTO.add(rt);
			}
		}
		return rolesDTO;

	}

	public ResultDTO listarUsuarios(UserAuthDTO usuario) {
		ResultDTO result = new ResultDTO();
		List<DTO> l = new ArrayList<DTO>();
		List<User> lista = userRepository.findAll();
		if (lista != null) {
			for (User u : lista) {
				UserDTO rt = new UserDTO();
				rt.setEmail(u.getEmail());
				rt.setName(u.getName());
				rt.setSecondName(u.getSecondName());
				rt.setEnable(u.isEnable());
				rt.setPassword(u.getPassword());
				rt.setRoles(toRolDTO(u.getRoles()));
				l.add(rt);
			}
		}
		result.setCode(0);
		result.setMessage("Lista de Usuarios.");
		result.setResult(l);
		return result;
	}

	public ResultDTO getDashBoard(UserAuthDTO usuario) {
		try {
			User user = userRepository.findByEmail(usuario.getMail());
			ResultDTO resultDTO = new ResultDTO();
			if (user != null) {

				ArrayList<String> roles = new ArrayList<String>();
				roles.add(UtilService.ADMIN);
				roles.add(UtilService.TUTOR);

				if (utilService.authenticate(usuario, resultDTO, roles)) {
					resultDTO.setCode(0);
					DashBoardDTO d = new DashBoardDTO();
					List<Role> rolesU = user.getRoles();
					if (rolesU != null && rolesU.size() > 0) {
						for (Role r : rolesU) {
							if (r.getName().equals(UtilService.TUTOR)) {
								List<Analisis> lista1 = analisisRepository.findByEmail(usuario.getMail());
								List<Analisis> lista2 = analisisRepository.findByNoProcesadoByEmail(usuario.getMail());
								List<File> lista3 = fileRepository.findByEmail(usuario.getMail());
								List<Analisis> lista4 = analisisRepository.findFilesPlagied(usuario.getMail());

								int cantA = 0;
								int cantB = 0;
								int cantC = 0;
								if (lista1 == null) {
									cantA = 0;
								} else {
									cantA = lista1.size();
								}

								if (lista2 == null) {
									cantB = cantA - 0;
									d.setAnalisisNoTerminados(0);
								} else {
									cantB = cantA - lista2.size();
									d.setAnalisisNoTerminados(lista2.size());
								}

								if (lista3 == null)
									cantC = 0;
								else
									cantC = lista3.size();
								d.setCantAnalisis("Cantidad de analisis realizados por el usuario: " + cantA);
								d.setCantAnalisisTerminados("Cantidad de analisis terminados: " + cantB);
								d.setCantArchivos("Cantidad de archivos del usuario: " + cantC);
								/////
								long totalp = 0;
								long totaln = 0;
								if (lista1 != null) {
									for (Analisis a : lista1) {
										if (a.isProcesado()) {
											totaln = totaln + a.getNwordsTotales();
											totalp = totalp + a.getNumMatches();
										}
									}
									if (totaln != 0) {
										d.setPorcentajeDePlagio("Porcentaje de contenido plagiado: "
												+ String.valueOf((Math.round(totalp / totaln))) + "%");
									}
								}
								if (lista4 != null && lista4.size() > 0) {
									d.setPorcentajeDeArchivosPlagiados("Porcentaje de analisis con plagio: "
											+ String.valueOf((Math.round(lista4.size() / lista1.size()))) + "%");
									d.setAnalisisTerminadosConPlagio(lista4.size() - lista2.size());
									d.setAnalisisTerminadosSinPlagio(lista1.size() - lista4.size() - lista2.size());
								} else {
									d.setPorcentajeDeArchivosPlagiados("Porcentaje de analisis con plagio: 0%");
									d.setAnalisisTerminadosConPlagio(0);
									d.setAnalisisTerminadosSinPlagio(lista1.size());
								}

								/////
								List<DTO> l = new ArrayList<DTO>();
								l.add(d);
								resultDTO.setResult(l);
							}
							if (r.getName().equals(UtilService.ADMIN)) {
								List<File> lista1 = fileRepository.findByEmail("sdptestadm@gmail.com");
								List<File> lista2 = fileRepository.findAll();
								List<User> lista3 = userRepository.findAll();

								int cantA = 0;
								int cantB = 0;
								int cantC = 0;
								if (lista1 == null)
									cantA = 0;
								else
									cantA = lista1.size();

								if (lista2 == null) {
									cantB = 0;
									d.setTotalArchivos(0);
								} else {
									cantB = lista2.size();
									d.setTotalArchivos(lista2.size());
								}

								if (lista3 == null) {
									cantC = 0;
									d.setListaTutoresCantArchivos("{}");
								} else {
									cantC = lista3.size();
									String ret = "{";
									int i = 0;

									for (User u : lista3) {

										if (u.getRoles() != null
												&& !u.getRoles().get(0).getName().equals(UtilService.ADMIN)) {
											// solo agrego tutores
											int l = 0;

											if (u.getFiles() != null) {
												l = u.getFiles().size();
											}
											if (i != 0) {
												ret = ret + ",";
											}
											i++;
											ret = ret + "[" + u.getEmail() + ":" + l + "]";
										}
									}
									ret = ret + "}";
									d.setListaTutoresCantArchivos(ret);
								}
								d.setCantArchivosCarpetaCompartida(
										"Cantidad de archivos en la carpeta compartida: " + cantA);
								d.setCantArchivosEnElSistema("Cantidad de archivos en el sistema: " + cantB);
								d.setCantUsuariosDelSistema("Cantidad de usuarios del sitema: " + cantC);
								List<DTO> l = new ArrayList<DTO>();
								l.add(d);
								resultDTO.setResult(l);
							}

						}
					}

				} else {
					resultDTO.setCode(1);
					resultDTO.setMessage("No se pudo autenticar.");
				}
			} else {
				resultDTO.setCode(1);
				resultDTO.setMessage("No existe usuario.");
			}

			return resultDTO;
		} catch (Exception e) {
			e.printStackTrace();
			ResultDTO resultDTO = new ResultDTO();
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrió un error");
			return resultDTO;
		}

	}

	public void recuperarContrasenia(UserDTO userDTO, ResultDTO resultDTO) {
		// if (user.getSessionExpiredDate()==null || new
		// Date(System.currentTimeMillis()).compareTo(user.getSessionExpiredDate()) < 0)
		// {
		User user = userRepository.findByEmail(userDTO.getEmail());

		try {
			if (user != null) {
				if (user.getPasswordToken() == null) {
					resultDTO.setCode(1);
					resultDTO.setMessage("No se puede cambiar la contraseña, recomience el proceso.");

				} else {
					if (user.getPasswordToken().equals(userDTO.getPasswordToken())) {
						user.setPassword(Base64.getEncoder().encodeToString(userDTO.getPassword().getBytes()));
						user.setPasswordExpiredDate(null);
						user.setPasswordToken(null);
						userRepository.save(user);
						resultDTO.setCode(0);
						resultDTO.setMessage("Contraseña modificada con éxito.");

					} else {
						resultDTO.setCode(1);
						resultDTO.setMessage("Token de cambio de contraseña incorrecto.");

					}
				}
			} else {
				resultDTO.setCode(1);
				resultDTO.setMessage("No existe usuario.");

			}
		} catch (Exception e) {
			e.printStackTrace();
			resultDTO.setCode(1);
			resultDTO.setMessage("Ocurrió un error.");

		}

	}

}
