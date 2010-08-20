import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.JSplitPane;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.io.*;
import java.util.Scanner;
public class GitBot implements ActionListener{
	private static final String APP_TITLE = "GitBot";
	private static final String APP_VERSION = "v0.1";
	private static final String REFRESH_BUT_LABEL = "Refresh";
	private static final String STATUS_BUT_LABEL = "Status";
	private static final String PULL_BUT_LABEL = "Pull";
	private static final String PULL_ALL_BUT_LABEL = "Pull All";
	private static final String PUSH_BUT_LABEL = "Push";
	private static final String PUSH_ALL_BUT_LABEL = "Push All";
	private static final String SETTINGS_FILE_CHOOSER_TITLE = "Choose Directory that contains all your git projects";
	private static final String SETTINGS_FILE_PATH = ".projectsPath";
	private static final int APP_MIN_WIDTH = 200;
	private static final int APP_MIN_HEIGHT = 150;
	private static final int APP_INIT_WIDTH = 550;
	private static final int APP_INIT_HEIGHT = 450;
	private static final int APP_INIT_X = 300;
	private static final int APP_INIT_Y = 250;
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
	private JButton statusBut;
	private JButton pullBut;
	private JButton pullAllBut;
	private JButton pushBut;
	private JButton pushAllBut;
	private JButton	settingsBut; // temp? want this in File... menu
	private JTextArea statusTextArea;
	private Process process;
	private String line;
	
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
		frame.setMinimumSize(new Dimension(APP_MIN_WIDTH, APP_MIN_HEIGHT));
		frame.setPreferredSize(new Dimension(APP_INIT_WIDTH, APP_INIT_HEIGHT));;
		frame.setLocation(APP_INIT_X,APP_INIT_Y);
		frame.setSize(APP_INIT_WIDTH, APP_INIT_HEIGHT);
		pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());
		
		toolBar = new JToolBar();
		pane.add(toolBar, BorderLayout.PAGE_END);
		
		// buttons for ui
		refreshBut = new JButton(REFRESH_BUT_LABEL);
		refreshBut.addActionListener(this);
		toolBar.add(refreshBut);
		
		statusBut = new JButton(STATUS_BUT_LABEL);
		statusBut.addActionListener(this);
		toolBar.add(statusBut);
		
		pullBut = new JButton(PULL_BUT_LABEL);
		pullBut.addActionListener(this);
		toolBar.add(pullBut);
		
		pullAllBut = new JButton(PULL_ALL_BUT_LABEL);
		pullAllBut.addActionListener(this);
		toolBar.add(pullAllBut);
		
		pushBut = new JButton(PUSH_BUT_LABEL);
		pushBut.addActionListener(this);
		toolBar.add(pushBut);
		
		pushAllBut = new JButton(PUSH_ALL_BUT_LABEL);
		pushAllBut.addActionListener(this);
		toolBar.add(pushAllBut);
		
		settingsBut = new JButton("Settings");
		settingsBut.addActionListener(this);
		toolBar.add(settingsBut);
		
		// setup table
		tableView = new TableView(gitBot);
		JScrollPane tableScrollPane = new JScrollPane(tableView.table);
		pane.add(tableView.table.getTableHeader(), BorderLayout.PAGE_START);
		
		statusTextArea = new JTextArea(ROBOT_SAYS + APP_TITLE + " " + APP_VERSION);
		statusTextArea.setEditable(false);
		JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, statusScrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(250);

		//Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 150);
		tableView.table.setMinimumSize(minimumSize);
		statusScrollPane.setMinimumSize(minimumSize);
		
		pane.add(splitPane, BorderLayout.CENTER);
		
		
		// setup scanner
		inspector = new Inspector(gitBot);
		readSettings();
	}
	
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == settingsBut) {
			askUserToSetPath();
        }else if(e.getSource() == refreshBut){
			inspector.scan(path, true);
		}else if(e.getSource() == statusBut){
			getStatus();
		}else if(e.getSource() == pullBut){
			pullSelectedRepos();
		}else if(e.getSource() == pullAllBut){
			pullAllRepos();
		}else if(e.getSource() == pushBut){
			pushSelectedRepos();
		}else if(e.getSource() == pushAllBut){
			pushAllRepos();
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
	
	private void getStatus(){
		String projectName = tableView.data.getValueAt(tableView.table.getSelectedRow(), 0).toString();
		String cmd = "cd "+ path+"/"+ projectName + " && git status; exit\n";
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			robotLog("Getting status on " + projectName);
			
			process.getOutputStream().write( cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				showLog(line);
			}
			closeProcess();
			
		}catch (IOException err) { 
			robotLog("GitBot.status("+projectName+")" + err.getMessage());
		}
	}
	
	private void pullRepo(String projectName){
		String cmd = "cd "+ path+"/"+ projectName + " && git pull; exit\n";
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			robotLog("Pulling " + projectName);
			
			process.getOutputStream().write( cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				showLog(line);
			}
			closeProcess();
			
		}catch (IOException err) { 
			robotLog("GitBot.pull("+projectName+")" + err.getMessage());
		}
		
		inspector.scan(path);
	}
	private void pullSelectedRepos(){
		int[] rows = tableView.table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			pullRepo(tableView.data.getValueAt(rows[i], 0).toString());
		}
	}
	private void pullAllRepos(){
		int num_rows = tableView.data.getRowCount();
		for(int i=0;i<num_rows;i++){
			pullRepo(tableView.data.getValueAt(i, 0).toString());
		}
	}
	
	private void pushRepo(String projectName){
		String cmd = "cd "+ path+"/"+ projectName + " && git push; exit\n";
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			robotLog("Pulling " + projectName);
			
			process.getOutputStream().write( cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				showLog(line);
			}
			closeProcess();
			
		}catch (IOException err) { 
			robotLog("GitBot.push("+projectName+")" + err.getMessage());
		}
		
		inspector.scan(path);
	}
	private void pushSelectedRepos(){
		int[] rows = tableView.table.getSelectedRows();
		for(int i=0;i<rows.length;i++){
			pushRepo(tableView.data.getValueAt(rows[i], 0).toString());
		}
	}
	private void pushAllRepos(){
		int num_rows = tableView.data.getRowCount();
		for(int i=0;i<num_rows;i++){
			pushRepo(tableView.data.getValueAt(i, 0).toString());
		}
	}
	
	private void closeProcess(){
		try{
			process.waitFor();
		}catch(InterruptedException err){
			robotLog("GitBot.process.waitFor() " + err.getMessage());
		}
		process.destroy();
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