import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException, ParseException {
		// parsing file "JSONExample.json"*
		/*File file = new File("./");
		File[] folder = file.listFiles();
		for(File f : folder){
			System.out.println(f.getName());
		}*/
		JSONParser parser = new JSONParser();
		FileReader fileReader = new FileReader("./src/main/resources/leagues.json");
		JSONObject jsonObject = (JSONObject) parser.parse(fileReader);
		JSONArray leaguesJSON = (JSONArray) jsonObject.get("leagues");


		Model model = ModelFactory.createOntologyModel();
		String namespace = "http://pandarql#";
		model.setNsPrefix("pandarql", namespace);
		//String rdfs =  "http://www.w3.org/2000/01/rdf-schema#";
		//model.setNsPrefix("rdfs", rdfs);
		Property nameProp  = model.createProperty(namespace, "name");
		Property idProp = model.createProperty(namespace, "id");
		Property typeProp = model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
		Resource resourceLeague;
		Resource leagueClass = model.createResource(namespace+"league");
		Resource owlClass = model.createResource("http://www.w3.org/2002/07/owl#Class");
		leagueClass.addProperty(typeProp, owlClass);
		//leagueClass.addProperty(typeProp, "http://www.w3.org/2000/01/rdf-schema#Class");
		//((OntModel) model).createClass("http://pandarql#league");
		//http://www.w3.org/2002/07/owl#Class
		JSONObject leagueJSON;
		//Resource root = model.createResource(namespace);{

		for(int i = 0 ; i<leaguesJSON.size() ; i++){
			leagueJSON = (JSONObject) leaguesJSON.get(i);
			resourceLeague = model.createResource(namespace+(leagueJSON.get("name").toString().replace(" ","")));
			resourceLeague.addProperty(nameProp, leagueJSON.get("name").toString());
			resourceLeague.addProperty(idProp, leagueJSON.get("id").toString());
			resourceLeague.addProperty(typeProp, leagueClass);
		}
		model.write(System.out, "RDF/XML");
		String fileName = "testJena.nt";
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
		/*Resource test = model.createResource(namespace+"/league1");
		Property name = model.createProperty(namespace, "nom");
		test.addProperty()
		model.add(root);

		model.add(model.createProperty("Nom"));
		model.createResource(baseURI).addProperty("Nom","test");
		System.out.println(((JSONObject)jsonArray.get(1)).get("id"));*/





	/*	// some definitions
		String personURI    = "http://somewhere/JohnSmith";
		String givenName    = "John";
		String familyName   = "Smith";
		String fullName     = givenName + " " + familyName;

	// create an empty Model
			Model model = ModelFactory.createDefaultModel();

	// create the resource
	//   and add the properties cascading style
			Resource johnSmith
					= model.createResource(personURI)
					.addProperty(VCARD.FN, fullName)
					.addProperty(VCARD.N,
							model.createResource()
									.addProperty(VCARD.Given, givenName)
									.addProperty(VCARD.Family, familyName));

			model.write(System.out);*/
	}
}
