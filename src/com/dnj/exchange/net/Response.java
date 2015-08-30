/**
 * 
 */
package com.dnj.exchange.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author dnj
 *
 */
public class Response {
     public static void handleHttpResponse(Context context,String response){
  		
		try {
			JSONObject jsonObject = new JSONObject(response);
			String errNum = jsonObject.getString("errNum");
			String errMsg = jsonObject.getString("errMsg");
			//http链接返回结果发生错误 
			if(!errNum.equals("0")){
				return;
			}
			
			
			JSONObject retData = jsonObject.getJSONObject("retData");
			String date = retData.getString("date");
			String time = retData.getString("time");
			String fromCurrency = retData.getString("fromCurrency");
			String amount = retData.getString("amount");
			String toCurrency = retData.getString("toCurrency");
			String currency = retData.getString("currency");
			String convertedamount = retData.getString("convertedamount");
			
			System.out.println("handleWeatherResponse errNum="+errNum+" date"+date+" time"+time+" fromCurrency"
			            +fromCurrency+" amount"+amount+" toCurrency"+toCurrency+" currency"+currency
			            +" convertedamount"+convertedamount);

			//保存各项的值
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putString("errNum", errNum);
			editor.putString("errMsg", errMsg);
			editor.putString("date", date);
			editor.putString("time", time);
			editor.putString("fromCurrency", fromCurrency);
			editor.putString("amount", amount);
			editor.putString("toCurrency", toCurrency);
			editor.putString("currency", currency);
			editor.putString("convertedamount", convertedamount);
			editor.commit();
			
 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
