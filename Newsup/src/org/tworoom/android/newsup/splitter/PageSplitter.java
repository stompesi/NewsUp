package org.tworoom.android.newsup.splitter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.text.TextPaint;

import org.tworoom.android.newsup.view.structure.ArticleDetailPage;
import org.tworoom.android.newsup.view.structure.ImageInfo;
import org.tworoom.android.newsup.view.structure.LayoutInfo;

public class PageSplitter {

	private Queue<Object> remnantContent;
	
	private String currentInputString, totalInputString;

	private TextPaint textPaint;
	
	private double currentViewHeight;
	private double textLineHeight;
	
	private ArticleDetailPage page;
	
	private boolean prevIsPageOver = false;
	
	LayoutInfo layoutInfo;
	
	Object content;
	
	boolean isComplete;
	
	private int stringEndIndex;
	
	ArrayList<Object> list; 
	public PageSplitter(TextPaint textPaint) {
		layoutInfo = LayoutInfo.getInstance();
		this.textPaint = textPaint;
		this.textLineHeight = Math.ceil(textPaint.getFontMetrics(null) * 1.1 + 1);
		this.remnantContent = new LinkedList<Object>(); 
		this.currentViewHeight = layoutInfo.getFirstPageAvailableHeight();
		this.currentInputString = "";
		this.totalInputString = "";
		this.list = new ArrayList<Object>();
		String testString = "안녕하세요. .??아씨발 못하겟네 저는 한종빈입니다 만나서 반갑습니다. 이 데이터는 무엇일까요? 이것은 테스트 데이터입니다. 한줄의 최대 index를 가져오기 위함입니다. ㅎㅎㅎ "; 
		stringEndIndex = textPaint.breakText(testString, true, layoutInfo.getAvailableTotalWidth(), null);
	}

	public ArticleDetailPage makePageList() {
		page = new ArticleDetailPage();
		isComplete = false;
		for (int i = 0; i < list.size() || !(remnantContent.isEmpty()) ;) {
			if (!(remnantContent.isEmpty()) && (!prevIsPageOver || remnantContent.peek() instanceof String) || i == list.size()) {
				content = remnantContent.poll();
				inputContent(content);
			} else {
				content = list.get(i);
				prevIsPageOver = false;
				inputContent(content);
				list.remove(0);
			}
			if(isComplete) {
				prevIsPageOver = false;
				return page;
			}	
		}
		
		if (!(totalInputString.isEmpty())){
			page.add(totalInputString);
		}
		
		if(!(page.isEmpty())) {
			completeMakeView();
			return page;
		} else {
			return null;
		}
	}
	
	private void inputImageContent(ImageInfo imageInfo) {
		if (currentViewHeight > imageInfo.getHeight()) {
			if (!(totalInputString.isEmpty()) || imageInfo.getHeight() >= layoutInfo.getAvailableTotalHeight()) {
				page.add(totalInputString);
				totalInputString = "";
			}
			
			page.add(imageInfo);
			currentViewHeight -= imageInfo.getHeight();
			
			if (isEndPage()) {
				completeMakeView();
			}
			
		} else if(list.size() == 0) {
			remnantContent.offer(imageInfo);
			if (!(totalInputString.isEmpty())){
				page.add(totalInputString);
			}
			completeMakeView();
		}	else {
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
				remnantContent.offer(currentInputString);
				prevIsPageOver = true;
			}
		}
	}

	private boolean fillText(String text) {
		String[] splittedText = text.split("\n");
		
		currentInputString = "";
		text = "";
		for (int i = 0 ; i < splittedText.length; i++) {
			if (splittedText[i].length() == 0) {
				continue;
			}
			text += (splittedText[i] + " ");
		}
		
		while(!text.equals("")){
			if(stringEndIndex > text.length()) {
//				Log.e("text.substring(0, stringEndIndex);", text.substring(0, text.length()));
				totalInputString += text.substring(0, text.length());
				text = text.substring(text.length());
			} else {
//				Log.e("text.substring(0, stringEndIndex);", text.substring(0, stringEndIndex));
				totalInputString += text.substring(0, stringEndIndex);
				text = text.substring(stringEndIndex);
			}
			currentViewHeight = currentViewHeight - textLineHeight;
			if (isEndPage()) {
				currentInputString += text;
				return true;
			}
		}

		currentViewHeight = currentViewHeight - textLineHeight;
		totalInputString += "\n\n";
		if (isEndPage()) {
			return true;
		}
		return false;
	}
	
	private void completeMakeView() {
		isComplete = true;
		totalInputString = "";
		currentViewHeight = layoutInfo.getAvailableTotalHeight();
	}
	
	private boolean isEndPage() {
		return 0 >= currentViewHeight - textLineHeight;
	}
	
	/////////////////////////////
	public void split(String str) {
		String[] midTemp, finalTemp, temp;
		temp  = str.split("<I_S>");
		for(int i = 0 ; i < temp.length ; i++) {
			if(temp[i].contains("<I_E>")) {
				midTemp = temp[i].split("<I_E>");
				if(midTemp.length == 1) {
					list.add(imageParser(midTemp[0].trim()));	
				}
				else {
					list.add(imageParser(midTemp[0].trim()));
					finalTemp = midTemp[1].split("\n\n");
					for(int j = 0 ; j < finalTemp.length ; j++) {
						if(finalTemp[j].trim().length() != 0) {
							list.add(finalTemp[j].trim());
						}
					}
				}
			} 
			else {
				midTemp = temp[i].split("\n\n");
				for(int j = 0 ; j < midTemp.length ; j++) {
					if(midTemp[j].trim().length() != 0) {
						list.add(midTemp[j].trim());
					}
				}
			}
		}
	}

	private ImageInfo imageParser(String str) {
		ImageInfo imageInfo;
		String imageURL;
		String color;
		int imageHeight = 0, imgaeWidth;
		
		double ratio;
		
		String[] imageResult = str.split(" ");

		color = imageResult[0];
		imageURL = imageResult[1];
		
		
		
		//가로가 세로보다 클때 
		if(Integer.parseInt(imageResult[3]) >= Integer.parseInt(imageResult[2])) {
			imgaeWidth = layoutInfo.getAvailableTotalWidth();
			ratio = (double) layoutInfo.getAvailableTotalWidth() / Integer.parseInt(imageResult[3]);
			imageHeight = (int)(Integer.parseInt(imageResult[2]) * ratio);
		} else {
			imageHeight = layoutInfo.getAvailableTotalHeight() / 2;
			ratio = (double) (layoutInfo.getAvailableTotalHeight() / 2) / Integer.parseInt(imageResult[2]);
			imgaeWidth = (int)(Integer.parseInt(imageResult[3]) * ratio);
		}
		
		// 세로보다 긴경우 (width > height)
		imageInfo = new ImageInfo(imageURL, imgaeWidth, imageHeight, color);

		return imageInfo;
	}
}
