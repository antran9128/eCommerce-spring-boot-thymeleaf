package com.shopme.admin.security;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

	@Bean
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorize -> authorize
				.antMatchers("/users/**", "/settings/**", "/countries/**", "/states/**")
				.hasAuthority("Admin")
				.antMatchers("/categories/**", "/brands/**", "/menus/**", "/articles/**")
				.hasAnyAuthority("Admin", "Editor")
				.antMatchers("/products/new", "/products/delete/**")
				.hasAnyAuthority("Admin", "Editor")
				.antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
				.hasAnyAuthority("Admin", "Editor", "Salesperson")
				.antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
				.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
				.antMatchers("/products/**", "/articles/**", "/menus/**", "/sections/**")
				.hasAnyAuthority("Admin", "Editor")
				.antMatchers("/customers/**", "/shipping/**", "/articles/**", "/get_shipping_cost", "/reports/**")
				.hasAnyAuthority("Admin", "Salesperson")
				.antMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**")
				.hasAnyAuthority("Admin", "Salesperson", "Shipper")
				.antMatchers("/states/list_by_country/**")
				.hasAnyAuthority("Admin", "Salesperson")
				.antMatchers("/orders_shipper/update/**")
				.hasAuthority("Shipper").antMatchers("/products/detail/**", "/customers/detail/**")
				.hasAnyAuthority("Admin", "Editor", "Salesperson", "Assistant")
				.antMatchers("/reviews/**", "/questions/**")
				.hasAnyAuthority("Admin", "Assistant")
				.antMatchers("/images/**", "/js/**", "/webjars/**").permitAll().anyRequest().authenticated())
				.formLogin(form -> form.loginPage("/login").usernameParameter("email").permitAll())
				.logout(logout -> logout.permitAll())
				.rememberMe(me -> me.key("AbcDefgKLDSLmvop_0123456789").tokenValiditySeconds(7 * 24 * 60 * 60)); // 7
																													// days
																													// 24
																													// hours
																													// 60
																													// minutes
																													// 60
																													// seconds
																													// ->
																													// 7days
																													// ;
		return http.build();

	}

	// Đoạn mã trên cấu hình Spring Security để cho phép tất cả các request vào ứng
	// dụng mà không cần xác thực.
	// Đây là cấu hình cơ bản nhất và thường được sử dụng
	// trong giai đoạn phát triển hoặc khi bạn muốn tạm thời tắt bảo mật cho các
	// request HTTP.

	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		return new ProviderManager(Collections.singletonList(authenticationProvider()));
	}

}
