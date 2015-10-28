package elvis.game.cognitive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.ColorData;
import elvis.game.cognitive.data.RectArea;
import elvis.game.cognitive.data.TimeRecorder;
import elvis.game.cognitive.material.UIModel;
import elvis.game.cognitive.utils.MixedConstant;

public class MixedColorView extends SurfaceView implements
		SurfaceHolder.Callback {

	private Context mContext;

	private MixedThread mUIThread;
	private Handler mHandler;

	private Drawable mTimeTotalImage;
	private Drawable mTimeExpendImage;
	private Drawable mCharacter;
	private Drawable mGridChar;
	private Drawable mDestination;
	private Drawable mDialog;
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

	private float rate;

	private int[][] hyperSet;
	private int[][] answerSet = {{-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}, {-1, -1}};
	
	private int setCounter;
	private int hyperCounter;
	private int blockCounter;
	
	private DBManager mgr;
	private int orintation;
	private TimeRecorder timeRecorder;
	
	private int SET_NUMBER = 5;
	
	private SharedPreferences mBaseSettings;
	private SharedPreferences mGameSettings;
	
	private int charWidth = 0;
	private int charHeight = 0;
	private int destWidth = 0;
	private int destHeight = 0;
	
	private int startXChar = 120;
	private int startYChar = 30;
	private int startXDest = 1000;
	private int startYDest = 50;


	public MixedColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);;
		mHandler = new Handler() {
			public void handleMessage(Message m) {

				LayoutInflater factory = LayoutInflater.from(mContext);
				if(m.getData().getInt(MixedConstant.GAME_STATUS_COMPLETE_SET) == UIModel.GAME_STATUS_COMPLETE_SET){
					View dialogView = factory.inflate(R.layout.congratulation,
							null);
					dialogView.setFocusableInTouchMode(true);
					dialogView.requestFocus();
					dialogView.setBackgroundDrawable(mDialog);
	
					final AlertDialog dialog = new AlertDialog.Builder(mContext)
							.setView(dialogView).create();
					dialog.setCancelable(false);
					dialog.show();
					dialogView.findViewById(R.id.toNextHyper).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
									restartGame();
								}
							});
	
		
					dialogView.findViewById(R.id.menu).setOnClickListener(
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
									((MixedColorActivity) mContext).finish();
								}
							});
				}

			}
		};
		
		mBaseSettings = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, Context.MODE_PRIVATE); 
		mGameSettings = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, Context.MODE_PRIVATE); 
		
		this.rate = mBaseSettings.getFloat("rate", 1f);

		hyperSet = new int[SET_NUMBER][2];
		
		if(mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_SEQUENCE, true))
			System.arraycopy(MixedConstant.HYPERSET1, 0, hyperSet, 0, SET_NUMBER);
		else
			System.arraycopy(MixedConstant.HYPERSET2, 0, hyperSet, 0, SET_NUMBER);
		

		this.setCounter = mGameSettings.getInt("setCounter", 0);
		this.hyperCounter = mGameSettings.getInt("hyperCounter", 0);
		this.blockCounter = mGameSettings.getInt("blockCounter", 0);
		Log.i("initial block counter", blockCounter+"");
		
		initRes();
		
		mUIThread = new MixedThread(holder, context, mHandler);
		
		mgr = new DBManager(mContext);
		this.orintation = mBaseSettings.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		getObjectInfo();
		
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
				R.drawable.settingrain1);
		if(blockCounter == 0){
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingrain1);
		}else if(blockCounter == 1){
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingfood2);
		}else if(blockCounter == 2){
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingfriend5);
		}else if(blockCounter == 3){
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingcold2);
		}else if(blockCounter == 4){
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settinghome);
		}
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
		mUIThread = new MixedThread(this.getHolder(), this.getContext(), mHandler);
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
		Log.i("initRes","initRes");
		mTimeTotalImage = mContext.getResources().getDrawable(
				R.drawable.time_total);
		mTimeExpendImage = mContext.getResources().getDrawable(
				R.drawable.time_expend);

		if(blockCounter == 0) {
			mCharacter = mContext.getResources().getDrawable(R.drawable.mainpablo);
			mGridChar = mContext.getResources().getDrawable(R.drawable.rain_penguin);
			mDestination = mContext.getResources().getDrawable(R.drawable.objectrain);
			mDialog = mContext.getResources().getDrawable(R.drawable.goalrain);
		}else if(blockCounter == 1){
			mCharacter = mContext.getResources().getDrawable(R.drawable.rain_penguin);
			mGridChar = mContext.getResources().getDrawable(R.drawable.goalfood1);
			mDestination = mContext.getResources().getDrawable(R.drawable.objectfood);
			mDialog = mContext.getResources().getDrawable(R.drawable.goalfood1);
		}else if(blockCounter == 2){
			mCharacter = mContext.getResources().getDrawable(R.drawable.gentleman_penguin);
			mGridChar = mContext.getResources().getDrawable(R.drawable.gentleman_penguin);
			mDestination = mContext.getResources().getDrawable(R.drawable.objectfriend2);
			mDialog = mContext.getResources().getDrawable(R.drawable.goalfriend1);
		}else if(blockCounter == 3){
			mCharacter = mContext.getResources().getDrawable(R.drawable.gentleman_penguin);
			mGridChar = mContext.getResources().getDrawable(R.drawable.goalcold2);
			mDestination = mContext.getResources().getDrawable(R.drawable.objectcold2);
			mDialog = mContext.getResources().getDrawable(R.drawable.goalcold1);
		}else if(blockCounter == 4){
			mCharacter = mContext.getResources().getDrawable(R.drawable.goalcold2);
			mGridChar = mContext.getResources().getDrawable(R.drawable.pinga);
			mDestination = mContext.getResources().getDrawable(R.drawable.objecthome);
			mDialog = mContext.getResources().getDrawable(R.drawable.goalhome);
		}
		
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
		curColor.setColor(Color.parseColor("#FFFFFF"));					
		colorBgMap.put(0, curColor); 
		
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

		private Handler mHandler;
		
		private boolean mRun = true;

		private UIModel mUIModel;

		public MixedThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mContext = context;
			mHandler = handler;

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
				
				
				if(flag == UIModel.GAME_STATUS_COMPLETE_SET){
					Log.i("GAME_STATUS_COMPLETE_SET", "GAME_STATUS_COMPLETE_SET");
					Log.i("timeRecorder", timeRecorder.toString());
					Log.i("blockCounter", blockCounter+"s");
					Log.i("hyperCounter", hyperCounter+"s");
					Log.i("SetCounter", setCounter+"s");
					
					hyperCounter++;
					mRun = false;
					clearSet(answerSet);
					setCounter = 0;

					if(hyperCounter < MixedConstant.BLOCK_NUMBER){
						Message message = new Message();
						Bundle bundle = new Bundle();
						bundle.putInt(MixedConstant.GAME_STATUS_COMPLETE_SET, UIModel.GAME_STATUS_COMPLETE_SET);
						message.setData(bundle);
						mHandler.sendMessage(message);
					}else{
						hyperCounter = 0;
						blockCounter++;
						mGameSettings.edit().putInt("blockCounter", blockCounter).commit();
						Log.i("Block Counter", blockCounter+"");
						Intent i = new Intent(mContext, SetCongratulation.class);
						mContext.startActivity(i);
					}
					
					
				}
				
				
				if ((mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_HARDMODE, false) && flag == UIModel.EFFECT_FLAG_MISS ) || 
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
			
			int distance = ((1020+20)-(250-20))/MixedConstant.HYPER_NUMBER;
			
			canvas.drawLine((250-20)+ distance * hyperCounter, 90, 1020+20, 90, new Paint(Paint.ANTI_ALIAS_FLAG));
			canvas.drawLine((250-20)+ distance * hyperCounter, 91, 1020+20, 91, new Paint(Paint.ANTI_ALIAS_FLAG));
			
			Paint circle_Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			circle_Paint.setStyle(Style.FILL);
			circle_Paint.setColor(Color.GRAY);
			
			for(int i = hyperCounter; i<= 5; i++){
				canvas.drawCircle((250-20)+distance*i, 90, 6, circle_Paint);
			}
			
			UIModel uiModel = mUIModel;

			FontMetrics fmsr = mGameMsgLeftPaint.getFontMetrics();
			canvas.drawText(uiModel.getStageText(), 5 * rate, 15 * rate
					- (fmsr.ascent + fmsr.descent), mGameMsgLeftPaint);

			mTimeTotalImage
					.setBounds((int) (mPaintArea.mMaxX / 2 - 80 * rate),
							(int) (15 * rate),
							(int) (mPaintArea.mMaxX / 2 + 80 * rate),
							(int) (25 * rate));
			
			
			mCharacter.setBounds(80, 30, 180, 150);
			mDestination.setBounds(950, 30, 1250, 150);
			
			if(blockCounter == 0){
				charWidth = 120;
				charHeight = 140;
				
				destWidth = 170;
				destHeight = 659/(945/destWidth);
				
				startXChar = 120 + hyperCounter*distance;
			}else if(blockCounter == 1){
				charWidth = 120;
				charHeight = 140;
				
				destWidth = 170;
				destHeight = 262/(425/destWidth);
				
				startXChar = 120 + hyperCounter*distance;
				
			}
			else if(blockCounter == 2){
				charWidth = 120;
				charHeight = 140;
				
				destWidth = 110;
				destHeight = 2400/(2001/destWidth);
				
				startXChar = 120 + hyperCounter*distance;
				startYDest = 20;
				
			}else if(blockCounter == 3){
				charWidth = 120;
				charHeight = 140;
				
				destWidth = 120;
				destHeight = 600/(495/destWidth);
				
				startXChar = 120 + hyperCounter*distance;
				startYDest = 20;
				
			}else if(blockCounter == 4){
				charWidth = 120;
				charHeight = 140;
				
				destWidth = 200;
				destHeight = 226/(607/destWidth);
				
				startXChar = 120 + hyperCounter*distance;
				startYDest = 40;
				
			}
			
			mCharacter.setBounds(startXChar, startYChar, startXChar+charWidth, startYChar+charHeight);
			mDestination.setBounds(startXDest, startYDest, startXDest+destWidth, startYDest+destHeight);
			
			mCharacter.draw(canvas);
			mDestination.draw(canvas);
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
				paint.setStyle(Style.STROKE);
				canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);
				 
				/*if(targetColors.indexOf(curColor) == firstPos ||
						targetColors.indexOf(curColor) == secondPos){
					
				}else
					canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);*/
				
				if (targetColors.indexOf(curColor) == firstPos && firstAns == -1) {
					int [] location = mUIModel.getGridLocation(firstPos);
					mGridChar.setBounds( location[0] + (mUIModel.getGridSize()-charWidth)/2,
							location[1]+10,
							location[0] + (mUIModel.getGridSize()-charWidth)/2 + charWidth, 
							location[1]+mUIModel.getGridSize());
					mGridChar.draw(canvas);
				}
				if(targetColors.indexOf(curColor) == secondPos && secondAns == -1){
					int [] location = mUIModel.getGridLocation(secondPos);
					mGridChar.setBounds( location[0] + (mUIModel.getGridSize()-charWidth)/2, 
							location[1]+10,
							location[0] + (mUIModel.getGridSize()-charWidth)/2 + charWidth, 
							location[1]+mUIModel.getGridSize());
					mGridChar.draw(canvas);
				}
				
				/*paint.setColor(color);
				paint.setStyle(Style.FILL);*/
			}
		}

		public void initUIModel(RectArea paintArea) {
			mUIModel = new UIModel(paintArea, orintation, timeRecorder, mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_HARDMODE, false));
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
