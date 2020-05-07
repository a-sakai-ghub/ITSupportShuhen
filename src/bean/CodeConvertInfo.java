package bean;

public class CodeConvertInfo {

	//コード変換有無
	private boolean boolConvert;

	//コード名
	private String codeId;

	//変換元
	private String beforeConvert;


	//変換先
	private String afterConvert;


	/**
	 * @return boolConvert
	 */
	public boolean isBoolConvert() {
		return boolConvert;
	}


	/**
	 * @param boolConvert セットする boolConvert
	 */
	public void setBoolConvert(boolean boolConvert) {
		this.boolConvert = boolConvert;
	}


	/**
	 * @return codeId
	 */
	public String getCodeId() {
		return codeId;
	}


	/**
	 * @param codeId セットする codeId
	 */
	public void setCodeId(String codeId) {
		this.codeId = codeId;
	}


	/**
	 * @return beforeConvert
	 */
	public String getBeforeConvert() {
		return beforeConvert;
	}


	/**
	 * @param beforeConvert セットする beforeConvert
	 */
	public void setBeforeConvert(String beforeConvert) {
		this.beforeConvert = beforeConvert;
	}


	/**
	 * @return afterConvert
	 */
	public String getAfterConvert() {
		return afterConvert;
	}


	/**
	 * @param afterConvert セットする afterConvert
	 */
	public void setAfterConvert(String afterConvert) {
		this.afterConvert = afterConvert;
	}



}
