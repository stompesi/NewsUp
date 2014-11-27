package org.tworoom.android.newsup.text;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import org.tworoom.android.newsup.R;

public class StyledTextView extends TextView {

	private static Typeface typeface;

	public StyledTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		applyTypeface(context, attrs);
	}

	public StyledTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyTypeface(context, attrs);
	}

	public StyledTextView(Context context) {
		super(context);
	}

	private void applyTypeface(Context context, AttributeSet attrs){
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
		String typefaceName = arr.getString(R.styleable.StyledTextView_typeface);
		
		try{
			if(typeface ==null){
				typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
			}
			setTypeface(typeface);
		}catch(Exception e){
			e.printStackTrace();
		}   
	}
}