package util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import model.City;
import model.County;
import model.Province;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import db.CoolWeatherDB;

public class Utility {
	
	/**
	 *解析和处理服务器返回的省级数据 
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces!=null && allProvinces.length>0){
				for(String p : allProvinces){
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 *解析和处理服务器返回的市级数据 
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response, int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allcities=response.split(",");
			if(allcities!=null && allcities.length>0){
				for(String c : allcities){
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 *解析和处理服务器返回的县级数据 
	 */
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allcounties=response.split(",");
			if(allcounties!=null && allcounties.length>0){
				for(String c : allcounties){
					String[] array=c.split("\\|");
					County county=new County();
					county.setCityId(cityId);
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 *解析服务器返回的JSON数据，并将解析出的数据存储到本地 
	 */
	public static void handleWeatherResponse(Context context,String response){
		try{
			JSONObject jsonObject=new JSONObject(response);
			JSONObject result=jsonObject.getJSONObject("result");
			JSONObject sk=result.getJSONObject("sk");
			String publishTime=sk.getString("time");
			
			JSONObject today=result.getJSONObject("today");
			String cityName=today.getString("city");
			String wind=today.getString("wind");
			String temperature=today.getString("temperature");
			String weatherDesp=today.getString("weather");
			
			saveWeatherInfo(context,cityName,wind,temperature,weatherDesp,publishTime);
		}catch(JSONException e){
			e.printStackTrace();
		}
	}
	
	/**
	 *将服务器返回的所有天气信息存储到SharedPreferences文件中 
	 */
	public static void saveWeatherInfo(Context context,String cityName,String wind,String temperature,String weatherDesp,String publishTime){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name",cityName);
		editor.putString("wind", wind);
		editor.putString("temperature", temperature);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
}
