package hc;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class Text extends TextView {
    private int mAvailableWidth = 0;
    private Paint mPaint;
    private List<String> mCutStr = new ArrayList<String>();
 
    public Text(Context context) {
        super(context);
    }
 
    public Text(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    private int setTextInfo(String text, int textWidth, int textHeight) {
        // 그릴 페인트 세팅
        mPaint = getPaint();
        mPaint.setColor(getTextColors().getDefaultColor());
        mPaint.setTextSize(getTextSize());
 
        int mTextHeight = textHeight;
        
        if (textWidth > 0) {
            // 값 세팅
            mAvailableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
 
            mCutStr.clear();
            int end = 0;
            do {
                // 글자가 width 보다 넘어가는지 체크
                end = mPaint.breakText(text, true, mAvailableWidth, null);
                if (end > 0) {
                    // 자른 문자열을 문자열 배열에 담아 놓는다.
                    mCutStr.add(text.substring(0, end));
                    // 넘어간 글자 모두 잘라 다음에 사용하도록 세팅
                    text = text.substring(end);
                    // 다음라인 높이 지정
                    if (textHeight == 0) mTextHeight += getLineHeight();
                }
            } while (end > 0);
        }
        mTextHeight += getPaddingTop() + getPaddingBottom();
        return mTextHeight;
    }
}