package common;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.ibm.icu.text.Transliterator;

public class Util {

	private static final String CODE_MASTER_PATH = "codemaster\\コードマスタ.xlsx";


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

		SimpleDateFormat format1 = new SimpleDateFormat(Const.SLASH_YYYYMMDD);
		SimpleDateFormat format2 = new SimpleDateFormat(Const.YYYYMMDD);
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

		SimpleDateFormat format1 = new SimpleDateFormat(Const.YYYYMMDD);
		SimpleDateFormat format2 = new SimpleDateFormat(Const.SLASH_YYYYMMDD);
		returnData = format1.format(format2.parse(targetData));

		return returnData;
	}

	/**
	 * 文字列を結合するメソッド
	 * targetData 結合するデータ配列
	 * count 結合するデータ数
	 * connection 接続文字
	 * @return returnData 編集後文字列
	 */
	public String unionData(String[] targetData, int count, String connection) {

		String returnData = "";

		for (int i = 0; i < count - 1; i++) {
			returnData = returnData + targetData[i] + connection;
		}

		returnData = returnData + targetData[count - 1];

		return returnData;
	}

	/**
	 * 全角変換をするメソッド（半角文字 →　全角文字）
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public String changeFullWidth(String targetData) {

		String returnData = "";

		Transliterator transliterator = Transliterator.getInstance(Const.HALF_FULL);
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

		Transliterator transliterator = Transliterator.getInstance(Const.FULL_HALF);
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

		//最長文字列よりも多い場合のみ処理を実施する
		if (count > digit) {
			//先頭を削除の場合
			if (direction.equals(Const.LEAD_STRING)) {
				returnData = targetData.substring(count - digit, count);
			//末尾を削除の場合
			}else if (direction.equals(Const.END_STRING)) {
				returnData = targetData.substring(0, digit);
			}
		}

		return returnData;
	}

	/**
	 * コード変換をするメソッド（東 → 西変換）
	 * eastCode 変換対象コード値
	 * dataNum 流通項目通番
	 * @return westCode 変換後コード値
	 */
	public String changeEastToWestCode(String dataNum, String eastCode) {

		String westCode = "";

		try {
			Workbook book = WorkbookFactory.create(new File(CODE_MASTER_PATH));
			//1シート目
			Sheet sheet = book.getSheetAt(0);

		    for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				//行数の指定
		    	Row row = sheet.getRow(rowNum);
				//通番と東コード取得セルを指定
		    	Cell numCell = row.getCell(0);
				Cell codeCell = row.getCell(5);

			    //通番と東コードが引数と一致したら、西コードを取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
			    		& String.valueOf(codeCell).equals(eastCode)) {
			    	Cell returnCell = row.getCell(3);
			    	westCode = returnCell.getStringCellValue();
			    	break;
			    }
		    }

		} catch (Exception e) {
			e.printStackTrace();
		}

		return westCode;
	}

	/**
	 * コード変換をするメソッド（西 → 東変換）
	 * westCode 変換対象コード値
	 * dataNum 流通項目通番
	 * @return eastCode 変換後コード値
	 */
	public String changeWestToEastCode(String dataNum, String westCode) {

		String eastCode = "";

		try {
			Workbook book = WorkbookFactory.create(new File(CODE_MASTER_PATH));
			//1シート目
			Sheet sheet = book.getSheetAt(0);

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				//行数の指定
				Row row = sheet.getRow(rowNum);
				//通番と西コード取得セルを指定
				Cell numCell = row.getCell(0);
				Cell codeCell = row.getCell(3);

				//通番と西コードが引数と一致したら、東コードを取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
						& String.valueOf(codeCell).equals(westCode)) {
					Cell returnCell = row.getCell(5);
					eastCode = returnCell.getStringCellValue();
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return eastCode;
	}

	/**
	 * 和名変換をするメソッド（コード → 和名変換）
	 * code 変換対象コード値
	 * dataNum 流通項目通番
	 * @return japaneseName 変換後文字列
	 */
	public String changeCodeToJapanese(String dataNum, String westCode) {

		String japaneseName = "";

		try {
			Workbook book = WorkbookFactory.create(new File(CODE_MASTER_PATH));
			//1シート目
			Sheet sheet = book.getSheetAt(0);

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				//行数の指定
				Row row = sheet.getRow(rowNum);
				//通番と西コード取得セルを指定
				Cell numCell = row.getCell(0);
				Cell codeCell = row.getCell(3);

				//通番と西コードが引数と一致したら、和名を取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
						& String.valueOf(codeCell).equals(westCode)) {
					Cell returnCell = row.getCell(4);
					japaneseName = returnCell.getStringCellValue();
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return japaneseName;
	}

	/**
	 * 記事欄編集
	 * itemName 項目名
	 * targetData 結合するデータ配列
	 * count 結合するデータ数
	 * @return returnData 編集後文字列
	 */
	public String editArticle(String[] itemName, String[] targetData, int count) {

		String returnData = "";

		for (int i = 0; i < count - 1; i++) {
			returnData = returnData + itemName[i] + Const.COLON
					+ targetData[i] + Const.FULLWIDTH_SPACE + Const.SQUARE;
		}

		returnData = returnData + itemName[count - 1] + Const.COLON + targetData[count - 1];

		return returnData;
	}

	/**
	 * 判定項目（有無判定など）
	 * targetData 判定するデータ
	 * @return returnData 判定後項目
	 */
	public String judgeData(String itemName) {

		String returnData = "";

		//G-elf-req-flag=1 or g-rd-cop-req-flg=1の場合　1 それ以外、0

		return returnData;
	}

	/**
	 * 修正箇所
	 * @return returnData 修正箇所
	 */


}