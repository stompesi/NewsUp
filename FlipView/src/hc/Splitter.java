package hc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import android.text.TextPaint;

public class Splitter {

	private Queue<Object> job;
	
	private String currentInputString, totalInputString;

	private TextPaint textPaint;
	
	private int availableHeight;
	private int currentHeight;
	private int textLineHeight;
	private int availableWidth;
	
	private List<ArticleDetailPage> articleDatailPageList;
	private ArticleDetailPage articleDatailPage;
	
	private boolean prevIsPageOver = false;
	
	public Splitter(TextPaint textPaint, int availableHeight, int availableWidth) {
		this.job = new LinkedList<Object>(); 
		this.textPaint = textPaint;
		this.textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null));
		this.availableHeight = availableHeight;
		this.availableWidth = availableWidth;
		this.articleDatailPageList = new ArrayList<ArticleDetailPage>();
		this.currentInputString = "";
		this.totalInputString = "";
	}

	public List<ArticleDetailPage> getList(ArrayList<Object> list) {
		Object inputContent;
		
		currentHeight = availableHeight;
		articleDatailPage = new ArticleDetailPage();

		for (int i = 0; i < list.size() ; i++) {
			if (!job.isEmpty() && (!prevIsPageOver || job.peek() instanceof String)) {
				inputContent = job.poll();
				i--;
			} else {
				inputContent = list.get(i);
				prevIsPageOver = false;
			}
			process(inputContent);
		}
		
		if(!job.isEmpty()) {
			inputContent = job.poll();
			articleDatailPage.add(inputContent);
		}
		if (!totalInputString.equals("")){
			articleDatailPage.add(totalInputString);
		}
		
		if(articleDatailPage.getContent().size() != 0) {
			articleDatailPageList.add(articleDatailPage);
		}

		return articleDatailPageList;
	}
	
	private void process(Object inputContent) {
		// 이미지 처리
		if (inputContent instanceof ImageInfo) {
			ImageInfo imageInfo = (ImageInfo) inputContent;
			
			if (currentHeight > imageInfo.getHeight()) {
				if (!(totalInputString.equals(""))) {
					articleDatailPage.add(totalInputString);
					totalInputString = "";
				}
				
				articleDatailPage.add(inputContent);
				currentHeight = currentHeight - imageInfo.getHeight();
				
				if (isEndPage()) {
					completeMakeView();
				}
				
			} else {
				job.offer(inputContent);
				prevIsPageOver = true;
			}
		}
		// 텍스트 처리
		else {
			String text = (String) inputContent;
			processText(text);
		}
	}
	
	private void completeMakeView() {
		currentHeight = availableHeight;
		articleDatailPageList.add(articleDatailPage);
		articleDatailPage = new ArticleDetailPage();
		totalInputString = "";
	}

	private void processText(String text) {
		if (fillText(text)) {
			articleDatailPage.add(totalInputString);
			completeMakeView();
			if (!(currentInputString.equals(""))) {
				processText(currentInputString);
			}
		}
	}

	private boolean fillText(String text) {
		int end;
		String[] textSplitted;
		
		end = 0;
		currentInputString = "";
		textSplitted = text.split("\n");

		for (int j = 0 ; j < textSplitted.length; j++) {
			
			if (textSplitted[j].length() == 0) {
				textSplitted[j] = " ";
			}
			
			do {
				
				end = textPaint.breakText(textSplitted[j], true, availableWidth, null);
				
				if (end > 0) {
					
					totalInputString += textSplitted[j].substring(0, end);
					textSplitted[j] = textSplitted[j].substring(end);
					
					currentHeight = currentHeight - textLineHeight;
					if (isEndPage()) {
						for(int i = j ; i < textSplitted.length ; i++) {
							currentInputString += textSplitted[i] + "\n";
						}
						return true;
					}
				}
			} while (end > 0);
		}
		totalInputString += "\n\n";
		return false;
	}
	
	private boolean isEndPage() {
		return 0 >= currentHeight - textLineHeight;
	}
}
