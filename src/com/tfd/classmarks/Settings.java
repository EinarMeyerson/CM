package com.tfd.classmarks;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
		if(value == 2)
			setTheme(T2);
		else
			setTheme(T1);

		super.onCreate(savedInstanceState);

		overridePendingTransition(R.anim.fade_in_principal, 0);
		setContentView(R.layout.settings_act);

		TextView txtSettings = (TextView)findViewById(R.id.txtTitleSettings);
		TextView txtChooseThemesSettings = (TextView)findViewById(R.id.txtSettings);

		Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

		txtSettings.setTypeface(tf);
		txtChooseThemesSettings.setTypeface(tf);

		final Intent in = new Intent(this, Principal.class);

		ImageView btnIcon = (ImageView)findViewById(R.id.iconoCMsettings);

		btnIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(in);
				finish();
			}
		});

		ImageView btnAzul = (ImageView)findViewById(R.id.blue_theme_button);
		ImageView btnMagenta = (ImageView)findViewById(R.id.dark_red_theme_button);

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
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}



}
