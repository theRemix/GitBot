import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JToolBar;
import java.awt.Dimension;
import java.awt.Container;
public class GitBot{
	private static final String APP_TITLE = "GitBot";
	private static final String APP_VERSION = "v0.1";
	private static final String REFRESH_BUT_LABEL = "Refresh";
	private static final String PULL_BUT_LABEL = "Pull";
	private static final String PULL_ALL_BUT_LABEL = "Pull All";
	private static final String PUSH_BUT_LABEL = "Push";
	private static final String PUSH_ALL_BUT_LABEL = "Push All";
	
	private static GitBot instance;
	
	public static void main(String[] args) 
    {
		GitBot gitBot = GitBot.getInstance();
        gitBot.init();
	}
	public static GitBot getInstance(){
		if(instance == null){
			instance = new GitBot();
		}
		return instance;
	}
	
	// singleton
	public Container pane;
	public Inspector inspector;
	public TableView tableView;
	private JToolBar toolBar;
	private JButton refreshBut;
	private JButton pullBut;
	private JButton pullAllBut;
	private JButton pushBut;
	private JButton pushAllBut;
	
	public void init(){
		GitBot gitBot = GitBot.getInstance();
		
		// setup ui
        JFrame frame = new JFrame(APP_TITLE + " " + APP_VERSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
		frame.setMinimumSize(new Dimension(250, 125));
		frame.setPreferredSize(new Dimension(500, 400));;
		pane = frame.getContentPane();
		
		toolBar = new JToolBar();
		pane.add(toolBar);
		
		// buttons for ui
		refreshBut = new JButton(REFRESH_BUT_LABEL);
		toolBar.add(refreshBut);
		
		pullBut = new JButton(PULL_BUT_LABEL);
		toolBar.add(pullBut);
		
		pullAllBut = new JButton(PULL_ALL_BUT_LABEL);
		toolBar.add(pullAllBut);
		
		pushBut = new JButton(PUSH_BUT_LABEL);
		toolBar.add(pushBut);
		
		pushAllBut = new JButton(PUSH_ALL_BUT_LABEL);
		toolBar.add(pushAllBut);
		
		// setup scanner
		inspector = new Inspector(gitBot);
		
		// setup table
		tableView = new TableView(gitBot);
		
		
		
	}
}