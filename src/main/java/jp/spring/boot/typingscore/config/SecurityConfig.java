package jp.spring.boot.typingscore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.service.UserService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;
    
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webjars/**", "/css/**");
		// 初回ログイン用ユーザ登録
		UserForm form = new UserForm();
		form.setUsername("jcjcjc");
		form.setPassword("212121");
        userService.create(form);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
					.antMatchers("/loginForm").permitAll()
					.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginProcessingUrl("/login")
				.loginPage("/loginForm")
				.failureUrl("/loginForm?error")
				.defaultSuccessUrl("/score", true)
				.usernameParameter("username").passwordParameter("password")
		.and()
			.logout()
				.logoutSuccessUrl("/loginForm");
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new Pbkdf2PasswordEncoder();
	}
}