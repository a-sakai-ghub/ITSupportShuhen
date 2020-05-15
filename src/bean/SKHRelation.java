package bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 進捗管理反映ツール　二つのパラメータ紐づけ用クラス
 */
public class SKHRelation {

	String input1Table;

	String input2Table;

	String input1Column;

	String input2Column;

	List<String> editList = new ArrayList<>();

	public String getInput1Table() {
		return input1Table;
	}

	public void setInput1Table(String input1Table) {
		this.input1Table = input1Table;
	}

	public String getInput2Table() {
		return input2Table;
	}

	public void setInput2Table(String input2Table) {
		this.input2Table = input2Table;
	}

	public String getInput1Column() {
		return input1Column;
	}

	public void setInput1Column(String input1Column) {
		this.input1Column = input1Column;
	}

	public String getInput2Column() {
		return input2Column;
	}

	public void setInput2Column(String input2Column) {
		this.input2Column = input2Column;
	}

	public List<String> getEditList() {
		return editList;
	}

	public void setEditList(List<String> editList) {
		this.editList = editList;
	}

	public void addEditList(String edit) {
		this.editList.add(edit);
	}

}
