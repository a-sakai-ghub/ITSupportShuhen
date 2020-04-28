package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ibm.icu.text.Transliterator;

public class Util {

	/**
	 * ハイフンを除去するメソッド
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public String removeHyphen(String targetData) {

		String returnData = "";

		returnData = targetData.replace(Const.HYPHEN, Const.EMPTY_STRING);

		return returnData;
	}

	/**
	 * スラッシュを付与するメソッド（yyyyMMdd　→　yyyy/MM/dd）
	 * targetData 編集対象文字列
	 * @throws ParseException
	 * @return returnData 編集後文字列
	 */
	public String grantSlash(String targetData) throws ParseException {

		String returnData = "";

		SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
		returnData = format1.format(format2.parse(targetData));

		return returnData;
	}

	/**
	 * スラッシュを削除するメソッド（yyyy/MM/dd → yyyyMMdd）
	 * targetData 編集対象文字列
	 * @throws ParseException
	 * @return returnData 編集後文字列
	 */
	public String removeSlash(String targetData) throws ParseException {

		String returnData = "";

		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");
		returnData = format1.format(format2.parse(targetData));

		return returnData;
	}

	/**
	 * 文字列を結合するメソッド
	 * targetData1 結合するデータ1
	 * targetData2 結合するデータ2
	 * connection 接続文字
	 * @return returnData 編集後文字列
	 */
	public String unionData(String targetData1, String targetData2, String connection) {

		String returnData = "";

		returnData = targetData1 + connection + targetData2;

		return returnData;
	}

	/**
	 * 全角変換をするメソッド（半角文字 →　全角文字）
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public String changeFullWidth(String targetData) {

		String returnData = "";

		Transliterator transliterator = Transliterator.getInstance("Halfwidth-Fullwidth");
		returnData = transliterator.transliterate(targetData);

		return returnData;
	}

	/**
	 * 半角変換をするメソッド（全角文字 →　半角文字）
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public String changeHalfWidth(String targetData) {

		String returnData = "";

		Transliterator transliterator = Transliterator.getInstance("Fullwidth-Halfwidth");
		returnData = transliterator.transliterate(targetData);

		return returnData;
	}

	/**
	 * 桁切をするメソッド
	 * targetData 編集対象文字列
	 * digit 編集後桁数
	 * direction 桁切方向（先頭削除　or 末尾削除）
	 * @return returnData 編集後文字列
	 */
	public String cutDigit(String targetData, int digit, String direction) {

		String returnData = "";
		returnData = targetData;
		int count = targetData.length();

		if (count > digit) {
			//先頭を削除の場合
			if (direction.equals(Const.LEAD_STRING)) {
				returnData = targetData.substring(count-digit,count);
			//末尾を削除の場合
			}else if (direction.equals(Const.END_STRING)) {
				returnData = targetData.substring(0, digit);
			}
		}

		return returnData;
	}


}