import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class GetContentOfArticles {
	
	private static String link = null, time = null, title = null, category = null, description = null, content = null;
	private static String[] keyWords = null, trends = null;
	private static String forder = null;
	
	public GetContentOfArticles() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Lấy nội dung của bài báo
	 * @param link: đường dẫn url bài báo
	 * @param n: Bài báo thứ n trong category hiện tại
	 * @param dirSave: đường dẫn thư mục lưu trữ
	 * @throws IOException
	 */
	public static void getContent(String linkS) throws IOException {
		
		boolean getDone = false;
		String htmlLine;
		
		link = linkS;
		
		URL url = new URL(link);
		URLConnection con = url.openConnection();
		BufferedReader sourceHtml = new BufferedReader(new InputStreamReader(con.getInputStream()));
		
		time = null; title = null; category = null; description = null; content = null;
		
		while ((htmlLine = sourceHtml.readLine()) != null && !getDone)	
		{						
			//Thời gian bài báo
			if ((htmlLine.indexOf("class=\"time\"") != -1) && (time == null))
				time = GetTime(htmlLine);
			
			//Tiêu đề bài báo
			if ((htmlLine.indexOf("class=\"title\"") != -1) && (title == null))
				title = GetTitle(htmlLine);
			
			//Lấy category
			if ((htmlLine.indexOf("page-header article-header") != -1) && (category == null))
	        	category = GetCategory(htmlLine);
			
			//Tóm tắt bài báo
			if ((htmlLine.indexOf("class=\"summary\"") != -1) && (description == null))
				description = GetDescription(htmlLine);
			
			//Nội dung bài báo
			if ((htmlLine.indexOf("itemprop=\"articleBody\"") != -1) && (content == null))
			{				
				htmlLine = sourceHtml.readLine();
				htmlLine = sourceHtml.readLine();
				content = GetContent(htmlLine);
			}
			
			//Lấy các keywords
			if (htmlLine.indexOf("class=\"keywords\"") != -1)
			{
				htmlLine = sourceHtml.readLine();
				htmlLine = sourceHtml.readLine();
				keyWords = GetKeyWord(htmlLine);
			}
			
			//Lấy các từ trends
			if (htmlLine.indexOf("<h4>Xu hướng đọc</h4>") != -1)
			{
				htmlLine = sourceHtml.readLine();
				trends = GetTrends(htmlLine, sourceHtml);
				getDone = true;
			}
		}
	}
	
	/**
	 * Ghi ra file Output
	 * @param dirSave Đường dẫn thư mục lưu
	 * @param n	bài báo thứ bao nhiêu trong category hiện tại
	 * @throws IOException
	 */
	public static String WriteOutput(int n) throws IOException {
		int i;
		
		forder = GetInfoFromHtml.GetPathDirSave(link);
		
		File file = new File(forder, n + ".json");
		FileOutputStream fileOut = new FileOutputStream(file);
		OutputStreamWriter outputStream = new OutputStreamWriter(fileOut);
		BufferedWriter bufferWrite = new BufferedWriter(outputStream);
		
		bufferWrite.write("{" + "\n");
		bufferWrite.write("\t\"url\": \"" + link + "\"," + "\n");
		bufferWrite.write("\t\"time\": \"" + time + "\"," + "\n");
		bufferWrite.write("\t\"title\": \"" + title + "\"," + "\n");
		bufferWrite.write("\t\"category\": \"" + category + "\"," + "\n");
		bufferWrite.write("\t\"description\": \"" + description + "\"," + "\n");
		bufferWrite.write("\t\"content\": \"" + content + "\"," + "\n");
		bufferWrite.write("\t\"key\": [" + "\n");
			for (i = 0; i < keyWords.length; i++)	
				bufferWrite.write("\t\t\"" + keyWords[i] + "\"," + "\n");
		bufferWrite.write("\t]," + "\n");
		bufferWrite.write("\t\"trends\": [" + "\n");
			for (i = 0; i < trends.length; i = i + 2)
			{
				bufferWrite.write("\t  {" + "\n");
				bufferWrite.write("\t\t\"word\": \"" + trends[i] + "\"," + "\n");
				bufferWrite.write("\t\t\"weith\": \"" + trends[i + 1] + "\"" + "\n");
				bufferWrite.write("\t  }," + "\n");
			}
		bufferWrite.write("\t]" + "\n");
		bufferWrite.write("}");
		
		bufferWrite.close();
		
		return forder;
	}
	
	/**
	 * Lấy thời gian bài báo
	 * @param html
	 * @return Xâu biểu diễn thời gian
	 */
	private static String GetTime(String html) {
		String time;
		time = html.substring(html.indexOf("class=\"time\"") + "class=\"time\"".length() + 1);
		time = time.substring(0, time.indexOf("</span>"));
		return time;
	}
	
	/**
	 * Lấy title bài báo
	 * @param html
	 * @return Xâu title
	 */
	private static String GetTitle(String html) {
		String title;
		Document doc;
		doc = Jsoup.parse(html);
		title = doc.text();
		title = title.replaceAll("\"", "'");
		return title;
	}
	
	/**
	 * Lấy category bài báo
	 * @param html
	 * @return Xâu biểu diễn categoty
	 */
	private static String GetCategory(String html) {
		String category;
		Document doc;
		int st = html.indexOf("<span>");
		int fn = html.indexOf("</span") + 7;
		category = html.substring(st,  fn);
		doc = Jsoup.parse(category);
		category = doc.text();
		return category;
	}
	
	/**
	 * Lấy mô tả bài báo
	 * @param html
	 * @return Xâu nội dung mô tả
	 */
	private static String GetDescription(String html) {
		String description;
		Document doc;
		doc = Jsoup.parse(html);
		description = doc.text();
		description = description.replaceAll("\"", "'");
		return description;
	}
	
	/**
	 * Lấy nội dung bài báo
	 * @param html
	 * @return Xâu nội dung bài báo
	 */
	private static String GetContent(String html) {
		String content;
		content = html.replaceAll("\"", "'");
		return content;
	}
	
	/**
	 * Lấy các key word của bài báo
	 * @param html
	 * @return màng String chưa các KeyWord
	 */
	private static String[] GetKeyWord(String html) {
		Document doc;
		String keyWord = "";
		String[] keyWords;
		
		doc = Jsoup.parse(html);
		Elements keys = doc.select("a[href]");
		
		for (Element key : keys)
			keyWord = keyWord + key.text() + ":";
			
		keyWords = keyWord.split(":");
		
		return keyWords;
	}
	
	/**
	 * Lấy các từ biểu thị xu hướng đọc
	 * @param html
	 * @param source
	 * @return mảng String các từ biểu hiện xu hướng và weith của chúng
	 * @throws IOException
	 */
	private static String[] GetTrends(String html, BufferedReader source) throws IOException {
		Document doc;
		String trend = "";
		String[] trends = null;
		
		while (html.indexOf("tagCloud") != -1)
		{
			doc = Jsoup.parse(html);
			trend = trend + doc.text() + ":";
			trend = trend + html.charAt(html.indexOf("tagCloud") + "tagCloud_".length()) + ":";
			
			html = source.readLine();
		}
		
		trends = trend.split(":");
		
		return trends;
	}
}
