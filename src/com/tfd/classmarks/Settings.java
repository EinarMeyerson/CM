package com.tfd.classmarks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
    	int value = preferences.getInt("fondo seleccionado", 0);
    	Presentacion pres = new Presentacion();
    	int T1 = pres.fondoElegido1;
    	int T2 = pres.fondoElegido2;
    	
    	if(value == 1)
			setTheme(T1);
		else
			setTheme(T2);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_act);
		
		Button btnAzul = (Button)findViewById(R.id.btn_select_standard);
		Button btnMagenta = (Button)findViewById(R.id.btn_select_alternative);
		Button btnSalir = (Button)findViewById(R.id.button1);
		
		final SharedPreferences.Editor editor = preferences.edit();
		  
		
		btnAzul.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editor.putInt("fondo seleccionado",1);
				editor.commit();
				getWindow().setBackgroundDrawableResource(R.drawable.back_blur_blue_ed);
				
			}
		});
		
		btnMagenta.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editor.putInt("fondo seleccionado",2);
				editor.commit();
				getWindow().setBackgroundDrawableResource(R.drawable.back_black_magenta);
				
			}
		});
		
		btnSalir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	
	}
	

}
