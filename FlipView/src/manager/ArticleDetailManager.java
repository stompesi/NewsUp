package manager;

import hc.ArticleDetailPage;
import hc.ContentSplitter;
import hc.ImageInfo;
import hc.Splitter;

import java.util.ArrayList;

import network.Network;
import ArticleReadInfo.ArticleReadInfo;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import application.NewsUpApp;

import com.example.flipview.R;

import database.Article;

public class ArticleDetailManager extends ArticleFlipViewManager {
	private int viewHeight;
	private int viewWidht;
	private int viewPadding;
	
	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;
	
	public ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
		
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		
		// TODO : viewHeight, viewWidth를 변경해야한다  
		this.context = context;
		this.viewHeight = metrics.heightPixels - ((int) context.getResources().getDimension(R.dimen.layout_padding_top));
		
		this.viewWidht = (int) context.getResources().getDimension(R.dimen.layout_width);//metrics.widthPixels;
	}
	

	
	public void getArticleDetail(int articleId) {
		View view;
		String str;
		Article article;
		Splitter splitter;
		TextPaint textPaint;
		ArrayList<Object> list;
		ContentSplitter contentSplitter;
		ArrayList<ArticleDetailPage> articleDetailPageList;
		
		removeAllFlipperItem();
		article = Article.getArticle(articleId);
		list = new ArrayList<Object>();
		str = article.getBody();
		contentSplitter = new ContentSplitter(viewWidht);
		list  = (ArrayList<Object>) contentSplitter.split(str);
		
		// article content 추출 
		textPaint = new TextPaint();
		int textSize = NewsUpApp.getInstance().getTextSize();
		textPaint.setTextSize(context.getResources().getDimension(textSize));
		
		splitter = new Splitter(textPaint, viewHeight, viewWidht - 20);
		
		// TODO: 시작페이지를 만들어야한다 (상세기사 첫화면)
		articleDetailPageList = (ArrayList<ArticleDetailPage>) splitter.getList(list);
		for (int i = 0 ; i < articleDetailPageList.size() ; i++) {
			view = viewMaker(articleDetailPageList.get(i));
			addView(view);
		}
	}
	
	private View viewMaker(ArticleDetailPage articleDetailPage) {
		ArrayList<Object> articleContent;
		LinearLayout view, layout;
		Object object;
		
		articleContent = (ArrayList<Object>) articleDetailPage.getContent();
		layout = (LinearLayout) inflater.inflate(R.layout.view_article_detail, null);
		view = (LinearLayout)(layout).findViewById(R.id.viewArticleDetail);
		
		
		for (int i = 0 ; i < articleContent.size() ; i++) {
			object = articleContent.get(i);

			// 이미지 처리
			if (object instanceof ImageInfo) {
				ImageInfo imageInfo;
				ImageView imageView;
				
				imageInfo = (ImageInfo) object;
				imageView = new ImageView(context);
				
				view.addView(imageView);
				
				imageView.getLayoutParams().height = imageInfo.getHeight() - 50;
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

				ImageViewManager.loadImage(imageView, imageInfo.getURL());
			}
			// 텍스트 처리
			else {
				String text, save;
				TextView textView;
				Paint mPaint;
				int end;
				String[] textArr;
				
				text = (String) object;
				save = "";
				
				Log.e("text", text);
				textView = new TextView(context);
				mPaint = textView.getPaint();
				int textSize = NewsUpApp.getInstance().getTextSize();
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(textSize));
				ApplyFont(context,textView);
				
				end = 0;
				textArr = text.split("\n");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() == 0)
						textArr[j] = " ";
					do {
						// 글자가 width 보다 넘어가는지 체크
						end = mPaint.breakText(textArr[j], true, viewWidht, null);
						if (end > 0) {
							// 자른 문자열을 문자열 배열에 담아 놓는다.
							save += textArr[j].substring(0, end) + "\n";
							// 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
							textArr[j] = textArr[j].substring(end);
						}
					} while (end > 0);
					
				}
				textView.setText(save);
				view.addView(textView);
			}
		}
		return layout;
	}
	
	private void ApplyFont(Context context, TextView tv){
		Typeface face = Typeface.createFromAsset(context.getAssets(),"SJSoju1.ttf.mp3");
		tv.setTypeface(face);
	}
	
	@Override
	public void outArticleDetail() {
		setReadTime();
		if(Network.isNetworkState(context)){
			Network.getInstance().updateUserLog(articleReadInfo);
		}
	}
	
	@Override
	public boolean upDownSwipe(int increase){
		int checkIndex = currentChildIndex + increase;
		if (checkIndex >= getChildChount() || checkIndex < minChildIndex) {
			return false;
		}
		setReadTime();
		display(checkIndex);
		return true;
	}
	
	private void setReadTime() {
		int index = getChildChount() - (currentChildIndex + 1);
		articleReadInfo.setReadTime(index, getTimestamp() - pageReadStartTime);
		pageReadStartTime = getTimestamp();
	}
	
	@Override
	public void inArticleDetail(int articleId) {
		getArticleDetail(articleId);
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime, getChildChount());
		display(getChildChount() - 1);
	}
	
	public void changeTextSize(int articleId) {
		removeAllFlipperItem();
		getArticleDetail(articleId);
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime, getChildChount());
		display(getChildChount() - 1);
	}
	
	private int getTimestamp() {
		return (int)(System.currentTimeMillis() / 1000L);
	}
}
