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
 * ユーザ用サービス
 * 
 * @author tejc999999
 *
 */
@Service
public class UserService {

	/**
	 * ユーザ用リポジトリ
	 */
	@Autowired
	UserRepository userRepository;
	
	/**
	 * ユーザを登録する.
	 * 
	 * @param userForm User Form.
	 * @return Registered User Form.
	 */
	public UserForm create(UserForm userForm) {

		// パスワードをエンコード
		userForm.setPassword(new BCryptPasswordEncoder().encode(userForm.getPassword()));

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(userForm, userBean);

		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("")) {
			// DBがIBM Cloudの場合
			System.out.println("DEBUG:" + VCAPHelper.VCAP_SERVICES);
			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			existUser = userStore.get(userForm.getUsername());
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
			// DBがH2データベースの場合
			
			userBean = userRepository.save(userBean);
			BeanUtils.copyProperties(userBean, userForm);
		}
		return userForm;
	}
	

	/**
	 * ユーザを更新する
	 * 
	 * @param userForm 更新用UserForm
	 * @return 更新後UserForm
	 */
	public UserForm update(UserForm userForm) {
		// パスワードをエンコードする
		userForm.setPassword(new BCryptPasswordEncoder().encode(userForm.getPassword()));

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(userForm, userBean);

		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("")) {
			// DBがIBM Cloudの場合

			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			existUser = userStore.get(userForm.getUsername());
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
			// DBがH2データベースの場合
			
			userBean = userRepository.save(userBean);
			BeanUtils.copyProperties(userBean, userForm);
		}
		return userForm;
	}
	
	/**
	 * ユーザを削除する
	 * 
	 * @param username 削除対象ユーザ名
	 */
	public void delete(String username) {

		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("")) {
			// DBがIBM Cloudの場合

			UserStore userStore = UserStoreFactory.getInstance();
			userStore.delete(username);
			
		} else {
			// DBがH2データベースの場合
			
			UserBean userBean = new UserBean();
			userBean.setUsername(username);

			userRepository.delete(userBean);
		}
	}
	
	/**
	 * DBからUserFormを取得する. 
	 * 
	 * @param username 対象ユーザ名
	 */
	public UserForm getDBUserForm(String username)  {
		UserForm userForm = null;
		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("")) {
			// DBがIBM Cloudの場合

			UserStore userStore = UserStoreFactory.getInstance();
			User user = null;
			user = userStore.get(username);
			if(user != null) {
				userForm = new UserForm();
				BeanUtils.copyProperties(user, userForm);
			}
		} else {
			// DBがH2データベースの場合
			
			Optional<UserBean> opt = userRepository.findById(username);
			UserBean userBean = opt.orElseThrow(() -> new UsernameNotFoundException("The requested user is not found."));
			if(userBean != null) {
				userForm = new UserForm();
				BeanUtils.copyProperties(userBean, userForm);
			}
		}
		return userForm;
	}
	
	/**
	 * DBにUserFormを登録する
	 * 
	 * @param userbean 更新用UserForm
	 * @return updated 更新後UserForm
	 */
	public UserForm setDBUserForm(UserForm userForm) {

		if(VCAPHelper.VCAP_SERVICES  != null && !VCAPHelper.VCAP_SERVICES.equals("")) {
			// DBがIBM Cloudの場合

			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			existUser = userStore.get(userForm.getUsername());
			if(existUser == null) {
				User user = new User();
				BeanUtils.copyProperties(userForm, user);
				userStore.persist(user);
			} else {
				BeanUtils.copyProperties(userForm, existUser);
				User user = userStore.update(existUser.get_id(), existUser);
				BeanUtils.copyProperties(user, userForm);
			}
		} else {
			// DBがH2データベースの場合
			UserBean userBean = new UserBean();
			BeanUtils.copyProperties(userForm, userBean);
			userBean = userRepository.save(userBean);
			BeanUtils.copyProperties(userBean, userForm);
		}
		return userForm;
	}
}