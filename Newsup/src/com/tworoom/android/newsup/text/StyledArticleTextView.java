package com.tworoom.android.newsup.text;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tworoom.android.newsup.R;

public class StyledArticleTextView extends TextView {

	private static Typeface article_typeface;

	public StyledArticleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		applyTypeface(context, attrs);
	}

	public StyledArticleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyTypeface(context, attrs);
	}

	public StyledArticleTextView(Context context) {
		super(context);
	}

	private void applyTypeface(Context context, AttributeSet attrs){
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
		String typefaceName = arr.getString(R.styleable.StyledTextView_typeface);
		
		try{
			if(article_typeface==null){
				article_typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
			}
			setTypeface(article_typeface);
		}catch(Exception e){
			e.printStackTrace();
		}   
	}
}