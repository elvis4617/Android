package elvis.game.cognitive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.ColorData;
import elvis.game.cognitive.data.RectArea;
import elvis.game.cognitive.data.TimeRecorder;
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

	private Paint mSrcPaint;
	private Paint mTarPaint;
	private Paint mGameMsgRightPaint;
	private Paint mGameMsgLeftPaint;

	@SuppressWarnings("unused")
	private Typeface mDataTypeface;
	private float rate;

	private int[][] hyperSet;
	private int[][] answerSet;
	
	private int setCounter;
	private int hyperCounter;
	private int blockCounter;
	
	private DBManager mgr;
	private int orintation;
	private TimeRecorder timeRecorder;
	
	private int SET_NUMBER = 5;
	
	private SharedPreferences mSharedPreferences; 


	public MixedColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message m) {
			}
		};
		
		mSharedPreferences = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, Context.MODE_PRIVATE); 
		
		this.rate = 1;

		hyperSet = new int[SET_NUMBER][2];
		answerSet = new int[SET_NUMBER][2];
		
		if(mSharedPreferences.getBoolean(MixedConstant.PREFERENCE_KEY_SEQUENCE, true))
			System.arraycopy(MixedConstant.HYPERSET1, 0, hyperSet, 0, SET_NUMBER);
		else
			System.arraycopy(MixedConstant.HYPERSET2, 0, hyperSet, 0, SET_NUMBER);
		
		for (int i = 0; i < SET_NUMBER; i++) {
			answerSet[i][0] = -1;
			answerSet[i][1] = -1;
		}

		initRes();
		
		mUIThread = new MixedThread(holder, context, mHandler);
		
		mgr = new DBManager(mContext);
		this.orintation = orintation;
		
		getObjectInfo();
		
		this.setCounter = setCounter;
		this.hyperCounter = hyperCounter;
		this.blockCounter = blockCounter;
		
		
		setFocusable(true);
	}

	@Override
	public void draw(Canvas canvas) {
		Log.i("draw", "draw");
		super.draw(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mPaintArea = new RectArea(0, 0, width, height);
		mUIThread.initUIModel(mPaintArea);
		mUIThread.setRunning(true);
		mUIThread.start();
		Log.i("mUIThread start", "mUIThread start");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("surfaceCreated", "surfaceCreated");
		mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.bg_game);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("surfaceDestroyed", "surfaceDestroyed");
		boolean retry = true;
		mUIThread.setRunning(false);
		while (retry) {
			try {
				mUIThread.join();
				retry = false;
				if(!mBgImage.isRecycled()) mBgImage.recycle();
			} catch (InterruptedException e) {
				Log.d("", "Surface destroy failure:", e);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mUIThread.checkSelection((int) event.getX(), (int) event.getY());
		}
		return true;
	}

	public void restartGame() {
		Log.i("restart", "restart");
		mUIThread = new MixedThread(this.getHolder(), this.getContext(),
				mHandler);
		mUIThread.initUIModel(mPaintArea);
		mUIThread.setRunning(true);
		mUIThread.start();
	}

	private void clearSet(int[][] set) {
		for (int i = 0; i < 5; i++) {
			set[i][0] = -1;
			set[i][1] = -1;
		}
	}

	@SuppressWarnings("deprecation")
	private void initRes() {
		Log.i("init res", "init res");
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
		colorBgMap.put(0, curColor); // #F5ABC6

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
		curColor.setColor(Color.parseColor("#FF8000"));// orange
		colorBgMap.put(6, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#FF3399"));// darkpink
		colorBgMap.put(7, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#00BCD4"));// cyan
		colorBgMap.put(8, curColor);
		
		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#34DCCD"));// light blue
		colorBgMap.put(9, curColor);
		
		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#B2FF66"));// green	
		colorBgMap.put(10, curColor);
		
		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#FFB266"));// light orange	
		colorBgMap.put(11, curColor);
		
		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#00CC66"));// green blue		
		colorBgMap.put(12, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#B973FA"));// light purpal	
		colorBgMap.put(13, curColor);
		
		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#F9A485"));// light pink
		colorBgMap.put(14, curColor);

		curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		curColor.setColor(Color.parseColor("#FF3333"));// light red
		colorBgMap.put(15, curColor);
		
		SharedPreferences baseSettings = mContext.getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, 0);
		mSoundsFlag = baseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_SOUNDS, true);
		mVibratorFlag = baseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_VIBRATE, true);
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);
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

	private void saveObject(TimeRecorder subject) {  
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, Context.MODE_PRIVATE);  
        try {  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            ObjectOutputStream oos = new ObjectOutputStream(baos);  
            oos.writeObject(subject);  
  
            String subjectBase64 = new String(Base64.encodeBase64(baos.toByteArray()));  
            SharedPreferences.Editor editor = mSharedPreferences.edit();  
            editor.putString("subjectBase64", subjectBase64);  
            editor.commit();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
	
	protected void getObjectInfo() {  
        try {  
            SharedPreferences mSharedPreferences = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, Context.MODE_PRIVATE);  
            String personBase64 = mSharedPreferences.getString("subjectBase64", "");  
            byte[] base64Bytes = Base64.decodeBase64(personBase64.getBytes());  
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);  
            ObjectInputStream ois = new ObjectInputStream(bais);  
            timeRecorder = (TimeRecorder) ois.readObject();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
          
    }  
	
	public MixedThread getmUIThread() {
		return mUIThread;
	}
	
	
	public int getSetCounter() {
		return setCounter;
	}
	
	public int getHyperCounter() {
		return hyperCounter;
	}
	
	public int getBlockCounter() {
		return blockCounter;
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
				
				if(flag == UIModel.EFFECT_FLAG_PASS){
					setCounter++;
				}
				
				//Log.i("flag", flag+"");
				if(flag == UIModel.GAME_STATUS_COMPLETE_SET){
					Log.i("here", "here");
					mRun = false;
					clearSet(answerSet);
					saveObject(timeRecorder);
					/*restartGame();*/
					Log.i("timeRecorder", timeRecorder.toString());
					Log.i("hyperCounter", hyperCounter+"s");
					Log.i("SetCounter", setCounter+"s");
					
					
					Intent i = new Intent(mContext, SetCongratulation.class);
					Bundle bundle = new Bundle();
					bundle.putString("blockCounter", blockCounter+"");
					bundle.putString("hyperCounter", hyperCounter+"");
					bundle.putString("setCounter", setCounter+"");
					i.putExtras(bundle);
					
					mContext.startActivity(i);
					
				}
				
				if ((mSharedPreferences.getBoolean(MixedConstant.PREFERENCE_KEY_HARDMODE, false) && flag == UIModel.EFFECT_FLAG_MISS ) || 
						flag == UIModel.EFFECT_FLAG_TIMEOUT) {
					Log.i("flag miss || flag timeout", "flag miss || flag timeout");
					mRun = false;
					setCounter = 0;
					clearSet(answerSet);
					/*restartGame();*/
					
					Intent i = new Intent(mContext, Go.class);
					saveObject(timeRecorder);
					/*Bundle bundle = new Bundle();
					bundle.putString("blockCounter", blockCounter+"");
					bundle.putString("hyperCounter", hyperCounter+"");
					bundle.putString("setCounter", setCounter+"");
					i.putExtras(bundle);*/
					mContext.startActivity(i);
				}
				
				
			}
		}

		private void doDraw(Canvas canvas) {
			canvas.drawBitmap(mBgImage, 0, 0, null);

			UIModel uiModel = mUIModel;
			
			//canvas.drawRoundRect(uiModel.getSrcPaintArea(), 15, 15, mSrcPaint);
			/*canvas.drawRoundRect(uiModel.getTarPaintArea(), 15, 15, mTarPaint);*/

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
			
			/*Log.i("first position", firstPos+"");
			Log.i("second position", secondPos+"");*/
			
			List<ColorData> targetColors = uiModel.getTargetColor();
			for (ColorData curColor : targetColors) {
				Paint paint = colorBgMap.get(curColor.getMBgColor());
				int color = paint.getColor();
				canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				
				if ((firstAns != -1 && targetColors.indexOf(curColor) == firstPos)
						|| (secondAns != -1 && targetColors.indexOf(curColor) == secondPos)) {
					paint.setColor(Color.WHITE);
					paint.setStyle(Style.STROKE);
					paint.setStrokeWidth(5);
					canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				} else if (targetColors.indexOf(curColor) == firstPos
						|| targetColors.indexOf(curColor) == secondPos) {
					paint.setColor(Color.DKGRAY);
					paint.setStyle(Style.STROKE);
					paint.setStrokeWidth(5);
					canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				}
				
				paint.setColor(color);
				paint.setStyle(Style.FILL);
			}
		}

		public void initUIModel(RectArea paintArea) {
			mUIModel = new UIModel(paintArea, orintation, timeRecorder, mSharedPreferences.getBoolean(MixedConstant.PREFERENCE_KEY_HARDMODE, false));
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
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				float streamVolumeMax = mgr
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				float volume = streamVolumeCurrent / streamVolumeMax * 0.5f;
				soundPool.play(soundPoolMap.get(soundId%10), volume, volume, 1, 0,
						1f);
			} catch (Exception e) {
				Log.d("PlaySounds", e.toString());
			}
		}

		public void setRunning(boolean run) {
			mRun = run;
		}

		public UIModel getmUIModel() {
			return mUIModel;
		}
		
	}// Thread

}
