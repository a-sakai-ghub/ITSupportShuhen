package common;

import java.text.SimpleDateFormat;

public class Util {




	/**
	 * ハイフンを除去するメソッド
	 */
	public String removeHyphen(String targetData) {

		String returnData = "";

		returnData = targetData.replace(Const.HYPHEN, Const.EMPTY_STRING);

		return returnData;
	}

	/**
	 * スラッシュを付与するメソッド
	 * yyyyMMdd　→　yyyy/MM/dd
	 */
	public String grantSlash(String targetData) {

		String returnData = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		returnData = format.format(targetData);

		return returnData;
	}

	/**
	 * スラッシュを削除するメソッド
	 * yyyy/MM/dd → yyyyMMdd
	 */
	public String removeSlash(String targetData) {

		String returnData = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        returnData = format.format(targetData);

		return returnData;
	}

	/**
	 * 文字列を結合するメソッド
	 */
	public String sample(String targetData1, String targetData2, String connection) {

		String returnData = "";

		returnData = targetData1 + connection + targetData2;

		return returnData;
	}

}