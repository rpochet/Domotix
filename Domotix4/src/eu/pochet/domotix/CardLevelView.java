package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Card;
import eu.pochet.domotix.dao.Level;

public class CardLevelView extends LevelView {
	
	private static final String TAG = CardLevelView.class.getName();

	private Bitmap mCardBitmap;

	private Bitmap mTempBitmap;

	public CardLevelView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public CardLevelView(Context context, Level level) {
		super(context, level);
	}

	public float getCardX(Card card) {
		return card.getRoom().getX() + levelX + card.getX();
	}

	public float getCardY(Card card) {
		return card.getRoom().getY() + levelY + card.getY();
	}

	public void init() {
		android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
		options.inDither = true;
		options.inPreferredConfig = android.graphics.Bitmap.Config.RGB_565;
		mCardBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.card, options);
		mTempBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.card_temp, options);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (Card card : level.getCards()) {
			float cardX = getCardX(card);
			float cardY = getCardY(card);
			if (card.getStatus() == 0) {
				canvas.drawBitmap(mCardBitmap, 
						Constants.CARD_OFFSET_X + cardX * getRatioX(), 
						Constants.CARD_OFFSET_Y + cardY * getRatioY(), 
						null);
			} else {
				canvas.drawBitmap(mTempBitmap, 
						Constants.CARD_OFFSET_X + cardX * getRatioX(), 
						Constants.CARD_OFFSET_Y + cardY * getRatioY(), 
						null);
			}
		}
	}
}
