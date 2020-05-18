package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.ibm.icu.text.Transliterator;

/**
 * 共通処理クラス
 * @author Mio Matsumoto
 *
 */
public class Util {

	private Util() {

	}

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
	 * 注文種類
	 */
	private static final String ORDER_KIND = "注文種類";
	/**
	 * サービス種別
	 */
	private static final String SERVICE_KIND = "サービス種別";
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
	 * IpOpsSO番号に付与
	 */
	private static final String ADD_IPOPS = "2020";



	/**
	 * ハイフンを除去するメソッド
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public static String removeHyphen(String targetData) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

		String returnData = "";

		//ハイフンを空文字に置き換える
		returnData = targetData.replace(Const.HYPHEN, Const.EMPTY_STRING);

		return returnData;
	}

	/**
	 * スラッシュを付与するメソッド（yyyyMMdd　→　yyyy/MM/dd）
	 * targetData 編集対象文字列
	 * @throws ParseException
	 * @return returnData 編集後文字列
	 */
	public static String grantSlash(String targetData) throws ParseException {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

		String returnData = "";

		//変換後の日付データフォーマットを設定する
		SimpleDateFormat format1 = new SimpleDateFormat(Const.SLASH_YYYYMMDD);
		//編集対象の日付データフォーマットを設定する
		SimpleDateFormat format2 = new SimpleDateFormat(Const.YYYYMMDD);
		//日付書式を変更する
		returnData = format1.format(format2.parse(targetData));

		return returnData;
	}

	/**
	 * スラッシュを削除するメソッド（yyyy/MM/dd → yyyyMMdd）
	 * targetData 編集対象文字列
	 * @throws ParseException
	 * @return returnData 編集後文字列
	 */
	public static String removeSlash(String targetData) throws ParseException {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

		String returnData = "";

		//変換後の日付データフォーマットを設定する
		SimpleDateFormat format1 = new SimpleDateFormat(Const.YYYYMMDD);
		//編集対象の日付データフォーマットを設定する
		SimpleDateFormat format2 = new SimpleDateFormat(Const.SLASH_YYYYMMDD);
		//日付書式を変更する
		returnData = format1.format(format2.parse(targetData));

		return returnData;
	}

	/**
	 * 文字列を結合するメソッド
	 * targetData 結合するデータ配列
	 * connection 接続文字
	 * @return returnData 編集後文字列
	 */
	public static String unionData(String[] targetData, String connection) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isAllEmpty(targetData)) {
			return "";
		}

		String returnData = "";
		//結合するデータ数を取得する
		int count = targetData.length;

		//データ数分ループし文字列を結合する
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
	public static String changeFullWidth(String targetData) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

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
	public static String changeHalfWidth(String targetData) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

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
	public static String cutDigit(String targetData, int digit, String direction) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

		String returnData = "";
		returnData = targetData;
		//編集対象文字列の桁数をカウントする
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
	 * 末尾の半角/全角スペースを削除するメソッド
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public static String trimSpace(String targetData) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

		String returnData = "";

		//半角・全角trimする
		returnData = targetData.replaceFirst("[\\h]+$", "");

		return returnData;
	}

	/**
	 * 記号を除去するメソッド
	 * targetData 編集対象文字列
	 * @return returnData 編集後文字列
	 */
	public static String removeSymbol(String targetData) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(targetData)) {
			return "";
		}

		String returnData = "";

		//記号除去する
		returnData = targetData.replaceAll("[\\p{Punct}]","");

		return returnData;
	}

	/**
	 * コード変換をするメソッド（東 → 西変換）
	 * dataNum 流通項目通番
	 * eastCode 変換対象コード値
	 * sheet エクセルシート情報
	 * @return westCode 変換後コード値
	 */
	public static String changeEastToWestCode(String dataNum, String eastCode, Sheet sheet) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(eastCode)) {
			return "";
		}

		String westCode = "";

		try {

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
	 * dataNum 流通項目通番
	 * westCode 変換対象コード値
	 * sheet エクセルシート情報
	 * @return eastCode 変換後コード値
	 */
	public static String changeWestToEastCode(String dataNum, String westCode, Sheet sheet) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(westCode)) {
			return "";
		}

		String eastCode = "";

		try {

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
	 * dataNum 流通項目通番
	 * westCode 変換対象コード値
	 * sheet エクセルシート情報
	 * @return japaneseName 変換後文字列
	 */
	public static String changeCodeToJapanese(String dataNum, String westCode, Sheet sheet) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(westCode)) {
			return "";
		}

		String japaneseName = "";

		try {

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
	 * 和名変換をするメソッド（和名→コード変換）
	 * dataNum 流通項目通番
	 * japaneseName 変換対象和名
	 * sheet エクセルシート情報
	 * @return japaneseName 変換後文字列
	 */
	public static String changeJapaneseToCode(String dataNum, String japaneseName, Sheet sheet) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(japaneseName)) {
			return "";
		}

		String westCode = "";

		try {

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				//行数の指定
				Row row = sheet.getRow(rowNum);
				//通番と和名取得セルを指定
				Cell numCell = row.getCell(DATA_NUM);
				Cell japaneseCell = row.getCell(JAPANESE_NAME);

				//通番と和名が引数と一致したら、西コードを取得し、forを抜ける
				if(String.valueOf(numCell).equals(dataNum)
						& String.valueOf(japaneseCell).equals(japaneseName)) {
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
	 * 記事欄編集
	 * itemName 項目名
	 * targetData 結合するデータ配列
	 * @return returnData 編集後文字列
	 */
	public static String editArticle(String[] itemName, String[] targetData) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isAllEmpty(targetData)) {
			return "";
		}

		String returnData = "";
		//結合するデータ数を取得する
		int count = itemName.length;

		//データ数分ループし文字列を結合する
		for (int i = 0; i < count - 1; i++) {
			returnData = returnData + itemName[i] + Const.COLON
					+ targetData[i] + Const.FULLWIDTH_SPACE + Const.SQUARE;
		}

		returnData = returnData + itemName[count - 1] + Const.COLON + targetData[count - 1];

		return returnData;
	}

	/**
	 * 記事欄抽出
	 * article 記事欄
	 * itemName 項目名
	 * @return returnData 編集後文字列
	 */
	public static String extractionArticle(String article, String itemName) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isEmpty(article)) {
			return "";
		}

		String returnData = "";

		//記事欄を項目ごとに区切る
		String[] articleData = article.split(Const.FULLWIDTH_SPACE + Const.SQUARE, 0);

		//記事欄の項目数
		int count = articleData.length;

		//記事欄の項目数分ループし、itemNameと一致した場合値をreturnDataに入れる
		for(int i = 0; i < count; i++) {
			boolean result = articleData[i].startsWith(itemName);
			if(result) {
				returnData = articleData[i].substring(itemName.length() + 1);
				break;
			}
		}

		//取得データを戻り値に設定する
		return returnData;
	}

	/**
	 * 判定項目（有無判定など）
	 * itemName 判定項目名
	 * data 判定するデータ
	 * @return returnData 判定後項目
	 */
	public static String judgeData(String itemName, String[] data) {

		//編集対象文字列がnullだった場合、空文字を返却する。
		if(StringUtils.isAllEmpty(data)) {
			return "";
		}

		String returnData = "";

		/**
		 * 統合SO番号
		 * SO番号より統合SO番号を生成する。
		 */
		if(itemName.equals(A_INTG_NUM)) {
			//data[0]IpOpsSO番号 != null && data[1]ARENASO番号 = nullの場合、IpOpsSO番号に2020付与
			if(data[0] != null && data[1] == null) {
				returnData = ADD_IPOPS + data[0];

			//data[0]IpOpsSO番号 = null && data[1]ARENASO番号 != nullの場合、ARENASO番号上2桁切
			} else if(data[0] == null && data[1] != null) {
				returnData = data[1].substring(2);

			//data[0]IpOpsSO番号 != null && data[1]ARENASO番号 != nullの場合、ARENASO番号上2桁切
			} else if(data[0] != null && data[1] != null) {
				returnData = data[1].substring(2);
			}

		/**
		 * 注文区分
		 */
		} else if(itemName.equals(ORDER_DIVISION)) {
			//data[0]注文種類が110の場合、注文区分は「11」
			if(data[0] == "110") {
				returnData = "11";

			//data[0]注文種類が111の場合、注文区分は「10」
			}else if(data[0] == "111") {
				returnData = "10";

			//data[0]注文種類が112の場合、注文区分は「01」
			}else if(data[0] == "112") {
				returnData = "01";

			//data[0]注文種類が113の場合、注文区分は「11」
			}else if(data[0] == "113") {
				returnData = "11";

			//data[0]注文種類が114の場合、注文区分は「10」
			}else if(data[0] == "114") {
				returnData = "10";

			//data[0]注文種類が115の場合、注文区分は「01」
			}else if(data[0] == "115") {
				returnData = "01";

			//data[0]注文種類が100の場合、注文区分は登録しない
			}else if(data[0] == "100") {
				returnData = "";

			//data[0]注文種類が120の場合、注文区分は登録しない
			}else if(data[0] == "120") {
				returnData = "";
			}

		/**
		 * 注文種類
		 */
		} else if(itemName.equals(ORDER_KIND)) {
			returnData = data[0];
			//data[0]注文種類が撤去の場合、注文種類を新設に設定
			if(data[0].equals("120")) {
				returnData = "100";
			}

		/**
		 * サービス種別
		 */
		} else if(itemName.equals(SERVICE_KIND)) {
			returnData = data[0];
			//data[0]サービス種別が102Qまたは102Sの場合、サービス種別を102Rに設定
			if(data[0].equals("102Q") || data[0].equals("102S")) {
				returnData = "102R";
			}

		/**
		 * 一体化設計対象
		 */
		} else if(itemName.equals(INTEGRATED_TARGET)) {
			//data[0]Rat-exam-sits = 5 && data[1]e-acc-wit = 9 の場合、「01:対象」
			if(data[0].equals("5") && data[1].equals("9")) {
				returnData = "01";
			//それ以外の場合、「02:対象外」とする
			}else {
				returnData = "02";
			}

		/**
		 * 社外申請有無
		 */
		} else if(itemName.equals(OUTSIDE_EXISTENCE)) {
			//data[0]G-elf-req-flag = 1 && data[1]g-rd-cop-req-flg = 1 の場合、「1」
			if(data[0].equals("1") && data[1].equals("1")) {
				returnData = "1";
			//それ以外の場合、「0」
			}else {
				returnData = "0";
			}

		/**
		 * 一体化設計有無
		 */
		} else if(itemName.equals(INTEGRATED_EXISTENCE)) {
			//data[0]Rmt-exam-sts = 5 && data[1].e-acc-wit = 1 の場合、「1」
			if(data[0].equals("5") && data[1].equals("1")) {
				returnData = "1";
			//それ以外の場合、「0」
			}else {
				returnData = "0";
			}

		/**
		 * サ総工事有無
		 */
		} else if(itemName.equals(SERVICE_EXISTENCE)) {
			//data[0]Rmt-exam-sts != 5 && data[1].e-acc-wit = 1 の場合、「1」
			if(data[0].equals("5") && data[1].equals("1")) {
				returnData = "1";
			//それ以外の場合、「0」
			}else {
				returnData = "0";
			}
		}

		return returnData;
	}

}