package com.internousdev.anemone.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.internousdev.anemone.dao.CartInfoDAO;
import com.internousdev.anemone.dao.UserInfoDAO;
import com.internousdev.anemone.dto.CartInfoDTO;
import com.internousdev.anemone.dto.UserInfoDTO;
import com.internousdev.anemone.util.InputChecker;
import com.opensymphony.xwork2.ActionSupport;


public class LoginAction extends ActionSupport implements SessionAware{
	private String loginId;
	private String password;
	private boolean savedLoginId;

	private List<String> loginIdErrorMessageList= new ArrayList<String>();
	private List<String> passwordErrorMessageList = new ArrayList<String>();
	private List<CartInfoDTO> cartInfoDTOList = new ArrayList<CartInfoDTO>();

	private Map<String, Object>session;


	public String execute() {

		//セッションタイムアウト処理
		if(!session.containsKey("mCategoryList")) {
			return "sessionError";
		}
		//ID保存にチェックが入っていれば、loginIdをセッションプット、入っていなければセッションリムーブ
		if(savedLoginId==true) {
			session.put("savedLoginId", true);
			session.put("loginId",loginId);
			session.put("keepId",loginId);
		}else {
			session.put("savedLoginId", false);
			session.remove("loginId", loginId);
			session.remove("keepId", loginId);
		}

		String result = ERROR;

		InputChecker inputChecker = new InputChecker();
		loginIdErrorMessageList = inputChecker.doCheck("ユーザーID",loginId,1,8,true,false,false,true,false,false,false,false,false);
		passwordErrorMessageList= inputChecker.doCheck("パスワード",password,1,16,true,false,false,true,false,false,false,false,false);

		//inputCheckerの中の要素の数(size)が0より大きい時(エラーがある時)、ログインフラグを未ログイン(0)にする
		if(loginIdErrorMessageList.size()>0
		|| passwordErrorMessageList.size()>0) {
			session.put("logined", 0);
			session.remove("loginId", loginId);
			return result;
		}


		//UserInfoDAOのisExistsUserInfoメソッドでDBに該当するloginIdとPasswordがあるか確認する
		UserInfoDAO userInfoDao = new UserInfoDAO();
		if(userInfoDao.isExistsUserInfo(loginId,password)) {

			//userinfoDaoのloginメソッドが>0、つまり1(ログイン済)の場合trueを返しセッションにUserId(キーはloginId)をプット
			if(userInfoDao.login(loginId,password)>0) {
				UserInfoDTO userInfoDTO = userInfoDao.getUserInfo(loginId, password);
				session.put("loginId", userInfoDTO.getUserId());

				//未ログイン(仮ID)状態でカートに商品があり決済する場合、ログインした時にカートの中身を引き継ぐ(count=引き継ぐ商品の数)
				int count=0;
				CartInfoDAO cartInfoDao = new CartInfoDAO();
				count = cartInfoDao.linkToLoginId(String.valueOf(session.get("tempUserId")),loginId);

				if(session.containsKey("cartFlg")) {

						if(count>0) {
							int totalPrice = 0;

							cartInfoDTOList = cartInfoDao.getCartInfoDtoList(loginId);
							session.put("cartInfoDTOList", cartInfoDTOList);
							totalPrice = cartInfoDao.getTotalPrice(loginId);
							session.put("totalPrice", totalPrice);
							session.remove("cartFlg");

						}
					result = "cart";
				}else {
					result = SUCCESS;
				}
			}
				session.put("logined", 1);
		}else {
			loginIdErrorMessageList.add("入力されたユーザーIDまたはパスワードが異なります。");

		}
		return result;
	}

	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isSavedLoginId() {
		return savedLoginId;
	}
	public void setSavedLoginId(boolean savedLoginId) {
		this.savedLoginId = savedLoginId;
	}
	public List<String> getLoginIdErrorMessageList(){
		return loginIdErrorMessageList;
	}
	public void setLoginIdErrorMessageList(List<String>loginIdErrorMessageList) {
		this.loginIdErrorMessageList = loginIdErrorMessageList;
	}
	public List<String> getPasswordErrorMessageList(){
		return passwordErrorMessageList;
	}
	public void setPasswordErrorMessageList(List<String>passwordErrorMessageList) {
		this.passwordErrorMessageList = passwordErrorMessageList;
	}
	public Map<String,Object>getSession(){
		return session;
	}
	public void setSession(Map<String,Object>session) {
		this.session= session;
	}

}
