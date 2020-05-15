package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import common.Util;

public class SoGaiGyoumuHanei {

	public static final Map<String, String> TABLE_LIST = new HashMap<String, String>(){
		{
			put("CSV_OUTPUT_DATA","CSV出力用");
			put("JOB_INFO","工事情報(制御)");
			put("JOB_INFO_DETAIL", "工事情報(詳細)");
			put("JOB_INFO_DETAIL2", "工事情報(詳細2)");
		}
	};

	private static final String EXPECTED_FILE_PATH = "C:\\ITSupport\\test\\"  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_期待値.xlsx";

	private static final String EDIT_INFO_FILE_PATH = "C:\\ITSupport\\test\\EditInfo_SO外.xlsx";

//	public static void main(String args[]) {
//
//		// 引数の値をチェック（ツールの区分、INPUT情報１のファイルパス、INPUT情報２のファイルパス）
//		//ツールの区分を読み込み
//		String toolKbn = args[0];
//
//		String input1 = args[1];
//
//		String input2 = args[2];
//
//		expectCalculate(input1, input2);
//	}

	/**
	 * 期待値算出メソッド。
	 */
	public static void expectCalculate(String input1, String input2) {

		System.out.println("input1 = " + input1);
		System.out.println("input2 = " + input2);


		//インプット情報のCSVファイルを保持する
		//シート List<Map 統合so< ヘッダ, 値>>


		//インプット情報のDBファイル情報を保持する。
		Map<String , Map<String, Map<String, String>>>  dbData = new HashMap();
		dbData = getDbData(input2);


		//編集内容を保持する。
		Map<String, Map<String, String>> editData = new HashMap();
		editData = getEditData();
//
		createSheet(input1, dbData, editData);


	}


	private static Map<String, Map<String, String>> getEditData(){

		Map<String, Map<String, String>> rowData = null;

		try {
			//編集情報ファイル
			Workbook  ecitInfoFile = WorkbookFactory.create(new FileInputStream(EDIT_INFO_FILE_PATH));

			Map<String, String> dataList = null;

			Sheet sheet = ecitInfoFile.getSheetAt(0);

			rowData = new HashMap();


			for(int i = 1; i <= sheet.getLastRowNum(); i++){

				String columnName = sheet.getRow(i).getCell(0).getStringCellValue();

				dataList = new HashMap();
				Map<String , String> dataMap = new HashMap();
				Map<String, Map<String, String>> rowNum = new HashMap();
				String headerValue = "";
				String dataValue = "";

				for(int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {

					headerValue = sheet.getRow(0).getCell(j).getStringCellValue();
					dataValue = sheet.getRow(i).getCell(j).getStringCellValue();
					dataList.put(headerValue, dataValue);

				}

				rowData.put(columnName, dataList);

			}

		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return rowData;


	}


	/**
	 * inputファイルからDB情報取得
	 * @param input2
	 * @return
	 */
	private static Map<String , Map<String, Map<String, String>>> getDbData(String input2) {

		Map<String , Map<String, Map<String, String>>> dbData = null;

		try {

			//DB情報ファイル
			Workbook  dbFile = WorkbookFactory.create(new FileInputStream(input2));

			dbData = new HashMap();
			Map<String, Map<String, String>> rowData = null;
			Map<String, String> dataList = null;

			//シートでループ
			for(Sheet sheet : dbFile) {

				rowData = new HashMap();

				//ヘッダ行を飛ばしてループ
				for(int i = 1; i <= sheet.getLastRowNum(); i++){

					String columnName = "";
					dataList = new HashMap();
					Map<String , String> dataMap = new HashMap();
					Map<String, Map<String, String>> rowNum = new HashMap();
					String headerValue = "";
					String dataValue = "";

					for(int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {

						headerValue = sheet.getRow(0).getCell(j).getStringCellValue();
						dataValue = sheet.getRow(i).getCell(j).getStringCellValue();

						if(headerValue.equals("A_INTG_SO_NUM")) {

							columnName = dataValue;
						}
						dataList.put(headerValue, dataValue);

					}
					rowData.put(columnName, dataList);
				}
				dbData.put(sheet.getSheetName(), rowData);
			}

		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dbData;

	}

	/**
	 * 期待値シート出力処理
	 * @param input1Path
	 * @param dbData
	 * @param editData
	 */
	private static void createSheet(
			String input1Path
			, Map<String , Map<String, Map<String, String>>> dbData
			, Map<String, Map<String, String>> editData) {

		try {

			//DB情報ファイル
			FileInputStream inputFile = new FileInputStream(input1Path);

			//inputファイル
			Workbook wb = WorkbookFactory.create(inputFile);

			//出力ファイル
			//期待値ファイルを作成
//			File expectFile = new File("C:\\ITSupport\\test\\"  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_期待値.xlsx");

			FileOutputStream os = new FileOutputStream(EXPECTED_FILE_PATH);
//			File os = new File("C:\\ITSupport\\test\\"  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_期待値.xlsx");

			int numberOfSheet = wb.getNumberOfSheets();

			for(int sheetIndex = 0 ;  sheetIndex < numberOfSheet; sheetIndex++ ) {

				Sheet cloneSheet = wb.cloneSheet(sheetIndex);
				wb.setSheetName(wb.getSheetIndex(cloneSheet.getSheetName()), wb.getSheetName(sheetIndex) + "_期待値");

				Map<String, String> dataList = null;
				Map<String, Map<String, String>> rowData = null;
				rowData = new HashMap();

				for(int i = 1; i <= cloneSheet.getLastRowNum(); i++) {

					String aIntgSoNum = "";
					Row row = cloneSheet.getRow(i);
					dataList = new HashMap();
					Map<String , String> dataMap = new HashMap();
					Map<String, Map<String, String>> rowNum = new HashMap();
					String headerValue = "";
					String dataValue = "";

					for(int j = 0; j < row.getLastCellNum(); j++) {

						headerValue = cloneSheet.getRow(0).getCell(j).getStringCellValue();
						dataValue = cloneSheet.getRow(i).getCell(j).getStringCellValue();

						if(headerValue.equals("統合SO番号")) {

							aIntgSoNum = dataValue;

						}

						row.getCell(j).setCellValue(editData(dbData, aIntgSoNum, headerValue, dataValue));

					}
				}
			}

			wb.write(os);
			wb.close();


		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}



	}

	private static String editData(Map<String , Map<String, Map<String, String>>>  dbData, String aIntgSoNum, String headerValue, String dataValue ) throws ParseException {

		String returnData = "";

		//インプット情報のDBファイル情報を保持する。
//		Map<String , Map<String, Map<String, String>>>  dbData = new HashMap();
//		dbData = getDbData();

		//編集内容を保持する。
		Map<String, Map<String, String>> editData = new HashMap();

		editData = getEditData();

//		System.out.println("▽----------------▽");
//
//		System.out.println("◆ 統合SO番号 = " + aIntgSoNum);
//		System.out.println("◆ ヘッダー = " + headerValue);
//		System.out.println("◆ 値 = " + dataValue);

		//編集内容からデータ取得元を取得
//		System.out.println("◆ データ取得元 = " + editData.get(headerValue).get("情報元テーブルID"));


		//データの取得元テーブル
		String tableName = editData.get(headerValue).get("情報元テーブルID");
		List<String> tableNameList = Arrays.asList(tableName.split(",",0));

		//データの取得元カラム
		String columnId = editData.get(headerValue).get("項目ID");
		List<String> columnIdList = Arrays.asList(columnId.split(",",0));


//		System.out.println("◆ テーブル = " + tableNameList);
//		System.out.println("◆ カラム = " + columnIdList);

		List<String> dbValue = new ArrayList();

		for(int i=0; i < tableNameList.size(); i++) {
			//System.out.println("◆DBデータ  = " + dbData.get(TABLE_LIST.get(tableNameList.get(i))).get(aIntgSoNum).get(columnIdList.get(i)));
			dbValue.add( dbData.get(TABLE_LIST.get(tableNameList.get(i))).get(aIntgSoNum).get(columnIdList.get(i)));
		}

//		System.out.println("◆ dbValue = " + dbValue);


//		String dbValue = dbData.get(tableName).get(aIntgSoNum).get(columnId)

//		for(int j = 0; j < dbValue.size(); j++) {
//
//			dbValue.get(j);
//
//
//		}

		//項目編集
		if(!editData.get(headerValue).get("スラッシュ除去").equals("0")){
//			System.out.println("◆ します" + editData.get(headerValue).get("スラッシュ除去"));


			returnData = Util.removeSlash(dbValue.get(0));

		}

		if(!editData.get(headerValue).get("スラッシュ付与").equals("0")){
//			System.out.println("◆ します" + editData.get(headerValue).get("スラッシュ付与"));
			returnData = dbValue.get(0);

		}

		if(!editData.get(headerValue).get("全角変換").equals("0")){
//			System.out.println("◆ します" + editData.get(headerValue).get("全角変換"));
			returnData = dbValue.get(0);

		}

		if(!editData.get(headerValue).get("半角変換").equals("0")){
//			System.out.println("◆ します" + editData.get(headerValue).get("半角変換"));
			returnData = dbValue.get(0);

		}

		if(!editData.get(headerValue).get("桁切").equals("0")){
//			System.out.println("◆ します" + editData.get(headerValue).get("桁切"));
			returnData = dbValue.get(0);

		}


		if(editData.get(headerValue).get("文字列結合").equals("0")){
//			System.out.println("◆ 文字列結合します" + editData.get(headerValue).get("文字列結合"));
			returnData = dbValue.get(0);

		}



//		System.out.println("△----------------△");

		return returnData;
	}

}
