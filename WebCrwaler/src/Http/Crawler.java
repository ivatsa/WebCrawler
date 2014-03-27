/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Http;


import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

/**
 *
 * @author vatsa
 */
public class Crawler {
    	private LinkedList<String> to_be_crawled;
	private ArrayList<String> crawled;
	private Map<String, Integer> myMap; 
	private StringBuffer buffer;
	
	public Crawler() {
		
		to_be_crawled = new LinkedList<String>();
		crawled = new ArrayList<String>();
		myMap = new HashMap<String, Integer> ();
		buffer = new StringBuffer();
	}

	public void Addinitialurl(String inputURL)
	throws MalformedURLException{
		
            URL initialURL = new URL(inputURL);
            to_be_crawled.add(initialURL.getPath());
	}

	public void GetAllLinks(String url, String CSRF, String SID)
	throws MalformedURLException{
		
	
		 GetUrlSource(url, CSRF, SID);
		//this function returns the source of the url passed and we get all links from that html page
		
		
		/* add the passed url to hasmap and a to_crawled list and get all the links present in that page*/
		
                int statuscode=GetStatusCode(buffer);
                if(statuscode>=200 && statuscode<300)    
                {
                    myMap.put(url, GetStatusCode(buffer));
                    crawled.add(url);
                    //System.out.println(crawled.size());
                }
                if(statuscode>=300 && statuscode<400)    
                {
                   String Location = buffer.substring(buffer.indexOf("Location: ") + "Location: ".length() ,
                                                      buffer.indexOf("Content-Length:", buffer.indexOf("Location: ") + "Location: ".length()-1));
                    URL newLocation;
                    newLocation= new URL (Location);
                    to_be_crawled.addFirst(newLocation.getPath());
                    System.out.println("300s: "+ newLocation.getPath());
                 }
                 if(statuscode>=500 && statuscode<600)
                 {  
                     to_be_crawled.addFirst(url);             
                     //System.out.println("500!!: " + url);
                 }
		
		String regexp = "(<a[^>]+>.+?</a>)";
		
		Pattern pattern = Pattern.compile(regexp);
		Matcher matcher = pattern.matcher(buffer);
		
		while (matcher.find()) {
			String link = matcher.group();
			link = link.substring(link.indexOf('"') + 1,
			link.indexOf('"', link.indexOf('"') + 1));
			if (link.startsWith("/fakebook/"))
			to_be_crawled.add(link);
			else
			continue;	
		}
		
	}
	
	public int GetStatusCode (StringBuffer buffernew)
	{

		String status_code= null;
		String regexp = "HTTP/1.1 (\\w+)(\\w+)(\\w+) ";
				
				
				
				Pattern pattern = Pattern.compile(regexp);
				Matcher matcher = pattern.matcher(buffernew);
				
				
				
				while (matcher.find()) {
					
					status_code = matcher.group();
					}
		
		
		return Integer.parseInt(status_code.substring(9,12));
		
	}
	
	
	
	public void GetUrlSource(String url, String CSRF, String SID)
	{
            String GET_HEADER;
            PrintWriter output;
            BufferedReader socketInput;
            try
            {
            Socket BrowseSocket = new Socket("cs5700f12.ccs.neu.edu", 80);
            BrowseSocket.setTcpNoDelay(true);
            output = new PrintWriter(BrowseSocket.getOutputStream(),true); 
            socketInput = new BufferedReader(new InputStreamReader(BrowseSocket.getInputStream()));
            String Cookie = "Cookie: csrftoken=" + CSRF + "; sessionid=" + SID + "\r\n"; // 
            GET_HEADER = "GET " + url + " HTTP/1.1 \r\n" +
                     "Host: " + "cs5700f12.ccs.neu.edu" + "\r\n"   + Cookie;
            
            //System.out.println(url);
            output.println(GET_HEADER);
            String line="";
            buffer.replace(0, buffer.length(), ""); //clear buffer
            if((line = socketInput.readLine()).startsWith("HTTP/1.1 2"))
            {
                buffer.append(line);
                while(!(line = socketInput.readLine()).contains("</html>") ) 
                buffer.append(line);    // whole page is here as a single line
            }
            else if(line.startsWith("HTTP/1.1 3"))
            {
                buffer.append(line);
                while(!(line.startsWith("Content-Length:")) ) 
                    buffer.append(line);    // whole page is here as a single line
            }      
            else{
                buffer.append(line);}
//            if(line.contains("HTTP/1.1 500 INTERNAL SERVER ERROR"))
//            { 
//                buffer.append(line);
//                to_be_crawled.addFirst(url);
//                
//                System.out.println("500!!: " + url);
//            }
            }
           catch(UnknownHostException unknown){
                System.out.println("Exception unknown host:");
           }
            catch(IOException unknown){
                System.out.println("Exception IO");
           }
            catch(NullPointerException unknown){
                System.out.println(buffer);
           }
	}
	
	
	public int StartCrawl(String CSRF, String SID)
	throws MalformedURLException{
		
		//Iterator to_be_crawled_iterator = to_be_crawled.iterator();
                
		while(!to_be_crawled.isEmpty())
		{
			String first = (String)to_be_crawled.remove();
                        
			
			if(!myMap.containsKey(first))
			{
				
				GetAllLinks(first, CSRF, SID);
			}
			
			else
			{
				continue;
			}
			
		}
		
		return crawled.size();
		
	}

}
