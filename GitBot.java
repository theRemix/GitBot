import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
public class GitBot implements ActionListener{
	private static final String APP_TITLE = "GitBot";
	private static final String APP_VERSION = "v0.1";
	private static final String REFRESH_BUT_LABEL = "Refresh";
	private static final String PULL_BUT_LABEL = "Pull";
	private static final String PULL_ALL_BUT_LABEL = "Pull All";
	private static final String PUSH_BUT_LABEL = "Push";
	private static final String PUSH_ALL_BUT_LABEL = "Push All";
	private static final String SETTINGS_FILE_CHOOSER_TITLE = "Choose Directory that contains all your git projects";
	private static final String SETTINGS_FILE_PATH = ".projectsPath";
	private static final int APP_INIT_WIDTH = 500;
	private static final int APP_INIT_HEIGHT = 600;
	private static final String ROBOT_SAYS = "~+> ";
	
	private static GitBot instance;
	
	public static void main(String[] args) 
    {
		SwingUtilities.invokeLater(new Runnable() {
			GitBot gitBot = GitBot.getInstance();
			public void run() {
		        gitBot.init();
		    }
		});
	}
	public static GitBot getInstance(){
		if(instance == null){
			instance = new GitBot();
		}
		return instance;
	}
	
	// singleton
	public String path;
	public Container pane;
	public Inspector inspector;
	public TableView tableView;
	private JToolBar toolBar;
	private JButton refreshBut;
	private JButton pullBut;
	private JButton pullAllBut;
	private JButton pushBut;
	private JButton pushAllBut;
	private JButton	settingsBut; // temp? want this in File... menu
	private JTextArea statusTextArea;
	
	public void readSettings(){
		String readpath = "";
		try{
			File file = new File(SETTINGS_FILE_PATH);
			Scanner inputFile = new Scanner(file);
			if (inputFile.hasNext())
				readpath = inputFile.nextLine();
			inputFile.close();
		
			if(readpath == "") askUserToSetPath();
			else setNewPath(readpath);
		}catch (java.io.FileNotFoundException err){ askUserToSetPath(); }
		
		System.out.print(path);
	}
	
	
	//http://download.oracle.com/javase/tutorial/uiswing/examples/components/SimpleTableDemoProject/src/components/SimpleTableDemo.java
	private void init(){
		GitBot gitBot = GitBot.getInstance();
		
		// setup ui
        JFrame frame = new JFrame(APP_TITLE + " " + APP_VERSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
		frame.setMinimumSize(new Dimension(APP_INIT_WIDTH, APP_INIT_HEIGHT));
		frame.setPreferredSize(new Dimension(APP_INIT_WIDTH, APP_INIT_HEIGHT));;
		pane = frame.getContentPane();
		pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
		
		toolBar = new JToolBar();
		pane.add(toolBar);
		
		// buttons for ui
		refreshBut = new JButton(REFRESH_BUT_LABEL);
		toolBar.add(refreshBut);
		refreshBut.addActionListener(this);
		
		pullBut = new JButton(PULL_BUT_LABEL);
		toolBar.add(pullBut);
		
		pullAllBut = new JButton(PULL_ALL_BUT_LABEL);
		toolBar.add(pullAllBut);
		
		pushBut = new JButton(PUSH_BUT_LABEL);
		toolBar.add(pushBut);
		
		pushAllBut = new JButton(PUSH_ALL_BUT_LABEL);
		toolBar.add(pushAllBut);
		
		settingsBut = new JButton("Settings");
		settingsBut.addActionListener(this);
		toolBar.add(settingsBut);
		
		// setup table
		tableView = new TableView(gitBot);
		JScrollPane tableScrollPane = new JScrollPane(tableView.table);
		tableScrollPane.add(tableView.table.getTableHeader());
		pane.add(tableScrollPane);
		tableView.table.setFillsViewportHeight(true);
		
		statusTextArea = new JTextArea(ROBOT_SAYS + APP_TITLE + " " + APP_VERSION);
		statusTextArea.setEditable(false);
		JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
		pane.add(statusScrollPane);
		
		// setup scanner
		inspector = new Inspector(gitBot);
		readSettings();
		
		tableView.data.setValueAt("hello", 0, 0);
	}
	
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == settingsBut) {
			askUserToSetPath();
        }else if(e.getSource() == refreshBut){
			inspector.scan(path);
		}
	}
	
	private void askUserToSetPath(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(SETTINGS_FILE_CHOOSER_TITLE);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = fileChooser.showOpenDialog(pane);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
			setNewPath(file.getAbsolutePath());
        }
	}
	
	private void setNewPath(String _path){
		path = _path;
		
		try{
			PrintWriter outputFile = new PrintWriter(SETTINGS_FILE_PATH);
			outputFile.println(path);
			outputFile.close();
		}catch (java.io.FileNotFoundException err){
			robotLog("Failed to save new path configuration to "+SETTINGS_FILE_PATH+" please try again.");
			robotLog(err.getMessage());
		}
		
		robotLog("Projects path is "+path);
		inspector.scan(path);
	}
	
	public void showLog(String message){
		statusTextArea.append("\n"+message);
		updateCaret();
	}
	public void robotLog(String message){
		String msg = "\n"+ROBOT_SAYS+message;
		statusTextArea.append(msg);
		System.out.println(msg);
		updateCaret();
	}
	private void updateCaret(){
		statusTextArea.setCaretPosition(statusTextArea.getText().length());
	}
}