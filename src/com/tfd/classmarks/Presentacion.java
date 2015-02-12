package com.tfd.classmarks;

import java.util.Timer;
import java.util.TimerTask;

import mysql.BaseDatos;
import mysql.ClaseCuatrimestres;
import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Presentacion extends Activity {

	private Timer time = new Timer();
	private Intent in;

	public int fondoElegido = 1;
	public int fondoElegido1 = R.style.CM_standard_windowBackground;
	public int fondoElegido2 = R.style.CM_alternative_windowBackground;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

    	SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
    	int value = preferences.getInt("fondo seleccionado", 0);
    	
    	if(value == 1)
    		setTheme(fondoElegido1);
		else
			setTheme(fondoElegido2);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.presentacion_act);

		overridePendingTransition(anim.fade_in, 0);

		TextView txt = (TextView) findViewById(R.id.TVtfd);
		ImageView logo = (ImageView)findViewById(R.id.lay_class);

		Typeface font = Typeface.createFromAsset(getAssets(), "TELE2.ttf");

		txt.setTypeface(font);
		txt.setTextColor(Color.argb(150, 0, 0, 0));

		final Animation anim = AnimationUtils.loadAnimation(this, R.anim.title_effect);
		logo.startAnimation(anim);

		Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.alpha_effect);
		txt.startAnimation(anim2);

		in = new Intent(getApplicationContext(), Principal.class);

		TimerTask tasko = new TimerTask() {

			@Override
			public void run() {
				
				BaseDatos cn = new BaseDatos(getApplicationContext());

				ClaseCuatrimestres CT = new ClaseCuatrimestres();
				int i = 1;
				try {
					i= cn.getClassMarks().getLon();
				}catch (Exception e) {
				}

				if (i  != 0){

				}else{
					CT.setCuatrimestre("Primero");
					cn.InsertarCuatrimestre(CT);
				}
				cn.closeDB();
				startActivity(in);

			}
		};  
		time.schedule(tasko, 2500);


	}

	public void onBackPressed() {
		super.onBackPressed();
		System.exit(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

}