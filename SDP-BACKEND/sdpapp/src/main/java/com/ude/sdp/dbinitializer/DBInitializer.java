package com.ude.sdp.dbinitializer;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


import com.ude.sdp.model.Role;
import com.ude.sdp.model.RoleType;
import com.ude.sdp.model.User;
import com.ude.sdp.repository.sql.RoleRepository;
import com.ude.sdp.repository.sql.UserRepository;

/**
 * Database initializer that populates the database with some initial bank
 * accounts using a JPA repository.
 * 
 * This component is started only when app.db-init property is set to true
 */
@Component
@ConditionalOnProperty(name = "app.db-init", havingValue = "true")
public class DBInitializer implements CommandLineRunner {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;

	public DBInitializer(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}
	
	private final String ADMIN="ADMIN";
	
	private final String TUTOR="TUTOR";

	@Override
	public void run(String... strings) throws Exception {
		// this.bankAccountRepository.deleteAll();
		if (roleRepository.findAll().size() == 0) {
			Role role1 = new Role();
			role1.setRoleID(1);
			role1.setDescription("ADMIN");
			role1.setName("ADMIN");
			role1.setRoleType(RoleType.ADMIN);
			roleRepository.save(role1);
			Role role2 = new Role();
			role2.setDescription("TUTOR");
			role2.setName("TUTOR");
			role2.setRoleID(2);
			role2.setRoleType(RoleType.TUTOR);
			roleRepository.save(role2);
			//roleRepository.flush();
			System.out.println(" -- Database has been initialized");
		}
		
		
		if (userRepository.findAll().size() == 0) {
			//Inicializar Usuarios
			//Adm
			User userObject = new User();
			userObject.setName(ADMIN);
			userObject.setEnable(true);
			userObject.setEmail("sdptestadm@gmail.com");
			userObject.setPassword(Base64.getEncoder().encodeToString("123123".getBytes()));
			//Obtener Roles
			
			userRepository.save(userObject);
			
			Role rol1=roleRepository.findByName(ADMIN);
			List<Role> roles1=new ArrayList<Role>();
			roles1.add(rol1);
			userObject.setRoles(roles1);
			userRepository.save(userObject);
			//Tutor
			User userObject2 = new User();
			userObject2.setName(TUTOR);
			userObject2.setEnable(true);
			userObject2.setEmail("sdptesttutor@gmail.com");
			userObject2.setPassword(Base64.getEncoder().encodeToString("123123".getBytes()));
			//Obtener Roles
			userRepository.save(userObject2);
		
			Role rol2=roleRepository.findByName(TUTOR);
			List<Role> roles2=new ArrayList<Role>();
			roles2.add(rol2);
			userObject2.setRoles(roles2);
			userRepository.save(userObject2);
		
			
		}

	}
}
