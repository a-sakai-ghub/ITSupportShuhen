package common;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.ibm.icu.text.Transliterator;

public class Util {

	/**
	 * コードマスタファイル名
	 */
	private static final String CODE_MASTER_PATH = "codemaster\\コードマスタ.xlsx";
	/**
	 * 流通項目通番セル番号
	 */
	private static final int DATA_NUM = 0;
	/**
	 * 西コードセル番号
	 */
	private static final int WEST_CODE = 3;
	/**
	 * 東コードセル番号
	 */
	private static final int EAST_CODE = 5;
	/**
	 * コード和名セル番号
	 */
	private static final int JAPANESE_NAME = 4;
	/**
	 * 統合SO番号
	 */
	private static final String A_INTG_NUM = "統合SO番号";
	/**
	 * 注文区分
	 */
	private static final String ORDER_DIVISION = "注文区分";
	/**
	 * サービス種別
	 */
	private static final String SERVICE_KIND = "サービス種別";
	/**
	 * 現場調査予定日
	 */
	private static final String BUILD_RESEARCH_DATE = "現場調査予定日";
	/**
	 * 一体化設計対象
	 */
	private static final String INTEGRATED_TARGET = "一体化設計対象";
	/**
	 * 社外申請有無
	 */
	private static final String OUTSIDE_EXISTENCE = "社外申請有無";
	/**
	 * 一体化申請有無
	 */
	private static final String INTEGRATED_EXISTENCE = "一体化申請有無";
	/**
	 * サ総工事有無
	 */
	private static final String SERVICE_EXISTENCE = "サ総工事有無";



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
		    	Cell numCell = row.getCell(DATA_NUM);
				Cell codeCell = row.getCell(EAST_CODE);

			    //通番と東コードが引数と一致したら、西コードを取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
			    		& String.valueOf(codeCell).equals(eastCode)) {
			    	Cell returnCell = row.getCell(WEST_CODE);
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
				Cell numCell = row.getCell(DATA_NUM);
				Cell codeCell = row.getCell(WEST_CODE);

				//通番と西コードが引数と一致したら、東コードを取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
						& String.valueOf(codeCell).equals(westCode)) {
					Cell returnCell = row.getCell(EAST_CODE);
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
				Cell numCell = row.getCell(DATA_NUM);
				Cell codeCell = row.getCell(WEST_CODE);

				//通番と西コードが引数と一致したら、和名を取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
						& String.valueOf(codeCell).equals(westCode)) {
					Cell returnCell = row.getCell(JAPANESE_NAME);
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
	 * itemName 判定項目名
	 * data 判定するデータ
	 * @return returnData 判定後項目
	 */
	public String judgeData(String itemName, String[] data) {

		String returnData = "";

		/**
		 * 統合SO番号
		 */
		if(itemName.equals(A_INTG_NUM)) {
			//[0]IpOpsSO番号 != null && [1]ARENASO番号 = nullの場合、IpOpsSO番号に2020付与
			if(data[0] != null && data[1] == null) {
				returnData = "2020" + data[0];

			//[0]IpOpsSO番号 = null && [1]ARENASO番号 != nullの場合、ARENASO番号上2桁切
			} else if(data[0] == null && data[1] != null) {
				returnData = data[1].substring(2);

			//[0]IpOpsSO番号 != null && [1]ARENASO番号 != nullの場合、ARENASO番号上2桁切
			} else if(data[0] != null && data[1] != null) {
				returnData = data[1].substring(2);
			}

		/**
		 * 注文区分
		 */
		} else if(itemName.equals(ORDER_DIVISION)) {
			//[0]注文種類が110の場合、注文区分は「11」
			if(data[0] == "110") {
				returnData = "11";

			//[0]注文種類が111の場合、注文区分は「10」
			}else if(data[0] == "111") {
				returnData = "10";

			//[0]注文種類が112の場合、注文区分は「01」
			}else if(data[0] == "112") {
				returnData = "01";

			//[0]注文種類が113の場合、注文区分は「11」
			}else if(data[0] == "113") {
				returnData = "11";

			//[0]注文種類が114の場合、注文区分は「10」
			}else if(data[0] == "114") {
				returnData = "10";

			//[0]注文種類が115の場合、注文区分は「01」
			}else if(data[0] == "115") {
				returnData = "01";

			//[0]注文種類が100の場合、注文区分は登録しない
			}else if(data[0] == "100") {
				returnData = "";

			//[0]注文種類が120の場合、注文区分は登録しない
			}else if(data[0] == "120") {
				returnData = "";
			}

		/**
		 * サービス種別
		 */
		} else if(itemName.equals(SERVICE_KIND)) {
			returnData = data[0];
			if(data[0].equals("102Q") || data[0].equals("102S")) {
				returnData = "102R";
			}

		/**
		 * 現場調査予定日
		 */
		} else if(itemName.equals(BUILD_RESEARCH_DATE)) {
			Date date1 = java.sql.Date.valueOf(data[0]);
			Date date2 = java.sql.Date.valueOf(data[1]);
			//[0]ビル調査日1[1]ビル調査日2
			if(date1.before(date2)) {
				returnData = date1.toString();
			//[0]ビル調査日1[1]ビル調査日2
			}else if(date1.after(date2)) {
				returnData = date2.toString();
			}

		/**
		 * 一体化設計対象
		 */
		} else if(itemName.equals(INTEGRATED_TARGET)) {
			//[0]Rat-exam-sits = 5 && [1]e-acc-wit = q. の場合、「01:対象」
			if(data[0] == "5" && data[1] == "q.") {
				returnData = "01";
			//それ以外の場合、「02:対象外とする」
			}else {
				returnData = "02";
			}

		/**
		 * 社外申請有無
		 */
		} else if(itemName.equals(OUTSIDE_EXISTENCE)) {
			//[0]G-elf-req-flag = 1 && [1]g-rd-cop-req-flg = 1 の場合、「1」
			if(data[0] == "1" && data[1] == "1") {
				returnData = "1";
			//それ以外の場合、「0」
			}else {
				returnData = "0";
			}

		/**
		 * 一体化設計有無
		 */
		} else if(itemName.equals(INTEGRATED_EXISTENCE)) {
			//[0]Rmt-exam-sts = 5 && [1].e-acc-wit = 1 の場合、「1」
			if(data[0] == "5" && data[1] == "1") {
				returnData = "1";
			//それ以外の場合、「0」
			}else {
				returnData = "0";
			}

		/**
		 * サ総工事有無
		 */
		} else if(itemName.equals(SERVICE_EXISTENCE)) {
			//[0]Rmt-exam-sts != 5 && [1].e-acc-wit = 1 の場合、「1」
			if(data[0] == "5" && data[1] == "1") {
				returnData = "1";
			//それ以外の場合、「0」
			}else {
				returnData = "0";
			}
		}

		return returnData;
	}

	/**
	 * 修正箇所
	 * targetData 対象データ
	 * historyData 履歴情報
	 * @return returnData 修正箇所
	 */
	public String fixLocation(String[] targetData, String[] historyData) {

		String returnData = "";

		for(int i = 0; i < targetData.length; i++) {
			if(!targetData[i].equals(historyData[i])){
				returnData = returnData + "," + targetData[i];
			}
		}

		return returnData;
	}




}