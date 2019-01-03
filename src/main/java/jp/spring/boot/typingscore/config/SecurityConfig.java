package jp.spring.boot.typingscore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.security.LoginAuthenticationFailureHandler;
import jp.spring.boot.typingscore.security.RoleName;
import jp.spring.boot.typingscore.service.LoginUserDetailsService;
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

	
	/**
	 * user details service.
	 */
	@Autowired
	private LoginUserDetailsService userDetailService;

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
		form.setLoginfailurecnt(0);
		form.setAccountNonLocked(true);
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
				.loginProcessingUrl("/loginprocess").loginPage("/login").failureHandler(new LoginAuthenticationFailureHandler("/login"))/*failureUrl("/login?err")*/
				.defaultSuccessUrl("/scores", true).usernameParameter("username").passwordParameter("password").and()
				.logout()
				// .logoutUrl("/logout")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login");
//		}
	}

	/**
	 * Create a DaoAuthenticationProvider
	 * Since UserNotFoundException is hidden, create your own AuhthenticationProvider.
	 * If you do not need UserNotFoundException, you do not need your own AuhthenticationProvider.
	 * 
	 * @return DaoAuthenticationProvider
	 */
    @Bean
    public AuthenticationProvider daoAuhthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        // Denied UserNotFoundException to be hidden
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
//        daoAuthenticationProvider.setPasswordEncoder(new Pbkdf2PasswordEncoder());
        return daoAuthenticationProvider;
    }
    
    /**
     * Add daoAuhthenticationProvider to AuthenticationManager.
     * 
     * @param auth Authentication object
     * @throws Exception
     */
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuhthenticationProvider());
    }
    
	/**
	 * get BCrypt password encoder
	 * @return password encoder.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}