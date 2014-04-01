import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class ArticleFromBaoMoi {
	
	private static String[] sourceLinks;
	private static String dirSave;
	
	public static void prepare()
	{
		sourceLinks = new String[7];
		
		sourceLinks[0] = "http://www.baomoi.com/Home/TheThao";
		sourceLinks[1] = "http://www.baomoi.com/Home/GiaiTri";
		sourceLinks[2] = "http://www.baomoi.com/Home/PhapLuat";
		sourceLinks[3] = "http://www.baomoi.com/Home/GiaoDuc";
		sourceLinks[4] = "http://www.baomoi.com/Home/SucKhoe";
		sourceLinks[5] = "http://www.baomoi.com/Home/OtoXemay";
		sourceLinks[6] = "http://www.baomoi.com/Home/NhaDat";
		/*sourceLinks[7] = "http://www.baomoi.com/Home/BongDa";
		sourceLinks[8] = "http://www.baomoi.com/Home/QuanVot";
		sourceLinks[9] = "http://www.baomoi.com/Home/AmNhac";
		sourceLinks[10] = "http://www.baomoi.com/Home/SanKhau";
		sourceLinks[11] = "http://www.baomoi.com/Home/SachBaoVanTho";
		sourceLinks[12] = "http://www.baomoi.com/Home/HinhSu";
		sourceLinks[13] = "http://www.baomoi.com/Home/AnNinh";
		sourceLinks[14] = "http://www.baomoi.com/Home/HocBong";
		sourceLinks[15] = "http://www.baomoi.com/Home/DaoTao";
		sourceLinks[16] = "http://www.baomoi.com/Home/LamDep";
		sourceLinks[17] = "http://www.baomoi.com/Home/TinhYeu";
		sourceLinks[18] = "http://www.baomoi.com/Home/OtoXemay";
		sourceLinks[19] = "http://www.baomoi.com/Home/DauTu-QuyHoach";
		sourceLinks[20] = "http://www.baomoi.com/Home/KhongGianSong";*/
	}
	
	public static void main( String[] args) throws IOException, InterruptedException {
		
		int i, j = 0, links;
		String link;
		
		//int delay;
		//Random rd;
		
		prepare();
		
		for (i = 0; i < 7; i++)
		{
			/**
			 * Lấy đường dẫn thư mục lưu trữ bài báo theo các chủ đề
			 */
			link = sourceLinks[i] + ".epi";
			dirSave = GetInfoFromHtml.GetPathDirSave(link);
			
			/**
			 * Duyệt 1000 trang tiêu để ở mỗi category và lưu các đường link vào file
			 */
			System.out.println("Get link of ' " + dirSave + " ' starting...");
			File file = new File(dirSave, "0_LinkOfArticle.txt");
			FileOutputStream fileOut = new FileOutputStream(file);
			OutputStreamWriter outputStream = new OutputStreamWriter(fileOut);
			BufferedWriter bufferWrite = new BufferedWriter(outputStream);
			
			for (j = 1; j <= 10000; j++)
			{
				//Thời gian delay tránh bị block mạng
				//rd = new Random();
				//delay = (rd.nextInt(100) % 5 + 1) * 1000;
				//Thread.sleep(delay);
				
				System.out.print("    *) Page: " + j + " of " + dirSave);
				link = sourceLinks[i] + "/p/" + j + ".epi";
				//Lấy các link của các bài báo ở mỗi trang 
				links = GetInfoFromHtml.getLinkOfArticke(link, bufferWrite);
				
				System.out.println(" DONE: " + links);
			}
			bufferWrite.close();
			j = 0;
			System.out.println(dirSave + ": get links: DONE!");
			
			/**
			 * Đọc file links, lấy nội dung của từng bài báo
			 */
			System.out.println("Get Articles from ' " + dirSave + " ' starting...");
			
			FileInputStream fileIn = new FileInputStream(file);
			InputStreamReader inputStream = new InputStreamReader(fileIn);
			BufferedReader bufferRead = new BufferedReader(inputStream);
			
			while ((link = bufferRead.readLine()) != null)
			{
				//Thời gian delay tránh bị block mạng
				//rd = new Random();
				//delay = (rd.nextInt(100) % 5 + 1) * 1000;
				//Thread.sleep(delay);
				j++;
				
				//Lấy nội dung của từng bài báo
				GetContentOfArticles.getContent(link);
				//Ghi ra File Output
				dirSave = GetContentOfArticles.WriteOutput(j);
				
				System.out.println("    *) " + j + " of " + dirSave);
			}
			bufferRead.close();
			System.out.println(dirSave + ": get Articles: DONE! Total: " + j);
		}
	}
}
