package com.vin.crawl;

import java.util.*;
import java.net.*;
import java.io.*;

public class VinWebCrawler
{
    private static final int THRESHOLD_DEPTH=10; // Depth of links of pages to crawl
    private static  int maxPages;   // Number of pages to lookup for
    private static HashMap parsedURLMap = new HashMap();
    private static ArrayList yetToParseURLList = new ArrayList();

    private static boolean isURLAccessible(URL url, int timeout)
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.setRequestMethod("GET");
            int responseCode= connection.getResponseCode();
            return (200 <= responseCode && responseCode <=399 );
        }
        catch (IOException exception)
        {
            return false;
        }
    }

    /*
    *   Read through content of page url via BR & IR
     */
    private static String readUrlPageContent(URL url)
    {
        try
        {
            URLConnection urlConnection = url.openConnection();
            //System.out.println("Downloading "+url.toString()); // Entire page content
            urlConnection.setAllowUserInteraction(false);
            InputStream inputStream =  url.openStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String nextLine, newContent="";

            while ((nextLine= bufferedReader.readLine())!=null)
            {
                newContent += nextLine;
            }
            return newContent;
        }
        catch (IOException e)
        {
            System.out.println("Error:: Could'nt open Url");
            return "";
        }
    }

    private static void addNewUrl(URL oldUrl, String newUrlSting)
    {
        URL url;
        System.out.println("URL: "+newUrlSting);
        try
        {
            url=new URL(oldUrl,newUrlSting);
            if(!parsedURLMap.containsKey(url))
            {
                String fileName = url.getFile();
                int i = fileName.lastIndexOf("htm");
                if((i==fileName.length()-3) || (i == fileName.length()-4))
                {
                    parsedURLMap.put(url,1);
                    yetToParseURLList.add(url);
                    System.out.println("New Url: "+url.toString());
                }
            }
        }
        catch (MalformedURLException me)
        {
            return;
        }

    }

    private static void parseUrlsOrImgsInPage(URL url, String page, String urlOrImg)
    {
        String pageContentInLowerCase = page.toLowerCase(); // As case of html tags could be mixed one
        int index=0, indexOfGtSymboll,indexOfHrefOrImg,indexOfUrlLink,indexOfClosedQuote,indexOfHash,endIndex;
        String tagElementName="", tagAttributeName="";
        if(urlOrImg.equals("url"))
        {
            tagElementName="<a";
            tagAttributeName="href";
        }
        else if(urlOrImg.equals("img"))
        {
            tagElementName="<img";
            tagAttributeName="src";
        }
        while (((index=pageContentInLowerCase.indexOf(tagElementName,index))!=-1))
        {
            indexOfGtSymboll= pageContentInLowerCase.indexOf(">",index);
            indexOfHrefOrImg=pageContentInLowerCase.indexOf(tagAttributeName,index);
            if(indexOfHrefOrImg!=-1)
            {
                indexOfUrlLink=pageContentInLowerCase.indexOf("\"",indexOfHrefOrImg)+1;
                if((indexOfUrlLink !=-1) && (indexOfGtSymboll!=-1) && (indexOfUrlLink <indexOfGtSymboll))
                {
                    indexOfClosedQuote = pageContentInLowerCase.indexOf("\"",indexOfUrlLink);
                    indexOfHash = pageContentInLowerCase.indexOf("#",indexOfUrlLink);
                    if((indexOfClosedQuote !=-1) && (indexOfClosedQuote < indexOfGtSymboll))
                    {
                        endIndex = indexOfClosedQuote;
                        if((indexOfHash!=-1) && (indexOfHash<indexOfClosedQuote))
                        {
                            endIndex=indexOfHash;
                        }

                        String newUrlString = page.substring(indexOfUrlLink,endIndex);
                        addNewUrl(url,newUrlString);
                    }
                }
            }
            index=indexOfGtSymboll;
        }
    }

    public static void main(String[] argv)
    {
        URL url;

        String BASE_URL = System.getProperty("base.url");
        int T_DEPTH=0;

        if(System.getProperty("threshold.pepth")!=null)
        {
            T_DEPTH=Integer.parseInt((System.getProperty("threshold.depth")));
        }
        if(argv.length <= 1 && BASE_URL==null)
        {
            throw new RuntimeException("Usage (Eg:) VinWebCrawler https://www.google.com/finance 10 . Exiting. Or run via ant");
        }
        else if ((argv.length >1))
        {
            maxPages=Integer.parseInt(argv[2])!=0 ? Integer.parseInt(argv[2]) : THRESHOLD_DEPTH;
        }
        else if(T_DEPTH!=0)
        {
            maxPages=T_DEPTH;
        }

        try
        {
            if(BASE_URL!=null)
            {
                url=new URL(BASE_URL);
            }
            else
            {
                url=new URL(argv[1]);
            }
            isURLAccessible(url,1000);
        }
        catch (MalformedURLException me)
        {
            System.out.println("Not a valid URL!!");
            return;
        }

        parsedURLMap.put(url,1);
        yetToParseURLList.add(url);
        System.out.print(".... Starting crawnl on URL: "+url.toString());

        // Set System proxy settings here Or in DNS /ets/hosts or change c:\Windows\system32\drivers\etc\hosts.rtf file to include the IP

        for(int i=0; i<maxPages;i++)
        {
            url= (URL) yetToParseURLList.get(0);
            yetToParseURLList.remove(0); // base url provided
            System.out.println("Fetching from: "+url.toString());
            String page = readUrlPageContent(url);
            //Display page to debug.. Entire page
            if(page.length()!=0)
            {
                parseUrlsOrImgsInPage(url,page,"url");
                parseUrlsOrImgsInPage(url,page,"img");
            }
            if(yetToParseURLList.isEmpty())
            {
                break;
            }
        }

    }


}
