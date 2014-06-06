import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FASTGraphsTest {
	
	public static void main(String... args){
		try {
			new FASTGraphsTest().runIt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void runIt() throws Exception {
		URL yahooDiv = new URL("http://www.fastgraphs.net/draw.FGlogin.php");
		HttpURLConnection uc = (HttpURLConnection) yahooDiv.openConnection();
		uc.setRequestMethod("POST");
		
	    String urlParameters = "username=domveit&password=dragon12&accept=1&login=Log%20In";

	    // Send post request
	    uc.setDoOutput(true);
	    DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
	    wr.writeBytes(urlParameters);
	    wr.flush();
	    wr.close();
	    int responseCode = uc.getResponseCode();
	    System.err.println("response code " + responseCode);
		
		String headerName=null;
		for (int i=1; (headerName = uc.getHeaderFieldKey(i))!=null; i++) {
		 	if (headerName.equals("Set-Cookie")) {                  
				String cookie = uc.getHeaderField(i);  
				System.err.println("Cookie:" + cookie);
		 	}
		 }
		 	
		InputStream isDiv = uc.getInputStream();
		InputStreamReader isrDiv = new InputStreamReader(isDiv, "UTF-8");
		BufferedReader brDiv = new BufferedReader(isrDiv);
		String s = null;
		while((s = brDiv.readLine()) != null){
			System.err.println(s);
		}
	}
}
