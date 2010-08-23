import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.awt.Color;
class TableView {
	private static final String REPO_COLUMN_LABEL = "Project Name";
	private static final String BRANCH_COLUMN_LABEL = "Current Branch";
	private static final String STATUS_COLUMN_LABEL = "Status";
	
	private Vector columnNames;
	private GitBot gitBot;

	public DefaultTableModel data;
	public JTable table;
	
	/**
	* TableView Constructor
	*
	* @param _gitBot	the instance of GitBot
	* @see GitBot
	*/
	public TableView(GitBot _gitBot)
	{
		gitBot = _gitBot;
		
		columnNames = new Vector();
		columnNames.addElement(REPO_COLUMN_LABEL);
		columnNames.addElement(BRANCH_COLUMN_LABEL);
		columnNames.addElement(STATUS_COLUMN_LABEL);
		
		data = new DefaultTableModel(columnNames, 2);
		
		// http://forums.sun.com/thread.jspa?threadID=427894
		table = new JTable(data){
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		table.setFillsViewportHeight(true);
		table.setSelectionForeground(Color.BLACK);
		table.setSelectionBackground(Color.ORANGE);
	}
	
	/**
	* Clears all rows in the table
	*
	*/
	public void clear()
	{
		int l = data.getRowCount();
		for(int i=l-1;i>0;i--){
			data.removeRow(i);
		}
	}
}
