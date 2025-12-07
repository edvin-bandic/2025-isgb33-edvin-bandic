package laboration3;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.List;
import java.lang.*;

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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;



public class LaborationController {

	// variabler för att connecta viewen och model till controller
	private LaborationView view;
    private LaborationModel model;
	
    
    // här så gör vi att variablarna blir till LabrationCotnroller  så att man kan sen anävnda deni main
    public LaborationController(LaborationView view,LaborationModel model) {
    	this.view = view;
    	this.model = model;
    }
    
    
    // här så gör vi att när vi klikcar på sök så tar vi in inputen i textfielden ochs kicakr den till hamtadoument i model för att kolla vilka genren i databsen du fråga efter och sen skicakr du vidare
    // till controller ingen för att sen sätta text area till den datan vi fick från mongo
    public void lasUppSearchBar() {
    view.addSokButtonListener(e ->{
    	String sokInput = view.getTextField().trim();
    	System.out.println("Button clicked! Input: " + sokInput); // debuga
    	String docs = model.hamtaDokument(sokInput);
    	// gör rutan tom
    	view.setTextArea("");
    	//skikcar mongo datan till textarean som vi fick från model
    	view.setTextArea(docs);
    });
    }
	
	
}
