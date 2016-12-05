package com.xe.lzh.rfid.Dao;

/**
 * 网络请求接口
 * 
 * @author Administrator
 * 
 */
public interface IAsyncDao {

	/**
	 * 一切正常时调用的具体业务函数 1.解析里层json数据
	 * 
	 */
	public void do_business(String json);

	/**
	 * 发送请求,post get or delete
	 */
	public void do_network();

}
