package hc;

public class ImageInfo {
	private String imageURL;
	private int imageWidth, imageHeight; 
	
	public ImageInfo(String imageURL, int imageWidth,int imageHeight) {
		this.imageURL = imageURL;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
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
