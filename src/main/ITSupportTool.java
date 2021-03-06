package main;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;

import common.Const;

/**
 *
 * ITテスト補助ツールのメインツール
 *
 */
public class ITSupportTool {
	/**
	 * ITテスト補助ツールのメインメソッド
	 * @param args
	 */
	public static void main(String args[]) {

		// 引数の値をチェック（ツールの区分、INPUT情報１のファイルパス、INPUT情報２のファイルパス）
		//ツールの区分を読み込み
		String toolKbn = args[0];

		String input1 = args[1];

		String input2 = args[2];


		try {
			if (Const.SINTYOKU_KANRI_HANEI.equals(toolKbn)) {

				//進捗管理反映のクラス呼び出し。
				SintyokuKanriHaneiTool skht = new SintyokuKanriHaneiTool();

				skht.expectCalculate(input1, input2);

			} else if (Const.CSV_OUTPUT.equals(toolKbn)) {

				//CSV出力クラスの呼び出し。
				CsvOutputTool csvOutput = new CsvOutputTool();

				csvOutput.expectCalculate(input1, input2);

			} else if (Const.SOGAI_GYOUMU_HANEI.equals(toolKbn)) {

				//CSV出力クラスの呼び出し。
				SoGaiGyoumuHanei sogai = new SoGaiGyoumuHanei();

				SoGaiGyoumuHanei.expectCalculate(input1, input2);

			}
		} catch (EncryptedDocumentException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


		// input1の情報、input2の情報、プロパティファイルを取得して保持
		// ※プロパティファイルはツール毎に作っておく
		// input2のシート分forで回す
		// input2の行分forで回す
		// input2のセル分forで回す
		// input2のキー情報からinput1の該当情報を得る
		// プロパティファイルの情報にしたがって期待値を算出
		// 別シートに算出した期待値を張り付け
		// 以降繰り返し
	}
}