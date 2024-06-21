package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
	
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(configurer ->
        configurer.anyRequest().permitAll()
		);
		return http.build();
		
		//Đoạn mã trên cấu hình Spring Security để cho phép tất cả các request vào ứng dụng mà không cần xác thực. 
		//Đây là cấu hình cơ bản nhất và thường được sử dụng 
		//trong giai đoạn phát triển hoặc khi bạn muốn tạm thời tắt bảo mật cho các request HTTP.
	}
	
    
}
