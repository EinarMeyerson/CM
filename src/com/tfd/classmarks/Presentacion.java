package com.tfd.classmarks;
 
import java.util.Timer;
import java.util.TimerTask;
 
import mysql.BaseDatos;
import mysql.ClaseCuatrimestres;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
 
public class Presentacion extends Activity {
 
    private Timer time = new Timer();
    private Intent in;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentacion_act);
 
        Log.d("DENSITY",""+getResources().getDisplayMetrics().density);
         
        TextView txt = (TextView) findViewById(R.id.TVtfd);
        ImageView logo = (ImageView)findViewById(R.id.lay_class);
 
        Typeface font = Typeface.createFromAsset(getAssets(), "TELE2.ttf");
 
        txt.setTypeface(font);
        txt.setTextColor(Color.argb(150, 0, 0, 0));
 
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.title_effect);
        logo.startAnimation(anim);
 
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.alpha_effect);
        txt.startAnimation(anim2);
 
        TimerTask tasko = new TimerTask() {
 
            @Override
            public void run() {
                in = new Intent(getApplicationContext(), Principal.class);
 
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
                overridePendingTransition(0, 0);
                 
                 
            }
        };  
        time.schedule(tasko, 2550);
         
 
    }
 
 
 
 
}