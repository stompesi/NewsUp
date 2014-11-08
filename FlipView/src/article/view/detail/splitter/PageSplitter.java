package article.view.detail.splitter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import application.NewsUpApp;
import article.view.detail.schema.ArticleDetailPage;
import article.view.detail.schema.ImageInfo;
import article.view.detail.schema.LayoutInfo;

public class PageSplitter {

	private Queue<Object> remnantContent;
	
	private String currentInputString, totalInputString;

	private TextPaint textPaint;
	
	private int currentViewHeight;
	private int textLineHeight;
	
	private List<ArticleDetailPage> pageList;
	private ArticleDetailPage page;
	
	private boolean prevIsPageOver = false;
	
	LayoutInfo layoutInfo;
	
	Object content;
	
	boolean isFirstPage;
	boolean isSecondRequest;
	
	public PageSplitter(TextPaint textPaint) {
		layoutInfo = LayoutInfo.getInstance();

		this.textPaint = textPaint;
		
		this.textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null));
		
		
		this.remnantContent = new LinkedList<Object>(); 
		this.currentViewHeight = layoutInfo.getAvailableTotalHeight() - layoutInfo.getFirstPageHeight();
		this.pageList = new ArrayList<ArticleDetailPage>();
		this.page = new ArticleDetailPage();
		this.currentInputString = "";
		this.totalInputString = "";
		this.isFirstPage = true;
		this.isSecondRequest = false;
	}
	

	public void makePageList(ArrayList<Object> list) {
		
		if(isSecondRequest) {
			this.remnantContent = new LinkedList<Object>(); 
			this.currentViewHeight = layoutInfo.getAvailableTotalHeight() - layoutInfo.getFirstPageHeight();
			this.pageList = new ArrayList<ArticleDetailPage>();
			this.page = new ArticleDetailPage();
			this.currentInputString = "";
			this.totalInputString = "";
		}
		for (int i = 0; i < list.size() ; i++) {
			
			// TODO : 조건문 함수로 변경 
			if (!(remnantContent.isEmpty()) && (!prevIsPageOver || remnantContent.peek() instanceof String)) {
				content = remnantContent.poll();
				inputContent(content);
			} else {
				content = list.get(i);
				prevIsPageOver = false;
				inputContent(content);
			}
			
			if(!isFirstPage && !isSecondRequest){
				break;
			}
		}
		isFirstPage = true;
		isSecondRequest = true;
		
		if(!(remnantContent.isEmpty())) {
			content = remnantContent.poll();
			page.add(content);
		}
		
		if (!(totalInputString.isEmpty())){
			page.add(totalInputString);
		}
		
		if(!(page.isEmpty())) {
			pageList.add(page);
		}
	}
	
	private void inputImageContent(ImageInfo imageInfo) {
		if (currentViewHeight > imageInfo.getHeight()) {
			if (!(totalInputString.isEmpty())) {
				page.add(totalInputString);
				// TODO : 함수로 뺄까 ?
				totalInputString = "";
			}
			
			page.add(imageInfo);
			currentViewHeight -= imageInfo.getHeight();
			
			if (isEndPage()) {
				completeMakeView();
			}
			
		} else {
			remnantContent.offer(imageInfo);
			prevIsPageOver = true;
		}
	}
	
	private void inputContent(Object content) {
		// 이미지 처리
		if (content instanceof ImageInfo) {
			ImageInfo imageInfo = (ImageInfo) content;
			inputImageContent(imageInfo);
		}
		// 텍스트 처리
		else {
			String text = (String) content;
			inputTextContent(text);
		}
	}

	private void inputTextContent(String text) {
		boolean isFinishMakePage;
		
		isFinishMakePage = fillText(text);
		if (isFinishMakePage) {
			page.add(totalInputString);
			completeMakeView();
			if (!(currentInputString.isEmpty())) {
				inputTextContent(currentInputString);
			}
		}
	}

	private boolean fillText(String text) {
		String[] splittedText = text.split("\n");
		int stringEndIndex = 0;
		
		currentInputString = "";
		text = "";
		for (int i = 0 ; i < splittedText.length; i++) {
			if (splittedText[i].length() == 0) {
				continue;
			}
			text += (splittedText[i] + " ");
		}
			
		do {
			stringEndIndex = textPaint.breakText(text, true, layoutInfo.getAvailableTextViewWidth(), null);
			if (stringEndIndex > 0) {
				totalInputString += text.substring(0, stringEndIndex);
				Log.e("content", text.substring(0, stringEndIndex));
				text = text.substring(stringEndIndex);
				currentViewHeight -= textLineHeight;
				
				if (isEndPage()) {
					currentInputString += text;
					return true;
				}
			}
		} while (stringEndIndex > 0);
		currentViewHeight = currentViewHeight - textLineHeight;
		currentViewHeight = currentViewHeight - textLineHeight;
		currentViewHeight = currentViewHeight - textLineHeight;
		totalInputString += "\n\n";
		return false;
	}
	
	private void completeMakeView() {
		
		if(isFirstPage) {
			isFirstPage = false;
		} 
		pageList.add(page);
		page = new ArticleDetailPage();
		totalInputString = "";
		currentViewHeight = layoutInfo.getAvailableTotalHeight();
	}
	
	public List<ArticleDetailPage> getPageList() {
		return pageList;
	}
	
	private boolean isEndPage() {
		return 0 >= currentViewHeight - textLineHeight;
	}
	
	/////////////////////////////
	
	
	public List<Object> split(String str) {
		String[] midTemp, finalTemp, temp;
		ArrayList<Object> articleContentList = new ArrayList<Object>();
		
		temp  = str.split("<I_S>");
		
		for(int i = 0 ; i < temp.length ; i++) {
			if(temp[i].contains("<I_E>")) {
				midTemp = temp[i].split("<I_E>");
				if(midTemp.length == 1) {
					articleContentList.add(imageParser(midTemp[0].trim()));	
				}
				else {
					articleContentList.add(imageParser(midTemp[0].trim()));
					finalTemp = midTemp[1].split("\n\n");
					for(int j = 0 ; j < finalTemp.length ; j++) {
						if(finalTemp[j].trim().length() != 0) {
							articleContentList.add(finalTemp[j].trim());
						}
					}
				}
			} 
			else {
				midTemp = temp[i].split("\n\n");
				for(int j = 0 ; j < midTemp.length ; j++) {
					if(midTemp[j].trim().length() != 0) {
						articleContentList.add(midTemp[j].trim());
					}
				}
			}
		}
		return articleContentList;
	}

	private ImageInfo imageParser(String str) {
		ImageInfo imageInfo;
		String imageURL;
		String color;
		int imageHeight;
		int imgaeWidth;
		
		double ratio;

		// TODO : COLOR 처리를 해야함 
		String[] imageResult = str.split(" ");

		color = imageResult[0];
		imageURL = "http://14.63.161.26/" + imageResult[1];
		
		ratio = (double) layoutInfo.getAvailableTotalWidth() / Integer.parseInt(imageResult[2]);
		imgaeWidth = layoutInfo.getAvailableTotalWidth();
		imageHeight = (int)(Integer.parseInt(imageResult[3]) * ratio);
		imageInfo = new ImageInfo(imageURL, imgaeWidth, imageHeight, color);

		return imageInfo;
	}
}
