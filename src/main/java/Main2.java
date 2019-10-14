import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.URIref;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main2 {
	public static void main(String[] args) throws IOException, ParseException {

		JSONArray playersJSON = getArrayFromName("players");
		Model model = ModelFactory.createOntologyModel();
		String namespace = "http://pandarql/";
		model.setNsPrefix("pandarql", namespace);
		Property nameProp  = model.createProperty(namespace, "name");
		Property idProp = model.createProperty(namespace, "id");
		//Property hometownProp = model.createProperty(namespace, "hometown");
		Property typeProp = model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
		Resource resourcePlayer;
		Resource playerClass = model.createResource(namespace+"player");
		Resource owlClass = model.createResource("http://www.w3.org/2002/07/owl#Class");
		playerClass.addProperty(typeProp, owlClass);
		JSONObject playerJSON;
		for(int i = 0 ; i<playersJSON.size() ; i++){
			playerJSON = (JSONObject) playersJSON.get(i);
			//System.out.println(URIref.encode(playerJSON.get("name").toString()));
			resourcePlayer = model.createResource(namespace+URIref.encode((playerJSON.get("name").toString().replace(" ",""))));
			resourcePlayer.addProperty(nameProp, URIref.encode((playerJSON.get("name").toString().replace(" ",""))));
			//resourcePlayer.addProperty(hometownProp, playerJSON.get("hometown").toString());
			resourcePlayer.addProperty(idProp, playerJSON.get("id").toString());
			resourcePlayer.addProperty(typeProp, playerClass);
		}
		JSONArray teamsJSON = getArrayFromName("teams");
		Bag playersResource;
		JSONObject teamJSON;
		Resource resourceTeam;
		Property playersProp = model.createProperty(namespace, "players");
		Resource teamClass = model.createResource(namespace+"team");
		teamClass.addProperty(typeProp, owlClass);
		for(int i = 0 ; i<teamsJSON.size() ; i++){
			teamJSON = (JSONObject) teamsJSON.get(i);
			playersJSON = (JSONArray) teamJSON.get("players");
			playersResource = model.createBag(namespace+teamJSON.get("name").toString().replace(" ", "")+"#players");
			resourceTeam = model.createResource(namespace+teamJSON.get("name").toString().replace(" ", ""));
			for(int j = 0 ; j < playersJSON.size() ; j++){
				playerJSON = (JSONObject) playersJSON.get(j);
				playersResource.add(model.getResource(namespace+ playerJSON.get("name").toString().replace(" ","")));
			}
			resourceTeam.addProperty(nameProp, teamJSON.get("name").toString().replace(" ", ""));
			//playersResource.addProperty(nameProp, teamJSON.get("name").toString().replace(" ","")+"#players");
			resourceTeam.addProperty(playersProp, playersResource);
			resourceTeam.addProperty(typeProp, teamClass);
		}
		model.write(System.out);
		String fileName = "modele.nt";
		FileWriter out = new FileWriter( fileName);
		try {
			model.write(out, "N-TRIPLES" );
		}
		finally {
			try {
				out.close();
			}
			catch (IOException closeException) {
				// ignore
			}
		}

	}
	public static JSONArray getArrayFromName(String name) throws IOException, ParseException {
		JSONParser parser = new JSONParser();
		FileReader fileReader = new FileReader("./src/main/resources/"+name+".json");
		JSONObject jsonObject = (JSONObject) parser.parse(fileReader);
		JSONArray res = (JSONArray) jsonObject.get(name);
		return res;
	}
}
