package com.tfd.classmarks;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<Item>{

	Activity activity;
	ArrayList<Item> items;
    HashMap<Item, Integer> mIdMap = new HashMap<Item, Integer>();

	
//	public ListAdapter(Activity activity, ArrayList<Item> items){
//		this.activity = activity;
//		this.items = items;
//	}
//	
//	
	 public ListAdapter(Activity activity, int textViewResourceId,
			 ArrayList<Item> objects) {
	        super(activity, textViewResourceId, objects);
	        this.activity = activity;
	        this.items = objects;
	        for (int i = 0; i < objects.size(); ++i) {
	            mIdMap.put(objects.get(i), i);
	        }
	    }
	 
//	
//	@Override
//	public int getCount() {
//		return items.size();
//		
//	}
//	
//	@Override
//	public Item getItem(int position) {
//		return items.get(position);
//		
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return items.get(position).getId();
//		
//	}

    
    @Override
    public long getItemId(int position) {
        Item item = getItem(position);
//        return items.get(position).getId();
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    
	@Override
	public View getView(int position, View contentView, ViewGroup parent) {
		View vi = contentView;
//        View vi = super.getView(position, contentView, parent);

		if(contentView == null){
			LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.listview_forma, null);
		}
		
		
		Item item = items.get(position);
		
		TextView evaluable = (TextView)vi.findViewById(R.id.tveva);
		Typeface tf = Typeface.createFromAsset(activity.getAssets(), "Roboto-Light.ttf");
		evaluable.setTypeface(tf );
		evaluable.setText(item.getEvaluable());
		
		TextView porcentaje = (TextView)vi.findViewById(R.id.tvpor);
		porcentaje.setTypeface(tf);
		porcentaje.setText(item.getPorcentaje());
		
		TextView nota = (TextView)vi.findViewById(R.id.tvnot);
		nota.setTypeface(tf);
		nota.setText(item.getNota());
		
//		Animation animation = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in);
//	    vi.startAnimation(animation);
		
		return vi;
		
	}

}
