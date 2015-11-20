package elvis.game.cognitive.material;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.graphics.RectF;
import android.util.Log;
import elvis.game.cognitive.data.ColorData;
import elvis.game.cognitive.data.RectArea;
import elvis.game.cognitive.utils.MixedConstant;

public class UIModel {


	public static final int FIELD_VIRGIN = 111;
	public static final int FIELD_MARK = 999;

	public static final int GAME_ATTRIBUTE_LEAST_COLOR = 9;
	public static final int GAME_ATTRIBUTE_TOTAL_STAGE = 7;
	public static final long GAME_ATTRIBUTE_MAX_TIME_PER_CLICK = 15000;
	public static final int GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT = 3;
	public static final int TOTAL_GRID_AMOUNT = GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT
			* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;

	public static final int GAME_STATUS_PAUSE = 11;
	public static final int GAME_STATUS_RUNNING = 12;
	public static final int GAME_STATUS_GAMEOVER = 13;
	
	public static final int GAME_STATUS_START = 10;
	public static final int GAME_STATUS_COMPLETE_SET = 14;

	public static final int EFFECT_FLAG_NO_EFFECT = 0;
	public static final int EFFECT_FLAG_PASS_FIRST = 1;
	public static final int EFFECT_FLAG_PASS = 2;
	public static final int EFFECT_FLAG_TIMEOUT = 3;
	public static final int EFFECT_FLAG_MISS = 4;

	public static final int UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_PORTRAIT = 60;
	public static final int UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_LANDSCAPE = 260;
	public static final int UI_ATTRIBUTE_TARGET_CELL_Y_MARGIN_LANDSCAPE = 25;
	public static final int UI_ATTRIBUTE_SOURCE_CELL_X_MARGIN = 25;
	public static final int UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP = 15;
	public static final int UI_ATTRIBUTE_INNER_PADDING_X = 120;

	private int mGameStatus;

	private RectArea mCanvasArea;

	private RectArea mSrcPaintArea;

	private RectArea mTarPaintArea;

	private RectArea[] mTarGrid;

	private ColorData mSrcColor;

	private List<ColorData> mTarColor = new ArrayList<ColorData>();

	private int[][] hyperSet;
	private int[][] answerSet;
	
	private int gridSize;
	
	private long chT;
	private long mvT;
	private long curTimeMillis;
	
	private boolean pauseBefore;
	
	private int mEffectFlag;

	private int mSetCounter;
	private int mSetNumber;

	private long mTimeLogger;
	private long mStageTime;
	private long led;
	
	private int col1, col2, col3;
	private int row0, row1, row2;
	
	public synchronized void updateUIModel() {
		if(pauseBefore) {
			pauseBefore = false;
			mTimeLogger = System.currentTimeMillis();
		}
		curTimeMillis = System.currentTimeMillis();
		mStageTime += curTimeMillis - mTimeLogger;
		mTimeLogger = curTimeMillis;
		
		if (mStageTime >= GAME_ATTRIBUTE_MAX_TIME_PER_CLICK) 
			mEffectFlag = EFFECT_FLAG_TIMEOUT;
	}

	public synchronized void buildStage() {
		mSetCounter++;
		if (mSetCounter < GAME_ATTRIBUTE_TOTAL_STAGE) {
			//buildPaintArea(GAME_ATTRIBUTE_LEAST_COLOR);
			mStageTime = 0;
			mTimeLogger = System.currentTimeMillis();
		} else {
			mGameStatus = GAME_STATUS_GAMEOVER;
		}
	}

	public void initStage() {
		mSetCounter = 0;
		mStageTime = 0;
		mTimeLogger = System.currentTimeMillis();
		buildPaintArea(GAME_ATTRIBUTE_LEAST_COLOR);
	}
	
	public void buildPaintArea(int colorAmount) {

		List<Integer> paintPos = new ArrayList<Integer>();

		for (int i = 0; i < colorAmount; i++) {
				paintPos.add(0);
		}

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
		int gridNumber = getGrid(x, y);
		Log.i("gridNumber", gridNumber+"");
		if(gridNumber != hyperSet[mSetCounter][0] && gridNumber != hyperSet[mSetCounter][1]){
			mEffectFlag = EFFECT_FLAG_NO_EFFECT;
			return;
		}
		if (answerSet[mSetCounter][0] == -1){
			if(gridNumber == hyperSet[mSetCounter][0]){
				answerSet[mSetCounter][0] = gridNumber;
				mEffectFlag = EFFECT_FLAG_PASS_FIRST;
				chT = mStageTime;
				mvT = 0;
				mStageTime = 0;
				mTimeLogger = System.currentTimeMillis();
			}else{
				mEffectFlag = EFFECT_FLAG_MISS;
				chT = 0;
				mvT = 0;
				Log.i("miss fist", "miss first");
			}
		}else if(answerSet[mSetCounter][1] == -1){
			if(gridNumber == hyperSet[mSetCounter][1]){
				if(mSetCounter == mSetNumber - 1){
					mEffectFlag = GAME_STATUS_COMPLETE_SET;
					mvT = mStageTime;
				}else{
					answerSet[mSetCounter][1] = gridNumber;
					mEffectFlag = EFFECT_FLAG_PASS;
					mvT = mStageTime;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						Log.d("", "Error at 'sleep 100'", e);
					}
					led = System.currentTimeMillis();
					buildStage();
				}
			}else{
				mEffectFlag = EFFECT_FLAG_MISS;
				mvT = 0;
				Log.i("miss second", "miss second");
			}
		}
		
	}

	private int getGrid(int x, int y) {
		if (y < 150) return -1;

		int col = 0;
		if(x >= col1 && x <= col1 +gridSize) col = 0;
		else if(x >= col2 && x <= col2 + gridSize) col = 1;
		else if(x >= col3 && x <= col3 + gridSize) col = 2;
		else col = -1;
		
		int row = 0;
		if(y >= row0 && y <= row0 +gridSize) row = 0;
		else if(y >= row1 && y <= row1 + gridSize) row = 1;
		else if(y >= row2 && y <= row2 + gridSize) row = 2;
		else row = -1;
	
		return row == -1 || col == -1 ? -1 : row * GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT + col;
	}
	
	public int[] getGridLocation(int x){
		int [] location = new int[2];
		location[0] = 260+(x%3)*gridSize+ (x%3)* UI_ATTRIBUTE_INNER_PADDING_X;
		location[1] = 170+(x/3)*gridSize+ (x/3)* UI_ATTRIBUTE_TARGET_CELL_Y_MARGIN_LANDSCAPE;
		return location;
	}
	
	public int getGridSize(){
		return gridSize;
	}
	
	public void resume(){
		pauseBefore = true;
	}
	
	public long getLed(){
			return led;
	}
	public void setLed(){
		led = 0;
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
		return hyperSet[mSetCounter][0];
	}

	public int getSecondPos() {
		return hyperSet[mSetCounter][1];
	}

	public int getFirstAns() {
		return answerSet[mSetCounter][0];
	}

	public int getSecondAns() {
		return answerSet[mSetCounter][1];
	}

	public String getStageText() {
		return (mSetCounter < GAME_ATTRIBUTE_TOTAL_STAGE ? (mSetCounter + 1)
				: mSetCounter)
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
		return 1 - (float) mStageTime / GAME_ATTRIBUTE_MAX_TIME_PER_CLICK;
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
	
	public int getmStageCounter() {
		return mSetCounter;
	}

	public void setmStageCounter(int mStageCounter) {
		this.mSetCounter = mStageCounter;
	}

	public long getChT() {
		return chT;
	}

	public long getMvT() {
		return mvT;
	}
	
	public void clearTimer(){
		chT = 0;
		mvT = 0;
	}


	public UIModel(RectArea canvasArea, int orintation, boolean hardmode) {
		mCanvasArea = canvasArea;
		mTarGrid = new RectArea[TOTAL_GRID_AMOUNT];
		
		int border = 0;
		int startX = 0;
		int startY = 0;
		
		if(orintation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
			border = 150;
			gridSize = (mCanvasArea.mMaxX - mCanvasArea.mMinX - 2* UI_ATTRIBUTE_INNER_PADDING_X - (GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT - 1)
					* UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_LANDSCAPE)
					/ GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			
			startX = UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_LANDSCAPE;
			startY = UI_ATTRIBUTE_TARGET_CELL_Y_MARGIN_LANDSCAPE;
			
			for (int i = 0; i < TOTAL_GRID_AMOUNT; i++) {
				int row = i/3;
				int col = i%3;
				
				if(row < i/3) row = i/3;
				if(col < i%3) col = i%3;
				
				mTarGrid[i] = new RectArea(
						startX + UI_ATTRIBUTE_INNER_PADDING_X *(col) + gridSize*(col), 
						border + (startY)*(row+1)+gridSize*(row),
						gridSize);
			}
					
		}else if(orintation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
			
			border = 400;
			gridSize = (mCanvasArea.mMaxX - mCanvasArea.mMinX - (GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT + 1)
					* UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_PORTRAIT)
					/ GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			startX = UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_PORTRAIT;
			startY = mCanvasArea.mMaxY
					- (gridSize + UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_PORTRAIT)
					* GAME_ATTRIBUTE_MATRIX_EDGE_GRID_AMOUNT;
			for (int i = 0; i < TOTAL_GRID_AMOUNT; i++) {
				mTarGrid[i] = new RectArea(
						startX + (gridSize + UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_PORTRAIT), startY
								+ (gridSize + UI_ATTRIBUTE_TARGET_CELL_X_MARGIN_PORTRAIT), gridSize);
			}
			Log.i("PORTRAIT", "PORTRAIT");
			
		}
		
		mSrcPaintArea = new RectArea(canvasArea.mMinX, canvasArea.mMinY
				+ UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP, canvasArea.mMaxX,
				border);

		mTarPaintArea = new RectArea(canvasArea.mMinX, border + UI_ATTRIBUTE_TARGET_PAINT_AREA_MARGIN_TOP,
				canvasArea.mMaxX, canvasArea.mMaxY);
		
		mTarColor = new ArrayList<ColorData>();
		
		//initStage();
		mGameStatus = GAME_STATUS_RUNNING;
		mEffectFlag = EFFECT_FLAG_NO_EFFECT;
		
		col1 = 250;
		col2 = 550;
		col3 = 840;
		
		row0 = 170;
		row1 = 370;
		row2 = 570;
		
		mSetNumber = MixedConstant.SET_NUMBER;
		pauseBefore = false;
		led = 0;
	}
}
