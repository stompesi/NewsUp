package hc;



public class ImageInfo {
	String ImageURL;
	int Image_width;
	int Image_height; 
	
	public ImageInfo(String ImageURL, int Image_width,int Image_height) {
		this.ImageURL = ImageURL;
		this.Image_height = Image_height;
		this.Image_width = Image_width;
	}
	
	public String getImageURL() {
		return ImageURL;
	}
	
	public int getImage_height() {
		return Image_height;
	}

	
	public int getImage_width() {
		return Image_width;
	}

}
