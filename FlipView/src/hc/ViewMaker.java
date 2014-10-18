package hc;


import java.util.ArrayList;

import manager.ImageViewManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.flipview.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewMaker {
	
	private static final float DEFAULT_HDIP_DENSITY_SCALE = 1.5f;
	
	private static LayoutInflater inflater;
	
	private String content;
	private ImageInfo imageInfo;
	private Context context;
	private boolean isImageText;
	
	private static int layoutHeight;
	private static int layoutWidth;
	private static float density;
	private static int dpi;
	
	public static ArrayList<View> getViewList(Context context, ArrayList<Object> resultList) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		layoutHeight = metrics.heightPixels;
		layoutWidth = metrics.widthPixels;
		density =  metrics.density;
		dpi = metrics.densityDpi;
		ArrayList<View> viewList = new ArrayList<View>();
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
				} else if(i == (resultList.size() - 1)) {
//					ViewMaker viewMaker = new ViewMaker(context, imageInfo, content);
//					viewList.add(viewMaker.getView());
				} else {
					isFirstText = false;
				}
				
			}else if(object instanceof String) {
				content = (String) object;
				currentHeight += Integer.parseInt(content.split(":")[0]);
				
				if(currentHeight == layoutHeight && isFirstText
						|| i == (resultList.size() - 1)) {
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
		TextView textView;
		ImageView imageView;
		
		int height = Integer.parseInt(content.split(":")[0]);
		if(isImageText) {
			view = inflater.inflate(R.layout.image_text, null);
			textView = (TextView) view.findViewById(R.id.content);
			imageView = (ImageView) view.findViewById(R.id.image);
			LayoutParams textViewParams = (LayoutParams) textView.getLayoutParams();
			textViewParams.weight = (float) imageInfo.getImage_height() / layoutHeight;
			textView.setLayoutParams(textViewParams);
			
			LayoutParams imageViewParams = (LayoutParams) imageView.getLayoutParams();
			imageViewParams.weight = (float) height / layoutHeight;
			imageView.setLayoutParams(imageViewParams);
		} else {
			view = inflater.inflate(R.layout.text_image, null);
			textView = (TextView) view.findViewById(R.id.content);
			imageView = (ImageView) view.findViewById(R.id.image);
		}
		
		textView.setHeight((int)(height / DEFAULT_HDIP_DENSITY_SCALE * density));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_size));
		textView.setText(content.split(":")[1]);
		
		ImageViewManager.loadImage(imageView, imageInfo.getImageURL());
		
		return view;
	}
	
	
	
}

