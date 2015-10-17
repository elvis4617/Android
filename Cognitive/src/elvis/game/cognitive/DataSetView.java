package elvis.game.cognitive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class DataSetView extends View {

	public DataSetView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	} 

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.FILL);
		paint.setTextSize(50f);
		canvas.drawText("Data", 100, 100, paint);
	}

}
