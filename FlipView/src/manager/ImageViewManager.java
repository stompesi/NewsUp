package manager;

import android.widget.ImageView;

import com.example.flipview.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageViewManager {
	private static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
			.cacheOnDisc(true).resetViewBeforeLoading(true)
			.showImageForEmptyUri(R.drawable.ic_launcher) // 처음 이미지 파일 보여주는 것 
			.showImageOnFail(R.drawable.ic_launcher) // 이미지 로드 실패시 보여주는 것 
			.showImageOnLoading(R.drawable.ic_launcher).build(); // 이미지 로딩중 보여주는것

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	public static void loadImage(ImageView imageView, String url) {
		imageLoader.displayImage(url, imageView, options);
	}
}
