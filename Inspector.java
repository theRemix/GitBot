import java.io.*;
import java.lang.InterruptedException;
class Inspector{
	private GitBot gitBot;
	private Process process;
	
	public Inspector(GitBot _gitBot)
	{
		super();
		gitBot = _gitBot;
	}
	
	public void scan(String path){
		try{
			process = Runtime.getRuntime().exec("/bin/bash");
			
			String line;
			
			line = "cd "+ path + "\nls\n";
		    process.getOutputStream().write(line.getBytes() );
		    process.getOutputStream().flush();
			
			gitBot.robotLog("Listing directories");
			
			gitBot.tableView.clear();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				updateStatus(path+"/"+line);
				gitBot.showLog(line);
			}
			try{
				process.waitFor();
			}catch(InterruptedException err){
				gitBot.robotLog("Inspector.process.waitFor() " + err.getMessage());
			}
			process.destroy();
				
		}catch (IOException err) { 
			gitBot.robotLog("Inspector.scan " + err.getMessage());
		}
	}
	
	public void updateStatus(String path){
		String line = "cd "+ path + "\ngit status\n";   
		try{
		    process.getOutputStream().write( line.getBytes() );
		    process.getOutputStream().flush();
		}catch (IOException err) { 
			gitBot.robotLog("Inspector.updateStatus("+path+")" + err.getMessage());
		}
	}
}