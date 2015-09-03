package elvis.game.cognitive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.ColorData;
import elvis.game.cognitive.data.RectArea;
import elvis.game.cognitive.material.UIModel;
import elvis.game.cognitive.utils.MixedConstant;

public class MixedColorView extends SurfaceView implements
		SurfaceHolder.Callback {

	private Context mContext;
	private Handler mHandler;

	private MixedThread mUIThread;

	private Drawable mTimeTotalImage;
	private Drawable mTimeExpendImage;
	private Bitmap mBgImage;

	private RectArea mPaintArea;

	private boolean mVibratorFlag;

	private boolean mSoundsFlag;

	private Vibrator mVibrator;

	private SoundPool soundPool;

	private HashMap<Integer, Integer> soundPoolMap;

	private Map<Integer, Paint> colorBgMap;

	private Map<Integer, Integer> textColorMap;

	private Paint mSrcPaint;
	private Paint mTarPaint;
	private Paint mGameMsgRightPaint;
	private Paint mGameMsgLeftPaint;

	@SuppressWarnings("unused")
	private Typeface mDataTypeface;
	private float rate;

	private int[][] hyperSet;
	private int[][] answerSet;
	private int trialCounter;
	private DBManager mgr;
	
	private Random mRan = new Random();

	public MixedColorView(Context context, float rate) {
		super(context);
		mContext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message m) {

				/*LayoutInflater factory = LayoutInflater.from(mContext);
				View dialogView = factory.inflate(R.layout.score_post_panel,
						null);
				dialogView.setFocusableInTouchMode(true);
				dialogView.requestFocus();

				final AlertDialog dialog = new AlertDialog.Builder(mContext) 
						.setView(dialogView).create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
				dialogView.findViewById(R.id.retry).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								clearSet(answerSet);
								restartGame();
							}
						});
				dialogView.findViewById(R.id.goback).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								((MixedColorActivity) mContext).finish();
							}
						});*/

			}
		};
		this.rate = rate;

		hyperSet = new int[10][2];
		answerSet = new int[10][2];

		for (int i = 0; i < 10; i++) {
			hyperSet[i][0] = mRan.nextInt(15) % (15 - 0 + 1) + 0;
			hyperSet[i][1] = mRan.nextInt(15) % (15 - 0 + 1) + 0;
			while (hyperSet[i][0] == hyperSet[i][1]) {
				hyperSet[i][1] = mRan.nextInt(15) % (15 - 0 + 1) + 0;
			}
			answerSet[i][0] = -1;
			answerSet[i][1] = -1;
		}

		initRes();
		mUIThread = new MixedThread(holder, context, mHandler);
		trialCounter = 1;
		mgr = new DBManager(mContext);
		setFocusable(true);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mPaintArea = new RectArea(0, 0, width, height);
		mUIThread.initUIModel(mPaintArea);
		mUIThread.setRunning(true);
		mUIThread.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		mUIThread.setRunning(false);
		while (retry) {
			try {
				mUIThread.join();
				retry = false;
			} catch (InterruptedException e) {
				Log.d("", "Surface destroy failure:", e);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mUIThread.mUIModel.setTrialCounter(trialCounter);
			mUIThread.checkSelection((int) event.getX(), (int) event.getY());
		}
		return true;
	}

	public void restartGame() {
		mUIThread = new MixedThread(this.getHolder(), this.getContext(),
				mHandler);
		mUIThread.initUIModel(mPaintArea);
		mUIThread.setRunning(true);
		mUIThread.start();
	}

	private void clearSet(int[][] set) {
		for (int i = 0; i < 10; i++) {
			set[i][0] = -1;
			set[i][1] = -1;
		}
	}

	@SuppressWarnings("deprecation")
	private void initRes() {
		mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.bg_game);
		mTimeTotalImage = mContext.getResources().getDrawable(
				R.drawable.time_total);
		mTimeExpendImage = mContext.getResources().getDrawable(
				R.drawable.time_expend);

		mDataTypeface = Typeface.createFromAsset(getContext().getAssets(),
				"fonts/halver.ttf");

		mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mSrcPaint.setColor(Color.parseColor("#AAC1CDC1"));
		mTarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTarPaint.setColor(Color.parseColor("#BBC1CDC1"));

		mGameMsgRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGameMsgRightPaint.setColor(Color.BLUE);
		mGameMsgRightPaint.setStyle(Style.FILL);
		mGameMsgRightPaint.setTextSize(17 * rate);
		mGameMsgRightPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mGameMsgRightPaint.setTextAlign(Paint.Align.RIGHT);

		mGameMsgLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mGameMsgLeftPaint.setColor(Color.BLUE);
		mGameMsgLeftPaint.setStyle(Style.FILL);
		mGameMsgLeftPaint.setTextSize(17 * rate);
		mGameMsgLeftPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mGameMsgLeftPaint.setTextAlign(Paint.Align.LEFT);

		colorBgMap = new HashMap<Integer, Paint>();

		Paint curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#F5ABC6"));// pink E91E63 light pink
														// #F5ABC6
		colorBgMap.put(0, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#D6C6ED"));// indigo
		colorBgMap.put(1, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#F6C18D"));// yellow
		colorBgMap.put(2, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#E43172"));// red
		colorBgMap.put(3, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#8FDCB0"));// lime #8FDCB0
		colorBgMap.put(4, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#2196F3"));// blue
		colorBgMap.put(5, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#FFC125"));// orange
		colorBgMap.put(6, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#B873FF"));// purple
		colorBgMap.put(7, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#00BCD4"));// cyan
		colorBgMap.put(8, curColor);

		textColorMap = new HashMap<Integer, Integer>();
		textColorMap.put(0, Color.parseColor("#F5ABC6"));
		textColorMap.put(1, Color.parseColor("#D6C6ED"));
		textColorMap.put(2, Color.parseColor("#F6C18D"));
		textColorMap.put(3, Color.parseColor("#E43172"));
		textColorMap.put(4, Color.parseColor("#8FDCB0"));
		textColorMap.put(5, Color.parseColor("#2196F3"));
		textColorMap.put(6, Color.parseColor("#FFC125"));
		textColorMap.put(7, Color.parseColor("#B873FF"));
		textColorMap.put(8, Color.parseColor("#00BCD4"));

		SharedPreferences baseSettings = mContext.getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, 0);
		mSoundsFlag = baseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_SOUNDS, true);
		mVibratorFlag = baseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_VIBRATE, true);
		soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
		soundPoolMap = new HashMap<Integer, Integer>();
		soundPoolMap.put(UIModel.EFFECT_FLAG_MISS,
				soundPool.load(getContext(), R.raw.miss, 1));
		soundPoolMap.put(UIModel.EFFECT_FLAG_PASS_FIRST,
				soundPool.load(getContext(), R.raw.pass, 1));
		soundPoolMap.put(UIModel.EFFECT_FLAG_PASS,
				soundPool.load(getContext(), R.raw.pass, 1));
		soundPoolMap.put(UIModel.EFFECT_FLAG_TIMEOUT,
				soundPool.load(getContext(), R.raw.timeout, 1));
	}

	// thread for updating UI
	class MixedThread extends Thread {

		private SurfaceHolder mSurfaceHolder;

		private Context mContext;

		private boolean mRun = true;

		private UIModel mUIModel;

		public MixedThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mContext = context;

		}

		@Override
		public void run() {
			while (mRun) {
				Canvas c = null;
				int flag = 0;
				try {
					mUIModel.updateUIModel();
					c = mSurfaceHolder.lockCanvas(null);
					synchronized (mSurfaceHolder) {
						doDraw(c);
					}
					flag = mUIModel.getEffectFlag();
					handleEffect(flag);
					Thread.sleep(10);
				} catch (Exception e) {
					Log.d("", "Error at 'run' method", e);
				} finally {
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
				if (flag == UIModel.EFFECT_FLAG_MISS || mUIModel.getStatus() == UIModel.GAME_STATUS_GAMEOVER) {
					trialCounter++;
					mRun = false;
					clearSet(answerSet);
					restartGame();
				}
			}
		}

		private void doDraw(Canvas canvas) {
			canvas.drawBitmap(mBgImage, 0, 0, null);

			UIModel uiModel = mUIModel;

			FontMetrics fmsr = mGameMsgLeftPaint.getFontMetrics();
			canvas.drawText(uiModel.getStageText(), 5 * rate, 15 * rate
					- (fmsr.ascent + fmsr.descent), mGameMsgLeftPaint);

			mTimeTotalImage
					.setBounds((int) (mPaintArea.mMaxX / 2 - 80 * rate),
							(int) (15 * rate),
							(int) (mPaintArea.mMaxX / 2 + 80 * rate),
							(int) (25 * rate));
			mTimeTotalImage.draw(canvas);

			mTimeExpendImage.setBounds(
					(int) (mPaintArea.mMaxX / 2 - 80 * rate),
					(int) (15 * rate),
					(int) (mPaintArea.mMaxX / 2 - 80 * rate + 160
							* uiModel.getTimePercent() * rate),
					(int) (25 * rate));
			mTimeExpendImage.draw(canvas);

			fmsr = mGameMsgRightPaint.getFontMetrics();
			canvas.drawText(uiModel.toTimeText(uiModel.getStageTime()),
					mPaintArea.mMaxX - 5 * rate, 15 * rate
							- (fmsr.ascent + fmsr.descent), mGameMsgRightPaint);

			int firstPos = mUIModel.getFirstPos();
			int secondPos = mUIModel.getSecondPos();
			int firstAns = mUIModel.getFirstAns();
			int secondAns = mUIModel.getSecondAns();

			List<ColorData> targetColors = uiModel.getTargetColor();
			for (ColorData curColor : targetColors) {
				Paint paint = colorBgMap.get(curColor.getMBgColor());
				int color = paint.getColor();
				canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				if ((firstAns != -1 && targetColors.indexOf(curColor) == firstPos)
						|| (secondAns != -1 && targetColors.indexOf(curColor) == secondPos)) {
					paint.setColor(Color.WHITE);
					paint.setStyle(Style.STROKE);
					paint.setStrokeWidth(10);
					canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				} else if (targetColors.indexOf(curColor) == firstPos
						|| targetColors.indexOf(curColor) == secondPos) {
					paint.setColor(Color.DKGRAY);
					paint.setStyle(Style.STROKE);
					paint.setStrokeWidth(10);
					canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				}
				paint.setColor(color);
				paint.setStyle(Style.FILL);
			}
		}

		public void initUIModel(RectArea paintArea) {
			if (mUIModel != null) {
			}
			mUIModel = new UIModel(paintArea);
			mUIModel.setHyperSet(hyperSet);
			mUIModel.setAnswerSet(answerSet);
			mUIModel.setMgr(mgr);
			mBgImage = Bitmap.createScaledBitmap(mBgImage, paintArea.mMaxX,
					paintArea.mMaxY, true);
		}

		public void checkSelection(int x, int y) {
			mUIModel.checkSelection(x, y);
		}

		private void handleEffect(int effectFlag) {
			if (effectFlag == UIModel.EFFECT_FLAG_NO_EFFECT)
				return;

			if (mSoundsFlag) {
				playSoundEffect(effectFlag);
			}

			if (mVibratorFlag) {
				if (effectFlag == UIModel.EFFECT_FLAG_PASS_FIRST
						|| effectFlag == UIModel.EFFECT_FLAG_PASS) {
					if (mVibrator == null) {
						mVibrator = (Vibrator) mContext
								.getSystemService(Context.VIBRATOR_SERVICE);
					}
					mVibrator.vibrate(50);
				}
			}
		}

		private void playSoundEffect(int soundId) {
			try {
				AudioManager mgr = (AudioManager) getContext()
						.getSystemService(Context.AUDIO_SERVICE);
				float streamVolumeCurrent = mgr
						.getStreamVolume(AudioManager.STREAM_RING);
				float streamVolumeMax = mgr
						.getStreamMaxVolume(AudioManager.STREAM_RING);
				float volume = streamVolumeCurrent / streamVolumeMax;
				soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, 0,
						1f);
			} catch (Exception e) {
				Log.d("PlaySounds", e.toString());
			}
		}

		public void setRunning(boolean run) {
			mRun = run;
		}
	}// Thread

}
