package main;
import static java.lang.System.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * CSV出力ツール用期待値算出ツール。
 * @author kanri_aforce
 *
 */

public class CsvOutputTool {

	private static final String COMMA = ",";

	/**
	 * input1の統合SO番号から、input2の統合SO番号が参照し、該当する値を取得し期待値とする。
	 * CSVの種別による、項目数の増減による影響はない。
	 */
	public void expectCalculate(String input1, String input2) {

		/**
		 * 任意の値を設定
		 */
		//DB貼り付け情報（CSV出力用、工事情報(制御)、工事情報(詳細)、工事情報(詳細2)の4シートが記載されたファイル）
		input1 = "C:\\user\\△△.xlsx";
		//CSV貼り付け情報（試験結果のCSVの情報の1シート）
		input2 = "C:\\user\\○○.xlsx";

		try {
			Properties p = new Properties();
			p.load(new FileInputStream("property/CSVOutput.properties"));

			// Input2「CSV情報」を取得
			Workbook book = WorkbookFactory.create(new FileInputStream(input2));
			Workbook dbBook = WorkbookFactory.create(new FileInputStream(input1));
			//1シート目
			Sheet sheet = book.getSheetAt(0);
			//1行目
			Row row = sheet.getRow(0);
			//	項目名リストを定義
			List <String> headerList = new ArrayList<>();
			//統合SO番号リストを定義
			List <String> soList = new ArrayList<>();

			for (int colIdx = 0;colIdx <= row.getLastCellNum(); colIdx++) {
				Cell cell = row.getCell(colIdx);
				if(cell == null) {
					break;
				}
				headerList.add(String.valueOf(cell));

				// 1行目の項目名から「統合SO番号を取得」
				if(String.valueOf(cell).equals( "統合SO番号" )) {

					for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {

						Cell soCell = sheet.getRow(rowIdx).getCell(colIdx);
						//統合SO番号リストに格納
						soList.add(String.valueOf(soCell));
					}
				}
			}

			// CSV情報から取得した統合SO番号分ループ
			for (int i=0; i<soList.size(); i++){

				//CSV項目名リストのサイズ分ループ
				for(int h = 0 ; h<headerList.size();h++) {
					String headerName = headerList.get(h);

					// すべてのプロパティのキーと値を取得する。
					for ( Object key: p.keySet() ) {
						Object value = p.get( key );

						//CSVの項目名をプロパティファイルから取得し、CSVの項目名リストを同一なものを処理。
						if(headerName.equals(getValue(value.toString(), 3))) {
							//取得元テーブル名をプロパティファイルから取得
							String table = getValue(value.toString(),0);
							//取得元カラム名をプロパティファイルから取得
							String colId = getValue(value.toString(),1);

							Sheet dbSheet = null;

							if(table.equals( "CSV_OUTPUT_DATA" )) {
								dbSheet = dbBook.getSheet("CSV出力用");
							}else if(table.equals( "JOB_INFO" )) {
								dbSheet = dbBook.getSheet("工事情報(制御)");
							}

							//DBシートの1行目
							Row idRow = dbSheet.getRow(0);

							//DBシートの1行目カラムIDの数分ループ
							for (int colDbIdx = 0;colDbIdx < idRow.getLastCellNum(); colDbIdx++) {
								Cell dbCell = idRow.getCell(colDbIdx);

								//プロパティファイルのカラムIDとイコールで、そのカラムが何列目かを特定
								if(String.valueOf(dbCell).equals( colId )) {

									//DBシートの1列目から、統合SO番号の数分ループ
									for (int rowDbIdx = 0; rowDbIdx <= dbSheet.getLastRowNum(); rowDbIdx++) {
										Cell dbSoCell = dbSheet.getRow(rowDbIdx).getCell(0);

										//CSV情報の統合SO番号とイコールで、位置を特定
										if(String.valueOf(dbSoCell).equals( soList.get(i) )) {
											//DB情報からカラムIDと統合SO番号で、何行目の何列目に対象項目があるかを特定し、期待値を算出。
											String expectValue = String.valueOf(dbSheet.getRow(rowDbIdx).getCell(colDbIdx));
											out.println("期待値：：" + colId + "::" + headerName + "::" + expectValue);

										}
									}
								}
							}
						}
					}
				}
			}
			out.println("FINISH");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *プロパティファイルから、任意の情報を取得する。
	 * @param str プロパティファイルの値（Value）
	 * @param i カンマ区切りで何個目の値かを指定。（取得元テーブル,カラムID,CSVタイプ,CSV項目名,編集内容）
	 * @return
	 */
	private static String getValue(String str,int i) {

		String[] values = str.split(COMMA);
		String value = values[i];
		return value;
	}
}
