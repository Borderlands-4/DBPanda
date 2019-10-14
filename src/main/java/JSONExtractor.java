import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JSONExtractor {
	public static void main(String[] args) throws Exception {
		String jsonArray = getJsonArray("pubgteams", 2);
		BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/pubgteams.json"));
		writer.write(jsonArray);
		writer.close();
	}
	public static String getJsonArray(String requestedObject, int pageMax) throws IOException {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		String res = "{ \""+requestedObject+"\":";
		HttpGet request = new HttpGet("https://api.pandascore.co/pubg/teams?token=aTeHZcNGhS2SOR_Ff0Uh0Zw0ah3hp7d6ARrzE8GvXLYOmJK198o&page[size]=100&page[number]=1");
		CloseableHttpResponse response = httpClient.execute(request);


		HttpEntity entity = response.getEntity();
		res+= EntityUtils.toString(entity);
		res = res.substring(0,res.length()-1);
		//System.out.println(res);
		String tempString;
		for(int i = 1; i< pageMax ; i++){
			request = new HttpGet("https://api.pandascore.co/pubg/teams?token=aTeHZcNGhS2SOR_Ff0Uh0Zw0ah3hp7d6ARrzE8GvXLYOmJK198o&page[size]=100&page[number]="+i);
			response = httpClient.execute(request);
			entity = response.getEntity();
			tempString = EntityUtils.toString(entity);
			tempString = tempString.substring(1, tempString.length()-1);
			res = res + ","+ tempString;
		}
		request = new HttpGet("https://api.pandascore.co/pubg/teams?token=aTeHZcNGhS2SOR_Ff0Uh0Zw0ah3hp7d6ARrzE8GvXLYOmJK198o&page[size]=100&page[number]="+pageMax);
		response = httpClient.execute(request);
		entity = response.getEntity();
		tempString = EntityUtils.toString(entity);
		tempString = tempString.substring(1);
		res+= "," + tempString + "}";
		System.out.println(res);
		return res;
	}
}
