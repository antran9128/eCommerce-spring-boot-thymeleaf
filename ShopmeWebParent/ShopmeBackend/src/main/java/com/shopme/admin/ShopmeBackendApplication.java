package com.shopme.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.Entity;

@SpringBootApplication
@EntityScan({"com.shopme.common.entity", "com.shopme.admin.user"})
//@ComponentScan({"MainController"})
public class ShopmeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopmeBackendApplication.class, args);
	}

}
