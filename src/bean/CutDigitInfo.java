package bean;

public class CutDigitInfo {

	//桁切有無
	private boolean boolCutDigit;

	//桁数
	private int cutNum;

	//前後 0:前から桁切、1:後ろから桁切
	private int fromTo;

	/**
	 * @return boolCutDigit
	 */
	public boolean isBoolCutDigit() {
		return boolCutDigit;
	}

	/**
	 * @param boolCutDigit セットする boolCutDigit
	 */
	public void setBoolCutDigit(boolean boolCutDigit) {
		this.boolCutDigit = boolCutDigit;
	}

	/**
	 * @return cutNum
	 */
	public int getCutNum() {
		return cutNum;
	}

	/**
	 * @param cutNum セットする cutNum
	 */
	public void setCutNum(int cutNum) {
		this.cutNum = cutNum;
	}

	/**
	 * @return fromTo
	 */
	public int getFromTo() {
		return fromTo;
	}

	/**
	 * @param fromTo セットする fromTo
	 */
	public void setFromTo(int fromTo) {
		this.fromTo = fromTo;
	}



}
