package common;

public class Util {


	/*
	 * ハイフン除去 サンプル
	 */
	public String removeHyphen(String data) {

		String returnData = "";

		returnData = data.replace(Const.HYPHEN, Const.EMPTY_STRING);

		return returnData;
	}

}
