package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

	public static void main(String args[]) {

		// 引数の値をチェック（ツールの区分、INPUT情報１のファイルパス、INPUT情報２のファイルパス）
		//ツールの区分を読み込み
		String toolKbn = args[0];

		String input1 = args[1];

		String input2 = args[2];

		expectCalculate(input1, input2);
	}

	/**
	 * 期待値算出メソッド。
	 */
	public static void expectCalculate(String input1, String input2) {


		//インプット情報のCSVファイルを保持する
		//シート List<Map 統合so< ヘッダ, 値>>


		//インプット情報のDBファイル情報を保持する。
//		Map<String , Map<String, Map<String, String>>>  dbData = new HashMap();
//		dbData = getDbData();


		//編集内容を保持する。
//		Map<String, Map<String, String>> editData = new HashMap();
//		editData = getEditData();
//

		createSheet();





	}


	private static Map<String, Map<String, String>> getEditData(){

		Map<String, Map<String, String>> rowData = null;

		try {
			//Csv情報ファイル
			Workbook  expectedFile = WorkbookFactory.create(new FileInputStream("C:\\ITSupport\\test\\EditInfo_SO外.xlsx"));

			Map<String, String> dataList = null;

			Sheet sheet = expectedFile.getSheet("編集定義");
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


	private static Map<String , Map<String, Map<String, String>>> getDbData() {

		Map<String , Map<String, Map<String, String>>> dbData = null;

		try {

			//DB情報ファイル
			Workbook  dbFile = WorkbookFactory.create(new FileInputStream("C:\\ITSupport\\test\\InputDB.xlsx"));

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

//					System.out.println("◆ ◆ dataList = " + dataList);

//					System.out.println("◆columnName = " + columnName);

					rowData.put(columnName, dataList);
				}

//				System.out.println("◆ ◆ rowData = " + rowData);


				dbData.put(sheet.getSheetName(), rowData);


			}

		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

//		System.out.println("◆◆ dbData = " + dbData);

		return dbData;

	}

	private static void createSheet() {

		try {

			//DB情報ファイル
			FileInputStream inputFile = new FileInputStream("C:\\ITSupport\\test\\InputSample.xlsx");

			//出力ファイル
			Workbook wb = WorkbookFactory.create(inputFile);

			//出力ファイル
			//期待値ファイルを作成
//			File expectFile = new File("C:\\ITSupport\\test\\"  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_期待値.xlsx");

			FileOutputStream os = new FileOutputStream("C:\\ITSupport\\test\\"  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_期待値.xlsx");
//			File os = new File("C:\\ITSupport\\test\\"  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_期待値.xlsx");


			Sheet cloneSheet = wb.cloneSheet(0);
			wb.setSheetName(wb.getSheetIndex(cloneSheet.getSheetName()), "期待値");

			Map<String, String> dataList = null;
			Map<String, Map<String, String>> rowData = null;
			rowData = new HashMap();

//			System.out.println("◆ aaa = " + cloneSheet.getLastRowNum());

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


					//dataList.put(headerValue, dataValue);

					String aaa = editData(aIntgSoNum, headerValue, dataValue);

					row.getCell(j).setCellValue(aaa);

				}

//				System.out.println("◆ rowData = " + rowData);
			}
			wb.write(os);
			wb.close();


		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	private static String editData(String aIntgSoNum, String headerValue, String dataValue ) {

		String returnData = "";

		//インプット情報のDBファイル情報を保持する。
		Map<String , Map<String, Map<String, String>>>  dbData = new HashMap();
		dbData = getDbData();

		//編集内容を保持する。
		Map<String, Map<String, String>> editData = new HashMap();

		editData = getEditData();

		System.out.println("▽----------------▽");

		System.out.println("◆ 統合SO番号 = " + aIntgSoNum);
		System.out.println("◆ ヘッダー = " + headerValue);
		System.out.println("◆ 値 = " + dataValue);

		//編集内容からデータ取得元を取得
		//System.out.println("◆ データ取得元 = " + TABLE_LIST.get(editData.get(headerValue).get("情報元テーブルID")));
		System.out.println("◆ データ取得元 = " + editData.get(headerValue).get("情報元テーブルID"));


		//データの取得元テーブル
//		String tableName = TABLE_LIST.get(editData.get(headerValue).get("情報元テーブルID"));
		String tableName = editData.get(headerValue).get("情報元テーブルID");
		List<String> tableNameList = Arrays.asList(tableName.split(",",0));

		//データの取得元カラム
		String columnId = editData.get(headerValue).get("項目ID");
		List<String> columnIdList = Arrays.asList(columnId.split(",",0));


		System.out.println("◆ テーブル = " + tableNameList);
		System.out.println("◆ カラム = " + columnIdList);

		List<String> dbValue = new ArrayList();
		for(int i=0; i < tableNameList.size(); i++) {
			//System.out.println("◆DBデータ  = " + dbData.get(TABLE_LIST.get(tableNameList.get(i))).get(aIntgSoNum).get(columnIdList.get(i)));
			dbValue.add( dbData.get(TABLE_LIST.get(tableNameList.get(i))).get(aIntgSoNum).get(columnIdList.get(i)));
		}

		System.out.println("◆ dbValue = " + dbValue);


//		String dbValue = dbData.get(tableName).get(aIntgSoNum).get(columnId)



		//項目編集
		if(!editData.get(headerValue).get("スラッシュ除去").equals("0")){
			System.out.println("◆ します" + editData.get(headerValue).get("スラッシュ除去"));


			returnData = Util.removeHyphen(dbValue.get(0));

		}

		if(!editData.get(headerValue).get("スラッシュ付与").equals("0")){
			System.out.println("◆ します" + editData.get(headerValue).get("スラッシュ付与"));
			returnData = dbValue.get(0);

		}

		if(!editData.get(headerValue).get("全角変換").equals("0")){
			System.out.println("◆ します" + editData.get(headerValue).get("全角変換"));
			returnData = dbValue.get(0);

		}

		if(!editData.get(headerValue).get("半角変換").equals("0")){
			System.out.println("◆ します" + editData.get(headerValue).get("半角変換"));
			returnData = dbValue.get(0);

		}

		if(!editData.get(headerValue).get("桁切").equals("0")){
			System.out.println("◆ します" + editData.get(headerValue).get("桁切"));
			returnData = dbValue.get(0);

		}


		if(editData.get(headerValue).get("文字列結合").equals("0")){
			System.out.println("◆ 文字列結合します" + editData.get(headerValue).get("文字列結合"));
			returnData = dbValue.get(0);

		}



		System.out.println("△----------------△");

		return returnData;
	}

}
