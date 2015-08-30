package com.dnj.exchange.service;

import com.dnj.exchange.Config;
import com.dnj.exchange.net.HttpCallbackListener;
import com.dnj.exchange.net.HttpUtil;
import com.dnj.exchange.net.Response;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.BufferedReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UpdateCurrencyService extends Service {
	private Intent intent;
    public static String ACTION_UPDATE = "com.dnj.exchange.service.update";
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	   System.out.println("UpdateCurrencyService --onStartCommand-s-");  
	   this.intent = intent;
       new Thread(new  Runnable() {
			@Override
			public void run() {
				UpdateCurrency();
			}
        }).start();
       
       
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void UpdateCurrency() {
		if(intent != null){
	    String upFromCurrency = intent.getStringExtra("from_currency");
	    String upToCurrency = intent.getStringExtra("to_currency");
	    String upAmount = intent.getStringExtra("amount");
	    
	    String httpArg = "fromCurrency="+upFromCurrency+"&toCurrency="+upToCurrency+"&amount="+upAmount;
	    System.out.println("UpdateCurrencyService --UpdateCurrency--");  
	    System.out.println("httpUrl ="+Config.httpUrl);
	    System.out.println("httpArg ="+httpArg);
	    
	    HttpUtil.request(Config.httpUrl, httpArg, new HttpCallbackListener() {
			
			@Override
			public void onfinish(String response) {
				Response.handleHttpResponse(UpdateCurrencyService.this, response);	
				//¸üÐÂUI
	            Intent intent = new Intent(ACTION_UPDATE);    
	            sendBroadcast(intent);  
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
		});
		}

	}

	
	
}
