package jp.spring.boot.typingscore.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloudant.client.org.lightcouch.NoDocumentException;

import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.cloudant.User;
import jp.spring.boot.typingscore.cloudant.store.UserStore;
import jp.spring.boot.typingscore.cloudant.store.UserStoreFactory;
import jp.spring.boot.typingscore.cloudant.store.VCAPHelper;
import jp.spring.boot.typingscore.form.UserForm;
import jp.spring.boot.typingscore.repository.UserRepository;

/**
 * Login user Manage service.
 * 
 * @author tejc999999
 *
 */
@Service
public class UserService {

	/**
	 * user repository.
	 */
	@Autowired
	UserRepository userRepository;
	
	/**
	 * Register a user.
	 * 
	 * @param userForm User Form.
	 * @return Registered User Form.
	 */
	public UserForm create(UserForm userForm) {

		// Password encoding
		userForm.setPassword(new BCryptPasswordEncoder().encode(userForm.getPassword()));

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(userForm, userBean);

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			try {
				existUser = userStore.get(userForm.getUsername());
			} catch(NoDocumentException e) {
				// Corresponds to Exception that occurs when the user does not exist
			}
			if(existUser == null) {
				User user = new User();
				user.set_id(userBean.getUsername());
				BeanUtils.copyProperties(userBean, user);
				user = userStore.persist(user);
				BeanUtils.copyProperties(user, userForm);
			} else {
				BeanUtils.copyProperties(userBean, existUser);
				User user = userStore.update(existUser.get_id(), existUser);
				BeanUtils.copyProperties(user, userForm);
			}
		} else {
			// case: h2 database
			userBean = userRepository.save(userBean);
			BeanUtils.copyProperties(userBean, userForm);
		}
		return userForm;
	}
	

	/**
	 * Updater a user.
	 * 
	 * @param userForm User Form
	 * @return Updated User Form.
	 */
	public UserForm update(UserForm userForm) {
		// Password encoding
		userForm.setPassword(new BCryptPasswordEncoder().encode(userForm.getPassword()));

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(userForm, userBean);

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			try {
				existUser = userStore.get(userForm.getUsername());
			} catch(NoDocumentException e) {
				// Corresponds to Exception that occurs when the user does not exist
			}
			if(existUser == null) {
				User user = new User();
				user.set_id(userBean.getUsername());
				BeanUtils.copyProperties(userBean, user);
				userStore.persist(user);
			} else {
				BeanUtils.copyProperties(userBean, existUser);
				User user = userStore.update(existUser.get_id(), existUser);
				BeanUtils.copyProperties(user, userForm);
			}
		} else {
			// case: h2 database
			userBean = userRepository.save(userBean);
			BeanUtils.copyProperties(userBean, userForm);
		}
		return userForm;
	}
	
	/**
	 * Delete user data.
	 * 
	 * @param username user name.
	 */
	public void delete(String username) {

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			UserStore userStore = UserStoreFactory.getInstance();
			userStore.delete(username);
			
		} else {
			UserBean userBean = new UserBean();
			userBean.setUsername(username);
			// case: h2 database.
			userRepository.delete(userBean);
		}
	}
	
	/**
	 * Method using bean. 
	 * Delete user data.
	 * 
	 * @param username user name.
	 */
	public UserBean getBean(String username) throws UsernameNotFoundException {
		UserBean userbean = null;
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			UserStore userStore = UserStoreFactory.getInstance();
			User user = null;
			try {
				user = userStore.get(username);
			} catch(NoDocumentException e) {
				throw new UsernameNotFoundException("The requested user is not found.");
			}
				userbean = new UserBean();
				BeanUtils.copyProperties(user, userbean);
		} else {
			// case: h2 database
			Optional<UserBean> opt = userRepository.findById(username);
			userbean = opt.orElseThrow(() -> new UsernameNotFoundException("The requested user is not found."));
		}
		return userbean;
	}
	
	/**
	 * Method using bean. 
	 * Update user data.
	 * 
	 * @param userbean update user bean.
	 * @return updated user bean.
	 */
	public UserBean updateBean(UserBean userbean) {
//		// Password encoding
//		userbean.setPassword(new BCryptPasswordEncoder().encode(userbean.getPassword()));

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// case: IBM Cloudant
			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			try {
				existUser = userStore.get(userbean.getUsername());
			} catch(NoDocumentException e) {
				// Corresponds to Exception that occurs when the user does not exist
			}
			if(existUser == null) {
				User user = new User();
				BeanUtils.copyProperties(userbean, user);
				userStore.persist(user);
			} else {
				BeanUtils.copyProperties(userbean, existUser);
				User user = userStore.update(existUser.get_id(), existUser);
				BeanUtils.copyProperties(user, userbean);
			}
		} else {
			// case: h2 database
			userbean = userRepository.save(userbean);
		}
		return userbean;
	}
}