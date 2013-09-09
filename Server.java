package sock;

import java.net.*;

public class Server
{
	public static void main(String[] args) throws Exception
	{
		ServerSocket welcomeSocket = new ServerSocket(6789);

		while (true)
		{
			Socket connectionSocket = welcomeSocket.accept();
			(new ServerThread(connectionSocket)).start();
		}
	}
}