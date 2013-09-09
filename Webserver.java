package sock;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Webserver
{
	public List<String> acceptedTypes = Arrays.asList("GET", "HEAD", "DELETE");
	public String type;
	public String file;
	public String appendFile;


	public void parseRequest(String oldHeader)
	{
		String header = oldHeader.trim();
		String[] file = header.split(" ");


		if( this.acceptedTypes.contains(file[0]) )
		{
			this.type = file[0];
		} else {
			this.type = "GET";
		}


		if(file[1].equals("/"))
		{
			this.file = getAppendFile() + "/index.html";
		}else{
			this.file = getAppendFile() + file[1];
		}
	}

	public String ContentType(String docuname)
	{
		if(docuname.endsWith(".html")) {
			return "text/html";
		} else if (docuname.endsWith(".gif")) {
			return "image/gif";
		} else if (docuname.endsWith(".png")) {
			return "image/png";
		} else if (docuname.endsWith(".jpg") || docuname.endsWith(".jpeg")) {
			return "image/jpg";
		} else if (docuname.endsWith(".js")) {
			return "text/javascript";
		} else if (docuname.endsWith(".css")) {
			return "text/css";
		} else {
			return "text/plain";
		}
	}

	public String constructHeader(int code, String error, String file)
	{
		return
				"HTTP " + code + " " + error + "\n" +
				"Content-Type: " + ContentType(file) + "\n" +
				"Connection: Close\n" +
				"\n";
	}

	public int getError(String header)
	{
		/**
		 * File check can trigger error
		 */
		File fileCheck = new File(this.file);
		if(!fileCheck.exists())
		{
			return 404;
		}

		/**
		 * Request methods
		 */
		if(getType().equals("HEAD"))
		{
			return 302;
		}

		if(getType().equals("DELETE"))
		{
			return 403;
		}


		// No error
		return 200;
	}

	public String getText(int error)
	{
		if(error == 200)
		{
			return "OK";
		}
		if(error == 404)
		{
			return "Not Found";
		}
		if(error == 302)
		{
			return "Found";
		}
		if(error == 403)
		{
			return "Forbidden";
		}

		// If no error found - just 404 that shit....
		return "Not Found";
	}

	public String getFile(int error)
	{
		// If HTTP code is 200 we will present the requested file
		if(error == 200)
		{
			return this.file;
		}


		/**
		 * Define HTTP code pages from here and down
		 */
		if(error == 404)
		{
			return getAppendFile() + "/404.html";
		}


		// If no error found - just 404 that shit....
		return getAppendFile() + "/404.html";
	}

	public byte[] read(String aInputFileName) throws FileNotFoundException
	{
		File file = new File(aInputFileName);

		byte[] result = new byte[ (int) file.length() ];
		try
		{
			InputStream input = null;
			try
			{
				int totalBytesRead = 0;
				input = new BufferedInputStream(new FileInputStream(file));
				while(totalBytesRead < result.length)
				{
					int bytesRemaining = result.length - totalBytesRead;
					int bytesRead = input.read(result, totalBytesRead, bytesRemaining);
					if (bytesRead > 0)
					{
						totalBytesRead = totalBytesRead + bytesRead;
					}
				}
			}
			finally
			{
				input.close();
			}
		}
		catch (FileNotFoundException ex) {
			throw ex;
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	public String getAppendFile()
	{
		return this.appendFile;
	}

	public void setAppendFile(String appendFile)
	{
		this.appendFile = appendFile;
	}

	public String getType()
	{
		return this.type;
	}

	public String getUseragent(String headers)
	{
		Matcher m = Pattern.compile("(User\\-Agent:)\\s(.*)").matcher(headers);
		while(m.find())
		{
			return m.group(2);
		}

		return null;
	}
}