package elvis.game.cognitive.data;

import java.io.Serializable;

import elvis.game.cognitive.utils.MixedConstant;

public class TimeRecorder implements Serializable {

	private static final long serialVersionUID = -6021316927784505448L;

	private String subjectID;
	private String homeKeyTime;

	private int blockCounter;
	private int hyperCounter;
	private int setCounter;

	private String setLedOn;
	private int chT;
	private int mvT;

	public TimeRecorder() {
		super();
	}

	public TimeRecorder(String homeKeyTime) {
		super();
		this.homeKeyTime = homeKeyTime;
		this.setLedOn = MixedConstant.parseTime(System.currentTimeMillis());
	}
	
	
	public TimeRecorder(String subjectID, String homeKeyTime, int blockCounter,
			int hyperCounter, int setCounter, String setLedOn, int chT, int mvT) {
		super();
		this.subjectID = subjectID;
		this.homeKeyTime = homeKeyTime;
		this.blockCounter = blockCounter;
		this.hyperCounter = hyperCounter;
		this.setCounter = setCounter;
		this.setLedOn = setLedOn;
		this.chT = chT;
		this.mvT = mvT;
	}

	public String getSubjectID() {
		return subjectID;
	}

	public void setSubjectID(String subjectID) {
		this.subjectID = subjectID;
	}

	public String getHomeKeyTime() {
		return homeKeyTime;
	}

	public void setHomeKeyTime(String homeKeyTime) {
		this.homeKeyTime = homeKeyTime;
	}

	public int getBlockCounter() {
		return blockCounter;
	}

	public void setBlockCounter(int blockCounter) {
		this.blockCounter = blockCounter;
	}

	public int getHyperCounter() {
		return hyperCounter;
	}

	public void setHyperCounter(int hyperCounter) {
		this.hyperCounter = hyperCounter;
	}

	public int getSetCounter() {
		return setCounter;
	}

	public void setSetCounter(int setCounter) {
		this.setCounter = setCounter;
	}

	public String getSetLedOn() {
		return setLedOn;
	}

	public void setSetLedOn(String setLedOn) {
		this.setLedOn = setLedOn;
	}

	public int getChT() {
		return chT;
	}

	public void setChT(int chT) {
		this.chT = chT;
	}

	public int getMvT() {
		return mvT;
	}

	public void setMvT(int mvT) {
		this.mvT = mvT;
	}

	@Override
	public String toString() {
		return "TimeRecorder [subjectID=" + subjectID + ", homeKeyTime="
				+ homeKeyTime + ", blockCounter=" + blockCounter
				+ ", hyperCounter=" + hyperCounter + ", setCounter="
				+ setCounter + ", setLedOn=" + setLedOn + ", chT=" + chT
				+ ", mvT=" + mvT + "]";
	}
	
	

	

	

	

	

}
