package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useRepresentation;
import static org.mockito.ArgumentMatchers.intThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithOneRole() {
		Role roleAdminRole =  entityManager.find(Role.class, 1);
		User userAnTran = new User("antranxinhgai@gmail.com", "an2023", "An", "Tran");
		userAnTran.addRole(roleAdminRole);
		
		User saveUser =  repo.save(userAnTran);
		
		assertThat(saveUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateNewUserWithTwoRole() {
		User userRavi = new User("ravi@gmail.com", "ravi2023", "Ravi", "Kumar");
		userRavi.addRole(new Role(3));
		userRavi.addRole(new Role(5));
		
		User saveUser =  repo.save(userRavi);
		
		assertThat(saveUser.getId()).isGreaterThan(0);
	}
	
	
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		User userAn = repo.findById(1).get();
		System.out.println(userAn);
		assertThat(userAn).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userAn = repo.findById(1).get();
		userAn.setEnabled(true);
		userAn.setEmail("an@gmail.com");
		
		repo.save(userAn);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userRavi = repo.findById(2).get();
		
		userRavi.getRoles().remove(new Role(3));
		userRavi.addRole(new Role(2));
		
		repo.save(userRavi);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "an@gmail.com";
		
		User user = repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Integer id = 1;
		Long countById = repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	@Test
	public void testEnableUser() {
		Integer id = 2;
		repo.updateEnabledStatus(id, true);
		
	}
	
	@Test
	public void testDisableUser() {
		Integer id = 3;
		repo.updateEnabledStatus(id, false);
	}
	
	@Test
	public void testListFirstPage() {
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> {
			System.out.println(user);
		});
	}
	
	@Test
	public void testSearchUsers() {
		String keyword = "bruce";
		int pageNumber = 0;
		int pageSize = 4;
		
		Pageable pageable = (Pageable) PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(keyword, pageable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> {
			System.out.println(user);
		});
		assertThat(listUsers.size()).isGreaterThan(0);
	}
	
}


















