package elvis.game.cognitive.data;

import android.graphics.RectF;

public class ColorData {

	public int mMinX;

	public int mMinY;

	public int mMaxX;

	public int mMaxY;

	private int mBgColor;
	private int mTextColor;
	private int mText;

	public RectF getRectF() {
		return new RectF(mMinX, mMinY, mMaxX, mMaxY);
	}

	public int getMBgColor() {
		return mBgColor;
	}

	public void setMBgColor(int bgColor) {
		mBgColor = bgColor;
	}

	public int getMTextColor() {
		return mTextColor;
	}

	public void setMTextColor(int textColor) {
		mTextColor = textColor;
	}

	public int getMText() {
		return mText;
	}

	public void setMText(int text) {
		mText = text;
	}
}
