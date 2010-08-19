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
		
			// line = "ls -lah" + "\n";   
			// 		    stdin.write(line.getBytes() );
			// 		    stdin.flush();
	
			BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();		
		}catch (IOException err) { err.printStackTrace(); }
	}
}