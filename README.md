# VinAntWebCrawlr
Simple Ant proj which simulates web crawling

Add jar files to build/lib and set JDK_HOME. Then run below one liner from unix env.
Last two arg.s we can specify to parse any specific web url.

java -classpath ${JDK_HOME}/jre/lib.tools.jar:build/lib/ant-1.9.3.jar:build/lib/ant-contrib-1.0b3.jar:build/lib/ml-ant-http-1.1.3.jar:${JDK_HOME}/lib/tools.jar org.apache.tools.ant.lauch.Launcher -f build/build.xml run-web-crawler -Dproj.home=. -Dbase.url.to.crawl=https://www.google.com/finance -Ddomain.name=google.com


#Run Java based Crawler with below command line Or with Ant call

Eg:

VinWebCrawler https://www.google.com/finance 10
