package com.tworoom.android.newsup.view.structure;

import android.content.Context;

import com.tworoom.android.newsup.R;

public class LayoutInfo {
	
	private int availableTotalWidth, availableTotalHeight;
	
	private int firstPageAbailableHeight;
	
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
		
		
		availableTotalWidth = (int)context.getResources().getDimension(R.dimen.divice_width) - (textViewPadding * 3); 
		
		availableTotalHeight = (int)context.getResources().getDimension(R.dimen.view_article_detail_content_height) - (textViewPadding * 2);
		
		firstPageAbailableHeight = (int)context.getResources().getDimension(R.dimen.view_article_detail_first_page_available_height) - (textViewPadding * 2);
	}
	
	
	public int getAvailableTotalWidth() {
		return availableTotalWidth;
	}
	
	public int getAvailableTotalHeight() {
		return availableTotalHeight;
	}
	
	public int getFirstPageAvailableHeight() {
		return firstPageAbailableHeight;
	}
	
	public int getTextViewPadding() {
		return textViewPadding;
	}
}
