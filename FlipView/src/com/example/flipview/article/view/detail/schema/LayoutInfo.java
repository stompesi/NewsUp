package com.example.flipview.article.view.detail.schema;

public class LayoutInfo {
	
	private int availableTotalWidth, availableTotalHeight;
	
	private int firstPageHeight;
	private int bottomHeight;
	
	private int availableTextViewWidth;
	
	private int textViewPadding;
	
	private static LayoutInfo layoutInfo;
	private LayoutInfo() {}
	
	
	
	public static LayoutInfo getInstance() {
		if(layoutInfo == null) {
			layoutInfo = new LayoutInfo();
		}
		
		return layoutInfo;
	}
	
	public void calLayoutInfo(int width, int height) {
		availableTotalWidth = width;
		
		textViewPadding = Math.round((float)(width * 0.03)); 
		availableTextViewWidth = width - (textViewPadding * 2); 
		
		bottomHeight =  Math.round((float)(height * 0.2));
		availableTotalHeight = height - bottomHeight;
		
		firstPageHeight = Math.round((float)(height * 0.4));
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
	
	public int getBottomHeight() {
		return bottomHeight;
	}
	
	public int getAvailableTextViewWidth() {
		return availableTextViewWidth;
	}
	
	public int getTextViewPadding() {
		return textViewPadding;
	}
}
