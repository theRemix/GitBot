import javax.swing.JTable;
class TableView extends JTable {
	private GitBot gitBot;
	
	public TableView(GitBot _gitBot)
	{
		super();
		gitBot = _gitBot;

	}
}
