import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
class TableView {
	private static final String REPO_COLUMN_LABEL = "Project Name";
	private static final String STATUS_COLUMN_LABEL = "Status";
	
	private Vector columnNames;
	private GitBot gitBot;

	public DefaultTableModel data;
	public JTable table;
	
	public TableView(GitBot _gitBot)
	{
		gitBot = _gitBot;
		
		columnNames = new Vector();
		columnNames.addElement(REPO_COLUMN_LABEL);
		columnNames.addElement(STATUS_COLUMN_LABEL);
		
		data = new DefaultTableModel(columnNames, 2);
		
		table = new JTable(data);
		
	}
}
