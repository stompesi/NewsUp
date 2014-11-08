package article.view.detail;

import java.util.ArrayList;

import manager.ImageViewManager;
import network.NewsUpNetwork;
import ArticleReadInfo.ArticleReadInfo;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import application.NewsUpApp;
import article.view.ArticleFlipViewManager;
import article.view.detail.schema.ArticleDetailPage;
import article.view.detail.schema.ImageInfo;
import article.view.detail.schema.LayoutInfo;
import article.view.detail.splitter.PageSplitter;

import com.example.flipview.R;

import database.Article;

public class ArticleDetailManager extends ArticleFlipViewManager {
	private int textSize;

	private ArticleReadInfo articleReadInfo;
	private int pageReadStartTime;

	LayoutInfo layoutInfo;

	public ArticleDetailManager(Context context, ViewFlipper flipper, int offset) {
		super(context, flipper, offset);
		this.context = context;
	}

	Handler handler = new Handler();
	ArrayList<Object> list;
	ArrayList<ArticleDetailPage> articleDetailPageList;
	PageSplitter splitter;

	public void getArticleDetail(int articleId) {
		String str;
		Article article;

		TextPaint textPaint;

		article = Article.getArticle(articleId);
		list = new ArrayList<Object>();
		str = article.getBody();

		layoutInfo = LayoutInfo.getInstance();

		TextView textView = setTextView(context,
				layoutInfo.getAvailableTotalHeight(), NewsUpApp.getInstance().getTextSize());
		textView.setPadding(layoutInfo.getTextViewPadding(), 0, layoutInfo.getTextViewPadding(), 0);
		textPaint = textView.getPaint();

		splitter = new PageSplitter(textPaint);

		// TODO: 시작페이지를 만들어야한다 (상세기사 첫화면)
		list = (ArrayList<Object>) splitter.split(str);
		splitter.makePageList(list);
		articleDetailPageList = (ArrayList<ArticleDetailPage>) splitter
				.getPageList();

		// 첫페이지 title, author

		LinearLayout view, layout;
		layout = (LinearLayout) inflater.inflate(R.layout.view_article_detail,
				null);
		view = (LinearLayout) (layout).findViewById(R.id.viewArticleDetail);

		textSize = R.dimen.text_title;
		textView = setTextView(context, 300, textSize);
		textView.setPadding(layoutInfo.getTextViewPadding(), 100, 0, 0);
		textView.setMaxLines(2);
		textView.setEllipsize(TextUtils.TruncateAt.END);

		textView.setText(article.getAuthor());
		view.addView(textView);

		textSize = R.dimen.text_author;
		textView = setTextView(context, 100, textSize);
		textView.setPadding(layoutInfo.getTextViewPadding(), 10, 0, 0);

		textView.setText(article.getAuthor());
		view.addView(textView);
		viewMaker(view, articleDetailPageList.get(0));
		setPageNumber(layout, 1, articleDetailPageList.size() + 1);
		addView(layout);

		

		InsertArticleTask insertArticleTask = new InsertArticleTask();
		insertArticleTask.execute();
		
		display(getChildChount() - 1);

		// 마지막 페이지.
	}

	class InsertArticleTask extends
			AsyncTask<Void, Void, ArrayList<ArticleDetailPage>> {

		@Override
		protected ArrayList<ArticleDetailPage> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			splitter.makePageList(list);
			articleDetailPageList = (ArrayList<ArticleDetailPage>) splitter
					.getPageList();
			return articleDetailPageList;
		}

		@Override
		protected void onPostExecute(
				final ArrayList<ArticleDetailPage> articleDetailPageList) {
			for (int i = 1; i < articleDetailPageList.size(); i++) {
				final ArticleDetailPage  articleDetailPage = articleDetailPageList.get(i);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						LinearLayout view, layout;
						layout = (LinearLayout) inflater.inflate(R.layout.view_article_detail, null);
						view = (LinearLayout) (layout).findViewById(R.id.viewArticleDetail);
						viewMaker(view, articleDetailPage);
						addView(layout);
						currentChildIndex++;
					}
				}, 300);
			}
		}
	}

	private void viewMaker(LinearLayout view,
			ArticleDetailPage articleDetailPage) {
		ArrayList<Object> articleContent;

		Object object;

		articleContent = (ArrayList<Object>) articleDetailPage.getContent();

		for (int i = 0; i < articleContent.size(); i++) {
			object = articleContent.get(i);

			// 이미지 처리
			if (object instanceof ImageInfo) {
				ImageInfo imageInfo;
				ImageView imageView;

				imageInfo = (ImageInfo) object;
				imageView = new ImageView(context);

				view.addView(imageView);

				imageView.getLayoutParams().height = imageInfo.getHeight() - 50;
				imageView.getLayoutParams().width = imageInfo.getWidth();
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

				textView = new TextView(context);
				textView.setWidth(layoutInfo.getAvailableTotalWidth());
				textView.setPadding(layoutInfo.getTextViewPadding(), 0,
						layoutInfo.getTextViewPadding(), 0);
				textView.setLineSpacing((float) 1.1, (float) 1.1);

				int textSize = NewsUpApp.getInstance().getTextSize();
				textView.setTextColor(Color.BLACK);
				textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context
						.getResources().getDimension(textSize));
				ApplyFont(context, textView);

				mPaint = textView.getPaint();

				end = 0;
				textArr = text.split("\n");
				for (int j = 0; j < textArr.length; j++) {
					if (textArr[j].length() == 0)
						textArr[j] = " ";
					do {
						// 글자가 width 보다 넘어가는지 체크
						end = mPaint.breakText(textArr[j], true,
								layoutInfo.getAvailableTextViewWidth(), null);
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
	}

	private void setPageNumber(LinearLayout layout, int pageNumber,
			int maxPageNumber) {
		TextView textView = (TextView) (layout)
				.findViewById(R.id.viewArticleBottom);
		textView.setText(pageNumber + "/" + maxPageNumber);

	}

	private TextView setTextView(Context context, int height, int textSize) {

		TextView textView = new TextView(context);
		textView.setWidth(layoutInfo.getAvailableTotalWidth());
		textView.setHeight(height);
		textView.setLineSpacing((float) 1.1, (float) 1.1);
		textView.setTextColor(Color.BLACK);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
				.getDimension(textSize));
		ApplyFont(context, textView);

		return textView;
	}

	private void ApplyFont(Context context, TextView tv) {
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"NanumGothic.ttf.mp3");
		tv.setTypeface(face);
	}

	private void ApplyFont(Context context, TextPaint tv) {
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"NanumGothic.ttf.mp3");
		tv.setTypeface(face);
	}

	@Override
	public void outArticleDetail() {
		setReadTime();
		removeAllFlipperItem();
		if (NewsUpNetwork.isNetworkState(context)) {
			NewsUpNetwork.getInstance().updateUserLog(articleReadInfo);
		}
	}

	@Override
	public boolean upDownSwipe(int increase) {
		int checkIndex = currentChildIndex + increase;

		if (checkIndex >= getChildChount() || checkIndex < minChildIndex) {
			return false;
		}

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
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime,
				getChildChount());
	}

	public void changeTextSize(int articleId) {
		getArticleDetail(articleId);
		pageReadStartTime = getTimestamp();
		articleReadInfo = new ArticleReadInfo(articleId, pageReadStartTime,
				getChildChount());
		display(getChildChount() - 1);
	}

	private int getTimestamp() {
		return (int) (System.currentTimeMillis() / 1000L);
	}
}
