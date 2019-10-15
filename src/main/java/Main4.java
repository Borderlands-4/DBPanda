import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
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

public class Main4{
	public static String namespace = "http://pandarql/";
	public static void main(String[] args) throws IOException, ParseException {
		// Création du modèle et du préfixe
		OntModel model = ModelFactory.createOntologyModel();
		//String namespace = "http://pandarql/";
		model.setNsPrefix("pandarql", namespace);
		//Récupération de la propriété type, permettant de définir le type d'un sujet
		Property typeProp = model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "type");
		//Création des propriétés de base, nom, id, ...
		Property nameProp  = model.createProperty(namespace, "name");
		Property idProp = model.createProperty(namespace, "id");
		//Création de la classe joueur et des sous classes (pour les différents jeux)
		OntClass playerClass = model.createClass(namespace+"Player");
		OntClass csgoPlayerClass = model.createClass(namespace+"CSGO_Player");
		OntClass dotaPlayerClass = model.createClass(namespace+"Dota2_Player");
		OntClass lolPlayerClass = model.createClass(namespace+"LOL_Player");
		OntClass owPlayerClass = model.createClass(namespace+"Overwatch_Player");
		OntClass pubgPlayerClass = model.createClass(namespace+"PUBG_Player");
		playerClass.addSubClass(csgoPlayerClass);
		playerClass.addSubClass(dotaPlayerClass);
		playerClass.addSubClass(lolPlayerClass);
		playerClass.addSubClass(owPlayerClass);
		playerClass.addSubClass(pubgPlayerClass);
		/* On ajoute tous les joueurs de csgo au modèle
		*	Pour cela on récupère le tableau JSON qui contient nos joueurs
		*/
		JSONArray playersJSON = getArrayFromName("csgoplayers");
		addEveryPlayer(model, csgoPlayerClass, playerClass, playersJSON, nameProp, idProp, typeProp);

		playersJSON = getArrayFromName("dotaplayers");
		addEveryPlayer(model, dotaPlayerClass, playerClass, playersJSON, nameProp, idProp, typeProp);

		playersJSON = getArrayFromName("lolplayers");
		addEveryPlayer(model, lolPlayerClass, playerClass, playersJSON, nameProp, idProp, typeProp);

		playersJSON = getArrayFromName("owplayers");
		addEveryPlayer(model, owPlayerClass, playerClass, playersJSON, nameProp, idProp, typeProp);

		playersJSON = getArrayFromName("pubgplayers");
		addEveryPlayer(model, pubgPlayerClass, playerClass, playersJSON, nameProp, idProp, typeProp);

		//On créé les propriétés qui relie les joueurs aux équipes
		ObjectProperty playersProp = model.createObjectProperty(namespace+"#player");
		ObjectProperty teamProp = model.createObjectProperty(namespace+"#team");
		playersProp.addInverseOf(teamProp);
		//On créé les différentes classes
		OntClass teamClass = model.createClass(namespace+"team");
		OntClass dotaTeamClass = model.createClass(namespace+"Dota2_team");
		OntClass lolTeamClass = model.createClass(namespace+"LOL_team");
		OntClass csgoTeamClass = model.createClass(namespace+"CSGO_team");
		OntClass owTeamClass = model.createClass(namespace+"Overwatch_team");
		OntClass pubgTeamClass = model.createClass(namespace+"PUBG_team");
		teamClass.addSubClass(csgoTeamClass);
		teamClass.addSubClass(dotaTeamClass);
		teamClass.addSubClass(lolTeamClass);
		teamClass.addSubClass(owTeamClass);
		teamClass.addSubClass(pubgTeamClass);

		JSONArray teamsJSON = getArrayFromName("csgoteams");
		addEveryTeam(model, teamClass, csgoTeamClass, teamsJSON, nameProp, idProp, typeProp, playersProp);

		teamsJSON = getArrayFromName("dotateams");
		addEveryTeam(model, teamClass, dotaTeamClass, teamsJSON, nameProp, idProp, typeProp, playersProp);

		teamsJSON = getArrayFromName("lolteams");
		addEveryTeam(model, teamClass, lolTeamClass, teamsJSON, nameProp, idProp, typeProp, playersProp);

		teamsJSON = getArrayFromName("owteams");
		addEveryTeam(model, teamClass, owTeamClass, teamsJSON, nameProp, idProp, typeProp, playersProp);

		teamsJSON = getArrayFromName("pubgteams");
		addEveryTeam(model, teamClass, pubgTeamClass, teamsJSON, nameProp, idProp, typeProp, playersProp);
		//On affiche le modèle dans la console en format XML/RDF (par défaut)
		model.write(System.out);
		String fileName = "modele.nt";
		FileWriter out = new FileWriter( fileName);
		try {
			//On écrit sous la forme de ntriples, d'où le .nt
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
		//On parse le fichier json correspondant, qu'on met dans un fichier JSONObjet avant de récupérer le tableau d'objet qu'il contient
		//Nb : on ne peut pas directement parser un JSONArray, d'où la création d'un objet json intermédiaire, notamment lors l'extraction du JSON via l'API
		JSONParser parser = new JSONParser();
		FileReader fileReader = new FileReader("./src/main/resources/"+name+".json");
		JSONObject jsonObject = (JSONObject) parser.parse(fileReader);
		JSONArray res = (JSONArray) jsonObject.get(name);
		return res;
	}

	/**
	 * Méthode pour ajouter tous les joueurs présents dans la tableau JSON à notre modèle. Nb : on lui passe les propriétés et les classes pour pas se faire chier à les récuperer dans la méthode
	 * @param model le modèle auquel on ajoute les joueurs
	 * @param playerSubClass la classe représentant la sous classe de joueur auquel les joueurs appartiennent (cs go, lol, ...)
	 * @param playerClass la classe représentant un joueur dans le modèle
	 * @param playersJSON le tableau JSON contenant les joueurs
	 * @param nameProp la propriété modélisant un nom dans le modèle
	 * @param idProp la propriété modélisant un id dans le modèle
	 * @param typeProp la propriété représentant le type d'un objet dans le modèle
	 */
	public static void addEveryPlayer(OntModel model, OntClass playerSubClass, OntClass playerClass, JSONArray playersJSON, Property nameProp, Property idProp, Property typeProp){
		Resource resourcePlayer;
		JSONObject playerJSON;
		for(int i = 0 ; i<playersJSON.size() ; i++){
			playerJSON = (JSONObject) playersJSON.get(i);
			//System.out.println(URIref.encode(playerJSON.get("name").toString()));
			resourcePlayer = model.createResource(namespace+URIref.encode((playerJSON.get("name").toString().replace(" ","").replace("[","").replace("]",""))));
			resourcePlayer.addProperty(nameProp, URIref.encode((playerJSON.get("name").toString().replace(" ",""))));
			//resourcePlayer.addProperty(hometownProp, playerJSON.get("hometown").toString());

			resourcePlayer.addProperty(idProp, playerJSON.get("id").toString());
			resourcePlayer.addProperty(typeProp, playerSubClass);
			//Pourquoi doit-on aussi ajouter cette propriété ? cela devrait être automatique
			resourcePlayer.addProperty(typeProp, playerClass);
		}
	}

	/**
	 * Méthode similaire à addEveryPlayer, cf cette dernière pour plus d'information
	 * @param model
	 * @param teamClass
	 * @param teamSubClass
	 * @param teamsJSON
	 * @param nameProp
	 * @param idProp
	 * @param typeProp
	 * @param playersProp
	 */
	public static void addEveryTeam(OntModel model, OntClass teamClass, OntClass teamSubClass, JSONArray teamsJSON, Property nameProp, Property idProp, Property typeProp, Property playersProp){
		JSONObject teamJSON;
		JSONArray playersJSON;
		JSONObject playerJSON;
		Resource resourcePlayer;
		Resource resourceTeam;
		for(int i = 0 ; i<teamsJSON.size() ; i++){
			teamJSON = (JSONObject) teamsJSON.get(i);
			playersJSON = (JSONArray) teamJSON.get("players");
			resourceTeam = model.createResource(namespace+URIref.encode(teamJSON.get("name").toString().replace(" ", "").replace("[","").replace("]","")));
			for(int j = 0 ; j < playersJSON.size() ; j++){
				playerJSON = (JSONObject) playersJSON.get(j);
				resourcePlayer = model.getResource(namespace+URIref.encode((playerJSON.get("name").toString().replace(" ","").replace("[","").replace("]",""))));
				resourceTeam.addProperty(playersProp, resourcePlayer);
			}
			resourceTeam.addProperty(idProp, teamJSON.get("id").toString());
			resourceTeam.addProperty(nameProp, teamJSON.get("name").toString().replace(" ", ""));
			resourceTeam.addProperty(typeProp, teamClass);
			resourceTeam.addProperty(typeProp, teamSubClass);
		}
	}
}