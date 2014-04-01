import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.Normalizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class GetInfoFromHtml {

	public GetInfoFromHtml(){
		
	}
	
	/**
	 * Lấy tên đường dẫn lưu nội dung các bài báo
	 * @param link
	 * @return đường dẫn thư mục lưu trữ
	 * @throws IOException
	 */
	public static String GetPathDirSave(String link) throws IOException {
		String path, htmlLine;
		Document doc;
		int st, fn;
		path = null;
		
		URL url = new URL(link);
		URLConnection con = url.openConnection();
        BufferedReader sourceHtml = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        while ((htmlLine = sourceHtml.readLine()) != null)
        	if (htmlLine.indexOf("page-header article-header") != -1)
        	{
        		//Lấy tên category của đường link
        		st = htmlLine.indexOf("<span>");
        		fn = htmlLine.indexOf("</span") + 7;
        		path = htmlLine.substring(st,  fn);
        		doc = Jsoup.parse(path);
        		path = doc.text();
        		
        		//Tạo đường dẫn thư lục lưu trữ
        		path = path.replaceAll(" > ", "/");
        		path = path.replaceAll(" ", "");
        		path = Normalizer.normalize(path, Normalizer.Form.NFD).replace("đ", "d").replace("Đ", "D").replaceAll("[^\\p{ASCII}]", "");
        		path = path.toLowerCase();
        		break;
        	}
		
		return path;
	}
	
	/**
	 * Lấy link các bài bài ở mỗi trang tóm tắt
	 * @param link
	 * @param bufferWrite
	 * @throws IOException
	 */
	public static int getLinkOfArticke(String link, BufferedWriter bufferWrite) throws IOException {
		
		String htmlLine;
		Document doc;
		int links = 0;
		
		URL url = new URL(link);
		URLConnection con = url.openConnection();
        BufferedReader sourceHtml = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        while ((htmlLine = sourceHtml.readLine()) != null)
        	if (htmlLine.indexOf("sprite clickable comment") != -1)
        	{
        		doc = Jsoup.parse(htmlLine);
        		Elements element = doc.select("a[href]");
        		bufferWrite.write(element.attr("abs:href") + "\n");
        		links++;
        	}
        return links;
	}
}
