import java.io.*;
import java.lang.InterruptedException;
import java.util.ArrayList;
class Inspector{
	private static final String STATE_CLEAN = "Clean!";
	private static final String STATE_HAS_CHANGES = "Has Modified Files ";
	private static final String STATE_HAS_CHANGES_TO_COMMIT = "Has Files Added to Commit ";
	private static final String STATE_HAS_COMMITS_TO_PUSH = "Has Commits to Push! ";
	private static final String STATE_HAS_UPDATES = "Can be updated! ";
	
	private GitBot gitBot;
	private Process process;
	private String line;
	
	public Inspector(GitBot _gitBot)
	{
		super();
		gitBot = _gitBot;
	}
	
	public void scan(String path){
		scanProjects(path, true);
	}
	public void scan(String path, Boolean verbose){
		scanProjects(path, !verbose);
	}
	
	private void scanProjects(String path, Boolean quiet){
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			if(!quiet) gitBot.robotLog("Listing directories");
			
			gitBot.tableView.clear();
			ArrayList<String> projects = new ArrayList<String>();
			
			String cmd = "cd "+ path + "&& ls; exit\n";
		    process.getOutputStream().write(cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				if(!quiet) gitBot.robotLog(line);
				projects.add(line);
			}
			closeProcess();
			
			// update statuses
			for(int i=0;i<projects.size();i++){
				updateStatus(path, projects.get(i), quiet);
				closeProcess();
			}
				
		}catch (IOException err) { 
			gitBot.robotLog("Inspector.scan " + err.getMessage());
		}
	}
	
	public void updateStatus(String path, String projectName, Boolean quiet){
		String cmd = "cd "+ path+"/"+ projectName + " && git fetch; git status; exit\n";
		String branch = "";
		String projectStatus = "";
		int countModified = 0;
		int versionsApart = 0;
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			if(!quiet) gitBot.robotLog("Refreshing " + path);
			
			process.getOutputStream().write( cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				
				// noisy
				//gitBot.showLog(line);
				
				
				// parse output
				if(line.startsWith("# On branch ")){
					branch = line.substring(12);
				}else{
					if(line.startsWith("#	modified")){
						countModified++;
					}
					if(line.startsWith("# Your branch is ahead")){
						versionsApart = Integer.parseInt(line.substring(line.lastIndexOf("by ")+3, line.lastIndexOf(" commit")));
						projectStatus = STATE_HAS_COMMITS_TO_PUSH;
					}
					if(line.startsWith("# Your branch is behind") && 
						projectStatus != STATE_HAS_COMMITS_TO_PUSH){
						versionsApart = Integer.parseInt(line.substring(line.lastIndexOf("by ")+3, line.lastIndexOf(" commit")));
						projectStatus = STATE_HAS_UPDATES;
					}
					if(line.startsWith("# Changes to be committed") && 
						projectStatus != STATE_HAS_COMMITS_TO_PUSH &&
						projectStatus != STATE_HAS_UPDATES){
						projectStatus = STATE_HAS_CHANGES_TO_COMMIT;
					}
					if(line.startsWith("# Changed but not updated")  && 
						projectStatus != STATE_HAS_COMMITS_TO_PUSH &&
						projectStatus != STATE_HAS_UPDATES &&
						projectStatus != STATE_HAS_CHANGES_TO_COMMIT){
						projectStatus = STATE_HAS_CHANGES;
					}
					if(line.startsWith("nothing to commit (working directory clean)") &&
						projectStatus != STATE_HAS_COMMITS_TO_PUSH  && 
						projectStatus != STATE_HAS_UPDATES &&
						projectStatus != STATE_HAS_CHANGES_TO_COMMIT &&
						projectStatus != STATE_HAS_CHANGES){
						projectStatus = STATE_CLEAN;
					}
				} 	
			}
			
			if(countModified>0){
				if(projectStatus == STATE_HAS_COMMITS_TO_PUSH || projectStatus == STATE_HAS_UPDATES){
					projectStatus += "("+String.valueOf(versionsApart)+")";
				}else{
					projectStatus += "("+String.valueOf(countModified)+")";
				}
			}
			
			if(!branch.equals("")){
				Object[] row = { projectName, branch, projectStatus };
				// replace blank row
				if(gitBot.tableView.data.getValueAt(0,0) == null){
					gitBot.tableView.data.setValueAt(row[0],0,0);
					gitBot.tableView.data.setValueAt(row[1],0,1);
					gitBot.tableView.data.setValueAt(row[2],0,2);
				}else{
					gitBot.tableView.data.addRow(row);
				}
			}
			
			if(!quiet) gitBot.robotLog("Scanning Complete! ");
			
		}catch (IOException err) { 
			gitBot.robotLog("Inspector.updateStatus("+path+")" + err.getMessage());
		}
	}
	
	private void closeProcess(){
		try{
			process.waitFor();
		}catch(InterruptedException err){
			gitBot.robotLog("Inspector.process.waitFor() " + err.getMessage());
		}
		// try{
			// process.getOutputStream().close();
			// process.getInputStream().close();
		// }catch (IOException err) { 
		// 	gitBot.robotLog("Inspector.closeProcess" + err.getMessage());
		// }
		process.destroy();
	}
}