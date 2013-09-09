package sock;

import java.net.*;
import java.io.*;

public class ServerThread extends Thread
{
	Socket connSocket;

	public ServerThread(Socket connSocket)
	{
		this.connSocket = connSocket;
	}

	public void run()
	{
		try
		{
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connSocket.getOutputStream());


			// Start new webserver
			Webserver websrv = new Webserver();

			// Set appendFile
			websrv.setAppendFile( this.getClass().getClassLoader().getResource("").getPath() + this.getClass().getPackage().getName() ); // Find the current working directory


			// Headers
			String firstHeader = inFromClient.readLine();
			String headers = firstHeader + "\n";
			while(true) // Yeah, well.... It works. :-)
			{
				String newLine = inFromClient.readLine();

				if(newLine.length() == 0) {
					break;
				} else {
					headers += newLine + "\n";
				}
			}
			String useragent = websrv.getUseragent(headers);


			if(firstHeader == null || useragent == null)
			{
				// Close connection if no data available
				// If user-agent not present, kill it with fire....
				connSocket.close();
			}else{
				// Parse the request, which will dessicate file and type
				websrv.parseRequest(firstHeader);


				// Call methods
				int httpCode = websrv.getError(firstHeader);
				String httpText = websrv.getText(httpCode);
				String file = websrv.getFile(httpCode);
				String headerType = websrv.getType();


				// Write headers
				outToClient.writeBytes(websrv.constructHeader(httpCode, httpText, file));


				if (headerType.equals("GET")) // Will output file
				{
					outToClient.write(websrv.read(file));
				}
				if (headerType.equals("HEAD") || headerType.equals("DELETE")) // Will not output file
				{
					byte[] theBytes = "\n".getBytes("UTF-8"); // Convert a newline to byte
					outToClient.write(theBytes);
				}


				connSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}