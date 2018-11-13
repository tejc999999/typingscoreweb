package jp.spring.boot.typingscore.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.repository.UserRepository;
import jp.spring.boot.typingscore.security.LoginUserDetails;

@Service
public class LoginUserDetailsService implements UserDetailsService {

	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserBean> opt = userRepository.findById(username);
        UserBean user = opt.orElseThrow(() -> new UsernameNotFoundException("The requested user is not found."));
        return new LoginUserDetails(user);
	}
}
