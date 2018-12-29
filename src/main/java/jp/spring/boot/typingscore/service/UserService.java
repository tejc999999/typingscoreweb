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

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;

	/**
	 * ユーザ登録
	 * 
	 * @param userForm ユーザForm
	 * @return 登録ユーザForm
	 */
	public UserForm create(UserForm userForm) {
//		  bookForm.setId(bookRepository.getBookId());

		// パスワードエンコード
		userForm.setPassword(new Pbkdf2PasswordEncoder().encode(userForm.getPassword()));

		UserBean userBean = new UserBean();
		BeanUtils.copyProperties(userForm, userBean);
//		  bookRepository.create(bookBean);
		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			UserStore userStore = UserStoreFactory.getInstance();
			User existUser = null;
			try {
				existUser = userStore.get(userForm.getUsername());
			} catch(NoDocumentException e) {
				// ユーザが存在しない場合に発生するExceptionに対応
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
			// H2データベースの処理
			userRepository.save(userBean);
		}
		return userForm;
	}
	
	/**
	 * ユーザデータ削除
	 * 
	 * @param username ユーザ名
	 */
	public void delete(String username) {

		if(VCAPHelper.VCAP_SERVICES  != null) {
			// IBM Cloudantの処理
			UserStore userStore = UserStoreFactory.getInstance();
			userStore.delete(username);
			
		} else {
			UserBean userBean = new UserBean();
			userBean.setUsername(username);
			// H2データベースの処理
			userRepository.delete(userBean);
		}
	}
}