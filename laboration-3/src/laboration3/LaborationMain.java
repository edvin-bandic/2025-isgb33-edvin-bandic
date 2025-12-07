package laboration3;

import java.awt.event.*;

public class LaborationMain {
	
	
	// här så exekverar vi vårat projekt och gör så att allt körs vi hämtar alla andra klasser och kör de functioner vi behöver
	 public static void main(String[] args) throws Exception {
		 
		 	LaborationModel model = new LaborationModel();
		 	model.initMongo();
	        LaborationView view = new LaborationView();
	        
	        LaborationController controller = new LaborationController(view, model);
	        controller.lasUppSearchBar();
	        
	        
	 }
}
