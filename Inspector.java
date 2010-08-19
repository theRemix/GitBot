import java.io.*;
import java.lang.InterruptedException;
import java.util.ArrayList;
class Inspector{
	private GitBot gitBot;
	private Process process;
	private String line;
	
	public Inspector(GitBot _gitBot)
	{
		super();
		gitBot = _gitBot;
	}
	
	public void scan(String path){
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			gitBot.robotLog("Listing directories");
			
			gitBot.tableView.clear();
			ArrayList<String> projects = new ArrayList<String>();
			
			String cmd = "cd "+ path + "&& ls; exit\n";
		    process.getOutputStream().write(cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				gitBot.robotLog(line);
				projects.add(line);
			}
			closeProcess();
			
			// update statuses
			for(int i=0;i<projects.size();i++){
				updateStatus(path+"/"+projects.get(i));
				closeProcess();
			}
				
		}catch (IOException err) { 
			gitBot.robotLog("Inspector.scan " + err.getMessage());
		}
	}
	
	public void updateStatus(String path){
		String cmd = "cd "+ path + " && git status; exit\n";
		String rawStatus = "";
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			gitBot.robotLog("Inspecting " + path);
			
			process.getOutputStream().write( cmd.getBytes() );
		    process.getOutputStream().flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				gitBot.robotLog(line);
			}
			
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