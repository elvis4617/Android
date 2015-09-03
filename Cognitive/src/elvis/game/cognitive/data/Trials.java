package elvis.game.cognitive.data;

public class Trials {
	private int trial;
	private int sets;
	private double chT;
	private double mvT;

	public int getSets() {
		return sets;
	}

	public int getTrial() {
		return trial;
	}

	public void setTrial(int trial) {
		this.trial = trial;
	}

	public Trials(int trial, int sets, double chT, double mvT) {
		super();
		this.trial = trial;
		this.sets = sets;
		this.chT = chT;
		this.mvT = mvT;
	}

	public void setSets(int sets) {
		this.sets = sets;
	}

	public double getChT() {
		return chT;
	}

	public void setChT(double chT) {
		this.chT = chT;
	}

	public double getMvT() {
		return mvT;
	}

	public void setMvT(double mvT) {
		this.mvT = mvT;
	}

	@Override
	public String toString() {
		return "Trials [trial=" + trial + ", sets=" + sets + ", chT=" + chT
				+ ", mvT=" + mvT + "]";
	}

}
