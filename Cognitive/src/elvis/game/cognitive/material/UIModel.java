package elvis.game.cognitive.material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.RectF;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.ColorData;
import elvis.game.cognitive.data.RectArea;
import elvis.game.cognitive.data.Trials;

public class UIModel {

	public static final int TOTAL_COLOR_AMOUNT = 16;

	public static final int FIELD_VIRGIN = 111;
	public static final int FIELD_MARK = 999;

	public static final int GAME_ATTRIBUTE_LEAST_COLOR = 16;
	public static final int GAME_ATTRIBUTE_TOTAL_STAGE = 10;
	public static final long GAME_ATTRIBUTE_MAX_TIME_PER_STAGE = 10000;
	public static final int GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT = 4;
	public static final int TOTAL_GRID_AMOUNT = GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT
			* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;

	public static final int GAME_STATUS_PAUSE = 0;
	public static final int GAME_STATUS_RUNNING = 1;
	public static final int GAME_STATUS_GAMEOVER = 2;

	public static final int EFFECT_FLAG_NO_EFFECT = 0;
	public static final int EFFECT_FLAG_PASS_FIRST = 1;
	public static final int EFFECT_FLAG_PASS = 2;
	public static final int EFFECT_FLAG_TIMEOUT = 3;
	public static final int EFFECT_FLAG_MISS = 4;

	public static final int UI_ATTRIBUTE_TARGET_CELL_MARGIN = 10;// 4
	public static final int UI_ATTRIBUTE_SOURCE_CELL_X_MARGIN = 25;// 25
	public static final int UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP = 5;// 3
	public static final int UI_ATTRIBUTE_INNER_PADDING_Y = 10;// 7

	private int mGameStatus;

	private RectArea mCanvasArea;

	private RectArea mSrcPaintArea;

	private RectArea mTarPaintArea;

	private RectArea[] mTarGrid;

	private ColorData mSrcColor;

	private List<ColorData> mTarColor = new ArrayList<ColorData>();

	private int[][] hyperSet;
	private int[][] answerSet;
	private long chT[];
	private long mvT[];
	private DBManager mgr;
	private int trialCounter;

	private int mEffectFlag;

	private int mStageCounter;

	private long mTimeLogger;

	private long mStageTime;

	private long mTotalTime;

	public synchronized void updateUIModel() {
		long curTimeMillis = System.currentTimeMillis();
		mStageTime += curTimeMillis - mTimeLogger;
		mTimeLogger = curTimeMillis;
		
		if ((answerSet[mStageCounter][0] != -1 && answerSet[mStageCounter][1] == -1)
				|| (answerSet[mStageCounter][0] == -1 && answerSet[mStageCounter][1] != -1)) {
			if (chT[mStageCounter] == -1)
				chT[mStageCounter] = mStageTime;
		}
		
		if (mStageTime >= GAME_ATTRIBUTE_MAX_TIME_PER_STAGE) {
			mEffectFlag = EFFECT_FLAG_TIMEOUT;
			if(chT[mStageCounter] == -1)
				chT[mStageCounter] = GAME_ATTRIBUTE_MAX_TIME_PER_STAGE;
			else if(mvT[mStageCounter] == -1)
				mvT[mStageCounter] = mStageTime - chT[mStageCounter];
			
			mgr.add(new Trials(trialCounter, mStageCounter + 1,
					getChT()[mStageCounter], getMvT()[mStageCounter]));
			
			buildStage();
		}

	}

	public synchronized void buildStage() {
		mTotalTime += (mStageTime < GAME_ATTRIBUTE_MAX_TIME_PER_STAGE) ? mStageTime
				: GAME_ATTRIBUTE_MAX_TIME_PER_STAGE;
		mStageCounter++;
		if (mStageCounter < GAME_ATTRIBUTE_TOTAL_STAGE) {
			buildPaintArea(GAME_ATTRIBUTE_LEAST_COLOR);
			mStageTime = 0;
			mTimeLogger = System.currentTimeMillis();
		} else {
			mGameStatus = GAME_STATUS_GAMEOVER;
		}
	}

	public void initStage() {
		mStageCounter = 0;
		mStageTime = 0;
		mTotalTime = 0;
		mTimeLogger = System.currentTimeMillis();
		buildPaintArea(GAME_ATTRIBUTE_LEAST_COLOR);
	}

	public void buildPaintArea(int colorAmount) {

		List<Integer> paintPos = new ArrayList<Integer>();

		for (int i = 0; i < colorAmount; i++) {
				paintPos.add(i);
		}

		Collections.shuffle(paintPos);

		int curColor;
		ColorData curColorData;
		RectArea curRectArea;
		mTarColor.clear();
		for (int i = 0; i < colorAmount; i++) {
			curColor = paintPos.get(i);
			curColorData = new ColorData();
			curColorData.setMBgColor(curColor);

			curRectArea = mTarGrid[i];
			curColorData.mMinX = curRectArea.mMinX;
			curColorData.mMinY = curRectArea.mMinY;
			curColorData.mMaxX = curRectArea.mMaxX;
			curColorData.mMaxY = curRectArea.mMaxY;
			mTarColor.add(curColorData);
		}

	}

	public void checkSelection(int x, int y) {
		
		if (mStageCounter >= 10) {
			mEffectFlag = GAME_STATUS_GAMEOVER;
			return;
		}
		if (answerSet[mStageCounter][0] == -1
				&& answerSet[mStageCounter][1] == -1) {
			int gridNumber = getGrid(x, y);
			if (gridNumber == hyperSet[mStageCounter][0]) {
				answerSet[mStageCounter][0] = gridNumber;
				mEffectFlag = EFFECT_FLAG_PASS_FIRST;
			} else if (gridNumber == hyperSet[mStageCounter][1]) {
				answerSet[mStageCounter][1] = gridNumber;
				mEffectFlag = EFFECT_FLAG_PASS_FIRST;
			} else{
				mEffectFlag = EFFECT_FLAG_MISS;
				mgr.add(new Trials(trialCounter, mStageCounter + 1,
						getChT()[mStageCounter], getMvT()[mStageCounter]));
			}
			return;
		}
		if (answerSet[mStageCounter][0] != -1
				&& answerSet[mStageCounter][1] == -1) {
			int gridNumber = getGrid(x, y);
			if (gridNumber == hyperSet[mStageCounter][1]) {
				answerSet[mStageCounter][1] = gridNumber;
				mEffectFlag = EFFECT_FLAG_PASS;
				
				mvT[mStageCounter] = mStageTime - chT[mStageCounter];
				
				mgr.add(new Trials(trialCounter, mStageCounter + 1,
						getChT()[mStageCounter], getMvT()[mStageCounter]));
				buildStage();
			} else {
				mEffectFlag = EFFECT_FLAG_MISS;
				mgr.add(new Trials(trialCounter, mStageCounter + 1,
						getChT()[mStageCounter], getMvT()[mStageCounter]));
			}

			return;
		}
		if (answerSet[mStageCounter][0] == -1
				&& answerSet[mStageCounter][1] != -1) {
			int gridNumber = getGrid(x, y);
			if (gridNumber == hyperSet[mStageCounter][0]) {
				answerSet[mStageCounter][0] = gridNumber;
				mEffectFlag = EFFECT_FLAG_PASS;
				mvT[mStageCounter] = mStageTime - chT[mStageCounter];
				mgr.add(new Trials(trialCounter, mStageCounter + 1,
						getChT()[mStageCounter], getMvT()[mStageCounter]));
				buildStage();
			} else {
				mEffectFlag = EFFECT_FLAG_MISS;
				mgr.add(new Trials(trialCounter, mStageCounter + 1,
						getChT()[mStageCounter], getMvT()[mStageCounter]));
			}
			return;
		}
	}

	private int getGrid(int x, int y) {
		if (y < mCanvasArea.mMaxY - mCanvasArea.mMaxX
				- UI_ATTRIBUTE_INNER_PADDING_Y) {
			return -1;
		}
		int gridSize = (mCanvasArea.mMaxX - mCanvasArea.mMinX)
				/ GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
		int row = (y - (mCanvasArea.mMaxY - mCanvasArea.mMaxX - UI_ATTRIBUTE_INNER_PADDING_Y))
				/ gridSize;
		int col = x / gridSize;
		return row * 4 + col;
	}

	public RectF getSrcPaintArea() {
		return mSrcPaintArea.getRectF();
	}

	public RectF getTarPaintArea() {
		return mTarPaintArea.getRectF();
	}

	public List<ColorData> getTargetColor() {
		return mTarColor;
	}

	public ColorData getSourceColor() {
		return mSrcColor;
	}

	public int getFirstPos() {
		return hyperSet[mStageCounter][0];
	}

	public int getSecondPos() {
		return hyperSet[mStageCounter][1];
	}

	public int getFirstAns() {
		return answerSet[mStageCounter][0];
	}

	public int getSecondAns() {
		return answerSet[mStageCounter][1];
	}

	public String getStageText() {
		return (mStageCounter < GAME_ATTRIBUTE_TOTAL_STAGE ? (mStageCounter + 1)
				: mStageCounter)
				+ "/" + GAME_ATTRIBUTE_TOTAL_STAGE;
	}

	public long getStageTime() {
		return mStageTime;
	}

	public String toTimeText(long mStageTime) {
		String decimal = String.valueOf((mStageTime % 1000));
		if (decimal.length() < 3) {
			decimal += "0";
		}
		return mStageTime / 1000 + "." + decimal + "s";
	}

	public float getTimePercent() {
		return 1 - (float) mStageTime / GAME_ATTRIBUTE_MAX_TIME_PER_STAGE;
	}

	public float getFinalRecord() {
		return (float) mTotalTime / (mStageCounter * 1000);
	}

	public int getStatus() {
		return mGameStatus;
	}

	public int getEffectFlag() {
		try {
			return mEffectFlag;
		} finally {
			mEffectFlag = EFFECT_FLAG_NO_EFFECT;
		}
	}

	public int[][] getHyperSet() {
		return hyperSet;
	}

	public void setHyperSet(int[][] hyperSet) {
		this.hyperSet = hyperSet;
	}

	public int[][] getAnswerSet() {
		return answerSet;
	}

	public void setAnswerSet(int[][] answerSet) {
		this.answerSet = answerSet;
	}

	public double[] getChT() {
		double[] chT = new double[GAME_ATTRIBUTE_TOTAL_STAGE];
		for (int i = 0; i < GAME_ATTRIBUTE_TOTAL_STAGE; i++) {
			chT[i] = (double) this.chT[i] / 1000;
		}
		return chT;
	}

	public double[] getMvT() {
		double[] mvT = new double[GAME_ATTRIBUTE_TOTAL_STAGE];
		for (int i = 0; i < GAME_ATTRIBUTE_TOTAL_STAGE; i++) {
			mvT[i] = (double) this.mvT[i] / 1000;
		}
		return mvT;
	}

	public void setMgr(DBManager mgr) {
		this.mgr = mgr;
	}

	public void setTrialCounter(int trialCounter) {
		this.trialCounter = trialCounter;
	}
	
	
	public int getmStageCounter() {
		return mStageCounter;
	}

	public void setmStageCounter(int mStageCounter) {
		this.mStageCounter = mStageCounter;
	}

	public UIModel(RectArea canvasArea) {
		chT = new long[10];
		mvT = new long[10];

		for (int i = 0; i < chT.length; i++) {
			chT[i] = -1;
			mvT[i] = -1;
		}

		mCanvasArea = canvasArea;
		mSrcPaintArea = new RectArea(canvasArea.mMinX, canvasArea.mMinY
				+ UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP, canvasArea.mMaxX,
				canvasArea.mMaxY - canvasArea.mMaxX
						- UI_ATTRIBUTE_INNER_PADDING_Y * 2);

		mTarPaintArea = new RectArea(canvasArea.mMinX, canvasArea.mMaxY
				- canvasArea.mMaxX - UI_ATTRIBUTE_INNER_PADDING_Y,
				canvasArea.mMaxX, canvasArea.mMaxY);

		mTarGrid = new RectArea[TOTAL_GRID_AMOUNT];
		mTarColor = new ArrayList<ColorData>();
		int gridSize = (mCanvasArea.mMaxX - mCanvasArea.mMinX - (GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT + 1)
				* UI_ATTRIBUTE_TARGET_CELL_MARGIN)
				/ GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
		int startX = UI_ATTRIBUTE_TARGET_CELL_MARGIN;
		int startY = mCanvasArea.mMaxY
				- (gridSize + UI_ATTRIBUTE_TARGET_CELL_MARGIN)
				* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
		int posOffsetX;
		int posOffsetY;
		for (int i = 0; i < TOTAL_GRID_AMOUNT; i++) {
			posOffsetX = i % GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			posOffsetY = i / GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			mTarGrid[i] = new RectArea(
					startX + (gridSize + UI_ATTRIBUTE_TARGET_CELL_MARGIN)
							* posOffsetX, startY
							+ (gridSize + UI_ATTRIBUTE_TARGET_CELL_MARGIN)
							* posOffsetY, gridSize);
		}
		initStage();
		mGameStatus = GAME_STATUS_RUNNING;
		mEffectFlag = EFFECT_FLAG_NO_EFFECT;
	}
}
