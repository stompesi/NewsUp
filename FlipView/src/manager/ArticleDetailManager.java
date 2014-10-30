package manager;

import hc.ArticleDetailPage;
import hc.CTextView;
import hc.FirstSplitter;
import hc.ImageInfo;
import hc.Splitter;

import java.util.ArrayList;

import network.Network;
import setting.RbPreference;
import ArticleReadInfo.ArticleReadInfo;
import activity.SettingActivity;
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

import com.example.flipview.R;

import database.Article;

public class ArticleDetailManager extends ArticleFlipViewManager {
	private int View_height;
	private int View_widht;
	private ArrayList<Object> list;
	private Context context;
	
	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;
	
	public ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
		this.context = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		View_height = metrics.heightPixels;
		View_widht= metrics.widthPixels;
	}
	
	// TODO : 상세 기사 검색
	public void getArticleDetail(int articleId) {
		Article article = Article.getArticle(articleId);
		removeAllFlipperItem();
		list = new ArrayList<Object>();
		FirstSplitter firstSplitter = new FirstSplitter();
		String str = article.getBody();
		list  = firstSplitter.FirstSplitter(str);
		// TODO: 시작페이지를 만들어야한다 (상세기사 첫화면)
		
//		ArrayList<Object> result_lits = getList(list);
		
		TextPaint textPaint = new TextPaint();
		textPaint_size(textPaint);
		Splitter splitter = new Splitter(textPaint, View_height - 300, View_widht);
		
		
		Log.e("aaaa", "aaaa");
		ArrayList<ArticleDetailPage> articleDetailPageList = (ArrayList<ArticleDetailPage>) splitter.getList(list);
		for (int i = 0 ; i < articleDetailPageList.size() ; i++) {
			View view = viewMaker(articleDetailPageList.get(i));
			addView(view);
		}
		
		Log.e("aaa","aaa");
//		ArrayList<Object> result_lits= getResult_List(list);
//		ArrayList<View> viewList = ViewMaker.getViewList(context, result_lits);
//		for(int i = 0 ; i < viewList.size() ; i++) {
//			addView(viewList.get(i));
//		}
	}
	
	private View viewMaker(ArticleDetailPage articleDetailPage) {
		ArrayList<Object> articleContent = (ArrayList<Object>) articleDetailPage.getContent();
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.view_article_detail, null);
		
		for (int i = 0 ; i < articleContent.size() ; i++) {
			Object object = articleContent.get(i);

			// 이미지 처리
			if (object instanceof ImageInfo) {
				Log.e("image", "a");
				ImageInfo imageInfo = (ImageInfo) object;
				ImageView imageView = new ImageView(context);
				view.addView(imageView);
				imageView.getLayoutParams().height = imageInfo.getImage_height() - 50;
//				imageView.getLayoutParams().width = 569;//imageInfo.getImage_width();
				imageView.setScaleType(ImageView.ScaleType.FIT_XY);

				ImageViewManager.loadImage(imageView, imageInfo.getImageURL());
				
			}
			// 텍스트 처리
			else {
				String text = (String) object;
				Log.e("text", text);
//				CTextView textView = new CTextView(context);
				TextView textView = new TextView(context);
				Paint mPaint = textView.getPaint();
				textPaint_size(textView);
				ApplyFont(context,textView);
				
				String save = "";
				
				
				int end = 0;
				String[] textArr = text.split("\n");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() == 0)
						textArr[j] = " ";
					do {
						// 글자가 width 보다 넘어가는지 체크
						end = mPaint.breakText(textArr[j], true, 569, null);
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
		Log.e("end", "----------------------------------------------");
		return view;
	}
	
	private void ApplyFont(Context context,TextView tv){
		Typeface face = Typeface.createFromAsset(context.getAssets(),"SJSoju1.ttf.mp3");
		tv.setTypeface(face);
	}
	private void textPaint_size(TextView textView)
	{

		RbPreference pref = new RbPreference(context);
		int num = pref.getValue(RbPreference.WORD_SIZE, SettingActivity.MEDIUM_WORD);
		switch(num)
		{
		case SettingActivity.SMALL_WORD:
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_small));

			break;
		case SettingActivity.MEDIUM_WORD:
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_medium));

			break;
		case SettingActivity.LARGE_WORD:
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_large));

			break;
		}


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
	
	private void textPaint_size(TextPaint textPaint)
	{
		RbPreference pref = new RbPreference(context);
		int num = pref.getValue(RbPreference.WORD_SIZE, SettingActivity.MEDIUM_WORD);
		switch(num)
		{
		case SettingActivity.SMALL_WORD:
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_small));//textsize설정.
			break;
		case SettingActivity.MEDIUM_WORD:
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_medium));//textsize설정.
			break;
		case SettingActivity.LARGE_WORD:
			textPaint.setTextSize(context.getResources().getDimension(R.dimen.text_large));//textsize설정.
			break;
		}
	}

}
