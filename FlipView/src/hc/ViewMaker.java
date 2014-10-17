package hc;


import image.handler.BitmapCache;

import java.util.ArrayList;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.example.flipview.R;

public class ViewMaker {
	
	private static LayoutInflater inflater;
	
	private String content;
	private ImageInfo imageInfo;
	private Context context;
	private boolean isImageText;
	
	
	
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	
	public static ArrayList<View> getViewList(Context context, ArrayList<Object> resultList) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int layoutHeight = metrics.heightPixels;
		
		ArrayList<View> viewList = new ArrayList<View>();
		View view = inflater.inflate(R.layout.text_image, null);
		boolean isFirstText = true;
		
		
		ImageInfo imageInfo = null;
		String content = null;
		int currentHeight = 0;
		for(int i = 0 ; i < resultList.size() ; i++) {
			Object object = resultList.get(i);
			
			if( object instanceof ImageInfo) {
				imageInfo = (ImageInfo) object;
				currentHeight += imageInfo.getImage_height();
				if(currentHeight == layoutHeight || !isFirstText) {
					ViewMaker viewMaker = new ViewMaker(context, imageInfo, content, false);
					viewList.add(viewMaker.getView());
					currentHeight = 0;
					isFirstText = true;
				} else {
					isFirstText = false;
				}
				
			}else if(object instanceof String) {
				content = (String) object;
				currentHeight += Integer.parseInt(content.split(":")[0]);
				
				if(currentHeight == layoutHeight && isFirstText) {
					ViewMaker viewMaker = new ViewMaker(context, content);
					viewList.add(viewMaker.getView());
					currentHeight = 0;
				} else if(currentHeight == layoutHeight) {
					ViewMaker viewMaker = new ViewMaker(context, imageInfo, content, true);
					viewList.add(viewMaker.getView());
					currentHeight = 0;
					isFirstText = true;
				} else {
					isFirstText = false;
				}
			}
		}
		return viewList;
	}
	
	private ViewMaker(Context context, String content) {
		this.content = content;
		this.imageInfo = null;
		this.context = context;
		
	}

	private ViewMaker(Context context, ImageInfo imageInfo, String content, boolean isImageText) {
		this.content = content;
		this.imageInfo = imageInfo;
		this.context = context;		
		this.isImageText = isImageText;
	}
	
	private View getView(){
		if(imageInfo == null) {
			return makeTextView();
		} else {
			return makeTextImageView();
		}
	}
	
	private View makeTextView() {
		View view = inflater.inflate(R.layout.text, null);
		int height = Integer.parseInt(content.split(":")[0]);
		TextView textView = (TextView) view.findViewById(R.id.content);
		// TODO : DP 변환값 변경
		textView.setHeight(height * 2);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size));
		textView.setText(content.split(":")[1]);
		
		return view;
	}
	
	private View makeTextImageView() {
		View view;
		if(isImageText) {
			view = inflater.inflate(R.layout.image_text, null);
		} else {
			view = inflater.inflate(R.layout.text_image, null);
		}
		
		int height = Integer.parseInt(content.split(":")[0]);
		TextView textView = (TextView) view.findViewById(R.id.content);
		// TODO : DP 변환값 변경
		textView.setHeight(height * 2);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size));
		textView.setText(content.split(":")[1]);
		
		// TODO : 이미지 로드되기 전에 유사이미지 로
		
		NetworkImageView imageView = (NetworkImageView) view.findViewById(R.id.image);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(imageInfo.getImage_height() * 2, 333*2));
		imageView.setImageUrl(imageInfo.getImageURL(), getImageLoader());
		
		return view;
	}
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}

		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		getRequestQueue();
		if (mImageLoader == null) {
			mImageLoader = new ImageLoader(this.mRequestQueue, new BitmapCache());
		}
		
		return this.mImageLoader;
	}
	
	
}
