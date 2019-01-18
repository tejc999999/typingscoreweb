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
 * 認証及び認可の設定
 * 
 * @author tejc999999
 *
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	/**
	 * ユーザ用サービス
	 */
	@Autowired
	private UserService userService;

	/**
	 * スコア用サービス
	 */
	@Autowired
	private ScoreService scoreService;

	/**
	 * ログインユーザ用サービス
	 */
	@Autowired
	private LoginUserDetailsService userDetailService;

//	@Value("${spring.h2.console.enabled:false}")
//	private boolean springH2ConsoleEnabled;

	/**
	 * 初期設定
	 * 
	 * @param web セキュリティフィルタチェーン設定用
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		// 実行用アーカイブファイルパス、CSSパスを許可する
		web.ignoring().antMatchers("/webjars/**", "/css/**");
		// 初期ログインユーザ作成
		UserForm form = new UserForm();
		form.setUsername("demouser");
		form.setPassword("password");
		form.setLoginfailurecnt(0);
		form.setAccountNonLocked(true);
		form.setRole(RoleName.ROLE_ADMIN.getString());
		userService.create(form);
		// IBM Cloudant用インデックス作成
		scoreService.init();
	}

	/**
	 * セキュリティ設定
	 * 
	 * @param http HTTPセキュリティフィルタチェーン設定用
	 * @throws Exception
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
		// ユーザ管理機能、データベース機能は管理権限ユーザのみアクセス可
		// ログイン機能は全ユーザがアクセス可
		// ログイン後に遷移する画面はスコア一覧画面
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
	 * DaoAuthenticationProviderを作成する
	 * 存在しないユーザ名を識別するためのUserNotFoundExceptionが無効になっているため、
	 * 独自のDaoAuthenticationProviderを作成した
	 * （UserNotFoundExceptionが無効でも問題ない場合は独自DaoAuthenticationProviderは不要）
	 * 
	 * @return DaoAuthenticationProvider 認証プロバイダ
	 */
    @Bean
    public AuthenticationProvider daoAuhthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        // ユーザ認証オブジェクトを設定
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        // Denied UserNotFoundException to be hidden
        // UserNotFoundException無効化を解除
        daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
        // パスワードエンコード方法を設定
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
//        daoAuthenticationProvider.setPasswordEncoder(new Pbkdf2PasswordEncoder());
        return daoAuthenticationProvider;
    }
    
    /**
     * 認証管理クラスにdaoAuhthenticationProviderを追加する
     * 
     * @param auth 認証情報
     * @throws Exception
     */
    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuhthenticationProvider());
    }
    
	/**
	 * パスワードエンコーダを取得する
	 * 
	 * @return パスワードエンコーダ
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}