package jp.spring.boot.typingscore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.security.RoleName;
import jp.spring.boot.typingscore.service.ScoreService;
import jp.spring.boot.typingscore.service.UserService;

/**
 * Authentication and authorization config.
 * 
 * @author tejc999999
 *
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * user service.
	 */
	@Autowired
	private UserService userService;

	/**
	 * score service.
	 */
	@Autowired
	private ScoreService scoreService;

//	@Value("${spring.h2.console.enabled:false}")
//	private boolean springH2ConsoleEnabled;

	/**
	 * configure default settings.
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webjars/**", "/css/**");
		// Default login user create.
		UserForm form = new UserForm();
		form.setUsername("demouser");
		form.setPassword("password");
		form.setRole(RoleName.ROLE_ADMIN.getString());
		userService.create(form);
		// IBM Cloudant index create.
		scoreService.init();
	}

	/**
	 * configure security settings.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

//		if(springH2ConsoleEnabled) {
//			http
//			.authorizeRequests()
//					.antMatchers("/**").permitAll();
//			http.csrf().disable();
//	        http.headers().frameOptions().disable();
//		} else {
		http.authorizeRequests().
				antMatchers("/users").hasRole(RoleName.ROLE_ADMIN.getRoleLessString()).
				antMatchers("/databases").hasRole(RoleName.ROLE_ADMIN.getRoleLessString()).
				antMatchers("/login").permitAll().anyRequest().authenticated().and().formLogin()
				.loginProcessingUrl("/loginprocess").loginPage("/login").failureUrl("/login?error")
				.defaultSuccessUrl("/scores", true).usernameParameter("username").passwordParameter("password").and()
				.logout()
				// .logoutUrl("/logout")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login");
//		}
	}

	/**
	 * Get pbkdf2 password encoder.
	 * @return password encoder.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new Pbkdf2PasswordEncoder();
	}
}