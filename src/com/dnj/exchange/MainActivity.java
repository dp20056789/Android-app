package com.dnj.exchange;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.net.ConnectivityManager;

import com.dnj.exchange.R.id;
import com.dnj.exchange.service.UpdateCurrencyService;
import com.umeng.analytics.MobclickAgent;



public class MainActivity extends Activity implements OnClickListener {

    private TextView title;
    private TextView fromCurrency;    
    private TextView toCurrency;
    private TextView currency;    
    private TextView convertedAmount;    
    private Spinner fromCurrencySpinner;
    private Spinner toCurrencySpinner;    
    private EditText currencyResponse;  
    private EditText convertedAmountEdit;  
    private TextView result;    
    private TextView time;   
    private Button convert_btn;
    private Button reverse_btn;  
	
    private String strFromCurrency;
    private String strToCurrency;    
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("MainActivity --onCreate--");
        
        title = (TextView) findViewById(R.id.title);
        fromCurrency = (TextView) findViewById(R.id.fromCurrency);
        toCurrency = (TextView) findViewById(R.id.toCurrency);
        currency = (TextView) findViewById(R.id.currency);
        convertedAmount = (TextView) findViewById(R.id.convertedAmount);
        fromCurrencySpinner = (Spinner) findViewById(R.id.fromCurrencySpinner);
        toCurrencySpinner = (Spinner) findViewById(R.id.toCurrencySpinner);
        currencyResponse = (EditText) findViewById(R.id.currencyResponse);
        convertedAmountEdit = (EditText) findViewById(R.id.convertedAmountEdit);
        result = (TextView) findViewById(R.id.result);
        time = (TextView) findViewById(R.id.time);
        convert_btn = (Button) findViewById(R.id.convert_btn);
        reverse_btn = (Button) findViewById(R.id.reverse_btn);
       
        result.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        
        strFromCurrency = null;
        strToCurrency = null; 
        System.out.println("onCreate"+(strFromCurrency == null));
        System.out.println("onCreate"+(strFromCurrency == " "));
        
        //设置可兑换货币
        String[] items = fromCurrencySpinner.getResources().getStringArray(R.array.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        int defalutItem = adapter.getPosition(Config.DEFAULT_CURRENCY_TYPE);
        fromCurrencySpinner.setAdapter(adapter);
        fromCurrencySpinner.setSelection(defalutItem);          
        fromCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        	// TODO Auto-generated method stub
        	
        }
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
        		int position, long id) {
        	System.out.println("onItemSelected id = "+id);
        	strFromCurrency = parent.getItemAtPosition(position).toString();        
        	
  	        if(strFromCurrency.equals("")){
  	        	currencyResponse.setText("");
  	            result.setVisibility(View.INVISIBLE);
  	            time.setVisibility(View.INVISIBLE);
  	        }
  	        
  	        boolean strIsNull;
  	        if(strToCurrency != null){
  	  	        strIsNull = strToCurrency.equals("");  	        	
  	        }else{
  	        	strIsNull = true;
  	        }
  	        if((!strIsNull) && strFromCurrency.equals(strToCurrency)){
  	        	Toast.makeText(MainActivity.this, R.string.fromEqualsTo, 2000).show();
  	        }
        }
		});
        
        toCurrencySpinner.setAdapter(adapter); 
        toCurrencySpinner.setSelection(defalutItem);
        toCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            	// TODO Auto-generated method stub
            	
            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
            		int position, long id) {
    	        strToCurrency = parent.getItemAtPosition(position).toString();      
    	        System.out.println("MainActivity toCurrencySpinner = " + strToCurrency);

      	        if(strToCurrency.equals("")){
      	        	currencyResponse.setText("");
      	            result.setVisibility(View.INVISIBLE);
      	            time.setVisibility(View.INVISIBLE);
      	        }
    	        
    	        boolean strIsNull;
      	        if(strFromCurrency != null){
      	  	        strIsNull = strFromCurrency.equals("");  	        	
      	        }else{
      	        	strIsNull = true;
      	        }
      	        if((!strIsNull) && strToCurrency.equals(strFromCurrency)){
      	        	Toast.makeText(MainActivity.this, R.string.fromEqualsTo, 2000).show();
      	        }
            }
    		});
        convert_btn.setOnClickListener(this);
        reverse_btn.setOnClickListener(this);
        
        MobclickAgent.updateOnlineConfig(this);//友盟统计工具
        
        //注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdateCurrencyService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);

    }



    @Override
    public void onClick(View v) {
        System.out.println("MainActivity --onClick-s-");
        System.out.println("onClick --strFromCurrency--" + strFromCurrency);
        System.out.println("onClick --strToCurrency--" + strToCurrency);

       //判读需要换算的货币是否为空
        boolean strEqua = strFromCurrency.equals("");
        System.out.println(strEqua);
    	if((strFromCurrency == null)||(strFromCurrency.equals(""))){
            System.out.println("strFromCurrency == null");    		
    		Toast.makeText(this, R.string.fromCurrencyIsNull, 2000).show();
    		return;
    	}else if(strToCurrency == null||(strToCurrency.equals(""))){
            System.out.println("strToCurrency == null");  
    		Toast.makeText(this, R.string.toCurrencyIsNull, 2000).show(); 
    		return;
    	}
    	
    	//判断网络状态是否打开
    	boolean netAccess = NetWorkStatus();
    	if(!netAccess){
	        currencyResponse.setText("");
  	        result.setVisibility(View.INVISIBLE);
  	        time.setVisibility(View.INVISIBLE);
    		Toast.makeText(this, R.string.netAccessIsNull, 2000).show();
    		return;
    	}
    	
    	
    	//传递兑换金额 如果为空 默认金额为10000
        String amount = convertedAmountEdit.getText().toString();
        if(amount.equals("")){
    	  amount = "10000";
        }
        System.out.println(amount);
        
        switch(v.getId()){
          case R.id.convert_btn: 
            Intent intent = new Intent(this, UpdateCurrencyService.class);
            //获取货币类型对应的英文字符部分
            intent.putExtra("from_currency", strFromCurrency.substring(strFromCurrency.lastIndexOf(" ")+1));
            intent.putExtra("to_currency", strToCurrency.substring(strToCurrency.lastIndexOf(" ")+1));
            intent.putExtra("amount", amount);
            startService(intent);
            System.out.println("MainActivity --onClick-e-");
            
            break;
        
          case R.id.reverse_btn: 
        	String tmpStr = strFromCurrency;
        	strFromCurrency = strToCurrency;
        	strToCurrency = tmpStr;
        	
        	int tmpItem = fromCurrencySpinner.getSelectedItemPosition();
        	fromCurrencySpinner.setSelection(toCurrencySpinner.getSelectedItemPosition());
        	toCurrencySpinner.setSelection(tmpItem);
        	
            Intent intent_reverse = new Intent(this, UpdateCurrencyService.class);
            //获取货币类型对应的英文字符部分
            intent_reverse.putExtra("from_currency", strFromCurrency.substring(strFromCurrency.lastIndexOf(" ")+1));
            intent_reverse.putExtra("to_currency", strToCurrency.substring(strToCurrency.lastIndexOf(" ")+1));
            intent_reverse.putExtra("amount", amount);
            startService(intent_reverse);
            System.out.println("MainActivity --onClick-e-");       	
        	
            break;
            
          default:
        	break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	unregisterReceiver(mReceiver);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override    
    protected void onResume() {
    	System.out.println("onResume");
    	super.onResume();
    	MobclickAgent.onResume(this);//友盟统计工具
    	
		SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		String defValue = pre.getString("errNum", "");
		//前次无结果则不显示
		if(!defValue.equals("0")){
			return;
		}
    	showCurrency(pre);   
    };
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	MobclickAgent.onPause(this);//友盟统计工具
    }
    
    //currency -- 需要转换的货币 比如 USD
    //
    private int convertCurrency(String currency){
    	if(currency == null){
    		return 0;
    	}
    	System.out.println("--convertCurrency--convert="+currency);
    	String[] items = fromCurrencySpinner.getResources().getStringArray(R.array.spinner);
    	for(int i = 1; i<items.length; i++){
    		String compare = items[i].substring(items[i].indexOf(" ")+1);
    		System.out.println("--convertCurrency--compare="+compare);
    		if (compare.equals(currency)){
    			System.out.println("--convertCurrency--i="+i);
    			return i;    
    		}	
    	}
    	return 0;
    }


    //更新UI的广播接收器
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(UpdateCurrencyService.ACTION_UPDATE.equals(intent.getAction())){
				SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
				String defValue = pre.getString("errNum", "");
				//传值结果错误
				if(!defValue.equals("0")){
					Toast.makeText(MainActivity.this, R.string.responseFail, 2000).show();
					return;
				}
				showCurrency(pre);
			}
			
		}
	};
	
	private void showCurrency(SharedPreferences pre){
		//保存各项的值
		String defValue = pre.getString("errNum", "");
		String errMsg = pre.getString("errMsg", "");
		String date = pre.getString("date", "");
		String timeNow = pre.getString("time", "");
		String fromCurrency = pre.getString("fromCurrency", "");
		String amount = pre.getString("amount", "");
		String toCurrency = pre.getString("toCurrency", "");
		
		String currencyTmp = pre.getString("currency", "");
		String currency;
		if(currencyTmp.length() > Config.CURRENCY_LENTH){
		  currency = currencyTmp.substring(0, Config.CURRENCY_LENTH);
		}else{
		  currency = currencyTmp;		  
		}
		
		String convertedTmp = pre.getString("convertedamount", "");
		String convertedamount;
        if(convertedTmp.substring(convertedTmp.indexOf(".")+1).length()>3){
		    convertedamount = convertedTmp.substring(0, convertedTmp.indexOf(".")+3);
        }else{
        	convertedamount = convertedTmp;
        }
		
		
        fromCurrencySpinner.setSelection(convertCurrency(fromCurrency));
        toCurrencySpinner.setSelection(convertCurrency(toCurrency));

        if(strFromCurrency == null){
        	strFromCurrency = fromCurrencySpinner.getSelectedItem().toString();
        }
        if(strToCurrency == null){
        	strToCurrency = toCurrencySpinner.getSelectedItem().toString();
        }
        String resultUpdate = amount+strFromCurrency+" = "+convertedamount+strToCurrency;
        String timeUpdate = "数据仅供参考 更新时间："+date+" "+timeNow;
        
        convertedAmountEdit.setText(amount);
        currencyResponse.setText(currency);
        result.setText(resultUpdate);
        result.setVisibility(View.VISIBLE);

        time.setText(timeUpdate);
        time.setVisibility(View.VISIBLE);
	}
	
	private boolean NetWorkStatus() {
        boolean netSataus = false;
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        cwjManager.getActiveNetworkInfo();

        if (cwjManager.getActiveNetworkInfo() != null) {
            netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
        }
        return netSataus;
    }
}
