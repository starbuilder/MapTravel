package com.android.maptravel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class CustomStreetView extends  WebView{
	

	

	private double latitude,longitude;
	Context context;
	public CustomStreetView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CustomStreetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CustomStreetView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public void init(Context context){
		this.context = context;
		WebSettings webSettings = getSettings();
		webSettings.setDomStorageEnabled(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setSupportZoom(false);
	}
	public void setLocation(double latitude,double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
		loadData(getHtmlCode(), "text/html", "utf8");
	}


	public String getHtmlCode(){
		String html = "";
		try{
			InputStream is = context.getAssets().open("streetview.html");
			html = convertStreamToString(is);
			html = html.replace("#lat", String.valueOf(latitude));
			html = html.replace("#long", String.valueOf(longitude));
			html = html.replace("#w", String.valueOf(150));
			html = html.replace("#h", String.valueOf(150));
		}catch(IOException e){
			e.printStackTrace();
		}
		return html;
	}
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	/***/
	private Bitmap captureWebViewVisibleInScreen(WebView webView){  
		Bitmap bmp = webView.getDrawingCache();  
		return bmp;  
	}  

	/**webview */
	private Bitmap captureWebView(WebView webView){  
		Picture snapShot = webView.capturePicture();  

		Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),snapShot.getHeight(), Bitmap.Config.ARGB_8888);  
		Canvas canvas = new Canvas(bmp);  
		snapShot.draw(canvas);  
		return bmp;  
	}  




} 
