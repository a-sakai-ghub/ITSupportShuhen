package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
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

	/* 結果出力用ファイル */
	public static final String RESULT_FILE = "進捗管理反映ツールresult用_";

	/* 結果出力用ファイル末尾 */
	public static final String RESULT_FILE_FIN = ".xlsx";

	/* 結果出力用ファイル出力先ディレクトリ */
	public static final String RESULT_FILE_DIR = "./expect/";

	/* 結果出力用シート名末尾 */
	public static final String RESULT_SHEET_FIN = "_期待値";

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
		// input1の内容取得
		Map<String,List<Map<String,Object>>> input1Map = getInputFile(input1);
		// input2の内容取得
		Map<String,List<Map<String,Object>>> input2Map = getInputFile(input2);
		// input2の値をexpectフォルダ内にコピー（期待値算出用のエクセルシートとする）
		// 以降、期待値算出用のエクセルシートに追加していく形で処理を進める。
		String expectFileName = createExpectFileFormat(input2);
		// 結果用のオブジェクト
		Map<String,List<Map<String,Object>>> resultMap = createExpectValue(input1Map, input2Map);
		// 期待値算出用のエクセルシートに結果用のオブジェクトにため込んだ内容を吐きだす
		createExpectFile(expectFileName, resultMap);

	}

	/**
	 * 期待値算出用メソッド。
	 * @param input1Map
	 * @param input2Map
	 * @return
	 */
	private static Map<String, List<Map<String, Object>>> createExpectValue(
			Map<String, List<Map<String, Object>>> input1Map,
			Map<String, List<Map<String, Object>>> input2Map) {
		// 結果用のオブジェクト
		Map<String,List<Map<String,Object>>> resultMap = new LinkedHashMap<>();
		// それぞれのテーブルのKey項目を取得
		Map<String,String> keyItemMap = getResourceBundleKeyItem();

		for (Map.Entry<String, List<Map<String, Object>>> table : input2Map.entrySet()) {
			String targetTable = table.getKey();
			// キー項目を取得
			String keyItem = keyItemMap.get(targetTable);
			List<Map<String, Object>> targetList = input2Map.get(targetTable);
			List<Map<String, Object>> resultList = new ArrayList<>();
			// 行でループ
			for (int i = 0; i < targetList.size(); i++) {
				Map<String,Object> targetMap = targetList.get(i);
				// 新規、修正、取消、Ip-Opsおよび設備構築の判定
				// TODO
				// 判定内容によりプロパティファイルの内容取得
				List<SKHRelation> relList = getResourceBundleSKHRelation();
				// キー項目の値を取得
				Object key = targetMap.get(keyItem);
				// input1から期待値取得対象の行を取得
				Map<String, Map<String,Object>> expTgtMap = getExpectTargetMap(key, keyItemMap, input1Map);
				Map<String,Object> resultCelVluMap = new LinkedHashMap<>();

				// 列でループ
				for(Map.Entry<String, Object> cellValue : targetMap.entrySet()) {

					// この辺で期待値算出(TODO)
					resultCelVluMap.put(cellValue.getKey(), cellValue.getValue());
				}
				resultList.add(resultCelVluMap);
			}
			resultMap.put(targetTable, resultList);
		}
		return resultMap;

	}

	/**
	 * 期待値算出用のMapを取得
	 * @param key
	 * @param keyItemMap
	 * @param targetMap
	 * @return
	 */
	private static Map<String, Map<String, Object>> getExpectTargetMap(Object key, Map<String,String> keyItemMap,
			Map<String, List<Map<String, Object>>> targetMap) {
		Map<String, Map<String, Object>> expTgtMap = new LinkedHashMap<>();
		for (Map.Entry<String, List<Map<String, Object>>> table : targetMap.entrySet()) {
			Map<String,Object> tgtLine = new LinkedHashMap<>();
			String targetTable = table.getKey();
			// 期待値算出先のキー項目を取得
			String targetKeyItem = keyItemMap.get(targetTable);
			// 全行分の情報を取得
			List<Map<String, Object>> targetList = targetMap.get(targetTable);
			// 行でループ
			for (int i = 0; i < targetList.size(); i++) {
				Map<String,Object> line = targetList.get(i);
				// 期待値算出先のキー項目の値を取得
				Object tgtKey = line.get(targetKeyItem);
				if(getStringValue(key).equals(getStringValue(tgtKey))) {
					expTgtMap.put(targetTable, line);
				}
			}
		}
		return expTgtMap;
	}

	/**
	 * ObjectをStringに変換して取得する。
	 * @param value
	 * @return
	 */
	private static String getStringValue(Object value) {
		String rtnValue = "";
		if(value instanceof String) {
			rtnValue = (String)value;
		} else if (value instanceof Double || value instanceof Boolean) {
			rtnValue = String.valueOf(value);
		} else if (value instanceof Date) {
			rtnValue = value.toString();
		}
		return rtnValue;
	}

	/**
	 * 期待値算出用エクセルシート書き込み用メソッド。
	 * @param fileName
	 * @param resultList
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EncryptedDocumentException
	 */
	private static void createExpectFile(String fileName,
			Map<String,List<Map<String,Object>>> resultMap)
					throws EncryptedDocumentException, FileNotFoundException, IOException {
		// エクセルファイルを読み込み
		Workbook wb = WorkbookFactory.create(new FileInputStream(fileName));
		// 結果用のオブジェクトでループ(テーブル(シート)毎)
		for (Map.Entry<String, List<Map<String, Object>>> table : resultMap.entrySet()) {
			String targetTable = table.getKey();
			List<Map<String,Object>> resultList = table.getValue();
			String expectShtName = targetTable + RESULT_SHEET_FIN;
			// 期待値用のシートを作成＆取得
			Sheet expectSht = wb.createSheet(expectShtName);
			// 初期値を設定
			int lineColumnInt = 0;
			int rowInt = 0;
			// カラム用のマップオブジェクトを作成
			Map<String, Object> columnMap = table.getValue().get(0);
			Row row = expectSht.createRow(rowInt);
			// 先頭行（カラム名）を出力
			for(Map.Entry<String,Object> column : columnMap.entrySet()) {
				Cell columnCell = row.createCell(lineColumnInt);
				columnCell.setCellValue(column.getKey());
				lineColumnInt++;
			}
			rowInt++;
			// 値を出力
			// 行でループ
			for (int i = 0; i < resultList.size(); i++) {
				// 列の値を初期化
				lineColumnInt = 0;
				Map<String,Object> resultCelVleMap = resultList.get(i);
				row = expectSht.createRow(rowInt);
				// 列でループ
				for(Map.Entry<String, Object> cellValue : resultCelVleMap.entrySet()) {
					Cell valueCell = row.createCell(lineColumnInt);
					Object value = cellValue.getValue();
					if(value instanceof String) {
						valueCell.setCellValue((String)value);
					} else if (value instanceof Boolean) {
						valueCell.setCellValue((Boolean)value);
					} else if (value instanceof Double) {
						valueCell.setCellValue((Double)value);
					} else if (value instanceof Date) {
						valueCell.setCellValue((Date)value);
					}
					lineColumnInt++;
				}
				rowInt++;
			}
		}
		FileOutputStream out = new FileOutputStream(fileName);
		wb.write(out);
	}

	/**
	 * 期待値算出用シートの作成
	 * @param fileName フォーマットとなるファイル
	 * @return 期待値算出用シートのファイル名
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EncryptedDocumentException
	 *
	 */
	private static String createExpectFileFormat(String fileName)
			throws EncryptedDocumentException, FileNotFoundException, IOException {
		// フォーマットのファイルを読み込んでコピペ
		Path source = Paths.get(fileName);
		String expectFileFormat = RESULT_FILE_DIR + RESULT_FILE + getDateFormat() + RESULT_FILE_FIN;
		Path target = Paths.get(expectFileFormat);
		Files.copy(source, target);
		return expectFileFormat;
	}


	/**
	 * エクセルファイルの内容取得用メソッド
	 * @param fileName
	 * @return Key：テーブル(シート)名 Value:カラムと値のオブジェクトファイル
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EncryptedDocumentException
	 *
	 */
	private static Map<String, List<Map<String, Object>>> getInputFile(String fileName)
			throws EncryptedDocumentException, FileNotFoundException, IOException {
		// エクセルファイルを読み込み
		Workbook wb = WorkbookFactory.create(new FileInputStream(fileName));
		// シート名の一覧を取得
		List<String> sheetList = getSheetNames(wb);
		// Key：テーブル(シート)名 Value:カラムと値のオブジェクトファイルを戻り値とする
		Map<String, List<Map<String, Object>>> resultMap = new LinkedHashMap<>();
		// 内容をマップに保存する
		for (String sheet : sheetList) {
			Sheet tgtSht = wb.getSheet(sheet);
			// カラム名を取得
			Row column = tgtSht.getRow(0);

			List<Map<String, Object>> rowList = new ArrayList<>();
			for (int rowInt = 1;; rowInt++) {
				Map<String, Object> shtMap = new LinkedHashMap<>();
				Row value = tgtSht.getRow(rowInt);
				// Rowに値がない場合（nullの場合）はループから抜ける
				if (value == null) {
					break;
				}
				for (int i = 0; i < column.getLastCellNum(); i++) {
					Cell clmCell = column.getCell(i);
					Cell vleCell = value.getCell(i);
					shtMap.put(clmCell.getStringCellValue(), getCellValue(vleCell));
				}
				rowList.add(shtMap);
			}
			resultMap.put(sheet, rowList);

		}

		return resultMap;
	}

	 public static Object getCellValue(Cell cell) {
	        Objects.requireNonNull(cell, "cell is null");

	        CellType cellType = cell.getCellType();
	        if (cellType == CellType.BLANK) {
	            return null;
	        } else if (cellType == CellType.BOOLEAN) {
	            return cell.getBooleanCellValue();
	        } else if (cellType == CellType.ERROR) {
	            throw new RuntimeException("Error cell is unsupported");
	        } else if (cellType == CellType.FORMULA) {
	            throw new RuntimeException("Formula cell is unsupported");
	        } else if (cellType == CellType.NUMERIC) {
	            if (DateUtil.isCellDateFormatted(cell)) {
	                return cell.getDateCellValue();
	            } else {
	                return cell.getNumericCellValue();
	            }
	        } else if (cellType == CellType.STRING) {
	            return cell.getStringCellValue();
	        } else {
	            throw new RuntimeException("Unknow type cell");
	        }
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
	 * 互いの値の結び付け用プロパティファイル呼び出し
	 */
	private static List<SKHRelation> getResourceBundleSKHRelation() {
		ResourceBundle rb = getResourceBundle("SintyokuKanriHanei");
		Enumeration<String> enm = rb.getKeys();
		List<SKHRelation> relList = new ArrayList<>();
		for(String key : Collections.list(enm)) {
			String line = rb.getString(key);
			String[] args = StringUtils.split(line, ",");
			if(args.length >= 5) {
				SKHRelation rel = new SKHRelation();
				rel.setInput1Table(args[0]);
				rel.setInput1Column(args[1]);
				rel.setInput2Table(args[2]);
				rel.setInput2Column(args[3]);
				for (int i = 4; i < args.length  ; i++) {
					rel.addEditList(args[i]);
				}
				relList.add(rel);
			}
		}
		return relList;
	}

	/**
	 * Key項目用プロパティファイル呼び出し
	 */
	private static Map<String,String> getResourceBundleKeyItem() {
		ResourceBundle rb = getResourceBundle("SintyokuKanriHaneiKeyItem");
		Map<String,String> keyItemMap = new HashMap<>();
		Enumeration<String> enm = rb.getKeys();
		for(String key : Collections.list(enm)) {
			String value = rb.getString(key);
			keyItemMap.put(key, value);
		}
		return keyItemMap;
	}


	/**
	 * プロパティファイル取得用メソッド
	 */
	private static ResourceBundle getResourceBundle(String bundle) {
		ResourceBundle rb = ResourceBundle.getBundle(bundle);
		return rb;
	}



	/**
	 * 現在日付をyyyyMMddhhmmssSSS形式で文字列返却する。
	 *
	 */
	private static String getDateFormat() {
		DateFormat format = new SimpleDateFormat("yyyyMMddhhmmssSSS");
		return format.format(new Date());

	}

}
