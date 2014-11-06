package article.view.detail.splitter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.text.TextPaint;
import android.util.Log;
import article.view.detail.schema.ImageInfo;

public class PageSplitter {

	private Queue<Object> remnantContent;
	
	private String currentInputString, totalInputString;

	private TextPaint textPaint;
	
	private int availableViewHeight;
	private int currentViewHeight;
	private int textLineHeight;
	private int availableViewWidth;
	
	private List<ArticleDetailPage> pageList;
	private ArticleDetailPage page;
	
	private boolean prevIsPageOver = false;
	
	public PageSplitter(TextPaint textPaint, int availableHeight, int availableWidth) {
		this.remnantContent = new LinkedList<Object>(); 
		this.textPaint = textPaint;
		this.textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null));
		// TODO : 바텀 높이 
		this.availableViewHeight = availableHeight;
		// TODO : 첫페이지 들어갈 높이  + 바텀 높이 
		this.currentViewHeight = availableHeight - 230;
		this.availableViewWidth = availableWidth;
		this.pageList = new ArrayList<ArticleDetailPage>();
		this.page = new ArticleDetailPage();
		this.currentInputString = "";
		this.totalInputString = "";
		;
	}
	

	public void makePageList(ArrayList<Object> list) {
		Object content;

		for (int i = 0; i < list.size() ; i++) {
			
			// TODO : 조건문 함수로 변경 
			if (!(remnantContent.isEmpty()) && (!prevIsPageOver || remnantContent.peek() instanceof String)) {
				content = remnantContent.poll();
				i--;
			} else {
				content = list.get(i);
				prevIsPageOver = false;
			}
			inputContent(content);
		}
		
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

		for (int i = 0 ; i < splittedText.length; i++) {
			if (splittedText[i].length() == 0) {
				continue;
			}
			
			do {
				stringEndIndex = textPaint.breakText(splittedText[i], true, availableViewWidth, null);
				if (stringEndIndex > 0) {
					totalInputString += splittedText[i].substring(0, stringEndIndex);
					Log.e("content", splittedText[i].substring(0, stringEndIndex));
					splittedText[i] = splittedText[i].substring(stringEndIndex);
					currentViewHeight -= textLineHeight;
					
					if (isEndPage()) {
						for(int j = i ; j < splittedText.length ; j++) {
							currentInputString += splittedText[j] + "\n";
						}
						return true;
					}
				}
			} while (stringEndIndex > 0);
		}
		currentViewHeight = currentViewHeight - textLineHeight;
		totalInputString += "\n\n";
		return false;
	}
	
	private void completeMakeView() {
		pageList.add(page);
		page = new ArticleDetailPage();
		totalInputString = "";
		currentViewHeight = availableViewHeight;
	}
	
	public List<ArticleDetailPage> getPageList() {
		return pageList;
	}
	
	private boolean isEndPage() {
		return 0 >= currentViewHeight - textLineHeight * 3;
	}
}
