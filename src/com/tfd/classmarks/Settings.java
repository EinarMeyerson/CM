package com.tfd.classmarks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
//		SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
//    	int value = preferences.getInt("fondo seleccionado", 0);
//    	Presentacion pres = new Presentacion();
//    	int T1 = pres.fondoElegido1;
//    	int T2 = pres.fondoElegido2;
//    	
//    	if(value == 1)
//			setTheme(T1);
//		else
//			setTheme(T2);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_act);
		
		ImageView btnAzul = (ImageView)findViewById(R.id.imageView1);
		ImageView btnMagenta = (ImageView)findViewById(R.id.ImageView01);
		
//		final SharedPreferences.Editor editor = preferences.edit();
		  
		
		btnAzul.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				editor.putInt("fondo seleccionado",1);
//				editor.commit();
				getWindow().setBackgroundDrawableResource(R.drawable.back_blur_blue_ed);
				
			}
		});
		
		btnMagenta.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				editor.putInt("fondo seleccionado",2);
//				editor.commit();
				getWindow().setBackgroundDrawableResource(R.drawable.back_black_magenta);
				
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	

}
