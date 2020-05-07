package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
	public static void expectCalculate(String input1, String input2) throws EncryptedDocumentException, FileNotFoundException, IOException {
		// プロパティファイルの内容取得
		List<SKHRelation> relList = getResourceBundle();
		// input1の内容取得
		getInputFile(input1);
		// input2の内容取得
		getInputFile(input2);
	}

	/**
	 * エクセルファイルの内容取得用メソッド
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws EncryptedDocumentException
	 */
	public static void getInputFile(String fileName)
			throws EncryptedDocumentException, FileNotFoundException, IOException {
		Workbook wb = WorkbookFactory.create(new FileInputStream(fileName));

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
