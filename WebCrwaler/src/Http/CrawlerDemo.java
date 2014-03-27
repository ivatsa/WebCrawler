/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Http;

import java.io.*;
//import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

/**
 *
 * @author vatsa
 */
public class CrawlerDemo {
    public static URL url;
    public static String CSRF = "", SID = "";
   
    
    public static boolean  Authenticate(String inputURL,String username,String password)
	throws Exception{
   
url = new URL(inputURL);
StringBuffer buffer = new StringBuffer();
String protocol = url.getProtocol();
if (!protocol.equals("http"))
       throw new IllegalArgumentException("URL must use 'http:' protocol");
String host = url.getHost();
int port = url.getPort();
if (port == -1) {port = 80;}  
Socket socket = new Socket(host, port);
PrintWriter output = new PrintWriter(socket.getOutputStream(),true); 
BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

output.println("GET " + "/accounts/login/" + " HTTP/1.1");
output.println("Host:" + host);
output.println();

String line = ""; 
while((line = socketInput.readLine()) != null)
 {
     String regexp = "Set-Cookie: csrftoken=(\\w+)*; expires=";
     Pattern pattern = Pattern.compile(regexp);
     Matcher matcher = pattern.matcher(line);
     if(matcher.find()) 
     {
         CSRF = line.substring(line.indexOf('=')+1, line.indexOf(';'));//
     }
     regexp = "Set-Cookie: sessionid=(\\w+)*; expires=";
     pattern = Pattern.compile(regexp);
     matcher = pattern.matcher(line);
     if(matcher.find()) 
     {
         SID = line.substring(line.indexOf('=')+1, line.indexOf(';'));//
     }  
 }
socketInput.close();
socket.close();

socket = new Socket(host, port);
output = new PrintWriter(socket.getOutputStream(),true); 
socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
String Content = "csrfmiddlewaretoken=" + CSRF + "&username=" + username + "&password=" + password + "&next=";
String POST_format = "POST " + "/accounts/login/" + " HTTP/1.1 \r\n" +
                      "Host: " + host + "\r\n" +
                      "Cookie: csrftoken=" + CSRF  +"; sessionid=" + SID +"\r\n" + //+ 
                      "Content-Type: " + "application/x-www-form-urlencoded \r\n" + 
                      "Content-length: " + Content.length() +"\r\n" + "\r\n" +  Content + "\r\n";

 output.println(POST_format);
 output.flush();
 socket.setTcpNoDelay(true);
while((line = socketInput.readLine()) != null)
{
 String regexp = "Set-Cookie: csrftoken=(\\w+)*; expires=";
 Pattern pattern = Pattern.compile(regexp);
 Matcher matcher = pattern.matcher(line);
 if(matcher.find()) 
 {
         CSRF = line.substring(line.indexOf('=')+1, line.indexOf(';'));
 }
 regexp = "Set-Cookie: sessionid=(\\w+)*; expires=";
 pattern = Pattern.compile(regexp);
 matcher = pattern.matcher(line);
 if(matcher.find()) 
 {
         SID = line.substring(line.indexOf('=')+1, line.indexOf(';'));
 }  
 buffer.append(line);
}

socketInput.close();
socket.close();
return true;
}
public static void main(String[] args) throws Exception {

        Crawler newcrawler = new Crawler();
        newcrawler.Addinitialurl(args[0]);

                if (Authenticate(args[0],args[1],args[2]))
                {
                        System.out.println("number of pages=" +newcrawler.StartCrawl(CSRF, SID));

                }
    }
}
