package com.ude.sdp.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ude.sdp.dto.ResultDTO;
import com.ude.sdp.dto.Role;
import com.ude.sdp.dto.RoleDTO;
import com.ude.sdp.dto.UserAuthDTO;
import com.ude.sdp.service.SystemService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EntityScan
@EnableJpaAuditing
@EnableJpaRepositories
@EnableTransactionManagement
@EnableCaching
public class LoginControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private SystemService mockSystemService;;

	private List<RoleDTO> mockRoleDTOs = getMockRolDTO();
	private UserAuthDTO mockUserAuthDTO = new UserAuthDTO("prototypeoneb@gmail.com", "Prototype12345", "123456",
			mockRoleDTOs);

	String email;

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	ResultDTO resultDTO;

	@PostConstruct
	public void init() {
		email = "prototypeoneb@gmail.com";
	}

	@Test
	public void logout() throws Exception {
		resultDTO = new ResultDTO(0, "User logged out successfully", null);
		when(mockSystemService.logout(mockUserAuthDTO)).thenReturn(resultDTO);
		this.mockMvc.perform(post("/loginController/logout/").content(asJsonString(mockUserAuthDTO))
				.contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk());

	}

	@Test
	public void login() throws Exception {
		resultDTO = new ResultDTO(0, "User logged in first time", null);
		when(mockSystemService.login(mockUserAuthDTO)).thenReturn(resultDTO);

		this.mockMvc.perform(post("/loginController/login/").content(asJsonString(mockUserAuthDTO))
				.contentType(APPLICATION_JSON_UTF8)).andExpect(status().isOk());

	}

	/*@Test
	public void sendPasswordByMail() throws Exception {
		resultDTO = new ResultDTO(0, "Mail Sent Successfully", null);
		when(mockSystemService.sendPasswordByMail(email,"localhost:8888")).thenReturn(resultDTO);

		mockMvc.perform(get("/loginController/sendPasswordByMail").param("email", email)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(jsonPath("code", is(0)))
				.andExpect(jsonPath("message", is("Mail Sent Successfully")));

		verify(mockSystemService, times(1)).sendPasswordByMail(email,"localhost:8888");
		verifyNoMoreInteractions(mockSystemService);
	}*/

	@Test
	public void isUsedId() throws Exception {
		when(mockSystemService.isUserID(email)).thenReturn(true);

		mockMvc.perform(get("/loginController/isUsedId").param("email", email)).andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON_UTF8)).andExpect(content().string("true"));

		verify(mockSystemService, times(1)).isUserID(email);
		verifyNoMoreInteractions(mockSystemService);
	}

	private List<RoleDTO> getMockRolDTO() {
		List<RoleDTO> mockRoleDTOs2 = new ArrayList<RoleDTO>();
		mockRoleDTOs2.add(new RoleDTO(1, "ADMIN","ADMIN"));
		return mockRoleDTOs2;
	}

	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

	public static String createStringWithLength(int length) {
		StringBuilder builder = new StringBuilder();

		for (int index = 0; index < length; index++) {
			builder.append("a");
		}

		return builder.toString();
	}
}