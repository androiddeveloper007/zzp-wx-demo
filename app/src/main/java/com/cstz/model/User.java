package com.cstz.model;

public class User {

	
	/**
	 * 用户ID
	 */
	private String userId;
	/**
	 * 手机号码
	 */
	private String phone;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 昵称
	 */
	private String nickName;  

	/**
	 * 真实姓名
	 */
	private String realName;
	/**
	 * 身份证号
	 */
	private String idNo;
	/**
	 * 实名认证状态
	 */
	private String realNameStatus;

	/**
	 * 性别
	 */
	private String sex;
	
	
	/**
	 * 是否签到
	 */
	private String hasSignIn;
	
	
	
	private String token;

	private String usableCount;//可用余额
	
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getUserId()
	{
		return this.userId;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getPhone()
	{
		return this.phone;
	}
	
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
	public String getNickName()
	{
		return this.nickName;
	}
	
	public void setrRealName(String realName)
	{
		this.realName = realName;
	}
	public String getRealName()
	{
		return this.realName;
	}
	public void setIdNo(String idNo)
	{
		this.idNo = idNo;
	}
	public String getIdNo()
	{
		return this.idNo;
	}
	public void setRealNameStatus(String realNameStatus)
	{
		this.realNameStatus = realNameStatus;
	}
	public String getRealNameStatus()
	{
		return this.realNameStatus;
	}
	
	public void setSex(String sex)
	{
		this.sex = sex;
	}
	public String getSex()
	{
		return this.sex;
	}
	
	public String getHasSignIn() {
		return hasSignIn;
	}
	public void setHasSignIn(String hasSignIn) {
		this.hasSignIn = hasSignIn;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public void setUsableCount(String count)
	{
		this.usableCount = count;
	}
	public String getUsableCount()
	{
		return this.usableCount;
	}
}
