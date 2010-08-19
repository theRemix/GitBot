import java.io.*;
class Inspector{
	private GitBot gitBot;
	private OutputStream stdin = null;
	private InputStream stderr = null;
	private InputStream stdout = null;
	
	public Inspector(GitBot _gitBot)
	{
		super();
		gitBot = _gitBot;
	}
	
	public void scan(String path){
		try{
			String line;
			Process process = Runtime.getRuntime().exec("/bin/bash");
			stdin = process.getOutputStream ();
			stderr = process.getErrorStream ();
			stdout = process.getInputStream ();
		
			line = "cd "+ path + "\nls\n";   
		    stdin.write(line.getBytes() );
		    stdin.flush();
			
			gitBot.robotLog("Listing directories");
			
			BufferedReader input = new BufferedReader(new InputStreamReader(stdout));
			while ((line = input.readLine()) != null) {
				updateStatus(path+"/"+line);
				gitBot.showLog(line);
			}
			input.close();
		}catch (IOException err) { 
			//gitBot.robotLog(err);
		}
	}
	
	public void updateStatus(String path){
		
	}
}