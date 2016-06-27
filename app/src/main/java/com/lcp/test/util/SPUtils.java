package com.lcp.test.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.lcp.test.App;

public class SPUtils {

	public static String SP_NAME = "config";
	private static SharedPreferences sp;
	private static Context mContext = App.getContext();

	public static void saveBoolean(String key, boolean value) {
		if (sp == null)
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBoolean(String key,boolean defValue){
		if(sp == null) {
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getBoolean(key, defValue);
	}
	
	public static void saveString(String key, String value) {
		if (sp == null)
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();
	}
	
	public static String getString(String key,String defValue){
		if(sp == null) {
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getString(key, defValue);
	}

	public static String getString(String key){
		if(sp == null) {
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getString(key, "");
	}
	
	public static void saveInt(String key, int value) {
		if (sp == null)
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		sp.edit().putInt(key, value).commit();
	}
	
	/**
	 * 根据key获取int值
	 * @param key
	 * @param defValue 如果没取到，默认返回defValue
	 * @return
	 */
	public static int getInt(String key,int defValue){
		if(sp == null) {
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getInt(key, defValue);
	}

	/**
	 * 根据key获取int值
	 * @param key
	 * @return 如果没取到，默认返回-1
	 */
	public static int getInt(String key){
		if(sp == null) {
			sp = mContext.getSharedPreferences(SP_NAME, 0);
		}
		return sp.getInt(key, -1);
	}
}
