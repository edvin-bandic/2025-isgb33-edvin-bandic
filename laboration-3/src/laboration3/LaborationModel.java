package laboration3;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.List;

import org.bson.Document;
import com.google.gson.JsonObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;


public class LaborationModel {
	// variabler
	private MongoCollection<Document> myCollection;
	
	// här så ininterar vi mongo connection så att vi kan hämta datan fårn mongo databasen
	 void initMongo() throws Exception {
	    Properties prop = new Properties();

        try (InputStream input = new FileInputStream("connection.properties")) {
            prop.load(input);
        }

        String connString = prop.getProperty("db.connection_string");
        String dbName = prop.getProperty("db.name");
        
		ConnectionString connectionString = new ConnectionString(connString);
		MongoClientSettings settings =
		MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase(prop.getProperty("db.name"));
		myCollection = database.getCollection("movies");
		
	}
	
	
	 // här är min fucntion för att hämta den datan vi vill hämta från mongo(aggrate) och då jag omvnadlar den till en string som jag sen retunerar till controller
	public String hamtaDokument(String sokInput){
		
		//variabler
		StringBuilder sb = null;
		String resultat = null;
		
		// här har vi en try o cathc för att catcha när de inte finns något i textfielden och om de ine står någon genre som finns i mongo databassen
	try {
    		
		// här ser vi om sokInput är tom om dne är skicak en exception
    		if(sokInput.isEmpty()) {
    			throw new Exception("Ingen film matchade kategorin");
    		}
    		
    	// här så anvädner vi aggregate för att bestäam vad vi vill ta ut och hur vi vill ta ut datan från mongo databsen
	AggregateIterable<Document> myDocs = myCollection.aggregate(Arrays.asList(
			Aggregates.match(Filters.in("genres", sokInput)),
			Aggregates.project(
					Projections.fields(
						Projections.excludeId(),
						Projections.include("title", "year")
					)
					),
			Aggregates.sort(Sorts.descending("title")),
			Aggregates.limit(10)
			
			));
	
		// här så gör vi en for lop där vi loopar igenom varjemdokument som vi hittar i myDocs sen så gör vi dem till en string med strinbuilder(så den blir muterbar) och sen så lägger vi dem i varaiblen resultat och
		// den forlopar tills 10 dokument har hittats då finns de 10 dokument från mongo i resultat
		for(Document d : myDocs) {
				
			 sb = new StringBuilder();
                sb.append(d.getString("title"))
                  .append(" (")
                  .append(d.getInteger("year"))
                  .append(")\n");
                
             resultat += sb;
		}
		
		// här så kollar vi att den första i mydoc om den är en value som inte finns i mongodb valuen(genres) vi vill ha så throwar den en catch och säger att ingen film matchade kategorin
		if(myDocs.first() == null) {
			throw new Exception("Ingen film matchade kategorin");
		}
			
		// cathcen som returnar felet till cotnroller som skickar felet till textarean
			}catch(Exception exception){
				return exception.getMessage();
			}
		
		
		// här retunrerar vi resultat till controller som skickar resultat till textarean 
		 return resultat;
         
	}
}
