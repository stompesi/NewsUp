package transmission;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.flipview.R;

public class TransmissionArticle implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int idx;
	private String title, content, provider, time;
	Bitmap image;
	
	public TransmissionArticle(View view) {
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView provider = (TextView) view.findViewById(R.id.provider);
//		ImageView image = (ImageView) view.findViewById(R.id.image);
		
		
		this.idx = view.getId();
		this.title = title.getText().toString();
		this.content = content.getText().toString();
		this.time = time.getText().toString();
		this.provider = provider.getText().toString();
		
//		BitmapDrawable d = (BitmapDrawable)((ImageView) view.findViewById(R.id.image)).getDrawable();
//		Bitmap b = d.getBitmap();
		
		
//		this.image = b;
	}
	
	public int getIdx() {
		return idx;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getProvider(){
		return provider;
	}
	
	public String getTime(){
		return time;
	}
	
	
	
}