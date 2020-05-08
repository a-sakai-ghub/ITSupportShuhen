package bean;

/**
 * 進捗管理反映ツール　二つのパラメータ紐づけ用クラス
 */
public class SKHRelation {

	String input1Table;

	String input2Table;

	String input1Column;

	String input2Column;

	String edit;

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

	public String getEdit() {
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

}
