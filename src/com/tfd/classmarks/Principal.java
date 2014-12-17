package com.tfd.classmarks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import mysql.BaseDatos;
import mysql.ClaseAsignaturas;
import mysql.ClaseClassMarks;
import mysql.ClaseCuatrimestres;
import mysql.ClaseNotas;
import android.R.anim;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Principal extends FragmentActivity implements FragmentProvider {
	//Declaramos las variables.
	ImageView ico, sub;
	TextView mAdd, txtsinasig;
	RelativeLayout resul;
	Intent in;
	Spinner spinner;
	int IDmodif;
	ViewPager mPager;
	FragmentAsig And = new FragmentAsig();
	FragmentAsig Ando;
	MyPagerAdapter mAdapter;
	Boolean isShown = false;
	Boolean isShown1 = false;
	Boolean isShown2 = false;
	Boolean isShown3 = false;
//	private int tapsCount = 0;

	ArrayList<Fragment> frags = new ArrayList<Fragment>();

	//Se ejecuta la app por primera vez(proceso)
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
//		
    	setTheme(R.style.CM_standard_windowBackground);
    	
		super.onCreate(savedInstanceState);
		/*ColorDrawable colorDrawable = new ColorDrawable( Color.TRANSPARENT );
        getWindow().setBackgroundDrawable( colorDrawable );*/
		overridePendingTransition(0, 0);
		setContentView(R.layout.principal_act);
		mPager = (ViewPager) findViewById(R.id.pager);
    	
		configuracionInicial();

		mAdapter = new MyPagerAdapter(this.getSupportFragmentManager(),this);
		mPager.setAdapter(mAdapter);

		Animation anim_frags = AnimationUtils.loadAnimation(this, R.anim.fade_in_principal);
		mPager.setAnimation(anim_frags);

		isEmpty();
	}

	@Override
	protected void onStart() {
		super.onStart();

		mPager.setOffscreenPageLimit(4);

		mPager.setPageTransformer(true, new ZoomOutPageTransformer());
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
    	int resetPosition = preferences.getInt("ultimo fragment", 0);
		
    	mAdapter.notifyDataSetChanged();
		mPager.setCurrentItem(resetPosition,true);
//		frags.get(resetPosition);
//		mAdapter.notifyDataSetChanged();
		
//		SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
//    	int value = preferences.getInt("fondo seleccionado", 0);
//    	
//    	if(value == 1)
//			getWindow().setBackgroundDrawableResource(R.drawable.back_blur_blue_ed);
//		else
//			getWindow().setBackgroundDrawableResource(R.drawable.back_black_magenta);
//    	
    	    	
	}

	@Override
	protected void onPause() {
		super.onPause();

		SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
		final SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("ultimo fragment",mPager.getCurrentItem());
		editor.commit();

		//Si hay alg�n dialog abierto, lo cierra
		if (isShown == true){
			dismissDialog(0);
		}
		else if (isShown1 == true) {
			dismissDialog(1);
		}
		else if (isShown2 == true) {
			dismissDialog(2);
		}
		else if (isShown3 == true) {
			dismissDialog(3);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(0, anim.fade_out); 
		
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}

	//M�todo que determina si hay, o no, asignaturas creadas
	public void isEmpty() {
		BaseDatos cn = new BaseDatos(getApplicationContext());

		Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
		txtsinasig.setTypeface(tf);

		Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_principal);
		if (cn.getCuatrimestreDataBase("Primero").getLon()==0){
			txtsinasig.setAnimation(anim);
			txtsinasig.setText(R.string.emptyfolder);
		}else
			txtsinasig.setText("");
		cn.closeDB();
	}

	//M�todo para escalar los iconos al clicar en ellos
	public void clickInTransformation(View view){

		float scale = 0.90f;
		view.setScaleX(scale);
		view.setScaleY(scale);

	}
	//M�todo para escalar los iconos al levantar el dedo
	public void clickOutTransformation(View view){

		float scale = 1f;
		view.setScaleX(scale);
		view.setScaleY(scale);

	}

	//Clase para transformar las transiciones de los fragments en el viewPager
	public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.90f;
		private static final float MIN_ALPHA = 0.7f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;

				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	/* Este metodo lo llamamos en el archivo de dise�o principal_act.xml con la funcion 'android:onClick="addSubject"', lo que nos permite
    definir este metodo como la accion que debe hacerse cuando se clica sobre el imageview de a�adir carpeta. */
	public void addSubject(View v){
		showDialog(0);
	}
	

	private void configuracionInicial(){

		BaseDatos cn = new BaseDatos(getApplicationContext());

		TextView txtTitulo = (TextView)findViewById(R.id.txtTitle);
		Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

		txtTitulo.setTypeface(tf);

		spinner =  ( Spinner ) findViewById ( R.id.cuatrimestre_spinner );

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				frags.clear();
				BaseDatos cn = new BaseDatos(getApplicationContext());
				//              SQLiteDatabase db = cn.getReadableDatabase();
				ClaseCuatrimestres Cuatrimestre = cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
				int i=0;

				while (i<Cuatrimestre.getLon())
				{
					frags.add(new FragmentAsig(Cuatrimestre.getAsignatura(i).getNombre()));
					i++;
				}

				cn.closeDB();
				//              db.close(); 
				mAdapter.notifyDataSetChanged();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		ClaseClassMarks Classmarks = cn.getClassMarks();
		List<String> list = new ArrayList<String>();

		int j=0;
		while (j<Classmarks.getLon())
		{
			list.add(Classmarks.getCuatrimestrebyid(j).getCuatrimestre());
			j++;
		}

//		ico = (ImageView)findViewById(R.id.imageView3);
//		ico.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(getApplicationContext(), Settings.class));
//			}
//		});

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom,list);
		adapter.setDropDownViewResource(R.layout.spinner_custom);
		spinner.setAdapter(adapter);

		sub = (ImageView)findViewById(R.id.subject);

		sub.setOnTouchListener(new View.OnTouchListener() {
			private Rect rect;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				switch(action){

				case MotionEvent.ACTION_UP:
					if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
						// User moved outside bounds
						clickOutTransformation(sub);

						return false;
					}
					else{
						clickOutTransformation(sub);
						addSubject(v);
					}
					return false;

				case MotionEvent.ACTION_DOWN:
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
					clickInTransformation(sub);
					return true;

				case MotionEvent.ACTION_MOVE:
					if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
						// User moved outside bounds
						clickOutTransformation(sub);
						return true;
					}
					else{
						clickInTransformation(sub);
					}
					return true;
				}
				return false;
			}
		});

		cn.closeDB();
		ImageView icon = (ImageView)findViewById(R.id.imageView3);
		
//		icon.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				tapCounting();
//				
//			}
//		});
		txtsinasig = (TextView)findViewById(R.id.txtsinasignaturas);
		Animation anim_frags = AnimationUtils.loadAnimation(this, R.anim.fade_in_principal);

		icon.setAnimation(anim_frags);
		sub.setAnimation(anim_frags);
		txtTitulo.setAnimation(anim_frags);
		txtsinasig.setAnimation(anim_frags);


	}

//	protected void tapCounting() {
//
//		while(tapsCount < 10){
//			tapsCount++;
//			break;
//		}
//		
////		SharedPreferences preferences = getSharedPreferences("CMpreferences", MODE_PRIVATE);
////    	int value = preferences.getInt("fondo seleccionado", 0);
////    	Presentacion pres = new Presentacion();
////    	int T1 = pres.fondoElegido1;
////    	int T2 = pres.fondoElegido2;
////    	
////    	if(value == 1)
////			setTheme(T1);
////		else
////			setTheme(T2);
////		
//    	setTheme(R.style.CM_alternative_windowBackground);
//		
//		tapsCount=0;
//	}

	public void setIDmodif(int id){
		IDmodif= id;
	}

	@Override
	public Fragment getFragmentForPosition(int position) {
		return frags.get(position);
	}

	@Override
	public int getCount() {
		return frags.size();
	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		private FragmentProvider mProvider;

		public MyPagerAdapter(FragmentManager fm, FragmentProvider provider) {
			super(fm);
			this.mProvider = provider;
		}

		@Override
		public int getItemPosition(Object object) {
			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			return mAdapter.mProvider.getFragmentForPosition(position);
		}

		@Override
		public int getCount() {
			return mProvider.getCount();
		}
	}

	public void removeCurrentItem() {

		final int position = mPager.getCurrentItem();
		int lastPosition = frags.size()-1;

		float fragwid= mPager.getWidth();

		View fragment = frags.get(position).getView();

		View fragmentLeft = frags.get(position).getView();
		try{
			fragmentLeft = frags.get(position-1).getView();
		}catch(IndexOutOfBoundsException e){
		}

		View fragmentRight = frags.get(position).getView();
		try{
			fragmentRight = frags.get(position+1).getView();
		}catch(IndexOutOfBoundsException e){
		}

		if (position == lastPosition && position != 0){

			final ObjectAnimator anim2 = ObjectAnimator.ofFloat(fragmentLeft, View.TRANSLATION_X, fragwid);
			anim2.setDuration(800);

			anim2.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					frags.remove(position);
					mAdapter.notifyDataSetChanged();
				}
			});
			anim2.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.interpolator.decelerate_cubic));

			ObjectAnimator anim = ObjectAnimator.ofFloat(fragment, View.ALPHA, 0);
			anim.setDuration(1200);

			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					anim2.start();
				}
			});
			anim.start();
		}

		else if(position >= 0 && position < lastPosition){
			final ObjectAnimator anim2 = ObjectAnimator.ofFloat(fragmentRight, View.TRANSLATION_X, -fragwid);
			anim2.setDuration(800);

			anim2.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					frags.remove(position);
					mAdapter.notifyDataSetChanged();
				}
			});
			anim2.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.interpolator.decelerate_cubic));

			ObjectAnimator anim = ObjectAnimator.ofFloat(fragment, View.ALPHA, 0);
			anim.setDuration(1200);

			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					anim2.start();
				}
			});
			anim.start();
		}
		else{

			ObjectAnimator anim = ObjectAnimator.ofFloat(fragment, View.ALPHA, 0);
			anim.setDuration(1200);

			anim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					frags.remove(position);
					mAdapter.notifyDataSetChanged();
					isEmpty();
				}
			});
			anim.start();
		}



	}


	@Override
	protected Dialog onCreateDialog(int id){
		switch (id){
		case 0:
			//Crear asignatura
			LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View newAsig = inflater.inflate(R.layout.nuevaasignatura_act, null);

//			SharedPreferences preferencesD = getSharedPreferences("CMpreferences", MODE_PRIVATE);
//	    	int valueD = preferencesD.getInt("fondo seleccionado", 0);
//	    	
//	    	if(valueD == 1)
//				newAsig.setBackgroundResource(R.drawable.dialog_background);
//			else
//				newAsig.setBackgroundResource(R.drawable.dialog_background_alternative);
	    	
			Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

			TextView txt1 = (TextView)newAsig.findViewById(R.id.textviewCarpeta);
			txt1.setTypeface(tf);  

			//          TextView txt = (TextView)newAsig.findViewById(R.id.textviewnuevaasig);
			//          txt.setTypeface(tf);

			final EditText edtxt = (EditText)newAsig.findViewById(R.id.edittextAsig);
			edtxt.setTypeface(tf);

			final Button btn = (Button)newAsig.findViewById(R.id.buttonCrearAsig);
			btn.setTypeface(tf);

			final Button btn1 = (Button)newAsig.findViewById(R.id.buttonSalirAsig);
			btn1.setTypeface(tf);

			//boton crear - gestos
			btn.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn);
							btn.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btn);
							btn.setShadowLayer(0, 0, 0, Color.WHITE);
							btn.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btn);
						btn.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn);
							btn.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btn);
							btn.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;

					}
					return false;
				}
			});

			//boton cancelar - gestos
			btn1.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn1);
							btn1.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btn1);
							btn1.setShadowLayer(0, 0, 0, Color.WHITE);
							btn1.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btn1);
						btn1.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn1);
							btn1.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btn1);
							btn1.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;

					}
					return false;
				}
			});

			//boton crear - funcion
			btn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					BaseDatos cn = new BaseDatos(getApplicationContext());
					//verificar si la casilla rellenar  nota esta vacia para mostrar mensaje
					if (edtxt.getText().length()==0)
					{
						Toast.makeText(getApplicationContext(),R.string.toastSinrellenar,Toast.LENGTH_SHORT).show();

					}
					else if(cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString()).getLon()<10) {
						//
						ClaseAsignaturas Asignatura = new ClaseAsignaturas();

						String asign = edtxt.getText().toString();

						Asignatura.setNombre(asign);

						Asignatura.setIdcuatrimestre(cn.IdCuatrimestre(spinner.getSelectedItem().toString()));

						int ControlInsertAsig =cn.InsertarAsignatura(Asignatura);
						if (ControlInsertAsig==0){
							frags.add(new FragmentAsig(asign));
							mAdapter.notifyDataSetChanged();
							cn.closeDB();
							//						db.close();
							mPager.setCurrentItem(frags.size());
							dismissDialog(0);
							removeDialog(0);
							isShown = false;

							edtxt.setText("");
							isEmpty();
						}
						else{
							edtxt.setText("");
							Toast.makeText(getApplicationContext(), Asignatura.getNombre()+ " "+getString(R.string.toastYaexiste), Toast.LENGTH_LONG).show();
						}
					}
					else
					{
						dismissDialog(0);
						removeDialog(0);
						isShown = false;
						edtxt.setText("");
						Toast.makeText(getApplicationContext(),R.string.toastMaxAsignaturas,Toast.LENGTH_LONG).show();
						cn.closeDB();

					}
				}
			});

			//boton cancelar - funcion
			btn1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissDialog(0);
					removeDialog(0);
					isShown = false;
					edtxt.setText("");

				}
			});
			Dialog dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(newAsig);
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			isShown = true;
			return dialog;

		case 1:
			//A�adir nota
			LayoutInflater inflater1=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View newNota = inflater1.inflate(R.layout.insertarnota_act, null);

//			SharedPreferences preferencesD1 = getSharedPreferences("CMpreferences", MODE_PRIVATE);
//	    	int valueD1 = preferencesD1.getInt("fondo seleccionado", 0);
//	    	
//	    	if(valueD1 == 1)
//	    		newNota.setBackgroundResource(R.drawable.dialog_background);
//			else
//				newNota.setBackgroundResource(R.drawable.dialog_background_alternative);
	    	
			BaseDatos cn = new BaseDatos(getApplicationContext());

			ClaseCuatrimestres cuatri = cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
			double porcentajeusado = cn.SumaPorcentajes(cuatri.getAsignatura(mPager.getCurrentItem()).getId());

			Typeface tf1 = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
			TextView txt3 = (TextView)newNota.findViewById(R.id.textviewAsignatura);
			String nom = cuatri.getAsignatura(mPager.getCurrentItem()).getNombre();

			txt3.setText(nom);
			txt3.setTypeface(tf1);

			TextView txt4 =(TextView)newNota.findViewById(R.id.textviewNombre);
			txt4.setTypeface(tf1);

			TextView txt5 =(TextView)newNota.findViewById(R.id.textviewPorcentaje);
			txt5.setTypeface(tf1);

			TextView txt6 =(TextView)newNota.findViewById(R.id.textviewNota);
			txt6.setTypeface(tf1);

			final EditText edtxtnombreexa = (EditText)newNota.findViewById(R.id.edittextNombre);
			edtxtnombreexa.setTypeface(tf1);
			final EditText edtxtporcetaje = (EditText)newNota.findViewById(R.id.edittextPorcentaje);
			edtxtporcetaje.setTypeface(tf1);
			final EditText edtxtnota = (EditText)newNota.findViewById(R.id.edittextNota);
			edtxtnota.setTypeface(tf1);

			edtxtnombreexa.setMaxLines(1);
			edtxtnombreexa.setLines(1);
			edtxtporcetaje.setMaxLines(1);
			edtxtporcetaje.setLines(1);
			double hint = Math.round((100-porcentajeusado)*100.0)/100.0;

			edtxtporcetaje.setHint("Max: "+String.valueOf(hint)+"%");
			edtxtnota.setMaxLines(1);
			edtxtnota.setLines(1);

			final Button btn2 = (Button)newNota.findViewById(R.id.buttonCrearNota);
			btn2.setTypeface(tf1);

			final Button btn3 = (Button)newNota.findViewById(R.id.buttonSalirNota);
			btn3.setTypeface(tf1);

			cn.closeDB();

			//boton crear - gestos
			btn2.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn2);
							btn2.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btn2);
							btn2.setShadowLayer(0, 0, 0, Color.WHITE);
							btn2.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btn2);
						btn2.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn2);
							btn2.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btn2);
							btn2.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;

					}
					return false;
				}
			});

			//boton cancelar - gestos
			btn3.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn3);
							btn3.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btn3);
							btn3.setShadowLayer(0, 0, 0, Color.WHITE);
							btn3.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btn3);
						btn3.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn3);
							btn3.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btn3);
							btn3.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;

					}
					return false;
				}
			});

			//boton crear - funcion
			btn2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					boolean verdad = true;
					if (edtxtnombreexa.getText().length()==0)
					{
						verdad = false;
					}
					if (edtxtporcetaje.getText().length()==0)
					{
						verdad = false;
					}

					if (edtxtnota.getText().length()==0)
					{
						verdad = false;
					}


					if (verdad ==true)
					{
						try {

							BaseDatos cn = new BaseDatos(getApplicationContext());
							//                      SQLiteDatabase db = cn.getWritableDatabase();
							ClaseNotas notas = new ClaseNotas();
							ClaseCuatrimestres cuatri = cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString());

							boolean datoscorrectos = true;
							String nombreexa = edtxtnombreexa.getText().toString();
							String porcetajeex = edtxtporcetaje.getText().toString();
							String notaex = edtxtnota.getText().toString();
							float nota = Float.parseFloat(notaex);
							double porcentajeusado = cn.SumaPorcentajes(cuatri.getAsignatura(mPager.getCurrentItem()).getId());							
							double porIf = Math.round((100-porcentajeusado)*100.0)/100.0;
							BigDecimal porcentajeusadoBig = new BigDecimal(""+porIf);

							
							if (nota>10)
							{
								datoscorrectos = false;
								edtxtnota.setText("");

							}

							if (Double.parseDouble(edtxtporcetaje.getText().toString())>porcentajeusadoBig.doubleValue() || Double.parseDouble(edtxtporcetaje.getText().toString())==0)
							{

								datoscorrectos = false;
								edtxtporcetaje.setText("");

							}
							if (datoscorrectos ==true)
							{
								notas.setEvaluable(nombreexa);
								notas.setNota(Double.parseDouble(notaex));  
								notas.setPorcentaje(Double.parseDouble(porcetajeex));
								notas.setIdasignatura(cuatri.getAsignatura(mPager.getCurrentItem()).getId());
								int ControlInsertNota=cn.InsertarNota(notas);
								if(ControlInsertNota==0){
									cn.closeDB();

									dismissDialog(1);
									removeDialog(1);
									isShown1 = false;

									mAdapter.notifyDataSetChanged();

									edtxtporcetaje.setText("");
									edtxtnota.setText("");
									edtxtnombreexa.setText("");
									edtxtnombreexa.requestFocus();
								}

								else{
									edtxtnombreexa.setText("");
									Toast.makeText(getApplicationContext(), notas.getEvaluable()+" ya existe", Toast.LENGTH_LONG).show();
									cn.closeDB();

								}
							}
							else
							{
								Toast.makeText(getApplicationContext(),R.string.toastDatosincorrectos,Toast.LENGTH_SHORT).show();   

							}
							cn.closeDB();

						} catch (NumberFormatException e) {
							Toast.makeText(getApplicationContext(),R.string.toastDatosincorrectos,Toast.LENGTH_SHORT).show();   
						}
					}
					else
						Toast.makeText(getApplicationContext(), R.string.toastCampossinrellenar,Toast.LENGTH_SHORT).show();    
				}
			});

			btn3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissDialog(1);
					removeDialog(1);
					isShown1 = false;
					edtxtporcetaje.setText("");
					edtxtnota.setText("");
					edtxtnombreexa.setText("");
				}
			});

			Dialog dialog1 = new Dialog(this);
			dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog1.setContentView(newNota);
			dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			isShown1 = true;
			return dialog1;

		case 2:
			//Modificar nota
			LayoutInflater inflater2=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View modifNota = inflater2.inflate(R.layout.modificarnota_act, null);

//			SharedPreferences preferencesD2 = getSharedPreferences("CMpreferences", MODE_PRIVATE);
//	    	int valueD2 = preferencesD2.getInt("fondo seleccionado", 0);
//	    	
//	    	if(valueD2 == 1)
//	    		modifNota.setBackgroundResource(R.drawable.dialog_background);
//			else
//				modifNota.setBackgroundResource(R.drawable.dialog_background_alternative);
	    	
			BaseDatos cn1 = new BaseDatos(getApplicationContext());

			ClaseCuatrimestres cuatri1 = cn1.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
			ClaseNotas notamodif = cn1.getNotaDataBase(IDmodif);
			double porcentajeusado1 = cn1.SumaPorcentajes(cuatri1.getAsignatura(mPager.getCurrentItem()).getId());

			Typeface tf11 = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");
			TextView txt31 = (TextView)modifNota.findViewById(R.id.textviewAsignatura);
			String nom1 = cuatri1.getAsignatura(mPager.getCurrentItem()).getNombre();

			txt31.setText(nom1);
			txt31.setTypeface(tf11);

			TextView txt41 =(TextView)modifNota.findViewById(R.id.textviewNombre);
			//txt41.setText(notamodif.getEvaluable());

			txt41.setTypeface(tf11);

			TextView txt51 =(TextView)modifNota.findViewById(R.id.textviewPorcentaje);
			//txt51.setText(""+notamodif.getPorcentaje());

			txt51.setTypeface(tf11);

			TextView txt61 =(TextView)modifNota.findViewById(R.id.textviewNota);
			//txt61.setText(""+notamodif.getNota());

			txt61.setTypeface(tf11);

			final EditText edtxtnombreexa1 = (EditText)modifNota.findViewById(R.id.edittextNombre);
			edtxtnombreexa1.setTypeface(tf11);
			final EditText edtxtporcetaje1 = (EditText)modifNota.findViewById(R.id.edittextPorcentaje);
			edtxtporcetaje1.setTypeface(tf11);
			final EditText edtxtnota1 = (EditText)modifNota.findViewById(R.id.edittextNota);
			edtxtnota1.setTypeface(tf11);

			edtxtnombreexa1.setMaxLines(1);
			edtxtnombreexa1.setLines(1);
			edtxtnombreexa1.setText(notamodif.getEvaluable());

			edtxtporcetaje1.setMaxLines(1);
			edtxtporcetaje1.setLines(1);
			double hint2 = Math.round((100-porcentajeusado1+notamodif.getPorcentaje())*100.0)/100.0;

			edtxtporcetaje1.setHint("Max: "+String.valueOf(hint2)+"%");
			edtxtporcetaje1.setText(""+notamodif.getPorcentaje());

			edtxtnota1.setMaxLines(1);
			edtxtnota1.setLines(1);
			edtxtnota1.setText(""+notamodif.getNota());

			final Button btn21 = (Button)modifNota.findViewById(R.id.buttonModificarNota);
			btn21.setTypeface(tf11);

			final Button btn31 = (Button)modifNota.findViewById(R.id.buttonSalirNota);
			btn31.setTypeface(tf11);

			cn1.closeDB();

			//boton modificar - gestos
			btn21.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn21);
							btn21.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btn21);
							btn21.setShadowLayer(0, 0, 0, Color.WHITE);
							btn21.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btn21);
						btn21.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn21);
							btn21.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btn21);
							btn21.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;

					}
					return false;
				}
			});

			//boton cancelar - gestos
			btn31.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn31);
							btn31.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btn31);
							btn31.setShadowLayer(0, 0, 0, Color.WHITE);
							btn31.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btn31);
						btn31.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btn31);
							btn31.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btn31);
							btn31.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;
					}
					return false;
				}
			});

			//boton modificar - funcion
			btn21.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					boolean verdad = true;
					if (edtxtnombreexa1.getText().length()==0)
					{
						verdad = false;
					}
					if (edtxtporcetaje1.getText().length()==0)
					{
						verdad = false;
					}
					if (edtxtnota1.getText().length()==0)
					{
						verdad = false;
					}

					if (verdad ==true)
					{
						try{

							BaseDatos cn2 = new BaseDatos(getApplicationContext());

							String nombreexa = edtxtnombreexa1.getText().toString();
							String porcetajeex = edtxtporcetaje1.getText().toString();
							String notaex = edtxtnota1.getText().toString();
							boolean datoscorrectos = true;

							ClaseNotas notas = new ClaseNotas();
							ClaseCuatrimestres cuatri2 = cn2.getCuatrimestreDataBase(spinner.getSelectedItem().toString());                     
							ClaseNotas notamodif2 = cn2.getNotaDataBase(IDmodif);

							float nota = Float.parseFloat(notaex);
							double porcentajeusado = cn2.SumaPorcentajes(cuatri2.getAsignatura(mPager.getCurrentItem()).getId());
							double porIf = Math.round((100-porcentajeusado+notamodif2.getPorcentaje())*100.0)/100.0;
							BigDecimal porcentajeusadoBig = new BigDecimal(""+porIf);

							if (nota>10)
							{
								datoscorrectos = false;
								edtxtnota1.setText("");

							}

							if (Double.parseDouble(edtxtporcetaje1.getText().toString())>porcentajeusadoBig.doubleValue() || Double.parseDouble(edtxtporcetaje1.getText().toString())==0)
							{

								datoscorrectos = false;
								edtxtporcetaje1.setText("");

							}
							if (datoscorrectos ==true)
							{

								notas.setId(IDmodif);
								notas.setEvaluable(nombreexa);
								notas.setNota(Double.parseDouble(notaex));  
								notas.setPorcentaje(Double.parseDouble(porcetajeex));
								notas.setIdasignatura(cuatri2.getAsignatura(mPager.getCurrentItem()).getId());

								int ControlUpdateNota=cn2.updateNota(notas);
								if(ControlUpdateNota==1){
									cn2.closeDB();

									dismissDialog(2);
									removeDialog(2);
									isShown2 = false;

									mAdapter.notifyDataSetChanged();

									edtxtporcetaje1.setText("");
									edtxtnota1.setText("");
									edtxtnombreexa1.setText("");
									edtxtnombreexa1.requestFocus();
								}

								else{
									edtxtnombreexa1.setText("");
									Toast.makeText(getApplicationContext(), notas.getEvaluable()+" ya existe", Toast.LENGTH_LONG).show();
									cn2.closeDB();

								}

							}
							else
							{
								Toast.makeText(getApplicationContext(),R.string.toastDatosincorrectos,Toast.LENGTH_SHORT).show();   

							}
							cn2.closeDB();

						}
						catch (NumberFormatException e) {
							Toast.makeText(getApplicationContext(),R.string.toastDatosincorrectos,Toast.LENGTH_SHORT).show();   
						}

					}
					else
						Toast.makeText(getApplicationContext(),R.string.toastCampossinrellenar,Toast.LENGTH_SHORT).show();   
				}
			});

			//boton cancelar - funcion
			btn31.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismissDialog(2);
					removeDialog(2);
					isShown2 = false;
					edtxtporcetaje1.setText("");
					edtxtnota1.setText("");
					edtxtnombreexa1.setText("");
				}
			});

			Dialog dialog2 = new Dialog(this);
			dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog2.setContentView(modifNota);
			dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog2.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			isShown2 = true;
			return dialog2;

		case 3:
			//Confirmaci�n eliminar asignatura
			BaseDatos cn3= new BaseDatos(getApplicationContext());

			ClaseCuatrimestres cuatri2 = cn3.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
			final String nombreAsig = cuatri2.getAsignatura(mPager.getCurrentItem()).getNombre();

			LayoutInflater inflater3 = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View confirmarEliminar = inflater3.inflate(R.layout.confimar_eliminar_act, null);

//			SharedPreferences preferencesD3 = getSharedPreferences("CMpreferences", MODE_PRIVATE);
//	    	int valueD3 = preferencesD3.getInt("fondo seleccionado", 0);
//	    	
//	    	if(valueD3 == 1)
//	    		confirmarEliminar.setBackgroundResource(R.drawable.dialog_background);
//			else
//				confirmarEliminar.setBackgroundResource(R.drawable.dialog_background_alternative);
			
			Typeface tf3 = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

			TextView txtAsigEliminarCabecera = (TextView)confirmarEliminar.findViewById(R.id.textviewAsignatura);
			TextView txtAsigEliminarTexto = (TextView)confirmarEliminar.findViewById(R.id.textviewAsignatura);

			//textviewConfirmacionEliminar
			txtAsigEliminarCabecera.setTypeface(tf3);
			txtAsigEliminarTexto.setTypeface(tf3);
			txtAsigEliminarCabecera.setText(nombreAsig);

			final Button btnSi = (Button)confirmarEliminar.findViewById(R.id.btnconfirmarSI);
			final Button btnNo = (Button)confirmarEliminar.findViewById(R.id.btnconfirmarNO);

			btnSi.setTypeface(tf3);
			btnNo.setTypeface(tf3);

			cn3.closeDB();

			btnSi.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btnSi);
							btnSi.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btnSi);
							btnSi.setShadowLayer(0, 0, 0, Color.WHITE);
							btnSi.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btnSi);
						btnSi.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btnSi);
							btnSi.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btnSi);
							btnSi.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;
					}
					return false;
				}
			});


			btnNo.setOnTouchListener(new View.OnTouchListener() {
				private Rect rect;
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int action = event.getAction();

					switch (action){
					case MotionEvent.ACTION_UP:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btnNo);
							btnNo.setShadowLayer(0, 0, 0, Color.WHITE);
							return false;
						}
						else{
							clickOutTransformation(btnNo);
							btnNo.setShadowLayer(0, 0, 0, Color.WHITE);
							btnNo.performClick();
						}
						return false;

					case MotionEvent.ACTION_DOWN:
						rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
						clickInTransformation(btnNo);
						btnNo.setShadowLayer(3, 0, 0, Color.WHITE);
						return true;

					case MotionEvent.ACTION_MOVE:
						if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
							// User moved outside bounds
							clickOutTransformation(btnNo);
							btnNo.setShadowLayer(0, 0, 0, Color.WHITE);
							return true;
						}
						else{
							clickInTransformation(btnNo);
							btnNo.setShadowLayer(3, 0, 0, Color.WHITE);
						}
						return true;
					}
					return false;
				}
			});

			btnSi.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					BaseDatos cn4= new BaseDatos(getApplicationContext());

					ClaseCuatrimestres cuatri2 = cn4.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
					final String nombreAsig = cuatri2.getAsignatura(mPager.getCurrentItem()).getNombre();

					removeCurrentItem();

					cn4.EliminarAsignatura(cn4.IdAsignatura(nombreAsig));
					cn4.closeDB();

					dismissDialog(3);
					removeDialog(3);
					isShown3 = false;
				}
			});

			btnNo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismissDialog(3);
					removeDialog(3);
					isShown3 = false;
				}
			});

			Dialog dialog3 = new Dialog(this);
			dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog3.setContentView(confirmarEliminar);
			dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			isShown3 = true;
			return dialog3;

		}
		return null;

	}

	@SuppressLint("CutPasteId")
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		BaseDatos cn = new BaseDatos(getApplicationContext());

		switch (id){
		case 0:
			//          String nom2 = spinner.getSelectedItem().toString();
			//          TextView txt1 = (TextView)dialog.findViewById(R.id.textviewCarpeta);
			//          txt1.setText(nom2); 
			
			break;

		case 1: 
			ClaseCuatrimestres cuatri = cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
			String nom = cuatri.getAsignatura(mPager.getCurrentItem()).getNombre();
			double porcentajeusado = cn.SumaPorcentajes(cuatri.getAsignatura(mPager.getCurrentItem()).getId());
			TextView txt3 = (TextView)dialog.findViewById(R.id.textviewAsignatura);
			txt3.setText(nom);
			EditText ed = (EditText)dialog.findViewById(R.id.edittextNombre);
			ed.requestFocus();
			EditText ep = (EditText)dialog.findViewById(R.id.edittextPorcentaje);
			double hint = Math.round((100-porcentajeusado)*100.0)/100.0;
			ep.setHint("Max: "+String.valueOf(hint)+"%");
			EditText en =(EditText)dialog.findViewById(R.id.edittextNota);

			ed.setText("");
			ep.setText("");
			en.setText("");

			break;

		case 2: 
			ClaseCuatrimestres cuatri1 = cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
			ClaseNotas notamodif = cn.getNotaDataBase(IDmodif);
			String nom1 = cuatri1.getAsignatura(mPager.getCurrentItem()).getNombre();
			double porcentajeusado1 = cn.SumaPorcentajes(cuatri1.getAsignatura(mPager.getCurrentItem()).getId());

			TextView txt31 = (TextView)dialog.findViewById(R.id.textviewAsignatura);
			txt31.setText(nom1);
			EditText ed1 = (EditText)dialog.findViewById(R.id.edittextNombre);
			ed1.requestFocus();
			EditText ep1 = (EditText)dialog.findViewById(R.id.edittextPorcentaje);
			double hint2 = Math.round((100-porcentajeusado1+notamodif.getPorcentaje())*100.0)/100.0;

			ep1.setHint("Max: "+String.valueOf(hint2)+"%");
			final EditText edtxtnombreexa1 = (EditText)dialog.findViewById(R.id.edittextNombre);
			final EditText edtxtporcetaje1 = (EditText)dialog.findViewById(R.id.edittextPorcentaje);
			final EditText edtxtnota1 = (EditText)dialog.findViewById(R.id.edittextNota);
			edtxtnombreexa1.setText(notamodif.getEvaluable());

			edtxtporcetaje1.setText(""+notamodif.getPorcentaje());

			edtxtnota1.setText(""+notamodif.getNota());
			break;

		case 3:

			ClaseCuatrimestres cuatri2 = cn.getCuatrimestreDataBase(spinner.getSelectedItem().toString());
			final String nombreAsig = cuatri2.getAsignatura(mPager.getCurrentItem()).getNombre();

			TextView txtAsigEliminar = (TextView)dialog.findViewById(R.id.textviewAsignatura);
			txtAsigEliminar.setText(nombreAsig);
			break;
		}

		cn.closeDB();
	}

}
