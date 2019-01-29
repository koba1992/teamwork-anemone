package com.internousdev.anemone.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class GoLoginAction extends ActionSupport implements SessionAware{

	private Map<String,Object>session;
	public String execute() {

		//ログアウト時にセッションクリアするため、ヘッダーに表示するカテゴリーリストを保持するためのセッションプット
		if(!session.containsKey("mCategoryList")) {
			return "sessionError";
		}

		return SUCCESS;

	}
	public Map<String,Object>getSession(){
		return session;
	}
	public void setSession(Map<String,Object>session) {
		this.session=session;
	}

}

