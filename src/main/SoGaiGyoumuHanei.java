package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import bean.SoGaiGyoumuHaneiEditList;
import common.Const;

public class SoGaiGyoumuHanei {

	/**
	 * 期待値算出メソッド。
	 */
	public static void expectCalculate(String input1, String input2) {


		// input1の情報、input2の情報、プロパティファイルを取得して保持
					// ※プロパティファイルはツール毎に作っておく

					// input2のキー情報からinput1の該当情報を得る
					// プロパティファイルの情報にしたがって期待値を算出
					// 別シートに算出した期待値を張り付け
					// 以降繰り返し


		//ファイルを開いて項目数のループ
		try {

			//DB情報ファイル
			Workbook  dbFile = WorkbookFactory.create(new FileInputStream("C:\\ITSupport\\test\\InputDB.xlsx"));

			//CSV情報ファイル
			Workbook  csvFile = WorkbookFactory.create(new FileInputStream("C:\\ITSupport\\test\\InputSample.xlsx"));

			//期待値情報ファイル
			FileOutputStream expectFile =  new FileOutputStream("C:\\ITSupport\\test\\" + Const.SOGAI_EXPECTED_FILE);

			//CSV情報分ループ
			int endOfBook =  csvFile.getNumberOfSheets();

			//List<String> headerList = new ArrayList();
			Map<Integer, String> headerList = new HashMap();

			for(Row row : csvFile.getSheetAt(0)) {

				if(row.getRowNum() == 0) {
					//ヘッダ
					//for(Cell cell : row) {
					for(int j = 0; j < row.getLastCellNum(); j++) {

						System.out.println("ヘッダ = " + row.getCell(j).getStringCellValue());
						headerList.put(j, row.getCell(j).getStringCellValue());
//
//						System.out.println("ヘッダ = " + cell.getStringCellValue());
//						headerList.add(cell.getStringCellValue());

					}
				}

				//編集定義取得
				//List<SoGaiGyoumuHaneiEditList> editInfoList = getEditInfo(headerList,row);
				List<SoGaiGyoumuHaneiEditList> editInfoList = getEditInfo(row);

//				for(SoGaiGyoumuHaneiEditList list : editInfoList) {
//
//					System.out.println("■ 項目 = " + list.getColumnId());
//					System.out.println("■ テーブル = " + list.getTable());
//					System.out.println("■ テーブル項目 = " + list.getTableColumnId());
//
//				}


				getExpected(headerList,row, editInfoList);


			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	//private static List<SoGaiGyoumuHaneiEditList> getEditInfo(List<String> headerList, Row row) {
	private static List<SoGaiGyoumuHaneiEditList> getEditInfo(Row row) {

		//編集情報を取得
		Workbook editFile;

		List<SoGaiGyoumuHaneiEditList> editInfoList = new ArrayList();

		try {
			editFile = WorkbookFactory.create(new FileInputStream("C:\\ITSupport\\test\\EditInfo_SO外.xlsx"));

			Sheet sheet = editFile.getSheet("編集定義");

			Map<Integer, String> editHeader = new HashMap();

			for(Row editRow : sheet) {

				if(editRow.getRowNum() == 0) continue;

				SoGaiGyoumuHaneiEditList editList = new SoGaiGyoumuHaneiEditList();

				//項目名
				editList.setColumnName(editRow.getCell(0).getStringCellValue());
				//項目ID
				editList.setColumnId(editRow.getCell(1).getStringCellValue());
				//情報元テーブルID
				editList.setTable(editRow.getCell(2).getStringCellValue());
				//テーブル項目ID
				editList.setTableColumnId(editRow.getCell(3).getStringCellValue());

				editInfoList.add(editList);
			}


		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return editInfoList;


	}



	//private static void getExpected(List<String> headerList, Row row, List<SoGaiGyoumuHaneiEditList> editInfoList) {
	private static void getExpected(Map<Integer, String> headerList, Row row, List<SoGaiGyoumuHaneiEditList> editInfoList) {


		//統合SO番号を取得
		String aIntgSoNo = row.getCell(0).getStringCellValue();

		System.out.println("■ 統合SO番号 = " + aIntgSoNo);

		try {

			//DB情報を取得
			Workbook  dbFile = WorkbookFactory.create(new FileInputStream("C:\\ITSupport\\test\\InputDB.xlsx"));

			String value = "";

			System.out.println("▽---------------------------▽");
			for(int i = 0; i< headerList.size();i++) {

//				String value = row.getCell(i).getStringCellValue().isEmpty() ? "" : row.getCell(i).getStringCellValue();
				System.out.println("項目のヘッダ = " + headerList.get(i));
				System.out.println("項目の値 = " + row.getCell(i).getStringCellValue());

				for(int j = 0; j < editInfoList.size(); j++) {

					System.out.println("カラムネーム = " + editInfoList.get(j).getColumnName());

					String table = "";

					if(headerList.get(i).equals(editInfoList.get(j).getColumnName())){

						if(editInfoList.get(j).getTable().equals("CSV_OUTPUT")){

							table = "CSV出力";


						} else if(editInfoList.get(j).getTable().equals("JOB_INFO")){
							table = "工事情報";

						}

						Sheet dbSheet = dbFile.getSheet(table);

						for(Row dbRow : dbSheet) {


							if(dbRow.getCell(0).equals(aIntgSoNo)) {

								for(Cell deCell : dbRow) {



								}
							}

						}

					}

				}




			}
			System.out.println("△---------------------------△");






		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}



	}





}
