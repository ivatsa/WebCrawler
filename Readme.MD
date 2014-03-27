##Crawler Project

the compiled .class files of .java files can be found in the "WebCrawler.tar.gz"

to run the program use the below command

java -classpath ../ Http.CrawlerDemo [initial_url] <username> <password> 




Code Description
-----------------

Data Structures used to hold url's

LinkedList-The crawler program uses a to_be_crawled LinkedList to hold all the links to be crawled

HashMap-Program uses a hash map to hold all the urls with status code 200 and which are crawled once.
	this is used to restrict crawler crawling urls redundantly


Class Crawler
---------------------
this java class has the following functions

public void Addinitialurl(URL url)-this adds the initial url which was passed to the program to the to_be_crawled list

public void GetAllLinks(String url, String CSRF, String SID)-this takes url CSRF token and Session 
id which the server returns after authentication and handles the Http Status code and gets the links of the url accordingly.

public int GetStatusCode (StringBuffer buffernew)- This takes the entire HTML page along with the HTTP headder and returns the HTTP status code

public void GetUrlSource(String url, String CSRF, String SID)-This takes the url CSRF token and a session ID and returns the HTTP headder and the 
HTML body

public int StartCrawl(String CSRF, String SID)-This takes CSRF token and Session ID and initiates the crawler on the initial URL.
once the crawling is done it returns the number of links crawled.

Class CrawlerDemo
-------------------------
This java class has the following functions

public static boolean  Authenticate(String username,String password)-This function takes username and passowrd and authenticates the initial URL

public static void main(String[] args)-main function from where the execution starts creates an instance of class Crawler and 
reads the command line arguments and adds the initial URL to the to_be_crawled list
then calls the authenticate function and finally starts crawling begining with initial URL. 		
