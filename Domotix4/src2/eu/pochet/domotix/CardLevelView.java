package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;
import eu.pochet.domotix.dao.Card;

public class CardLevelView extends LevelView {
	
	private static final int OFFSET = -20;

	private Bitmap mCardBitmap;

	private Bitmap mTempBitmap;

	public CardLevelView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CardLevelView(Context context, Level level) {
		super(context, level);
	}

	public void init() {
		Options opts = new Options();
		opts.inDither = true;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		mCardBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card, opts);
		mTempBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.card_temp, opts);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float cardX = 0;
		float cardY = 0;
		for (Card card : this.level.getCards()) {
			cardX = getCardX(card);
			cardY = getCardY(card);
			if (card.getStatus() == 0) {
				canvas.drawBitmap(mCardBitmap, (cardX * getRatioX()) + OFFSET, cardY * getRatioY(), null);
			} else {
				canvas.drawBitmap(mTempBitmap, (cardX * getRatioX()) + OFFSET, cardY * getRatioY(), null);
			}
		}
	}

	public float getCardX(Card card) {
		float roomX = card.getRoom().getX();
		float cardX = this.levelX + roomX + card.getX();
		return cardX;
	}

	public float getCardY(Card card) {
		float roomY = card.getRoom().getY();
		float cardY = this.levelY + roomY + card.getY();
		return cardY;
	}

}
