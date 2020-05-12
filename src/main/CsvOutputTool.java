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
import org.apache.poi.ss.usermodel.CellType;
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

	private static final String MAPKEY_OTHER_INFO="OTHER_INFO";

	private static final String A_INTG_SO_NUM="統合SO番号";

	/**
	 * 編集方法
	 */
	private static final String EDIT_TYPE_NOTHING="1";
	private static final String EDIT_TYPE_REMOVE_SLASH="2";
	private static final String EDIT_TYPE_REMOVE_HYPHEN="3";
	private static final String EDIT_TYPE_UNION_DATA="4";
	private static final String EDIT_TYPE_CHANGE_HALF_WIDTH="5";
	private static final String EDIT_TYPE_CUT_DIGIT="6";
	private static final String EDIT_TYPE_CHANGE_EAST_TO_WEST_CODE="7";
	private static final String EDIT_TYPE_CHANGE_CODE_TO_JAPANESE="8";
	private static final String EDIT_TYPE_UMU="9";

	/**
	 * Input2(試験結果CSVの情報)のキー情報をもとに、Input2(DB情報)から期待値を算出し、期待値シートに出力する。
	 * @param input1 DB情報
	 * @param input2 試験結果CSV情報
	 * @throws IOException
	 */
	public void expectCalculate(String input1, String input2) {

		//	public static void main(String[] args) {
		/**
		 * 任意の値を設定
		 */
		//DB貼り付け情報（CSV出力用、工事情報(制御)、工事情報(詳細)、工事情報(詳細2)の4シートが記載されたファイル）
		input1 = "input1\\CSV出力ツール用INPUT1.xlsx";
		//CSV貼り付け情報（試験結果のCSVの情報の1シート）
		input2= "input2\\CSV出力ツール用INPUT2.xlsx";
		//「期待値ファイル」のパスを指定
		String outDir = "C:\\Sample\\ExpectValue\\";

		//期待値ファイルを作成
		File expectFile = new File(outDir + "期待値_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx");
		XSSFWorkbook wbExpect = new XSSFWorkbook();
		CellStyle textStyle = wbExpect.createCellStyle();
		textStyle.setDataFormat(wbExpect.createDataFormat().getFormat("text"));
		//期待値ファイルにシートを作成
		Sheet testCsvSheet = wbExpect.createSheet("CSV出力_結果");
		Sheet expectSheet = wbExpect.createSheet("CSV出力_期待値");

		try ( FileOutputStream out =  new FileOutputStream(expectFile);
				Workbook wb1 = WorkbookFactory.create(new FileInputStream(input1));
				Workbook wb2 = WorkbookFactory.create(new FileInputStream(input2));) {

			// Input1(DB情報)とInput2(試験結果CSV情報)を取得
			Sheet input2Sheet = wb2.getSheetAt(0);

			//読み込んだInput2のシートを、期待値ファイルの1シート目にコピーする。
			copySheets(testCsvSheet, input2Sheet);

			//	項目名リストを定義
			List <String> headerList = new ArrayList<>();
			//統合SO番号リストを定義
			List <String> soList = new ArrayList<>();

			Row input2Row = input2Sheet.getRow(0);
			Row expectRow = createRow(expectSheet, 0);
			//			Cell input2ItemCell = null;

			//Input2の1行目(項目行)を最終行まで取得。
			for( int colIdx = 0;colIdx <= input2Row.getLastCellNum(); colIdx++ ) {

				Cell input2ItemCell = input2Row.getCell(colIdx);
				if(input2ItemCell == null ) {
					break;
				}

				//項目名リストに項目名を追加。
				headerList.add(String.valueOf(input2ItemCell));
				//項目名を期待値シートに出力する。
				setCell(expectRow, colIdx, textStyle, input2ItemCell.getStringCellValue());

				// 1行目の項目名から「統合SO番号」列の位置を特定し、統合SO番号を最終行まで取得しリストに格納。
				if( String.valueOf(input2ItemCell).equals( A_INTG_SO_NUM )){
					for(int rowIdx = 1; rowIdx <= input2Sheet.getLastRowNum(); rowIdx++ ) {
						Cell tougouSoCell = (input2Sheet.getRow(rowIdx).getCell(colIdx));

						if(tougouSoCell == null) {
							break;
						}
						soList.add(String.valueOf(tougouSoCell));
					}
				}
			}

			Properties prop = new Properties();
			prop.load(new FileInputStream(PATH_CSV_PROPERTIES));

			// CSV情報から取得した統合SO番号分ループ
			for( int i=0; i < soList.size(); i++ ){

				//SO番号を取得
				String targetSoNo = soList.get(i);
				expectRow = createRow(expectSheet, i + 1);

				//試験結果CSV情報から取得した、CSV項目名リストの項目ごとにループ
				for( int h = 0 ; h < headerList.size();h++ ) {

					//CSV項目名を取得
					String headerName = headerList.get(h);

					//CSV項目名からプロパティファイルの情報を取得する。
					Map<String,String> propMap = getProp(prop, headerName);

					//プロパティファイルより、テーブル名を取得する
					String[] dbList = editProp(propMap, MAPKEY_TABLE_NAME, FULL_POINT);

					List<String> targetValues = new ArrayList<>();

					//取得元DBの数分ループ
					for(int d = 0; d < dbList.length; d++) {
						//プロパティファイルのテーブル情報から、参照先シート名を取得。
						Sheet dbSheet = wb1.getSheet(dbList[d]);

						//シートから取得した、値をリストに格納
						targetValues.add(getDbRecord(dbSheet, targetSoNo, propMap, d));
					}

					//DBシートから取得したデータおよびプロパティファイル情報から、期待値を算出する。
					String expectValue = editValue( targetValues, propMap );

					//期待値シートに期待値を出力する。
					setCell(expectRow, h ,textStyle, expectValue);
				}
			}

			//後処理
			wbExpect.write(out);
			out.close();
			wbExpect.close();
			print("処理終了");
		} catch (Exception e) {
			e.printStackTrace();
			if(expectFile.exists()) {
				print("削除して処理終了");
				expectFile.delete();
			}
		}
	}

	/**
	 *指定したDBシートから、統合SO番号およびカラムIDをキーに1カラム値を取得する。
	 * @param dbSheet DBシート
	 * @param soNum 統合SO番号
	 * @param propMap プロパティ情報Map
	 * @param d テーブルリストの○番目
	 * @return DB取得データ
	 */
	private static String getDbRecord(Sheet dbSheet, String soNum, Map<String,String> propMap, int d) {

		//カラムIDリスト
		String[] valueList = editProp(propMap, MAPKEY_COL_ID, FULL_POINT);
		String targetValue = "";

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

			//DBMapから、カラムIDをキーに期待値を算出
			targetValue =  dbMap.get(valueList[d]);
			break;
		}

		return targetValue;
	}

	/**
	 * 複数DB取得や複数カラムから取得する場合、値を分割する。
	 * @param propMap
	 * @param mapKey
	 * @param splitStr
	 * @return
	 */
	private static String[] editProp(Map<String,String> propMap , String mapKey, String splitStr) {

		String info = propMap.get(mapKey);

		String[] list = info.split(splitStr);

		return list;
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
		String[] propList = propInfo.split(COMMA);

		Map <String, String> propMap = new HashMap<>();
		propMap.put(MAPKEY_TABLE_NAME, propList[0]);
		propMap.put(MAPKEY_COL_ID, propList[1]);
		propMap.put(MAPKEY_CSV_TYPE, propList[2]);
		propMap.put(MAPKEY_CSV_ITEM_NAME, propList[3]);
		propMap.put(MAPKEY_EDIT_TYPE, propList[4]);

		//値6(桁切り桁数、コード通番等)がある場合、取得する。
		if(propList.length > 5) {
			propMap.put(MAPKEY_OTHER_INFO, propList[5]);
		}
		return propMap;
	}

	/**
	 * プロパティファイルの項目編集方法情報より、ValueをUtilの各編集メソッドに通す。
	 * @param valueList DBシート対応値(項目編集前)
	 * @param propMap プロパティ情報Map
	 * @return 期待値（編集後値）
	 * @throws ParseException
	 */
	private static String editValue(List<String> valueList, Map <String, String>propMap) throws ParseException {

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
		String[] editTypeInfo = editProp(propMap, MAPKEY_EDIT_TYPE, FULL_POINT);
		//プロパティファイルMapから、その他の情報を取得する。（桁切り桁数、コード通番等）
		String editOtherInfo = propMap.get(MAPKEY_OTHER_INFO);

		//編集後の値(期待値)
		String expectValue = value;
		Util util = new Util();

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
				expectValue = util.removeSlash(value);
			}
			//ハイフン除去
			if( editType.equals(EDIT_TYPE_REMOVE_HYPHEN)) {
				expectValue = util.removeHyphen(value);
			}
			//文字列結合
			if( editType.equals(EDIT_TYPE_UNION_DATA)) {
				expectValue = util.unionData(editValues, Const.EMPTY_STRING);
			}
			//全角→半角変換
			if( editType.equals(EDIT_TYPE_CHANGE_HALF_WIDTH)) {
				expectValue = util.changeHalfWidth(value);
			}
			//桁切り
			if( editType.equals(EDIT_TYPE_CUT_DIGIT)) {
				expectValue = util.cutDigit(value,  Integer.parseInt(editOtherInfo), Const.END_STRING);
			}
			//コード変換(東コード→西コード)
			if( editType.equals(EDIT_TYPE_CHANGE_EAST_TO_WEST_CODE) ) {
				expectValue = util.changeEastToWestCode(editOtherInfo, value);
			}
			//コード変換(西コード→西和名)
			if( editType.equals(EDIT_TYPE_CHANGE_CODE_TO_JAPANESE) ){
				expectValue = util.changeCodeToJapanese(editOtherInfo, value);
			}
			//有無項目
			if( editType.equals(EDIT_TYPE_UMU) ) {
				expectValue = util.judgeData(propMap.get(MAPKEY_CSV_ITEM_NAME), editValues);
			}
		}
		return expectValue;
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
		cell.setCellValue(str);

	}

	/**
	 * 指定したシートの行を作成する。
	 * @param sheet シート
	 * @param rowNum 行番号
	 * @return
	 */
	private static Row createRow(Sheet sheet, int rowNum) {
		Row row = sheet.getRow(rowNum);
		row =sheet.createRow(rowNum);
		return row;
	}

	/**
	 *既存のシートを新規シートにコピーする。
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
	public static void copySheets(Sheet newSheet, Sheet sheet, boolean copyStyle){
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
	public static void copyRow(Row srcRow, Row destRow, Map<Integer, CellStyle> styleMap) {
		for (int j = srcRow.getFirstCellNum(); j < srcRow.getLastCellNum(); j++) {
			Cell oldCell = srcRow.getCell(j);
			Cell newCell = destRow.getCell(j);
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				// copy chaque cell
				copyCell(oldCell, newCell, styleMap);
			}
		}
	}

	/**
	 * @param oldCell コピー元のセル
	 * @param newCell コピー先のセル
	 * @param styleMap 書式情報
	 */
	public static void copyCell(Cell oldCell, Cell newCell, Map<Integer, CellStyle> styleMap) {
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