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

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import common.Const;
import common.Util;


/**
 * CSV出力・２次回答返却ツール期待値ファイル作成クラス
 * @author Hiroki Fujiwara
 *
 */
public class CsvOutputTool {

	private static final String MAPKEY_TABLE_NAME = "TABLE_NAME";

	private static final String MAPKEY_COL_ID = "COL_ID";

	private static final String MAPKEY_CSV_TYPE = "CSV_TYPE";

	private static final String MAPKEY_CSV_HEADER_NAME = "CSV_HEADER_NAME";

	private static final String MAPKEY_EDIT_TYPE = "EDIT_TYPE";

	private static final String MAPKEY_OTHER_INFO = "OTHER_INFO";

	private static final String COL_A_INTG_SO_NUM = "A_INTG_SO_NUM";

	private static final String SO_NUM = "SO番号";

	private static final String TEXT_STYLE = "text";

	/*	編集なし	*/
	private static final String EDIT_TYPE_NOTHING = "1";
	/*	スラッシュ除去	*/
	private static final String EDIT_TYPE_REMOVE_SLASH = "2";
	/*	ハイフン除去	*/
	private static final String EDIT_TYPE_REMOVE_HYPHEN = "3";
	/*	文字列結合	*/
	private static final String EDIT_TYPE_UNION_DATA = "4";
	/*	全角→半角変換	*/
	private static final String EDIT_TYPE_CHANGE_HALF_WIDTH = "5";
	/*	桁切り	*/
	private static final String EDIT_TYPE_CUT_DIGIT = "6";
	/*	東コード→西コード	*/
	private static final String EDIT_TYPE_CHANGE_EAST_TO_WEST_CODE = "7";
	/*	西コード→西和名	*/
	private static final String EDIT_TYPE_CHANGE_CODE_TO_JAPANESE = "8";
	/*	西和名→西コード	*/
	private static final String EDIT_TYPE_CHANGE_JAPANESE_TO_CODE = "9";
	/* 記事欄抽出 */
	private static final String EDIT_TYPE_EXTRACTION_ARTICLE="10";
	/*	有無項目 */
	private static final String EDIT_TYPE_UMU="11";

	/**
	 * Input2(試験結果CSVの情報)のキー情報をもとに、Input2(DB情報)から期待値を算出し、期待値シートに出力する。
	 * @param input1 DB情報
	 * @param input2 試験結果CSV情報
	 * @throws IOException
	 */
	public void expectCalculate(String input1, String input2) {

		//	public static void main(String[] args) {
		print("・・・・・・・・・・・・・・・・・・・・・・・・・・・・CsvOutputTool.java処理開始・・・・・・・・・・・・・・・・・・・・・・・・・・・・");


		//「期待値ファイル」のパスを指定
		String outDir = "C:\\Sample\\" + Const.EXPECTED_FILE_PATH;

		//期待値ファイルを作成
		File expectFile = new File( outDir
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + Const.CSV_NIJIKAITOU_EXPECTED_FILE_NAME);

		try ( Workbook wb1 = WorkbookFactory.create(new FileInputStream(input1));
				Workbook wb2 = WorkbookFactory.create(new FileInputStream(input2));
				Workbook wbCodeM = WorkbookFactory.create(
						new File(Const.CODE_MASTER_FILE_PATH + Const.CODE_MASTER_FILE_NAME));
				FileOutputStream out = new FileOutputStream(expectFile);
				XSSFWorkbook wbExpect = new XSSFWorkbook();) {

			// Input2の1シート目を取得
			Sheet input2Sheet = wb2.getSheetAt(0);

			CellStyle textStyle = wbExpect.createCellStyle();
			textStyle.setDataFormat(wbExpect.createDataFormat().getFormat(TEXT_STYLE));
			Sheet csvSheet = wbExpect.createSheet("CSV出力");
			Sheet expectSheet = wbExpect.createSheet("CSV出力_期待値");

			//読み込んだInput2のシートを、期待値ファイルの1シート目にコピーする。
			copySheets(csvSheet, input2Sheet);

			Row input2Row = input2Sheet.getRow(0);
			Row expectRow = createRow(expectSheet, 0);

			// 項目名リストを定義
			List <String> headerList = new ArrayList<>();
			// 統合SO番号リストを定義
			List <String> soList = new ArrayList<>();

			//Input2の1行目(CSV項目名)を最終列までループ
			for( int cellNum = 0;cellNum < input2Row.getLastCellNum(); cellNum++ ) {

				String headerName = getCellValue(input2Row.getCell(cellNum));

				//項目名リストに項目名を追加。
				headerList.add(headerName);
				//項目名を期待値シートに出力する。
				setCell(expectRow, cellNum, textStyle, headerName);

				// 1行目の項目名から「統合SO番号」列の位置を特定し、統合SO番号を最終行まで取得しリストに格納。
				if( headerName.equals( Const.A_INTG_SO_NUM_NAME ) || headerName.equals( SO_NUM )){
					for(int rowNum = 1; rowNum <= input2Sheet.getLastRowNum(); rowNum++ ) {

						soList.add(getCellValue(input2Sheet.getRow(rowNum).getCell(cellNum)));
					}
				}
			}

			Properties prop = new Properties();
			prop.load(new FileInputStream(Const.PROPERTY_FILE_PATH + Const.CSV_PROPERTY_FILE_NAME));
			Sheet codeMSheet = wbCodeM.getSheetAt(0);

			// CSV情報から取得した統合SO番号分ループ
			for( int soIdx = 0; soIdx < soList.size(); soIdx++ ){
				//統合SO番号を取得
				String targetSoNo = soList.get(soIdx);
				expectRow = createRow(expectSheet, soIdx + 1);
				print("\n" + Const.A_INTG_SO_NUM_NAME + "：" + targetSoNo);

				//試験結果CSV情報から取得した、CSV項目名リストの項目ごとにループ
				for( int headerIdx = 0 ; headerIdx < headerList.size();headerIdx++ ) {

					//CSV項目名からプロパティファイルの情報を取得する。
					Map<String, String> propMap = getProp(prop, headerList.get(headerIdx));

					//プロパティファイルより、テーブル名を取得する
					String[] dbList = editProp(propMap, MAPKEY_TABLE_NAME, Const.FULL_POINT);

					List<String> targetValues = new ArrayList<>();

					//取得元DBの数分ループ
					for(int dbIdx = 0; dbIdx < dbList.length; dbIdx++) {
						//プロパティファイルのテーブル情報から、参照先シートを取得。
						Sheet dbSheet = wb1.getSheet(dbList[dbIdx]);

						//DBシートから取得した値をリストに格納
						targetValues.add(getDbData(dbSheet, targetSoNo, propMap, dbIdx));
					}

					//DBシートから取得したデータおよびプロパティファイル情報から、期待値を算出する。
					String expectValue = editValue( targetValues, propMap, codeMSheet );

					//期待値シートに期待値を出力する。
					setCell(expectRow, headerIdx, textStyle, expectValue);
					print("項目名 " + propMap.get(MAPKEY_CSV_HEADER_NAME) + " ● 期待値 " + expectValue);
				}
			}

			//書き出し
			wbExpect.write(out);
		} catch (Exception e) {
			e.printStackTrace();
			if(expectFile.exists()) {
				expectFile.delete();
				print("！削除して処理終了！");
			}
		}finally {
			print("\n・・・・・・・・・・・・・・・・・・・・・・・・・・・・CsvOutputTool.java処理終了・・・・・・・・・・・・・・・・・・・・・・・・・・・・");
		}
	}

	/**
	 *指定したDBシートから、統合SO番号およびカラムIDをキーに1カラム値を取得する。
	 * @param dbSheet DBシート
	 * @param soNo 統合SO番号
	 * @param propMap プロパティ情報Map
	 * @param dbIdx テーブルリストの○番目
	 * @return DB取得データ
	 */
	private static String getDbData(Sheet dbSheet, String soNo, Map<String,String> propMap, int dbIdx) {

		//カラムIDリスト
		String[] valueList = editProp(propMap, MAPKEY_COL_ID, Const.FULL_POINT);
		String targetValue = "";
		Row input1Row = dbSheet.getRow(0);

		for(int cellNum = 0; cellNum < input1Row.getLastCellNum(); cellNum++) {

			String dbColId = getCellValue(input1Row.getCell(cellNum));
			if( ! dbColId.equals( COL_A_INTG_SO_NUM ) ){
				continue;
			}

			//DBシートのA_INTG_SO_NUM列から、統合SO番号の数分ループ
			for( int rowNum = 0; rowNum <= dbSheet.getLastRowNum(); rowNum++ ) {
				//統合SO番号が一致しないものは、排除。
				if( ! getCellValue(dbSheet.getRow(rowNum).getCell(cellNum)).equals(soNo) ) {
					continue;
				}

				//「A_INTG_SO_NUM」列の統合SO番号とCSVの統合SO番号が一致したら、項目名をキーにレコードをMapに格納。
				Map <String, String> dbMap = new HashMap<>();
				for( int targetCellNum = 0; targetCellNum < input1Row.getLastCellNum(); targetCellNum++ ) {

					//DBシートの対象の行をMapに格納
					dbMap.put( getCellValue(dbSheet.getRow(0).getCell(targetCellNum)),
							getCellValue(dbSheet.getRow(rowNum).getCell(targetCellNum)) );
				}
				//DBMapから、カラムIDをキーに対象の値を取得。
				targetValue = dbMap.get(valueList[dbIdx]);
				break;
			}
		}
		return targetValue;
	}

	/**
	 * プロパティファイルからキーを指定し、値を取得して情報をMapに格納する。
	 * @param headerName CSV項目名
	 * @return プロパティ情報Map
	 * @throws IOException
	 */
	private static Map<String,String> getProp(Properties prop, String headerName) throws IOException {

		//CSV項目(キー)から、プロパティファイルの値を取得する。
		String propInfo = String.valueOf(prop.get(headerName));
		String[] propList = propInfo.split(Const.COMMA);

		Map <String, String> propMap = new HashMap<>();
		propMap.put(MAPKEY_TABLE_NAME, propList[0]);
		propMap.put(MAPKEY_COL_ID, propList[1]);
		propMap.put(MAPKEY_CSV_TYPE, propList[2]);
		propMap.put(MAPKEY_CSV_HEADER_NAME, propList[3]);
		propMap.put(MAPKEY_EDIT_TYPE, propList[4]);

		//値6(桁切り桁数、コード通番等)がある場合、取得する。
		if(propList.length > 5) {
			propMap.put(MAPKEY_OTHER_INFO, propList[5]);
		}
		return propMap;
	}

	/**
	 * 複数DB取得や複数カラムから取得する場合、値を分割する。
	 * @param propMap プロパティファイル情報
	 * @param mapKey マップキー
	 * @param splitStr 区切り文字
	 * @return 指定したマップキーから算出した各情報。
	 */
	private static String[] editProp(Map<String,String> propMap , String mapKey, String splitStr) {
		String info = propMap.get(mapKey);
		String[] list = info.split(splitStr);
		return list;
	}

	/**
	 * プロパティファイルの項目編集方法情報より、ValueをUtilの各編集メソッドに通す。
	 * @param valueList DBシート対応値(項目編集前)
	 * @param propMap プロパティ情報Map
	 * @return 期待値（編集後値）
	 * @throws ParseException
	 */
	private static String editValue(List<String> valueList, Map <String, String>propMap, Sheet codeMSheet) throws ParseException {

		//DBシートから取得した、項目リストを配列に格納。
		String[] editValues = new String [valueList.size()];
		String value = "";

		//DBから取得した値が1つではない場合、配列に格納する。(文字列結合用)
		if( valueList.size() != 1 ) {
			for(int i = 0 ; i < valueList.size(); i++ ) {
				editValues[i] = valueList.get(i);
			}
		}else{
			value = valueList.get(0);
		}

		//プロパティファイルから編集方法情報を取得する。
		String[] editTypeInfo = editProp(propMap, MAPKEY_EDIT_TYPE, Const.FULL_POINT);
		//プロパティファイルMapから、その他の情報を取得する。（桁切り桁数、コード通番等）
		String editOtherInfo = propMap.get(MAPKEY_OTHER_INFO);

		//編集後の値(期待値)
		String expectValue = value;

		//項目編集を行う回数分ループ
		for(int editTypeIdx = 0; editTypeIdx < editTypeInfo.length; editTypeIdx++) {
			String editType = editTypeInfo[editTypeIdx];
			//2回目以降の編集の場合は、すでに出ている期待値を引数として、各編集を行う。
			if(editTypeIdx >= 1) {
				value = expectValue;
			}

			//項目編集なし
			if( editType.equals(EDIT_TYPE_NOTHING) ) {
				expectValue = value;
			}
			//スラッシュ除去
			if( editType.equals(EDIT_TYPE_REMOVE_SLASH)) {
				expectValue = Util.removeSlash(value);
			}
			//ハイフン除去
			if( editType.equals(EDIT_TYPE_REMOVE_HYPHEN)) {
				expectValue = Util.removeHyphen(value);
			}
			//文字列結合
			if( editType.equals(EDIT_TYPE_UNION_DATA)) {
				expectValue = Util.unionData(editValues, Const.EMPTY_STRING);
			}
			//全角→半角変換
			if( editType.equals(EDIT_TYPE_CHANGE_HALF_WIDTH)) {
				expectValue = Util.changeHalfWidth(value);
			}
			//桁切り
			if( editType.equals(EDIT_TYPE_CUT_DIGIT)) {
				expectValue = Util.cutDigit(value, Integer.parseInt(editOtherInfo), Const.END_STRING);
			}
			//コード変換(東コード→西コード)
			if( editType.equals(EDIT_TYPE_CHANGE_EAST_TO_WEST_CODE) ) {
				expectValue = Util.changeEastToWestCode(editOtherInfo, value, codeMSheet);
			}
			//コード変換(西コード→西和名)
			if( editType.equals(EDIT_TYPE_CHANGE_CODE_TO_JAPANESE) ){
				expectValue = Util.changeCodeToJapanese(editOtherInfo, value, codeMSheet);
			}
			//コード変換(西和名→西コード)
			if( editType.equals(EDIT_TYPE_CHANGE_JAPANESE_TO_CODE) ){
				expectValue = Util.changeJapaneseToCode(editOtherInfo, value, codeMSheet);
			}
			//記事欄抽出
			if( editType.equals(EDIT_TYPE_EXTRACTION_ARTICLE) ) {
				expectValue = Util.extractionArticle(value, propMap.get(MAPKEY_CSV_HEADER_NAME));
			}
			//有無項目
			if( editType.equals(EDIT_TYPE_UMU) ) {
				expectValue = Util.judgeData(propMap.get(MAPKEY_CSV_HEADER_NAME), editValues);
			}
		}
		if( StringUtils.isEmpty(expectValue) ) {
			expectValue = "";
		}
		return expectValue;
	}

	/**
	 * セルを指定して、文字列を取得する。
	 * @param cell セル
	 * @return 文字列
	 */
	private static String getCellValue(Cell cell) {
		String cellValue = "";

		if(cell == null) {
			cellValue = "";
		}else {
			cellValue = String.valueOf(cell);
		}

		return cellValue;
	}

	/**
	 *指定したセルに文字を出力する。
	 * @param row 行
	 * @param cellNum セル番号
	 * @param style 書式
	 * @param str 文字
	 */
	private static void setCell(Row row, int cellNum, CellStyle style, String str) {
		Cell cell = row.createCell(cellNum);
		cell.setCellStyle(style);

		if(StringUtils.isEmpty(str)) {
			cell.setCellValue("");
		}else {
			cell.setCellValue(str);
		}
	}

	/**
	 * 指定したシートの行を作成する。
	 * @param sheet シート
	 * @param rowNum 行番号
	 * @return
	 */
	private static Row createRow(Sheet sheet, int rowNum) {

		Row row = sheet.getRow(rowNum);
		row = sheet.createRow(rowNum);

		return row;
	}

	/**
	 *既存のシートを新規シートにコピーする。(文字列、書式のみ。セルの高さや幅はコピーしない。)
	 * @param newSheet コピー先のシート
	 * @param sheet コピー元のシート
	 */
	public static void copySheets(Sheet newSheet, Sheet sheet){
		copySheets(newSheet, sheet, true);
	}

	/**
	 * @param newSheet コピー先シート
	 * @param sheet コピー元シート
	 * @param copyStyle true or false
	 */
	private static void copySheets(Sheet newSheet, Sheet sheet, boolean copyStyle){
		int maxColumnNum = 0;
		Map<Integer, CellStyle> styleMap = (copyStyle) ? new HashMap<Integer, CellStyle>() : null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			Row srcRow = sheet.getRow(i);
			Row destRow = newSheet.createRow(i);
			if (srcRow != null) {
				copyRow(srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
	}

	/**
	 *
	 * @param srcRow コピー元の行
	 * @param destRow コピー先の行
	 * @param styleMap 書式情報
	 */
	private static void copyRow(Row srcRow, Row destRow, Map<Integer, CellStyle> styleMap) {
		for (int j = srcRow.getFirstCellNum(); j < srcRow.getLastCellNum(); j++) {
			Cell oldCell = srcRow.getCell(j);
			Cell newCell = destRow.getCell(j);
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				copyCell(oldCell, newCell, styleMap);
			}
		}
	}

	/**
	 * @param oldCell コピー元のセル
	 * @param newCell コピー先のセル
	 * @param styleMap 書式情報
	 */
	private static void copyCell(Cell oldCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
		if(styleMap != null) {
			if(oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()){
				newCell.setCellStyle(oldCell.getCellStyle());
			} else{
				int stHashCode = oldCell.getCellStyle().hashCode();
				CellStyle newCellStyle = styleMap.get(stHashCode);
				if(newCellStyle == null){
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		switch(oldCell.getCellType()) {
		case STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case BLANK:
			newCell.setCellType(CellType.BLANK);
			break;
		case BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}
	}

	/**
	 * 実装用System.out.println
	 * @param str
	 */
	private static void print(String str) {
		System.out.println(str);
	}

}