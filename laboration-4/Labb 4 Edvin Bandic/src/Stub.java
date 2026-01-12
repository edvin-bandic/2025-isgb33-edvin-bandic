import static com.mongodb.client.model.Filters.eq;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.List;

public class Stub {

    private static final Logger logger = LoggerFactory.getLogger(Stub.class);

    private static MongoCollection<Document> myCollection;

    public static void main(String[] args) throws Exception {
        initMongo();
        startServer();
    }

    private static void initMongo() throws Exception {
        Properties prop = new Properties();

        try (InputStream input = new FileInputStream("connection.properties")) {
            prop.load(input);
        }

        String connString = prop.getProperty("db.connection_string");
        String dbName = prop.getProperty("db.name");

        ConnectionString connectionString = new ConnectionString(connString);

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .build();

        MongoClient mongoClient = MongoClients.create(settings);

        MongoDatabase database = mongoClient.getDatabase(dbName);

        myCollection = database.getCollection("movies");

        logger.info("Connected to MongoDB, using database '{}'", dbName);
    }

    private static void startServer() {
        Javalin app = Javalin.create().start(4567);
        
        // alla GETs 
        app.get("/title/{title}", Stub::getTitleByTitle);
        app.get("/fullplot/{title}" , Stub::getFullplotByTitle);
        app.get("/cast/{title}" , Stub::getCastByTitle);
        app.get("/genre/{genre}" , Stub::getGenreByGenre);
        app.get("/actor/{actor}" , Stub::getActorByActor);
        app.get("/similar/{title}", Stub::getSimilarMoviesByTitle);
        app.get("/image/{title}", Stub::getImageByTitle);
        
        // alla POSTs
        app.post("/title", Stub::createMovie);
        
        // alla DELETEs
        app.delete("/title/{title}", Stub::deleteMovie);
        
        // alla PUTs
        app.put("/title/{title}", Stub::editMovie);
       
    }

    // GET Title
    private static void getTitleByTitle(Context ctx) {
    	// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen title
        String inputName = ctx.pathParam("title");
        String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));
        
        // tar vi fram titel hur min collection och lägger den i variablen doc
        Document doc = myCollection.find(eq("title", title)).first();

        // om variablen doc inte är null så ska doc ta bort id,poster,cast,fullplot och sen skaoa ett Json objekt som heter response 
        // där i gör vi doc till json. Sen skickar vi en status av 200 vilken typ (Json) och sen själva resultatet variablen response  och gör den till string
        // om inte så skicakr vi en status av 404 och ett resultat av en error text där vi säger att något gick snett
        if (doc != null) {
            doc.remove("_id");
            doc.remove("poster");
            doc.remove("cast");
            doc.remove("fullplot");
            JsonObject response = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(response.toString());
        } else {
            ctx.status(404);
            ctx.contentType("application/json");
            ctx.result(jsonError("Title not found.").toString());
        }
    }
    
    // GET Fullplot
    private static void getFullplotByTitle(Context ctx) {
    	// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen title
        String inputName = ctx.pathParam("title");
        String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));
        
     // här så gör vi en variable doc och hämtar värde från title i min collection och vi säger till att endast visa title och fullplot och
        // att inte visa id eller något annat sen att hämta den första matchande dokument i min collection
        Document doc = myCollection.find(eq("title", title)).projection(new Document("title", 1).append("fullplot", 1).append("_id", 0)).first();

     // om variablen doc inte är null så ska den skaoa ett Json objekt som heter response 
        // där i gör vi doc till json. Sen skickar vi en status av 200 vilken typ (Json) och sen själva resultatet variablen response och gör den till string
        // om inte så skicakr vi en status av 404 och ett resultat av en error text där vi säger att något gick snett
        if (doc != null) {
            JsonObject response = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(response.toString());
        } else {
            ctx.status(404);
            ctx.contentType("application/json");
            ctx.result(jsonError("Title not found.").toString());
        }
    }
    
    // GET Cast
    private static void getCastByTitle(Context ctx) {
    	// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen titeln
    	String inputName = ctx.pathParam("title");
        String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));

     // här så gör vi en variable doc och hämtar värde från title i min collection och vi säger till att endast visa title och cast och
        // att inte visa id eller något annat sen att hämta den första matchande dokument i min collection
        Document doc = myCollection.find(eq("title", title)).projection(new Document("title", 1).append("cast", 1).append("_id", 0)).first();

     // om variablen doc inte är null så ska den skaoa ett Json objekt som heter response 
        // där i gör vi doc till json. Sen skickar vi en status av 200 vilken typ (Json) och sen själva resultatet variablen response och gör den till string
        // om inte så skicakr vi en status av 404 och ett resultat av en error text där vi säger att något gick snett
        if (doc != null) {
            JsonObject response = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(response.toString());
        } else {
            ctx.status(404);
            ctx.contentType("application/json");
            ctx.result(jsonError("Title not found.").toString());
        }
    }
    
    // GET Genre med Limit
    private static void getGenreByGenre(Context ctx) {
    	// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen genre
    	String inputName = ctx.pathParam("genre");
        String genre = capitalizeFully(inputName.toLowerCase(Locale.ROOT));
        
        // här gör jag en variabel som heter limit och vad den gör är att den hämtar värdet från en parametern limit om 
        //  inte parametern finns så ger man en defualt värde av 10
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);

        // här skapar vi en variabel results som vi sen itarerar med en for loop men de denna varaiblen ska hämta ut är alla filmer med samma 
     	// genre som den genren vi la i parametern tidigare och denna variebel har också en limit som vi gick igenom innan.
		FindIterable<Document> results = myCollection.find(eq("genres", genre))
				.limit(limit);
		
		//här skapar vi en Json array döper den till array
		JsonArray array = new JsonArray();
		
		// här så gör vi for loopen och loopar igenom alla filmer som har samma Genres och tar bort id,poster,cast,fullplot sen lägger vi dem
		// i array mängden loops beror på limit
		for (Document doc : results) {
			 doc.remove("_id");
             doc.remove("poster");
             doc.remove("cast");
             doc.remove("fullplot");
             
			array.add(JsonParser.parseString(doc.toJson()).getAsJsonObject());
		}
		
		// här kollar vi om arrayn är tom så ska den skicka en status av 404 med typen json och skicka en lämplig error text som i detta falet är
		// "Genre not found"
		if(array.isEmpty()){
			ctx.status(404);
	        ctx.contentType("application/json");
	        ctx.result(jsonError("Genre not found.").toString());
	        return;
		}
		
		// sen så skickar den statusen 200 med typen json
		// och sen skickar den värdet i responsen och den är gjord till en string
		ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(array.toString());

        }
    
    // GET Actor med Limit
    private static void getActorByActor(Context ctx) {
    	// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen actor
        String inputName = ctx.pathParam("actor");
        String actor = capitalizeFully(inputName.toLowerCase(Locale.ROOT));
        
        // här gör jag en variabel som heter limit och vad den gör är att den hämtar värdet från en parametern limit om 
        //  inte parametern finns så ger man en defualt värde av 10
        int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
        
        // här skapar vi en variabel results som vi sen itarerar med en for loop men de denna varaiblen ska hämta ut är alla filmer som den
     	// valda den valda actor i parametern har varit med i och denna variebel har också en limit som vi gick igenom innan.
		FindIterable<Document> results = myCollection.find(eq("cast", actor)).projection(new Document("title", 1).append("_id", 0))
				.limit(limit);
		
		//här skapar vi en Json array döper den till array
		JsonArray array = new JsonArray();
		
		// här så gör vi for loopen och loopar igenom alla filmer som Actor har varit med om sen lägger vi dem i array mängden loops beror på limit
		for (Document doc : results) {
			array.add(JsonParser.parseString(doc.toJson()).getAsJsonObject());
		}
		
		// här kollar vi om arrayn är tom så ska den skicka en status av 404 med typen json och skicka en lämplig error text som i detta falet är
		// "Actor not found"
		if(array.isEmpty()){
			ctx.status(404);
	        ctx.contentType("application/json");
	        ctx.result(jsonError("Actor not found.").toString());
	        return;
		}
		
		// sen så skickar den statusen 200 med typen json
		// och sen skickar den värdet i responsen och den är gjord till en string
		ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(array.toString());

        }
    
    // POST Title
	private static void createMovie(Context ctx) {
		// try and catch för att fånga fel som i detta falet är status 500
		try {
			// här så hämtar vi de som står i bodyn och gör den till ett nytt dokument med variablen newDoc
			String requestBody = ctx.body();
			Document newDoc = Document.parse(requestBody);

			// här så lägger vi till de ny dokument i min collection
			myCollection.insertOne(newDoc);

			// sen så skickar den statusen 202 med typen json
			// och utan något resultat
			ctx.status(202).contentType("application/json").result();
			
			// i catchen så catchar den om de blir något fel med insättning av de nya dokument med en 500 status med json typen och ett error
			// text som i detta falet står de "Failed to insert movie"
		} catch (Exception e) {
			logger.error("Insert failed: {}", e.getMessage());
			ctx.status(500).contentType("application/json")
					.result(jsonError("Failed to insert movie.").toString());
		}
	}
	
	// PUT Title
	private static void editMovie(Context ctx) {
		// try and catch för att fånga fel som i detta falet är status 409
		try {
			// här så hämtar vi de som står i bodyn och gör den till ett nytt dokument med variablen newDoc
			String requestBody = ctx.body();
			Document newDoc = Document.parse(requestBody);
			
			// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen title
			String inputName = ctx.pathParam("title");
	        String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));
	        
	        // denna kod delen gör så att ingen kan ändra titlen på de dokument du ska byta så titeln kommer alltid vara samma som den i parametern
	        newDoc.put("title", title);

	     // här så byter vi de dokument i min collection som har samma title som den i vår parameter med newDoc
	        UpdateResult result = myCollection.replaceOne(eq("title", title), newDoc);
	        
	        // här kollar vi om inte en film (MatchedCount == 0 betyder att en film med den titeln inte finns i collectionen)
	        //finns så skickar vi en status av 409 med typen json med en error text som är "Movie not found"
	        if (result.getMatchedCount() == 0) {
	            ctx.status(409)
	               .contentType("application/json")
	               .result(jsonError("Movie not found.").toString());
	            return;
	        }

	        // sen så skickar den statusen 202 med typen json
	     	// och utan något resultat
			ctx.status(202).contentType("application/json").result();

			// i catchen så catchar den om de blir något fel med redigering av de matchande dokument med en 409 status med json typen och ett error
			// text som i detta falet står de "Failed to edit movie"
		} catch (Exception e) {
			logger.error("Edit failed: {}", e.getMessage());
			ctx.status(409).contentType("application/json")
					.result(jsonError("Failed to edit movie.").toString());
		}
	}
	
	//DELETE Title
	private static void deleteMovie(Context ctx) {
		// try and catch för att fånga fel som i detta falet är status 409
		try {
			// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen title
			String inputName = ctx.pathParam("title");
	        String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));

	     // här så raderar vi de dokument i min collection som har samma title som den i vår parameter 
			DeleteResult result = myCollection.deleteOne(eq("title", title));
			
			// här kollar vi om inte en film (DeletedCount == 0 betyder att en film med den titeln inte finns i collectionen)
	        //finns så skickar vi en status av 409 med typen json med en error text som är "Movie not found"
			 if (result.getDeletedCount() == 0) {
		            ctx.status(409)
		               .contentType("application/json")
		               .result(jsonError("Movie not found.").toString());
		            return;
		        }
			 
			// sen så skickar den statusen 204 med typen json
			// och utan något resultat
			ctx.status(204).contentType("application/json").result();

			// i catchen så catchar den om de blir något fel med raderingen av de matchande dokument med en 409 status med json typen och ett error
			// text som i detta falet står de "Failed to delete movie"
		} catch (Exception e) {
			logger.error("Delete failed: {}", e.getMessage());
			ctx.status(409).contentType("application/json")
					.result(jsonError("Failed to delete movie.").toString());
		}
	}
    
    
	// GET Similar med Limit
	private static void getSimilarMoviesByTitle(Context ctx) {
		// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen title
		String inputName = ctx.pathParam("title");
		String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));
		
        // här gör jag en variabel som heter limit och vad den gör är att den hämtar värdet från en parametern limit om 
        //  inte parametern finns så ger man en defualt värde av 10
		int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
		
		// här så gör vi en variable som heter movie och i den så hämtar den dokumenten som har samma title som parametern som vi skrev in
		Document movie = myCollection.find(eq("title", title)).first();
		
		// här kollar vi som movie är null om den de så skickar den en status av 404 med typen json och den 
		//skickar en lämplig error text som i detta falet är "Title not found" vi kollar detta för att se om filmen finns i min collection
		if (movie == null) {
	        ctx.status(404).json(jsonError("Title not found").toString());
	        return;
	    }
		
		// här skapar vi en lista med alla genres som den filmen vi valde i parametern har 
		List<String> genres = movie.getList("genres", String.class);
		
		// här kollar vi som genres är null eller om den är tom om den är något av de så skickar den en status av 404 med typen json och den 
		//skickar en lämplig error text som i detta falet är "Genre not found" vi kollar detta för att se om filmen i parametern har en genre
		// från första början 
		if (genres == null || genres.isEmpty()) {
			ctx.status(404);
            ctx.contentType("application/json");
            ctx.result(jsonError("Genre not found.").toString());
	        return;
	    }
		
		// säger till att ta den första genre om en film har mer än en genre
		String genre = genres.get(0);
		
		// här skapar vi en variabel results som vi sen itarerar med en for loop men de denna varaiblen ska hämta ut är alla filmer med samma 
		// genre som den filmen vi la i parametern tidigare och denna variebel har också en limit som vi gick igenom innan.
		FindIterable<Document> results = myCollection.find(eq("genres", genre))
				.limit(limit);
		
		//här skapar vi en Json array döper den till array
		JsonArray array = new JsonArray();
		
		
		// här så gör vi en for loop för att loopa igenom alla dokument i results och i loopen så tar vi bort id poster cast och fullplot sen
		// så lägger vi till värdet vi får från dokumnetet i arrayn vi skapa tidigare.
		for (Document doc : results) {
			
			// här kollar vi om de är samma title i docen som de är i parametern då fortsätter vi och hoppar for lopen 
			// vi gör detta för vi vill inte skriva ut samma film som vi skrev in i parametern 
			 if (title.equals(doc.getString("title"))) {
			        continue; 
			    }
			
			doc.remove("_id");
			doc.remove("poster");
	        doc.remove("cast");
	        doc.remove("fullplot");
			array.add(JsonParser.parseString(doc.toJson()).getAsJsonObject());
		}
		
		// här kollar vi om arrayn är tom så ska den skicka en status av 404 med typen json och skicka en lämplig error text som i detta falet är
		// "Similar Movies not found"
		if(array.isEmpty()){
			ctx.status(404);
	        ctx.contentType("application/json");
	        ctx.result(jsonError("Similar Movies not found.").toString());
	        return;
		}
		
		// sen så skickar den statusen 200 med typen json
		// och sen skickar den värdet i responsen och den är gjord till en string
		ctx.status(200).contentType("application/json").result(array.toString());
	}
	
	
	// GET Image
    private static void getImageByTitle(Context ctx) {
    	// hämtar parametern från pathen och lägger den i variablen inputName sen kör den igenom functionen capitalizeFully i variablen title
        String inputName = ctx.pathParam("title");
        String title = capitalizeFully(inputName.toLowerCase(Locale.ROOT));

        // här så gör vi en variable doc och hämtar värde från title i min collection och vi säger till att endast visa title och poster och
        // att inte visa id eller något annat sen att hämta den första matchande dokument i min collection
        Document doc = myCollection.find(eq("title", title)).projection(new Document("title", 1).append("poster", 1).append("_id", 0)).first();
        
        // Om doc är lika med null så kommer den skicka en status på 404 med typen json och skicka med en lämplig error text som i detta 
        // falet är "Title not Found"
        if (doc == null) {
            ctx.status(404);
            ctx.contentType("application/json");
            ctx.result(jsonError("Title not found.").toString());
            return;
        } 
		
        // här skapar jag en string som heter image och i den lägger jag stringen från poster som vi hittar i doc
        String image = doc.getString("poster");
        
        // här kollar jag om image stringen är null eller tom så skickar den en status på 404 med typen json och skickar en lämplig error text
        // som i detta falet är "Movie Image/Poster not found" denna ifen finns här för att se om de matchande dokumnetet har en poster/har en 
        // bild länk
		if (image == null || image.isBlank()) {
			ctx.status(404);
            ctx.contentType("application/json");
            ctx.result(jsonError("Movie Image/Poster not found.").toString());
	        return;
	    }

		//om allt går som de ska kommer den hit och gör ett Json object som heter response sen så skickar den statusen 200 med typen json
		// och sen skickar den värdet i responsen och den är gjord till en string
        	JsonObject response = JsonParser.parseString(doc.toJson()).getAsJsonObject();
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(response.toString());
        
    }
    
    

    // lånad function från föreläsning 11
    private static String capitalizeFully(String input) {
        String[] words = input.trim().split("\\s+");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                char first = Character.toUpperCase(word.charAt(0));
                String rest = word.substring(1).toLowerCase();

                result.append(first);
                result.append(rest);
                result.append(" ");
            }
        }

        return result.toString().trim();
    }

 

    private static JsonObject jsonError(String error) {
        JsonObject obj = new JsonObject();
        obj.addProperty("error", error);
        return obj;
    }
}