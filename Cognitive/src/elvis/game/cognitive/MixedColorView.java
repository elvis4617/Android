package elvis.game.cognitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.Toast;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.ColorData;
import elvis.game.cognitive.data.CurCell;
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
	private Drawable mDestination;
	private Drawable mDialog;
	private Drawable go;
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
	private int[][] answerSet = { { -1, -1 }, { -1, -1 }, { -1, -1 },
			{ -1, -1 }, { -1, -1 }, { -1, -1 }, { -1, -1 } };

	private int setCounter;
	private int hyperCounter;
	private int blockCounter;

	private DBManager mgr;
	private int orintation;
	private List<TimeRecorder> timeRecorders;

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

	private String subject_name;
	private long home_key_time = System.currentTimeMillis();
	private long set_led_on = System.currentTimeMillis();

	public MixedColorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		;
		mHandler = new Handler() {
			public void handleMessage(Message m) {

				final LayoutInflater factory = LayoutInflater.from(mContext);
				final View dialogView = factory.inflate(
						R.layout.congratulation, null);
				dialogView.setFocusableInTouchMode(true);
				dialogView.requestFocus();
				dialogView.setBackground(mDialog);

				final AlertDialog dialog = new AlertDialog.Builder(mContext)
						.setView(dialogView).create();
				dialog.setCancelable(false);
				dialog.show();
				dialogView.findViewById(R.id.toNextHyper).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								if (hyperCounter < MixedConstant.HYPER_NUMBER)
									restartGame();
								else {
									hyperCounter = 0;
									blockCounter++;
									mGameSettings
											.edit()
											.putInt("blockCounter",
													blockCounter).commit();
									Log.i("Block Counter", blockCounter + "");
									mgr.add(timeRecorders);

									List<TimeRecorder> eles = mgr
											.queryForSubjects(mGameSettings
													.getString("subject_name",
															"unknow"));
									if (!eles.isEmpty()) {
										Log.i("in", "in");
										List<CurCell> data = new ArrayList<CurCell>();
										for (TimeRecorder t : eles) {
											data.add(new CurCell(eles
													.indexOf(t) + 1, 0, t
													.getSubjectID()));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 1, t
													.getBlockCounter() + 1 + ""));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 2, t
													.getHyperCounter() + 1 + ""));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 3, t
													.getHomeKeyTime()));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 4, t
													.getSetCounter() + 1 + ""));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 5, t
													.getSetLedOn()));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 6, t
													.getChT() + ""));
											data.add(new CurCell(eles
													.indexOf(t) + 1, 7, t
													.getMvT() + ""));
										}
										Log.i("subjet name", subject_name);
										MixedConstant.writeExcel(data,
												subject_name);
									}

									Intent i = new Intent(mContext,
											SetCongratulation.class);
									mContext.startActivity(i);
								}
							}
						});

				dialogView.findViewById(R.id.menu).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								final View dialogView = factory.inflate(
										R.layout.alertdialog, null);
								dialogView.setFocusableInTouchMode(true);
								dialogView.requestFocus();
								
								final AlertDialog alertDialog = new AlertDialog.Builder(mContext).setView(dialogView).create();
								alertDialog.setCancelable(false);
								alertDialog.show();
								
								final EditText pw = (EditText) dialogView.findViewById(R.id.password);
								
								dialogView.findViewById(R.id.password_exit).setOnClickListener(
										new OnClickListener() {
											@Override
											public void onClick(View v) {
												// TODO Auto-generated method stub
												if(pw.getText().toString().equals(MixedConstant.PREFERENCE_KEY_PASSWORD)){
													mgr.add(timeRecorders);
													mGameSettings.edit()
															.putString("subjectBase64", "")
															.commit();
													mGameSettings.edit().putInt("blockCounter", 0)
															.commit();
													mGameSettings.edit().putInt("hyperCounter", 0)
															.commit();
													mGameSettings.edit().putInt("setCounter", 0)
															.commit();

													
													List<TimeRecorder> eles = mgr
															.queryForSubjects(mGameSettings
																	.getString("subject_name",
																			"unknow"));
													if (!eles.isEmpty()) {
														Log.i("in", "in");
														List<CurCell> data = new ArrayList<CurCell>();
														for (TimeRecorder t : eles) {
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 0, t
																			.getSubjectID()));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 1, t
																			.getBlockCounter()
																			+ 1
																			+ ""));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 2, t
																			.getHyperCounter()
																			+ 1
																			+ ""));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 3, t
																			.getHomeKeyTime()));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 4, t
																			.getSetCounter()
																			+ 1
																			+ ""));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 5, t
																			.getSetLedOn()));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 6, t
																			.getChT() + ""));
															data.add(new CurCell(
																	eles.indexOf(t) + 1, 7, t
																			.getMvT() + ""));
														}
														Log.i("subjet name", subject_name);
														MixedConstant
																.writeExcel(data, subject_name);
													}
													
													alertDialog.dismiss();
													dialog.dismiss();
													
													Intent i = new Intent(mContext, Go.class);
													i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													mContext.startActivity(i);
												}else Toast.makeText(mContext, "Password invalid.", Toast.LENGTH_SHORT).show();
											}
										});
								dialogView.findViewById(R.id.password_cancel).setOnClickListener(
										new OnClickListener() {
											@Override
											public void onClick(View v) {
												alertDialog.dismiss();
											}
											
										});
							}
						});
			}
		};

		mBaseSettings = mContext.getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO,
				Context.MODE_PRIVATE);
		mGameSettings = mContext.getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO,
				Context.MODE_PRIVATE);

		this.rate = mBaseSettings.getFloat("rate", 1f);

		hyperSet = new int[MixedConstant.SET_NUMBER][2];

		if (mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_SEQUENCE,
				true))
			System.arraycopy(MixedConstant.HYPERSET1, 0, hyperSet, 0,
					MixedConstant.SET_NUMBER);
		else
			System.arraycopy(MixedConstant.HYPERSET2, 0, hyperSet, 0,
					MixedConstant.SET_NUMBER);

		this.setCounter = mGameSettings.getInt("setCounter", 0);
		this.hyperCounter = mGameSettings.getInt("hyperCounter", 0);
		this.blockCounter = mGameSettings.getInt("blockCounter", 0);

		Log.i("initial block counter", blockCounter + "");

		initRes();
		mgr = new DBManager(mContext);

		/*timeRecorders = (List<TimeRecorder>) MixedConstant
				.getObjectInfo(mContext);*/
		
		timeRecorders = new ArrayList<TimeRecorder>();
		subject_name = mGameSettings.getString("subject_name", "subject_"
				+ System.currentTimeMillis());
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
		Log.i("width", width + "");
		Log.i("height", height + "");
		mPaintArea = new RectArea(0, 0, width, height);
		mUIThread.initUIModel(mPaintArea);
		Canvas c = holder.lockCanvas();
		synchronized (holder) {
			mUIThread.doDraw(c);
		}
		if (c != null) {
			holder.unlockCanvasAndPost(c);
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("surfaceCreated", "surfaceCreated");
		mgr = new DBManager(mContext);
		mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.settingrain1);
		if (blockCounter == 0) {
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingrain1);
		} else if (blockCounter == 1) {
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingfood3);
		} else if (blockCounter == 2) {
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingfriend5);
		} else if (blockCounter == 3) {
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settingcold2);
		} else if (blockCounter == 4) {
			mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
					R.drawable.settinghome);
		}
		mUIThread = new MixedThread(this.getHolder(), this.getContext(),
				mHandler);
		mPaintArea = new RectArea(0, 0, 1280, 800);
		mUIThread.initUIModel(mPaintArea);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("surfaceDestroyed", "surfaceDestroyed");
		boolean retry = true;
		mUIThread.setRunning(false);
		mgr.closeDB();
		while (retry) {
			try {
				mUIThread.join();
				retry = false;
				if (!mBgImage.isRecycled())
					mBgImage.recycle();
			} catch (InterruptedException e) {
				Log.d("", "Surface destroy failure:", e);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& mUIThread.getRunning()) {
			mUIThread.checkSelection((int) event.getX(), (int) event.getY());
		} else if (event.getAction() == MotionEvent.ACTION_DOWN
				&& !mUIThread.getRunning()) {
			if (event.getX() > 450 && event.getX() < 850 && event.getY() > 250
					&& event.getY() < 650) {
				mUIThread.playSoundEffect(UIModel.GAME_STATUS_START);
				mUIThread.setRunning(true);
				clearSet(answerSet);
				mUIThread.getmUIModel().initStage();
				mUIThread.start();
				home_key_time = System.currentTimeMillis();
				set_led_on = System.currentTimeMillis();
			}
		}
		return true;
	}

	public void restartGame() {
		Log.i("restart", "restart");
		Log.i("mUIThread.getmUIModel().getEffectFlag()", mUIThread
				.getmUIModel().getEffectFlag() + "");
		mUIThread = new MixedThread(this.getHolder(), this.getContext(),
				mHandler);
		mUIThread.initUIModel(mPaintArea);
		mUIThread.setRunning(true);
		mUIThread.getmUIModel().initStage();
		mUIThread.start();
		home_key_time = System.currentTimeMillis();
		set_led_on = System.currentTimeMillis();
	}

	public void restartGameMiss() {
		Log.i("restart", "restart");
		home_key_time = System.currentTimeMillis();
		set_led_on = System.currentTimeMillis();
		mUIThread = new MixedThread(this.getHolder(), this.getContext(),
				mHandler);
		mUIThread.initUIModel(mPaintArea);
		mUIThread.setRunning(false);
		Canvas c = getHolder().lockCanvas();
		synchronized (c) {mUIThread.doDraw(c);}
		if (c != null) 
			getHolder().unlockCanvasAndPost(c);
	}

	private void clearSet(int[][] set) {
		for (int i = 0; i < MixedConstant.SET_NUMBER; i++) {
			set[i][0] = -1;
			set[i][1] = -1;
		}
	}

	@SuppressWarnings("deprecation")
	private void initRes() {
		Log.i("initRes", "initRes");
		mTimeTotalImage = mContext.getResources().getDrawable(
				R.drawable.time_total);
		mTimeExpendImage = mContext.getResources().getDrawable(
				R.drawable.time_expend);

		go = mContext.getResources().getDrawable(R.drawable.go);

		if (blockCounter == 0) {
			mCharacter = mContext.getResources().getDrawable(
					R.drawable.mainpablo);
			mDestination = mContext.getResources().getDrawable(
					R.drawable.objectrain);
			mDialog = mContext.getResources().getDrawable(R.drawable.ia1);
		} else if (blockCounter == 1) {
			mCharacter = mContext.getResources().getDrawable(
					R.drawable.rain_penguin);
			mDestination = mContext.getResources().getDrawable(
					R.drawable.objectfood);
			mDialog = mContext.getResources().getDrawable(R.drawable.ia21);
		} else if (blockCounter == 2) {
			mCharacter = mContext.getResources().getDrawable(
					R.drawable.gentleman_penguin);
			mDestination = mContext.getResources().getDrawable(
					R.drawable.objectfriend2);
			mDialog = mContext.getResources().getDrawable(R.drawable.ia31);
		} else if (blockCounter == 3) {
			mCharacter = mContext.getResources().getDrawable(
					R.drawable.gentleman_penguin);
			mDestination = mContext.getResources().getDrawable(
					R.drawable.objectcold2);
			mDialog = mContext.getResources().getDrawable(R.drawable.ia41);
		} else if (blockCounter == 4) {
			mCharacter = mContext.getResources().getDrawable(
					R.drawable.goalcold2);
			mDestination = mContext.getResources().getDrawable(
					R.drawable.objecthome);
			mDialog = mContext.getResources().getDrawable(R.drawable.ia51);
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
		soundPoolMap.put(UIModel.GAME_STATUS_COMPLETE_SET,
				soundPool.load(getContext(), R.raw.set_victory, 1));
		soundPoolMap.put(UIModel.GAME_STATUS_START,
				soundPool.load(getContext(), R.raw.start, 1));
	}

	public MixedThread getmUIThread() {
		return mUIThread;
	}

	public List<TimeRecorder> getSubjects() {
		return timeRecorders;
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

		private boolean mRun = false;
		private boolean mPause = false;

		private UIModel mUIModel;

		public MixedThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {
			mSurfaceHolder = surfaceHolder;
			mContext = context;
			mHandler = handler;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			while (mRun) {
				if(!mPause){
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
						// Thread.sleep(1);
					} catch (Exception e) {
						Log.d("", "Error at 'run' method", e);
					} finally {
						if (c != null) {
							mSurfaceHolder.unlockCanvasAndPost(c);
						}
					}
	
					if (flag == UIModel.EFFECT_FLAG_PASS) {
						Log.i("in", "in");
						saveSubject();
						Log.i("ArrayList", timeRecorders.toString());
						
						while(mUIModel.getLed() == 0){}
						set_led_on = mUIModel.getLed();
						mUIModel.setLed();
					}
	
					if (flag == UIModel.GAME_STATUS_COMPLETE_SET) {
						Log.i("GAME_STATUS_COMPLETE_SET",
								"GAME_STATUS_COMPLETE_SET");
						Log.i("blockCounter", blockCounter + "s");
						Log.i("hyperCounter", hyperCounter + "s");
						Log.i("SetCounter", setCounter + "s");
	
						saveSubject();
					
						hyperCounter++;
						mRun = false;
						clearSet(answerSet);
						
						setCounter = 0;
	
						Message message = new Message();
						Bundle bundle = new Bundle();
						message.setData(bundle);
						
						if (blockCounter == 0) {
							if (hyperCounter == 2)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia2);
							else if (hyperCounter == 3)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia3);
							else if (hyperCounter == 4)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia4);
							else if (hyperCounter == 5)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia5);
						} else if (blockCounter == 1) {
							if (hyperCounter == 2)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia22);
							else if (hyperCounter == 3)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia23);
							else if (hyperCounter == 4)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia24);
							else if (hyperCounter == 5)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia25);
						} else if (blockCounter == 2) {
							if (hyperCounter == 2)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia32);
							else if (hyperCounter == 3)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia33);
							else if (hyperCounter == 4)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia34);
							else if (hyperCounter == 5)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia35);
						} else if (blockCounter == 3) {
							if (hyperCounter == 2)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia42);
							else if (hyperCounter == 3)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia43);
							else if (hyperCounter == 4)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia44);
							else if (hyperCounter == 5)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia45);
						} else if (blockCounter == 4) {
							if (hyperCounter == 2)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia52);
							else if (hyperCounter == 3)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia54);
							else if (hyperCounter == 4)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.ia55);
							else if (hyperCounter == 5)
								mDialog = mContext.getResources().getDrawable(
										R.drawable.finalcongrats);
						}
						mHandler.sendMessage(message);
					}
	
					if ((mBaseSettings.getBoolean(
							MixedConstant.PREFERENCE_KEY_HARDMODE, true) && flag == UIModel.EFFECT_FLAG_MISS)
							|| flag == UIModel.EFFECT_FLAG_TIMEOUT) {
						Log.i("flag miss || flag timeout",
								"flag miss || flag timeout");
						mRun = false;
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							Log.d("", "Error at 'sleep 100'", e);
						}
	
						saveSubject();
						
						setCounter = 0;
						clearSet(answerSet);
						
						restartGameMiss();
					}
	
				}
			}
		}

		public void doDraw(Canvas canvas) {
			canvas.drawBitmap(mBgImage, 0, 0, new Paint(Paint.ANTI_ALIAS_FLAG));

			int distance = ((1020 + 20) - (250 - 20))
					/ MixedConstant.HYPER_NUMBER;

			canvas.drawLine((250 - 20) + distance * hyperCounter, 90,
					1020 + 20, 90, new Paint(Paint.ANTI_ALIAS_FLAG));
			canvas.drawLine((250 - 20) + distance * hyperCounter, 91,
					1020 + 20, 91, new Paint(Paint.ANTI_ALIAS_FLAG));

			Paint circle_Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			circle_Paint.setStyle(Style.FILL);
			circle_Paint.setColor(Color.GRAY);

			for (int i = hyperCounter; i <= MixedConstant.HYPER_NUMBER; i++) {
				canvas.drawCircle((250 - 20) + distance * i, 90, 6,
						circle_Paint);
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

			if (blockCounter == 0) {
				charWidth = 120;
				charHeight = 140;

				destWidth = 170;
				destHeight = 659 / (945 / destWidth);

				startXChar = 120 + hyperCounter * distance;
			} else if (blockCounter == 1) {
				charWidth = 120;
				charHeight = 140;

				destWidth = 170;
				destHeight = 262 / (425 / destWidth);

				startXChar = 120 + hyperCounter * distance;

			} else if (blockCounter == 2) {
				charWidth = 120;
				charHeight = 140;

				destWidth = 110;
				destHeight = 2400 / (2001 / destWidth);

				startXChar = 120 + hyperCounter * distance;
				startYDest = 20;

			} else if (blockCounter == 3) {
				charWidth = 120;
				charHeight = 140;

				destWidth = 120;
				destHeight = 600 / (495 / destWidth);

				startXChar = 120 + hyperCounter * distance;
				startYDest = 20;

			} else if (blockCounter == 4) {
				charWidth = 120;
				charHeight = 140;

				destWidth = 200;
				destHeight = 226 / (607 / destWidth);

				startXChar = 120 + hyperCounter * distance;
				startYDest = 40;

			}

			mCharacter.setBounds(startXChar, startYChar,
					startXChar + charWidth, startYChar + charHeight);
			mDestination.setBounds(startXDest, startYDest, startXDest
					+ destWidth, startYDest + destHeight);

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
			if (mRun) {
				// Log.i("mRun", mRun+"");
				int firstPos = mUIModel.getFirstPos();
				int secondPos = mUIModel.getSecondPos();
				int firstAns = mUIModel.getFirstAns();
				int secondAns = mUIModel.getSecondAns();

				List<ColorData> targetColors = uiModel.getTargetColor();
				for (ColorData curColor : targetColors) {
					Paint paint = colorBgMap.get(curColor.getMBgColor());
					paint.setColor(Color.WHITE);
					paint.setStyle(Style.STROKE);
					paint.setStrokeWidth(2.0f);
					canvas.drawRoundRect(curColor.getRectF(), 20, 20, paint);

					if (targetColors.indexOf(curColor) == firstPos
							&& firstAns == -1) {
						if (mBaseSettings.getBoolean(
								MixedConstant.PREFERENCE_KEY_DEBUG, true)) {
							paint.setColor(Color.RED);
							canvas.drawRoundRect(curColor.getRectF(), 20, 20,
									paint);
						}
						int[] location = mUIModel.getGridLocation(firstPos);
						mCharacter.setBounds(
								location[0]
										+ (mUIModel.getGridSize() - charWidth)
										/ 2, location[1] + 10, location[0]
										+ (mUIModel.getGridSize() - charWidth)
										/ 2 + charWidth,
								location[1] + mUIModel.getGridSize());
						mCharacter.draw(canvas);
						paint.setColor(Color.WHITE);
					}
					if (targetColors.indexOf(curColor) == secondPos
							&& firstAns != -1) {
						if (mBaseSettings.getBoolean(
								MixedConstant.PREFERENCE_KEY_DEBUG, true)) {
							paint.setColor(Color.RED);
							canvas.drawRoundRect(curColor.getRectF(), 20, 20,
									paint);
						}
						paint.setColor(Color.WHITE);
					}
					if (targetColors.indexOf(curColor) == secondPos
							&& secondAns == -1) {
						int[] location = mUIModel.getGridLocation(secondPos);
						mCharacter.setBounds(
								location[0]
										+ (mUIModel.getGridSize() - charWidth)
										/ 2, location[1] + 10, location[0]
										+ (mUIModel.getGridSize() - charWidth)
										/ 2 + charWidth,
								location[1] + mUIModel.getGridSize());
						mCharacter.draw(canvas);
					}

					// paint.setColor(color);
					paint.setStyle(Style.FILL);
				}
			} else {
				go.setBounds(400, 150, 900, 800);
				go.draw(canvas);
			}
		}

		public void initUIModel(RectArea paintArea) {
			mUIModel = new UIModel(paintArea, orintation,
					mBaseSettings.getBoolean(
							MixedConstant.PREFERENCE_KEY_HARDMODE, false));
			mUIModel.setHyperSet(hyperSet);
			mUIModel.setAnswerSet(answerSet);
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
				float volume = streamVolumeCurrent / streamVolumeMax;

				if (soundId == UIModel.EFFECT_FLAG_PASS
						|| soundId == UIModel.EFFECT_FLAG_PASS_FIRST)
					volume *= 0.1f;
				else if (soundId == UIModel.GAME_STATUS_COMPLETE_SET)
					volume *= 0.8f;
				else
					volume = 1.3f;

				soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, 0,
						1f);
			} catch (Exception e) {
				Log.d("PlaySounds", e.toString());
			}
		}
		
		public void saveSubject(){
			TimeRecorder subject = new TimeRecorder();
			subject.setSubjectID(subject_name);
			subject.setBlockCounter(blockCounter);
			subject.setHyperCounter(hyperCounter);
			subject.setSetCounter(setCounter++);
			subject.setSetLedOn(MixedConstant.parseTime(set_led_on));
			subject.setHomeKeyTime(MixedConstant
					.parseTime(home_key_time));
			subject.setChT((int) mUIModel.getChT());
			subject.setMvT((int) mUIModel.getMvT());

			timeRecorders.add(subject);

			MixedConstant.saveObject(mContext, timeRecorders,
					MixedConstant.SUBJECT_NAME);
			
			mUIModel.clearTimer();
		}

		public void delay(int ms){
			long timeLogger = System.currentTimeMillis();
			while(System.currentTimeMillis()-timeLogger < ms){}
		}
		public void setRunning(boolean run) {
			mRun = run;
		}

		public boolean getRunning() {
			return mRun;
		}
		
		public void setPause(boolean run) {
			mPause = run;
		}

		public boolean getPause() {
			return mPause;
		}

		public UIModel getmUIModel() {
			return mUIModel;
		}

	}// Thread

}
