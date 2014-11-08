package transmission;

import image.handler.Image;

import java.io.Serializable;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flipview.R;
/***
 * 
 * @author stompesi
 * 
 * Atcivity간의 Article 정보를 전달하기 위한 class
 */
public class TransmissionArticle implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int idx;
	private String title, content, provider, time;
	private String imageURL;
	
	public TransmissionArticle(View view) {
		TextView title = (TextView) view.findViewById(R.id.title);
		TextView content = (TextView) view.findViewById(R.id.content);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView provider = (TextView) view.findViewById(R.id.provider);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		
		this.idx = view.getId();
		this.title = title.getText().toString();
		this.content = content.getText().toString();
		this.time = time.getText().toString();
		this.provider = provider.getText().toString();
		this.imageURL = ((Image) image.getTag()).getURL();
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
	
	public String getImageURL() {
		return imageURL;
	}
}