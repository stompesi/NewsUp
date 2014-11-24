package com.tworoom.android.newsup.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tworoom.android.newsup.R;


public class StyledLastArticleTextView extends TextView {

	private static Typeface last_article_typeface;

	public StyledLastArticleTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		applyTypeface(context, attrs);
	}

	public StyledLastArticleTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		applyTypeface(context, attrs);
	}

	public StyledLastArticleTextView(Context context) {
		super(context);
	}

	private void applyTypeface(Context context, AttributeSet attrs){
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
		String typefaceName = arr.getString(R.styleable.StyledTextView_typeface);
		
		try{
			if(last_article_typeface==null){
				last_article_typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
			}
			setTypeface(last_article_typeface);
		}catch(Exception e){
			e.printStackTrace();
		}   
	}
}
