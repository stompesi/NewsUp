package org.tworoom.android.newsup.network;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsUpImageLoader {
	private static final String imageUrl = "http://14.63.161.26/";
	private static DisplayImageOptions options;
	
	private static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static void loadImage(ImageView imageView, String path, String color) {
		
		int width = 200;
		int height = 200;
		
	
		
		
		// TODO : 여기 이미지 처리해야한다 
		ShapeDrawable drawable = new ShapeDrawable(new RectShape());
		drawable.setIntrinsicWidth(width);
		drawable.setIntrinsicHeight((int)(height * 0.3));
		drawable.getPaint().setColor(Color.parseColor(color));
		
		
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).resetViewBeforeLoading(true)
				.showImageForEmptyUri(drawable) // 처음 이미지 파일 보여주는 것 
				.showImageOnFail(drawable) // 이미지 로드 실패시 보여주는 것 
				.showImageOnLoading(drawable).build(); // 이미지 로딩중 보여주는것;
		if(path.startsWith("http")) {
			imageLoader.displayImage(path, imageView, options);
		} else {
			imageLoader.displayImage(imageUrl + path, imageView, options);
		}
		
	}
}
