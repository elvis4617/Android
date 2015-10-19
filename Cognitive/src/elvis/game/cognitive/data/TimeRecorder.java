package elvis.game.cognitive.data;

import java.io.Serializable;
import java.util.Arrays;

public class TimeRecorder implements Serializable{

	private static final long serialVersionUID = -6021316927784505448L;
	
	private String subjectID;
	private int blockNumber;
	private int hyperNumber;
	private int homeKeyTime;
	private int setNumber;
	private int setLedOn;
	private double[][][] chT;
	private double[][][] mvT;

	public TimeRecorder(int blockNumber, int hyperNumber, int setNumber) {
		super();
		this.blockNumber = blockNumber;
		this.hyperNumber = hyperNumber;
		this.setNumber = setNumber;
		chT = new double[blockNumber][hyperNumber][setNumber];
		mvT = new double[blockNumber][hyperNumber][setNumber];
		
		for( int i = 0; i<5; i++){
			for(int j = 0; j<5; j++){
				for(int k = 0; k<5; k++){
					chT[i][j][k] = -1;
					mvT[i][j][k] = -1;
				}
			}
		}
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public int getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(int blockNumber) {
		this.blockNumber = blockNumber;
	}

	public int getHyperNumber() {
		return hyperNumber;
	}

	public void setHyperNumber(int hyperNumber) {
		this.hyperNumber = hyperNumber;
	}

	public int getHomeKeyTime() {
		return homeKeyTime;
	}

	public void setHomeKeyTime(int homeKeyTime) {
		this.homeKeyTime = homeKeyTime;
	}

	public int getSetNumber() {
		return setNumber;
	}

	public void setSetNumber(int setNumber) {
		this.setNumber = setNumber;
	}

	public int getSetLedOn() {
		return setLedOn;
	}

	public void setSetLedOn(int setLedOn) {
		this.setLedOn = setLedOn;
	}

	public double[][][] getChT() {
		return chT;
	}

	public void setChT(double[][][] chT) {
		this.chT = chT;
	}

	public double[][][] getMvT() {
		return mvT;
	}

	public void setMvT(double[][][] mvT) {
		this.mvT = mvT;
	}

	@Override
	public String toString() {
		return "TimeRecorder [subjectID=" + subjectID + ", blockNumber="
				+ blockNumber + ", hyperNumber=" + hyperNumber
				+ ", homeKeyTime=" + homeKeyTime + ", setNumber=" + setNumber
				+ ", setLedOn=" + setLedOn + ", chT=" + Arrays.toString(chT)
				+ ", mvT=" + Arrays.toString(mvT) + "]";
	}
	
	

}
