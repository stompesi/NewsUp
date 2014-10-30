package hc;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.StyleSpan;

public class TextViewHeight {
    private final int pageWidth;
    private final int pageHeight;
    private final float lineSpacingMultiplier;
    private final int lineSpacingExtra;
    private final List<CharSequence> pages = new ArrayList<CharSequence>();
    private SpannableStringBuilder currentLine = new SpannableStringBuilder();
    private SpannableStringBuilder currentPage = new SpannableStringBuilder();
    private int currentLineHeight;
    private int pageContentHeight;
    private int currentLineWidth;
    private int textLineHeight;
    /***
   	페이지의 넓이, 높이 , 줄간격 정보를 가져온다.
   	먼저 개행 문자로 글을 각가의 단락으로 만든다.
   	단락에서 space로 구분해서 문자 단위로 만든다.
   	문자의 넓이를 체크 -> 현재 화면 보다 클시 다음 라인으로 넘길때 페이지의 높이가 끝이 나는 지 판단 -> 페이지의 높이가 끝났다면 현재 페이지를 등록 하고 다음페이지에다가 쓴다.
   				   -> 현재 화면 보다 작을시 다음 라인에 쓰고 현재 라인의 높이를 저장.
     */
    

    public TextViewHeight(int pageWidth, int pageHeight, float lineSpacingMultiplier, int lineSpacingExtra) {//글을 붙일 창의 넓이 높이 라인 간격을 받아온다.
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        this.lineSpacingExtra = lineSpacingExtra;
    }

    public int getTextheight(String text, TextPaint textPaint) {
        textLineHeight = (int) Math.ceil(textPaint.getFontMetrics(null) * lineSpacingMultiplier + lineSpacingExtra);//줄간격 포함 한라인에 높이를 몇을 줄지를 정한다.
        String[] paragraphs = text.split("\n", -1);//개행 문자 까지 자른다 
        int i;
        for (i = 0; i < paragraphs.length - 1; i++) {//자른 단락의 배열을 넣는다.
            appendText(paragraphs[i], textPaint);
            appendNewLine();//단락이 끝나면 개행 문자를 넣어 준다.
        }
        appendText(paragraphs[i], textPaint);//마지막 단락을 넣는다.
        return pageContentHeight;
       
    }

    private void appendText(String text, TextPaint textPaint) {
        String[] words = text.split(" ", -1);//space 구분으로 자른다.(단어를 만든다)
        int i;
        for (i = 0; i < words.length - 1; i++) {//자른 단어의 배열을 넣는다
            appendWord(words[i] + " ", textPaint);
        }
        appendWord(words[i], textPaint);//마지막 단어
    }

    private void appendWord(String appendedText, TextPaint textPaint) {//받은 단어를 가지고 단어의 넓이를 측정한다.
        int textWidth = (int) Math.ceil(textPaint.measureText(appendedText));//현재단어의 넓이를 측정. 
        if (currentLineWidth + textWidth >= pageWidth) {//단어의 넓이가 페이지의 넓이 보다 크면 이 단락이 페이지의 끝인가 아닌가 측정을한다.
            checkForPageEnd();							
            appendLineToPage(textLineHeight);//페이지의 끝일경우.
        }
    
        appendTextToLine(appendedText, textPaint, textWidth);
    }
    
    private void checkForPageEnd() {
        if (pageContentHeight + currentLineHeight > pageHeight) {//만약 현재 라인이 뷰의 높이 보다 크면 현재 페이지에다가 추가 
        	pages.add(currentPage);
            currentPage = new SpannableStringBuilder();
            pageContentHeight = 0;
        }
  
 
    }
    
    private void appendNewLine() {
    	
        //currentLine.append("\n");//개행 문자 넣는다 
        checkForPageEnd();//현재 끝인가 아닌가를 판단.
        appendLineToPage(textLineHeight);
      
    }
  
    private void appendLineToPage(int textLineHeight) {
        currentPage.append(currentLine);
        pageContentHeight += currentLineHeight;
        currentLine = new SpannableStringBuilder();
        currentLineHeight = textLineHeight;
        currentLineWidth = 0;

    }

    private void appendTextToLine(String appendedText, TextPaint textPaint, int textWidth) {//페이지의 끝이 아닐경우 
        currentLineHeight = Math.max(currentLineHeight, textLineHeight);
        currentLine.append(renderToSpannable(appendedText, textPaint));
        currentLineWidth += textWidth;//글자의 넓이를 현재 넓이에 더해준다.
       
    }

    private SpannableString renderToSpannable(String text, TextPaint textPaint) {//볼드가 표시가 되어있을 경우 볼드 처리 .
        SpannableString spannable = new SpannableString(text);

        if (textPaint.isFakeBoldText()) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, spannable.length(), 0);
        }
        return spannable;
    }
}
