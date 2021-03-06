package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;
import eu.pochet.domotix.dao.DomotixDao;
import eu.pochet.domotix.dao.Level;

public abstract class LevelView extends ImageView {
	
	private static final String TAG = LevelView.class.getName();

	protected Level level = null;

	private float ratioX = 0f;

	private float ratioY = 0f;

	public LevelView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
		int i = attributeset.getAttributeIntValue(null, "levelId", -1);
		if (i != -1) {
			level = DomotixDao.getLevel(context, i);
		}
		init();
	}

	public LevelView(Context context, Level level) {
		super(context);
		this.level = level;
		init();
	}

	protected float getRatioX() {
		return ratioX;
	}

	protected float getRatioY() {
		return ratioY;
	}

	protected void init() {
	}

	protected void onDraw(Canvas canvas) {
		// Log.d(TAG, new
		// StringBuilder("W=").append(canvas.getWidth()).append(", H=").append(canvas.getHeight()).toString());
		ratioX = (1.0F * (float) canvas.getWidth()) / (float) Constants.BACKGROUNG_MAX_X;
		ratioY = (1.0F * (float) canvas.getHeight()) / (float) Constants.BACKGROUNG_MAX_Y;
	}

	public void setLevel(Level level1) {
		level = level1;
	}

}
