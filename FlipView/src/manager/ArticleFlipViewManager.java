package manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.example.flipview.R;

abstract public class ArticleFlipViewManager {
	private static final int MENUE_PAGE = 0;
	protected int offset;
	
	protected ViewFlipper flipper;
	
	
	protected int currentChildIndex;
	protected int maxChildIndex;
	protected int minChildIndex;
	
	protected Context context;
	
	
	protected int menuPrevChildIndex;
	
	protected LayoutInflater inflater;

	public ArticleFlipViewManager(Context context, ViewFlipper flipper, int offset) {
		this.offset = offset;
		this.flipper = flipper;
		this.context = context;
		minChildIndex = 1;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void removeAllFlipperItem() {
		while (flipper.getChildCount() > offset) {
			flipper.removeViewAt(minChildIndex);
		}
	}
	
	public void removeFlipperItem() {
		flipper.removeViewAt(minChildIndex);
	}


	// show child
	public void display(int checkWhichChild) {
		currentChildIndex = checkWhichChild;
		flipper.setDisplayedChild(currentChildIndex);
	}
	
	public void setAnimation(int in, int out) {
		Animation inAnimation = AnimationUtils.loadAnimation(context, in);
		Animation outAnimation = AnimationUtils.loadAnimation(context, out);
		flipper.setInAnimation(inAnimation);
		flipper.setOutAnimation(outAnimation);
	}
	
	
	
	protected void addView(View view) {
		flipper.addView(view, minChildIndex);
	}
	
	public boolean isMenuState() {
		return MENUE_PAGE == currentChildIndex;
	}
	
	public void outMenuPage() {
//		currentState = 1;
		setAnimation(R.anim.fade_in, R.anim.fade_out);
		display(menuPrevChildIndex);
	}
	
	public boolean inMenuPage() {
		if (currentChildIndex != MENUE_PAGE) {
			menuPrevChildIndex = flipper.getDisplayedChild();
			display(MENUE_PAGE);
		}
		return true;
	}
	
	public void setFlipperTouchListener(OnTouchListener listener) {
		flipper.setOnTouchListener(listener);
		
	}
	
	public ViewFlipper getFlipper() {
		return flipper;
	}
	
	public int getChildChount(){
		return flipper.getChildCount() - offset;
	}
	
	public View getChildAt(int index) {
		return flipper.getChildAt(index);
	}
	
	public int getCurrentChildIndex() {
		return currentChildIndex;
	}
	
	
	
	
	abstract public void inArticleDetail(int articleId);
	abstract public void outArticleDetail();
	abstract public boolean upDownSwipe(int page);

	
	
}
