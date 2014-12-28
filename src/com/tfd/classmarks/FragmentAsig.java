package com.tfd.classmarks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import mysql.BaseDatos;
import mysql.ClaseAsignaturas;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class FragmentAsig extends Fragment {

	private int EliminarID;
	public int ModifID;
	private static final int ID_EDIT = 1;
	private static final int ID_ELIMINAR = 2;
//	private boolean isAnimating=false;
//	private boolean didSlideOut=false;
	public String mText, mTextminmax;
	public TextView txtnotaexfin, txttotal, txtmedia, txtsobre, txtaadir;
	public ListView lv;
	public Principal prin;
	public ListAdapter adap;
	public ArrayList<Item> items;
	public View itemselected;
	public QuickAction quickAction;
	private View fragment;

	HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		items = new ArrayList<Item>();

		final View footer = ((LayoutInflater) getActivity()
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listview_forma_footer, null, false);

		footer.setOnTouchListener(new View.OnTouchListener() {
			private Rect rect;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				switch (action) {

				case MotionEvent.ACTION_UP:
					if (!rect.contains(v.getLeft() + (int) event.getX(),
							v.getTop() + (int) event.getY())) {
						// User moved outside bounds
						((Principal) getActivity())
								.clickOutTransformation(footer);

						return false;
					} else {
						((Principal) getActivity())
								.clickOutTransformation(footer);
						v.performClick();

					}
					return false;

				case MotionEvent.ACTION_DOWN:
					rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v
							.getBottom());
					((Principal) getActivity()).clickInTransformation(footer);

					return true;

				case MotionEvent.ACTION_MOVE:
					if (!rect.contains(v.getLeft() + (int) event.getX(),
							v.getTop() + (int) event.getY())) {
						// User moved outside bounds
						((Principal) getActivity())
								.clickOutTransformation(footer);
						return true;
					} else {
						((Principal) getActivity())
								.clickInTransformation(footer);
					}
					return true;
				}

				return false;
			}
		});

		footer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BaseDatos cn = new BaseDatos(getActivity()
						.getApplicationContext());
				double txtsob = Math.round(cn.SumaPorcentajes(cn
						.IdAsignatura(mText)) * 100.0) / 100.0;
				Log.d("addmark_txtsob", "" + txtsob);
				if (txtsob < 100) {
					addMark();
				} else {
					Toast.makeText(getActivity().getApplicationContext(),
							R.string.toastMaxPorcentaje, Toast.LENGTH_LONG)
							.show();

				}
				cn.closeDB();
			}
		});

		final BaseDatos cn = new BaseDatos(this.getActivity());
		SQLiteDatabase db = cn.getReadableDatabase();

		final ClaseAsignaturas asign = cn.getAsignaturaDataBase(mText);
		
		fragment = inflater.inflate(R.layout.asignatura_frag, container,
				false);

		// Configuracion de objetos
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"Roboto-Light.ttf");

		// TextView nombre de la asignatura
		final TextView txt = (TextView) fragment.findViewById(R.id.textViewAnd);
		txt.setText(mText);
		
		//TextView valor minimo y maximo de la asignatura
		TextView txt_minmax = (TextView)fragment.findViewById(R.id.textView_asig_minmax);
		txt_minmax.setText("(Mín: "+asign.getMin()+" - Máx: "+asign.getMax()+")");
		
		txt_minmax.setTypeface(tf);
		txt.setTypeface(tf);
		
//		txt.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				fragment_transactions();
//				
//			}
//		});

		// Codigo para crear y escalar el indicardor verde
		Drawable indic = getActivity().getResources().getDrawable(
				R.drawable.indicador_verde_x);
		Bitmap bm = ((BitmapDrawable) indic).getBitmap();
		int dim = (int) getResources().getDimension(R.dimen.iconos_asignatura);
		final Drawable indicator = new BitmapDrawable(getResources(),
				Bitmap.createScaledBitmap(bm, dim, dim, true));

		// Codigo para crear y escalar el indicardor rojo
		Drawable indicR = getActivity().getResources().getDrawable(
				R.drawable.indicador_rojo_x);
		Bitmap bm1 = ((BitmapDrawable) indicR).getBitmap();
		final Drawable indicatorR = new BitmapDrawable(getResources(),
				Bitmap.createScaledBitmap(bm1, dim, dim, true));

		// Codigo para crear y escalar el indicardor nulo
		Drawable indicN = getActivity().getResources().getDrawable(
				R.drawable.indicador_nulo_x);
		Bitmap bm2 = ((BitmapDrawable) indicN).getBitmap();
		final Drawable indicatorN = new BitmapDrawable(getResources(),
				Bitmap.createScaledBitmap(bm2, dim, dim, true));

		ImageView x = (ImageView) fragment.findViewById(R.id.imageViewEliminar);

		x.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				((Principal) getActivity()).showDialog(3);
			}
		});

		final TextView txttotal = (TextView) fragment
				.findViewById(R.id.TVtotal);
		final TextView txtmedia = (TextView) fragment
				.findViewById(R.id.TVmedia);
		final TextView txtnotaneeded = (TextView) fragment
				.findViewById(R.id.TVnotaneeded);

		txttotal.setTypeface(tf);
		txtmedia.setTypeface(tf);
		txtnotaneeded.setTypeface(tf);

		/*
		 * 
		 * EMPIEZA EL CODIGO DEL BOCADILLO (SPEECH BUBBLE)
		 */

		ActionItem editItem = new ActionItem(ID_EDIT, null, getResources()
				.getDrawable(R.drawable.ic_action_edit));
		ActionItem eliminarItem = new ActionItem(ID_ELIMINAR, null,
				getResources().getDrawable(R.drawable.ic_action_discard));

		// Crea un objeto QuickAction y determina que su orientacion sea
		// horizontal
		quickAction = new QuickAction(getActivity(), QuickAction.HORIZONTAL);

		// add action items into QuickAction
		quickAction.addActionItem(editItem);
		quickAction.addActionItem(eliminarItem);

		// Set listener for action item clicked
		quickAction
				.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
					@Override
					public void onItemClick(QuickAction source, int pos,
							int actionId) {
						BaseDatos cn = new BaseDatos(getActivity()
								.getApplicationContext());
						// here we can filter which action item was clicked with
						// pos or actionId parameter
						if (actionId == ID_EDIT) {

							((Principal) getActivity()).setIDmodif(cn
									.IdNota(items.get(EliminarID).getId()));

							getActivity().showDialog(2);
							adap.notifyDataSetChanged();
							itemselected.clearFocus();

						} else if (actionId == ID_ELIMINAR) {
							cn.EliminarNota(cn.IdNota(items.get(EliminarID)
									.getId()));

							double txtsob = cn.SumaPorcentajes(cn
									.IdAsignatura(mText));
							Log.d("txtsob", "" + txtsob);
							double txttotsinRound = cn.TotalProducto(cn
									.IdAsignatura(mText));
							Log.d("txttotsinRound", "" + txttotsinRound);

							double txttotRound = Math
									.round(txttotsinRound * 100.0) / 100.0;
							BigDecimal txttotBig = new BigDecimal(""
									+ txttotRound);
							double txttot = txttotBig.doubleValue();

							final double txtmedRound = Math
									.round((txttotsinRound / (txtsob / 100)) * 100.0) / 100.0;
							BigDecimal txtmedbig = new BigDecimal(""
									+ txtmedRound);
							final double txtmed = txtmedbig.doubleValue();

							double txtporrest = Math
									.round((100 - txtsob) * 100.0) / 100.0;

							double notanece = 0;
							if (txttot < asign.getMin()) {
								notanece = Math
										.round(((asign.getMin() - txttotsinRound) / ((100 - txtsob) / 100)) * 100.0) / 100.0;

							}

							ObjectAnimator anim = ObjectAnimator.ofFloat(itemselected, View.ALPHA, 0);
							anim.setDuration(1000);
							anim.addListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									items.remove(items.get(EliminarID));
									if (items.isEmpty() == true) {
										txt.setCompoundDrawablesWithIntrinsicBounds(
												indicatorN, null, null, null);
									} else {

										if (txtmed >= asign.getMin()) {
											txt.setCompoundDrawablesWithIntrinsicBounds(
													indicator, null, null, null);
										} else {

											txt.setCompoundDrawablesWithIntrinsicBounds(
													indicatorR, null, null,
													null);
										}
									}
									adap.notifyDataSetChanged();

									itemselected.setAlpha(1);
								}
							});
							anim.start();

							txttotal.setText(getString(R.string.Total) + " "
									+ txttot);
							txtmedia.setText(getString(R.string.Media) + " "
									+ txtmed);
							txtnotaneeded
									.setText(getString(R.string.recuadroo)
											+ " " + notanece + " ("
											+ txtporrest + "%)");

							if (notanece < asign.getMax() && notanece > 0) {
								txtnotaneeded
										.setText(getString(R.string.recuadroo)
												+ " " + notanece + " ("
												+ txtporrest + "%)");
							}
							itemselected.clearFocus();
						}
						adap.notifyDataSetChanged();
						cn.closeDB();
					}
				});

		// Mostramos los datos recogidos de la base de datos
		Log.d("LOG CURSOR DATA-BASE ERROR 2", "Valor asignatura: " + mText);
		ClaseAsignaturas Asignatura = cn.getAsignaturaDataBase(mText);
		int i = 0;

		while (i < Asignatura.getLon()) {
			items.add(new Item(Asignatura.getNotas(i).getId(), Asignatura
					.getNotas(i).getEvaluable(), Double.toString(Asignatura
					.getNotas(i).getPorcentaje()) + " %", Double
					.toString((Asignatura.getNotas(i).getNota()))));
			i++;
		}

		TextView tvcrearnota = (TextView) footer.findViewById(R.id.tvanadir);
		tvcrearnota.setTypeface(tf);
		// Fin de la configuracion de objetos

		lv = (ListView) fragment.findViewById(R.id.listView1);
		lv.addFooterView(footer);
		adap = new ListAdapter(this.getActivity(), R.layout.listview_forma,
				items);
		lv.setAdapter(adap);

		lv.setLongClickable(true);

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				arg1.setSelected(true);
				EliminarID = arg2;
				quickAction.show(arg1);
				itemselected = arg1;

				return true;
			}
		});

		double txtsob = cn.SumaPorcentajes(cn.IdAsignatura(mText));

		double txttotsinRound = cn.TotalProducto(cn.IdAsignatura(mText));
		double txttotRound = Math.round(txttotsinRound * 100.0) / 100.0;
		BigDecimal txttotBig = new BigDecimal("" + txttotRound);
		double txttot = txttotBig.doubleValue();

		double txtmedRound = Math
				.round((txttotsinRound / (txtsob / 100)) * 100.0) / 100.0;
		BigDecimal txtmedbig = new BigDecimal("" + txtmedRound);
		final double txtmed = txtmedbig.doubleValue();

		double txtporrest = Math.round((100 - txtsob) * 100.0) / 100.0;
		double notanece = 0;
		if (txttot < asign.getMin()) {
			notanece = Math
					.round(((asign.getMin() - txttotsinRound) / ((100 - txtsob) / 100)) * 100.0) / 100.0;

		}
		if (items.isEmpty() == true) {
			txt.setCompoundDrawablesWithIntrinsicBounds(indicatorN, null, null,
					null);
		} else {

			if (txtmed >= asign.getMin()) {
				txt.setCompoundDrawablesWithIntrinsicBounds(indicator, null,
						null, null);
			} else {

				txt.setCompoundDrawablesWithIntrinsicBounds(indicatorR, null,
						null, null);
			}
		}
		txttotal.setText(getString(R.string.Total) + " " + txttot);
		txtmedia.setText(getString(R.string.Media) + " " + txtmed);
		txtnotaneeded.setText(getString(R.string.recuadroo) + " " + notanece
				+ " (" + txtporrest + "%)");
		if (notanece < asign.getMax() && notanece > 0) {
			txtnotaneeded.setText(getString(R.string.recuadroo) + " "
					+ notanece + " (" + txtporrest + "%)");
		} else if (notanece > asign.getMax()) {

			txtnotaneeded.setText(getString(R.string.recuadroo) + " +"+asign.getMax()+" ("
					+ txtporrest + " %)");

		}
		cn.closeDB();
		db.close();
		return fragment;
	}

//	protected void fragment_transactions() {
//		
//		// TODO Auto-generated method stub
//		if(isAnimating){
//			return;
//		}
//		isAnimating=true;
//		
//		if(didSlideOut){
//			didSlideOut=false;
//			getFragmentManager().popBackStack();
//		}
//		
// 		AnimatorListener listener = new AnimatorListenerAdapter() {
//			@Override
//			public void onAnimationEnd(Animator arg0){
//				android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
//				transaction.setCustomAnimations(R.anim.grow_from_bottom, R.anim.pump_bottom);
//				transaction.add(, arg1)
//			}
//			
//		};
//		
//	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	public void dismPopup() {
		quickAction.dismiss();
	}

	public FragmentAsig() {
	}
	
	public FragmentAsig(String text) {
		this.mText = text;
	}
	
	public String NombreAsig() {
		return mText;
	}

	public void setModifID(int id) {
		this.EliminarID = id;
	}

	public int getModifID() {

		return EliminarID;
	}

	public void addMark() {
		this.getActivity().showDialog(1);
	}

}
