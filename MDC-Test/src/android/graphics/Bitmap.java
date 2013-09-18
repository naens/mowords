package android.graphics;


public class Bitmap {

	private int width;
	private int height;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public enum Config{ARGB_8888}

	public static Bitmap createBitmap(int i, int j, Config argb8888) {
		Bitmap result = new Bitmap();
		result.width = i;
		result.height = j;
		return result;
	}

	public void getPixels(int[] pixels, int i, int width2, int j, int k,
			int width3, int height2) {
		// TODO Auto-generated method stub
		
	}

	public static Bitmap createBitmap(Bitmap bitmap, int left, int top,
			int nWidth, int nHeight, Matrix matrix, boolean b) {
		// TODO Auto-generated method stub
		return null;
	};

}
