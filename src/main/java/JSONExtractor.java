import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 */
public class JSONExtractor {
	public static final String API_KEY="aTeHZcNGhS2SOR_Ff0Uh0Zw0ah3hp7d6ARrzE8GvXLYOmJK198o";

	public static void main(String[] args) throws Exception {
		String[] games = {"csgo", "dota2", "lol", "ow", "pubg"};
		String[] endpoint = {"series", "matches", "leagues", "players", "teams", "tournaments"};
		String name;
		String directory;
		String jsonArray;
		BufferedWriter writer;
		for(int i = 0 ; i< games.length ; i++){
			for(int j = 0 ; j < endpoint.length ; j++){
				name = games[i] + "_" + endpoint[j];
				directory = endpoint[j];
				jsonArray = getJsonArray(name, "https://api.pandascore.co/"+games[i]+"/"+endpoint[j]);
				writer = new BufferedWriter(new FileWriter("./src/main/resources/"+directory+"/"+name+".json"));
				writer.write(jsonArray);
				writer.close();
			}
		}
		/*// Initialization
		String name = "csgo_series";
		String directory = "series";

		// Retrieve json data from the API
		String jsonArray = getJsonArray(name, "https://api.pandascore.co/csgo/series");

		// Saving the data to a file
		BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/"+directory+"/"+name+".json"));
		writer.write(jsonArray);
		writer.close();*/
	}


	/**
	 * @param name Name of the entity
	 * @param url API url
	 * @return Entity as a JSON string
	 * @throws IOException
	 */
	public static String getJsonArray(String name, String url) throws IOException {

		// Initialization
		int pageSize = 100;
		String res = "{ \""+name+"\":";

		// Http client initialization
		CloseableHttpClient httpClient = HttpClients.createDefault();

		try {
			// Building the first query
			HttpGet request = new HttpGet(url + "?token=" + API_KEY + "&page[size]=" + pageSize + "&page[number]=1");

			// Executing the first query
			CloseableHttpResponse response = httpClient.execute(request);

			// Retrieving the response
			HttpEntity entity = response.getEntity();
			res += EntityUtils.toString(entity);
			res = res.substring(0, res.length() - 1); // Removing unnecessary characters

			// Retrieving the total number of items from the first response's header
			int totalItems = Integer.valueOf(response.getFirstHeader("X-Total").getValue());

			String tempString;
			System.out.println(totalItems);
			// Calculating the total number of pages
			int totalPages = totalItems / pageSize + 1;
			System.out.println(totalPages);
			// Looping over the rest of the pages
			for (int i = 2; i <= totalPages; i++) {
				request = new HttpGet(url + "?token=" + API_KEY + "&page[size]=" + pageSize + "&page[number]=" + i);
				response = httpClient.execute(request);
				entity = response.getEntity();
				tempString = EntityUtils.toString(entity);
				tempString = tempString.substring(1, tempString.length() - 1); // Removing unnecessary characters
				res += "," + tempString;
			}

			// Closing the json file
			res += "]}";
		}finally {
			httpClient.close();
		}

		return res;
	}
}
