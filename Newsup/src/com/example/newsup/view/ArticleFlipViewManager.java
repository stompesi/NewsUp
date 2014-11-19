package com.example.newsup.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

abstract public class ArticleFlipViewManager {
	protected int offset;
	
	protected ViewFlipper flipper;
	protected int currentChildIndex;
	protected int minChildIndex;
	
	protected Context context;
	
	protected LayoutInflater inflater;

	public ArticleFlipViewManager(Context context, ViewFlipper flipper, int offset) {
		this.offset = offset;
		this.flipper = flipper;
		this.context = context;
		minChildIndex = 0;
		currentChildIndex = -1;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	
	abstract public void removeAllFlipperItem();
	
	
	public void removeFlipperItem() {
		currentChildIndex--;
		flipper.removeViewAt(minChildIndex);
	}

	// show child
	public void display(int checkWhichChild) {
		currentChildIndex = checkWhichChild;
		flipper.setDisplayedChild(currentChildIndex);
	}
	
	public void setAnimation(int in, int out) {
		Animation inAnimation = AnimationUtils.loadAnimation(context, in);
		Animation outAnimation = AnimationUtils.loadAnimation(context, out);
		flipper.setInAnimation(inAnimation);
		flipper.setOutAnimation(outAnimation);
	}
	
	protected void addView(View view) {
		currentChildIndex++;
		flipper.addView(view, minChildIndex);
	}
	
	public void setFlipperTouchListener(OnTouchListener listener) {
		flipper.setOnTouchListener(listener);
		
	}
	
	public ViewFlipper getFlipper() {
		return flipper;
	}
	
	public int getChildChount(){
		return flipper.getChildCount() - offset;
	}
	
	public View getChildAt(int index) {
		return flipper.getChildAt(index);
	}
	
	public int getCurrentChildIndex() {
		return currentChildIndex;
	}
	
	public boolean isErrorView() {
		return getChildChount() == 1 || getCurrentChildIndex() == 0;
	}
	abstract public void inArticleDetail(int articleId);
	abstract public void outArticleDetail();
	abstract public boolean upDownSwipe(int page);
	
}
