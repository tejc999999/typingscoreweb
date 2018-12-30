package jp.spring.boot.typingscore.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
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
		userForm.setPassword(new Pbkdf2PasswordEncoder().encode(userForm.getPassword()));

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
				user.setUsername(userBean.getUsername());
				user.setPassword(userBean.getPassword());
				user.setRole(userBean.getRole());
				userStore.persist(user);
			}
		} else {
			// case: h2 database
			userRepository.save(userBean);
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
}