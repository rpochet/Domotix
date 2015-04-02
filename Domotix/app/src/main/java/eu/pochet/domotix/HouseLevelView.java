package eu.pochet.domotix;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import eu.pochet.domotix.dao.Level;

public class HouseLevelView extends LevelView {

	private static final String TAG = HouseLevelView.class.getName();

	public HouseLevelView(Context context, AttributeSet attributeset) {
		super(context, attributeset);
	}

	public HouseLevelView(Context context, Level level) {
		super(context, level);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
