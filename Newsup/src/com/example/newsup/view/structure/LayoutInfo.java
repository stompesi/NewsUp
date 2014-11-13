package com.example.newsup.view.structure;

import android.content.Context;

import com.example.newsup.R;

public class LayoutInfo {
	
	private int availableTotalWidth, availableTotalHeight;
	
	private int firstPageHeight;
	private int bottomHeight;
	
	private int textViewPadding;
	
	private static LayoutInfo layoutInfo;
	private LayoutInfo() {}
	
	
	
	public static LayoutInfo getInstance() {
		if(layoutInfo == null) {
			layoutInfo = new LayoutInfo();
		}
		
		return layoutInfo;
	}
	
	public void calLayoutInfo(Context context) {
		
		textViewPadding = (int)context.getResources().getDimension(R.dimen.view_article_detail_content_padding); 
		
		
		availableTotalWidth = (int)context.getResources().getDimension(R.dimen.divice_width) - (textViewPadding * 2); 
		
		availableTotalHeight = (int)context.getResources().getDimension(R.dimen.view_article_detail_content_height) - (textViewPadding * 2);
		
		firstPageHeight = Math.round((float)(availableTotalHeight * 0.4));
	}
	
	
	public int getAvailableTotalWidth() {
		return availableTotalWidth;
	}
	
	public int getAvailableTotalHeight() {
		return availableTotalHeight;
	}
	
	public int getFirstPageHeight() {
		return firstPageHeight;
	}
	
	public int getTextViewPadding() {
		return textViewPadding;
	}
}
