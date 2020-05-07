package bean;

import java.util.ArrayList;
import java.util.List;

public class SoGaiGyoumuHaneiEditList {

	//項目名
	private String columnName;

	//項目ID
	private String columnId;

	//情報元テーブル/項目ID (2次元配列でもいっか）
	//private List<DbInfo> dbInfo = new ArrayList<DbInfo>();
	private String table ;

	private String tableColumnId ;

	//コード変換 変換元情報と変換先情報必要
	private List<CodeConvertInfo> codeConvertInfo = new ArrayList<CodeConvertInfo>();


	//和名変換
	private String convertName;


	//スラッシュ付与
	private boolean addSlash;

	//スラッシュ除去
	private boolean removeSlash;

	//ハイフン除去
	private boolean removeHypen;

	//文字列結合
	//private String concatColumn;

	//全角変換
	private String convertFullWidth;

	//半角変換
	private String convertHalfWidth;

	//桁切
	private List<CutDigitInfo> cutDigitInfo = new ArrayList<CutDigitInfo>();

	/**
	 * @return columnName
	 */
	public String getColumnName() {
		return columnName;
	}

	/**
	 * @param columnName セットする columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	/**
	 * @return columnId
	 */
	public String getColumnId() {
		return columnId;
	}

	/**
	 * @param columnId セットする columnId
	 */
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	/**
	 * @return table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table セットする table
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return tableColumnId
	 */
	public String getTableColumnId() {
		return tableColumnId;
	}

	/**
	 * @param tableColumnId セットする tableColumnId
	 */
	public void setTableColumnId(String tableColumnId) {
		this.tableColumnId = tableColumnId;
	}

	/**
	 * @return codeConvertInfo
	 */
	public List<CodeConvertInfo> getCodeConvertInfo() {
		return codeConvertInfo;
	}

	/**
	 * @param codeConvertInfo セットする codeConvertInfo
	 */
	public void setCodeConvertInfo(List<CodeConvertInfo> codeConvertInfo) {
		this.codeConvertInfo = codeConvertInfo;
	}

	/**
	 * @return convertName
	 */
	public String getConvertName() {
		return convertName;
	}

	/**
	 * @param convertName セットする convertName
	 */
	public void setConvertName(String convertName) {
		this.convertName = convertName;
	}

	/**
	 * @return addSlash
	 */
	public boolean isAddSlash() {
		return addSlash;
	}

	/**
	 * @param addSlash セットする addSlash
	 */
	public void setAddSlash(boolean addSlash) {
		this.addSlash = addSlash;
	}

	/**
	 * @return removeSlash
	 */
	public boolean isRemoveSlash() {
		return removeSlash;
	}

	/**
	 * @param removeSlash セットする removeSlash
	 */
	public void setRemoveSlash(boolean removeSlash) {
		this.removeSlash = removeSlash;
	}

	/**
	 * @return removeHypen
	 */
	public boolean isRemoveHypen() {
		return removeHypen;
	}

	/**
	 * @param removeHypen セットする removeHypen
	 */
	public void setRemoveHypen(boolean removeHypen) {
		this.removeHypen = removeHypen;
	}

	/**
	 * @return convertFullWidth
	 */
	public String getConvertFullWidth() {
		return convertFullWidth;
	}

	/**
	 * @param convertFullWidth セットする convertFullWidth
	 */
	public void setConvertFullWidth(String convertFullWidth) {
		this.convertFullWidth = convertFullWidth;
	}

	/**
	 * @return convertHalfWidth
	 */
	public String getConvertHalfWidth() {
		return convertHalfWidth;
	}

	/**
	 * @param convertHalfWidth セットする convertHalfWidth
	 */
	public void setConvertHalfWidth(String convertHalfWidth) {
		this.convertHalfWidth = convertHalfWidth;
	}

	/**
	 * @return cutDigitInfo
	 */
	public List<CutDigitInfo> getCutDigitInfo() {
		return cutDigitInfo;
	}

	/**
	 * @param cutDigitInfo セットする cutDigitInfo
	 */
	public void setCutDigitInfo(List<CutDigitInfo> cutDigitInfo) {
		this.cutDigitInfo = cutDigitInfo;
	}





}
