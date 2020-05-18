package common;

import java.util.HashMap;
import java.util.Map;

public class Const {


	/* ツール区分：進捗管理反映 */
	public static final String SINTYOKU_KANRI_HANEI = "1";

	/* ツール区分：CSV出力 */
	public static final String CSV_OUTPUT = "2";

	/* ツール区分：SO外業務反映 */
	public static final String SOGAI_GYOUMU_HANEI = "3";

	/* ハイフン */
	public static final String HYPHEN = "-";

	/* 空文字 */
	public static final String EMPTY_STRING = "";

	/* yyyyMMdd形式の日付 */
	public static final String YYYYMMDD = "yyyyMMdd";

	/* yyyy/MM/dd形式の日付 */
	public static final String SLASH_YYYYMMDD = "yyyy/MM/dd";

	/* 半角から全角 */
	public static final String HALF_FULL = "Halfwidth-Fullwidth";

	/* 全角から半角 */
	public static final String FULL_HALF = "Fullwidth-Halfwidth";

	/* 先頭 */
	public static final String LEAD_STRING = "lead";

	/* 末尾 */
	public static final String END_STRING = "end";

	/* コロン */
	public static final String COLON = "：";

	/* 全角スペース */
	public static final String FULLWIDTH_SPACE = "　";

	/* 四角 */
	public static final String SQUARE = "■";

	/* カンマ */
	public static final String COMMA = ",";

	/* 全角「・」 */
	public static final String FULL_POINT = "・";

	/* プロパティファイル */
	public static final String PROPERTY_FILE_PATH = "property/";

	/* CSV出力ツール、2次回答返却ツール：プロパティファイル名 */
	public static final String CSV_PROPERTY_FILE_NAME = "CSVOutput.properties";

	/* コードマスタファイルパス */
	public static final String CODE_MASTER_FILE_PATH = "codemaster/";

	/* コードマスタファイル名 */
	public static final String CODE_MASTER_FILE_NAME = "コードマスタ.xlsx";

	/* 期待値ファイル出力先 */
	public static final String EXPECTED_FILE_PATH = "output/";

	/* CSV出力ツール、2次回答返却ツール：期待値ファイル */
	public static final String CSV_NIJIKAITOU_EXPECTED_FILE_NAME = "CSV出力・2次回答期待値ファイル.xlsx";

	/* SO外業務反映ツール：期待値ファイル */
	public static final String SOGAI_EXPECTED_FILE_NAME = "SO外業務期待値ファイル.xlsx";

	/* SO外業務反映ツール：編集情報ファイル */
	public static final String EDIT_INFO_SO_GAI = "EditInfo_SoGai.xlsx";

	/* テーブルリスト */
	public static final Map<String, String> TABLE_LIST = new HashMap<String, String>(){
		{
			put("CSV_OUTPUT_DATA","CSV出力用");
			put("JOB_INFO","工事情報(制御)");
			put("JOB_INFO_DETAIL", "工事情報(詳細)");
			put("JOB_INFO_DETAIL2", "工事情報(詳細2)");
		}
	};


	/* 統合SO番号 ：項目名 */
	public static final String A_INTG_SO_NUM_NAME = "統合SO番号";






}
