package hc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.text.TextPaint;
import android.widget.TextView;

public class Splitter {
	ArrayList<Object> result = new ArrayList<Object>();
	TextPaint textPaint;

	String string = new String();
	String totalString = new String();

	int availableWidth;

	// height 관련 정보
	int availableHeight;
	int currentHeight;
	int textLineHeight;
	Queue<Object> q = new LinkedList<Object>();
	List<ArticleDetailPage> articleDatailPageList= new ArrayList<ArticleDetailPage>();
	ArticleDetailPage articleDatailPage;
	
	private boolean prevIsPageOver = false;
	
	public Splitter(TextPaint textPaint, int availableHeight, int availableWidth) {
		this.textPaint = textPaint;
		this.textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null));
		this.availableHeight = availableHeight;
		this.availableWidth = availableHeight;
	}

	public List<ArticleDetailPage> getList(ArrayList<Object> list) {
		
		currentHeight = availableHeight;
		articleDatailPage = new ArticleDetailPage();

		for (int i = 0; i < list.size() ; i++) {
			Object object;
			if (!q.isEmpty() && (!prevIsPageOver || q.peek() instanceof String)) {
				object = q.poll();
				i--;
			} else {
				object = list.get(i);
				prevIsPageOver = false;
			}
			process(object);
		}
		
		if(!q.isEmpty()) {
			Object object = q.poll();
			articleDatailPage.add(object);
		}
		if (!totalString.equals("")){
			articleDatailPage.add(totalString);
		}
		
		if(articleDatailPage.getContent().size() != 0) {
			articleDatailPageList.add(articleDatailPage);
		}

		return articleDatailPageList;
	}
	
	private void process(Object object) {
		// 이미지 처리
		if (object instanceof ImageInfo) {
			ImageInfo imageInfo = (ImageInfo) object;
			if (currentHeight > imageInfo.getImage_height()) {
				if (!(totalString.equals(""))) {
					articleDatailPage.add(totalString);
					totalString = "";
				}
				articleDatailPage.add(object);
				
				currentHeight = currentHeight - imageInfo.getImage_height();
				
				if (0 >= currentHeight - textLineHeight) {
					completeMakeView();
				}
				
			} else {
				q.offer(object);
				prevIsPageOver = true;
			}
		}
		// 텍스트 처리
		else {
			String text = (String) object;
			processText(text);
		}
	}
	
	private void completeMakeView() {
		currentHeight = availableHeight;
		articleDatailPageList.add(articleDatailPage);
		articleDatailPage = new ArticleDetailPage();
		totalString = "";
	}

	private void processText(String text) {
		if (get(text)) {
			articleDatailPage.add(totalString);
			completeMakeView();
			if (!(string.equals(""))) {
				processText(string);
			}
		}
	}

	private boolean get(String text) {
		string = "";
		int end = 0;
		String[] textArr = text.split("\n");
		for (int j = 0; j < textArr.length; j++) {
			if (textArr[j].length() == 0)
				textArr[j] = " ";
			do {
				// 글자가 width 보다 넘어가는지 체크
				end = textPaint.breakText(textArr[j], true, availableHeight, null);
				if (end > 0) {
					// 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
					totalString += textArr[j].substring(0, end);
					textArr[j] = textArr[j].substring(end);
					currentHeight = currentHeight - textLineHeight;
					if (0 > currentHeight - textLineHeight) {
						for(int i = j ; i < textArr.length ; i++) {
							string += textArr[i] + "\n";
						}
						return true;
					}
				}
			} while (end > 0);
//			totalString += "\n";
			
		}
		
		totalString += "\n\n";
//		currentHeight = currentHeight - textLineHeight;
		return false;
	}
}
