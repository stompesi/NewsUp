package article.view.detail.schema;

public class ImageInfo {
	private String imageURL;
	private int imageWidth, imageHeight; 
	private String color;
	
	public ImageInfo(String imageURL, int imageWidth,int imageHeight, String color) {
		this.imageURL = imageURL;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		this.color = color;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getURL() {
		return imageURL;
	}
	
	public int getHeight() {
		return imageHeight;
	}

	public int getWidth() {
		return imageWidth;
	}

}
