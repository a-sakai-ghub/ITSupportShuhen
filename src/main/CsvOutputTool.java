package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.Const;
import common.Util;

/**
 * CSV出力ツール用期待値算出ツール。
 * @author kanri_aforce
 *
 */

public class CsvOutputTool {

	private static final String COMMA = ",";

	private static final String FULL_POINT = "・";

	private static final String PATH_CSV_PROPERTIES = "property/CSVOutput.properties";

	private static final String MAPKEY_TABLE_NAME="TABLE_NAME";

	private static final String MAPKEY_COL_ID="COL_ID";

	private static final String MAPKEY_CSV_TYPE="CSV_TYPE";

	private static final String MAPKEY_CSV_ITEM_NAME="CSV_ITEM_NAME";

	private static final String MAPKEY_EDIT_TYPE="EDIT_TYPE";

	private static final String EDIT_TYPE_NOTHING="1";

	private static final String EDIT_TYPE_REMOVE_SLASH="2";

	private static final String EDIT_TYPE_REMOVE_HYPHEN="3";

	private static final String EDIT_TYPE_UNION_DATA="4";

	private static final String EDIT_TYPE_CHANGE_HALF_WIDTH="5";

	private static final String EDIT_TYPE_CUT_DIGIT="6";

	private static final String CSV_OUTPUT_DATA="CSV_OUTPUT_DATA";

	private static final String JOB_INFO="JOB_INFO";

	private static final String JOB_INFO_DETAIL="JOB_INFO_DETAIL";

	private static final String JOB_INFO_DETAIL2="JOB_INFO_DETAIL2";

	/**
	 * Input2(試験結果CSVの情報)のキー情報をもとに、Input2(DB情報)から期待値を算出し、期待値シートに出力する。
	 * @param input1 DB情報
	 * @param input2 試験結果CSV情報
	 */
		public void expectCalculate(String input1, String input2) {
		/**
		 * 任意の値を設定
		 */
		//DB貼り付け情報（CSV出力用、工事情報(制御)、工事情報(詳細)、工事情報(詳細2)の4シートが記載されたファイル）
		input1 = "";
		//CSV貼り付け情報（試験結果のCSVの情報の1シート）
		input2 = "";

		//「期待値ファイル」のパスを指定
		String outputDir = "";
		// 現在日時を取得
		Date date = new Date();
		// 表示形式を指定
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String nowTime = sdf.format(date);
		File expectFile = new File(outputDir + "期待値_" + nowTime + ".xlsx");

		try {
			// Input1(DB情報)とInput2(試験結果CSV情報)を取得
			Workbook wb1 = WorkbookFactory.create(new FileInputStream(input1));
			Workbook wb2 = WorkbookFactory.create(new FileInputStream(input2));
			Sheet input2Sheet = wb2.getSheetAt(0);
			Row input2Row = input2Sheet.getRow(0);

			//	項目名リストを定義
			List <String> headerList = new ArrayList<>();
			//統合SO番号リストを定義
			List <String> soList = new ArrayList<>();

			//期待値を出力するファイルを作成
			FileOutputStream outputStream =  new FileOutputStream(expectFile);
			XSSFWorkbook wbExpect = new XSSFWorkbook();

			//書式を文字列に指定
			CellStyle textStyle = wbExpect.createCellStyle();
			DataFormat dataFormat = wbExpect.createDataFormat();
			textStyle.setDataFormat(dataFormat.getFormat("text"));
			//期待値ファイルのシートを作成
			wbExpect.createSheet("CSV出力_期待値");
			Sheet expectSheet = wbExpect.getSheetAt(0);

			Row expectRow = expectSheet.getRow(0);
			expectRow =expectSheet.createRow(0);
			Cell expectItemCell = null;

			for( int colIdx = 0;colIdx <= input2Row.getLastCellNum(); colIdx++ ) {
				Cell input2ItemCell = input2Row.getCell(colIdx);

				if(input2ItemCell == null) {
					break;
				}

				//項目名リストに項目名を追加
				headerList.add(String.valueOf(input2ItemCell));
				//期待値シートの書式を文字列（項目名）を出力
				//期待値シートに出力する行数を指定
				expectItemCell = expectRow.createCell(colIdx);
				expectItemCell.setCellStyle(textStyle);
				expectItemCell.setCellValue(input2ItemCell.getStringCellValue());

				// 1行目の項目名から「統合SO番号」列の位置を特定
				if(String.valueOf(input2ItemCell).equals( "統合SO番号" )) {

					//表示されている統合SO番号の最終列までループ
					for(int rowIdx = 1; rowIdx <= input2Sheet.getLastRowNum(); rowIdx++ ) {

						//統合SO番号リストに統合SO番号を格納
						soList.add(String.valueOf(input2Sheet.getRow(rowIdx).getCell(colIdx)));
					}
				}
			}

			// CSV情報から取得した統合SO番号分ループ
			for( int i=0; i < soList.size(); i++ ){

				//SO番号を取得
				String targetSoNum = soList.get(i);
				//期待値シートの行を作成（＝INPUT2のCSV情報と同一の行）
				expectRow = expectSheet.getRow(i + 1);
				expectRow = expectSheet.createRow(i + 1);

				//試験結果CSV情報から取得した、CSV項目名リストの項目ごとにループ
				for( int h = 0 ; h < headerList.size();h++ ) {

					//項目名を取得
					String headerName = headerList.get(h);

					//項目名からプロパティファイルの情報を取得する。
					Map<String,String> propMap = getProp(headerName);

					if( ! headerName.equals(propMap.get(MAPKEY_CSV_ITEM_NAME)) ) {
						continue;
					}

					String[] dbList = getDbList(propMap);

					List<String> targetValues = new ArrayList<>();

					//取得元DBの数分ループ
					for(int d = 0; d < dbList.length; d++) {

						String dbName =dbList[d];

						//プロパティファイルのテーブル情報から、参照先シート名を取得。
						Sheet dbSheet = wb1.getSheet(getSheetName(dbName));

						targetValues = getDbRecord(dbSheet, targetSoNum, propMap);
					}

						String expectValue = editValue( targetValues, propMap );

						//期待値シートの行を指定し（＝INPUT2のCSV情報と同一のセル）、書式を期待値を出力
						expectItemCell = expectRow.createCell(h);
						expectItemCell.setCellStyle(textStyle);
						expectItemCell.setCellValue(expectValue);
				}
			}
			//後処理
			wbExpect.write(outputStream);
			outputStream.close();
			wbExpect.close();
			System.out.println("処理終了");
		} catch (Exception e) {
			e.printStackTrace();
			if(expectFile.exists()) {
				expectFile.delete();
			}
		}
	}

	/**
	 * 指定したDBシートから、統合SO番号とCSV項目名をもとに、DBカラム1項目を取得する。
	 * @param dbSheet シート名
	 * @param soNum 統合SO番号
	 * @param propMap プロパティファイル名
	 * @return カラム値リスト
	 */
	private static List<String> getDbRecord(Sheet dbSheet, String soNum, Map<String,String> propMap) {

		List<String> valueList = new ArrayList<>();

		//DBシートの1列目から、統合SO番号の数分ループ
		for( int rowDbIdx = 0; rowDbIdx <= dbSheet.getLastRowNum(); rowDbIdx++ ) {

			//DBシートの1列目の統合SO番号とCSVの統合SO番号が一致しないものは排除
			if( ! String.valueOf(dbSheet.getRow(rowDbIdx).getCell(0)).equals(soNum) ) {
				continue;
			}

			Map <String, String> dbMap = new HashMap<>();

			//DBシートの1行目
			Row idRow = dbSheet.getRow(0);
			//DBシートの1行目カラムIDの数分ループ
			for( int colDbIdx = 0;colDbIdx < idRow.getLastCellNum(); colDbIdx++ ) {
				//DBシートの対象の行をMapに格納
				dbMap.put( String.valueOf(dbSheet.getRow(0).getCell(colDbIdx)),
						String.valueOf(dbSheet.getRow(rowDbIdx).getCell(colDbIdx)) );
			}

			//DBMapから期待値を算出
			valueList.add( dbMap.get(propMap.get(MAPKEY_COL_ID)) );
		}

		return valueList;

	}

	private static String[] getDbList(Map<String,String> propMap) {

		String dbInfo = propMap.get(MAPKEY_TABLE_NAME);

		String[] dbList = dbInfo.split(FULL_POINT);

		return dbList;
	}

	/**
	 * プロパティファイルからキーを指定し、値を取得して情報をMapに格納する。
	 * @param key CSV項目名
	 * @return プロパティ情報Map
	 * @throws IOException
	 */
	private static Map<String,String> getProp(String key) throws IOException {

		Properties prop = new Properties();
		prop.load(new FileInputStream(PATH_CSV_PROPERTIES));

		String propInfo = String.valueOf(prop.get(key));
		String[] propList = propInfo.split(COMMA);

		Map <String, String> propMap = new HashMap<>();
		propMap.put(MAPKEY_TABLE_NAME, propList[0]);
		propMap.put(MAPKEY_COL_ID, propList[1]);
		propMap.put(MAPKEY_CSV_TYPE, propList[2]);
		propMap.put(MAPKEY_CSV_ITEM_NAME, propList[3]);
		propMap.put(MAPKEY_EDIT_TYPE, propList[4]);

		return propMap;
	}

	/**
	 * プロパティファイル情報からシート名を取得。
	 * @param propMap プロパティファイル情報
	 * @return シート名
	 */
	private static String getSheetName(String dbName) {

		String sheetName = "";

		if( CSV_OUTPUT_DATA.equals(dbName) ) {
			sheetName = "CSV出力用";
		}else if( JOB_INFO.equals(dbName) ) {
			sheetName = "工事情報(制御)";
		}else if ( JOB_INFO_DETAIL.equals(dbName) ) {
			sheetName = "工事情報(詳細)";
		}else if ( JOB_INFO_DETAIL2.equals(dbName) ) {
			sheetName = "工事情報(詳細2)";
		}
		return sheetName;
	}

	/**
	 * プロパティファイルの項目編集方法情報より、ValueをUtilの各編集メソッドに通す。
	 * @param valueList DBシート対応値(項目編集前)
	 * @param propMap プロパティ情報Map
	 * @return 期待値（編集後値）
	 * @throws ParseException
	 */
	private static String editValue(List<String> valueList, Map <String, String>propMap ) throws ParseException {

		String[] editValues = new String [valueList.size()];
		String value = "";
		int i = 0;

		if( valueList.size() != 1) {
			for( i = 0 ; i < valueList.size(); i++ ) {
				editValues[i] = valueList.get(i);
			}
		}else {
			value = valueList.get(0);
		}

		//編集後の値
		String editedValue = value;

		//プロパティファイルから編集方法情報を取得する。
		String editType = propMap.get(MAPKEY_EDIT_TYPE);
		Util util = new Util();

		//項目編集なし
		if( editType.equals(EDIT_TYPE_NOTHING) ) {
			editedValue = value;
		}
		//スラッシュ除去
		if( editType.equals(EDIT_TYPE_REMOVE_SLASH)) {
			editedValue = util.removeSlash(value);
		}
		//ハイフン除去
		if( editType.equals(EDIT_TYPE_REMOVE_HYPHEN)) {
			editedValue = util.removeHyphen(value);
		}
		//文字列結合
		if( editType.equals(EDIT_TYPE_UNION_DATA)) {
			editedValue = util.unionData(editValues, i, Const.EMPTY_STRING);
		}
		//全角→半角変換
		if( editType.equals(EDIT_TYPE_CHANGE_HALF_WIDTH)) {
			editedValue = util.changeHalfWidth(value);
		}
		//桁切り
		if( editType.equals(EDIT_TYPE_CUT_DIGIT)) {
			//	editedValue = util.cutDigit(value);
		}
		return editedValue;
	}

}
