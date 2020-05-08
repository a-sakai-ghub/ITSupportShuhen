package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import bean.SKHRelation;

/**
 * 進捗管理反映ツール用期待値算出ツール。
 * @author kanri_aforce
 *
 */
public class SintyokuKanriHaneiTool {
	/**
	 * ITテスト補助ツールのメインメソッド
	 * @param args
	 */
	public static void main(String args[]) {

		String input1 = args[0];

		String input2 = args[1];

		try {
			expectCalculate(input1, input2);
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 期待値算出メソッド。
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EncryptedDocumentException
	 */
	public static void expectCalculate(String input1, String input2)
			throws EncryptedDocumentException, FileNotFoundException, IOException {
		// プロパティファイルの内容取得
		List<SKHRelation> relList = getResourceBundle();
		// input1の内容取得
		Map<String,List<Map<String,String>>> input1Map = getInputFile(input1);
		// input2の内容取得
		Map<String,List<Map<String,String>>> input2Map = getInputFile(input2);
	}

	/**
	 * エクセルファイルの内容取得用メソッド
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EncryptedDocumentException
	 *
	 */
	public static Map<String,List<Map<String,String>>> getInputFile(String fileName)
			throws EncryptedDocumentException, FileNotFoundException, IOException {
		// エクセルファイルを読み込み
		Workbook wb = WorkbookFactory.create(new FileInputStream(fileName));
		// シート名の一覧を取得
		List<String> sheetList = getSheetNames(wb);
		// Key：テーブル(シート)名 Value:カラムと値のオブジェクトファイルを戻り値とする
		Map<String,List<Map<String,String>>> resultMap = new HashMap<>();
		// 内容をマップに保存する
		for(String sheet: sheetList) {
			Map<String,String> shtMap = new LinkedHashMap<>();
			Sheet tgtSht = wb.getSheet(sheet);
			// カラム名を取得
			Row column = tgtSht.getRow(0);

			List<Map<String,String>> rowList = new ArrayList<>();
			for(int rowInt = 1;;rowInt++) {
				Row value = tgtSht.getRow(rowInt);
				// 値欄に値がない場合は無限ループから抜ける
				if(value == null) {
					break;
				}
				for(int i = 0; i < column.getLastCellNum(); i++) {
					Cell clmCell = column.getCell(i);
					Cell vleCell = value.getCell(i);
					shtMap.put(clmCell.getStringCellValue(), vleCell.getStringCellValue());
				}
				rowList.add(shtMap);
				resultMap.put(sheet, rowList);
			}

		}

		return resultMap;
	}

	/**
	 *
	 * シート名のリストを取得
	 * @param fileName
	 * @return シート名のリスト
	 * @throws EncryptedDocumentException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static List<String> getSheetNames(Workbook wb)
			throws EncryptedDocumentException, FileNotFoundException, IOException {
		return IntStream.range(0, wb.getNumberOfSheets()).mapToObj(wb::getSheetAt).map(Sheet::getSheetName)
				.collect(Collectors.toList());
	}

	/**
	 * プロパティファイル取得用メソッド
	 */
	public static List<SKHRelation> getResourceBundle() {
		ResourceBundle rb = ResourceBundle.getBundle("SintyokuKanriHanei");
		Enumeration<String> enm = rb.getKeys();
		List<SKHRelation> relList = new ArrayList<>();
		for(String key : Collections.list(enm)) {
			String line = rb.getString(key);
			String[] args = StringUtils.split(line, ",");
			if(args.length == 5) {
				SKHRelation rel = new SKHRelation();
				rel.setInput1Table(args[0]);
				rel.setInput1Column(args[1]);
				rel.setInput2Table(args[2]);
				rel.setInput2Column(args[3]);
				rel.setEdit(args[4]);
				relList.add(rel);
			}
		}
		return relList;
	}

}
