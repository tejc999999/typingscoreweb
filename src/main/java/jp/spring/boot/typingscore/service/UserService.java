package jp.spring.boot.typingscore.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import jp.spring.boot.typingscore.bean.ScoreBean;
import jp.spring.boot.typingscore.bean.UserBean;
import jp.spring.boot.typingscore.db.ScoreId;
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
		userRepository.save(userBean);
		return userForm;
	}
	
	/**
	 * ユーザデータ削除
	 * 
	 * @param username ユーザ名
	 */
	public void delete(String username) {
		UserBean userBean = new UserBean();
		userBean.setUsername(username);
		userRepository.delete(userBean);
	}
}