package laboration3;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class LaborationView extends JFrame {
	// variabler
	private JTextArea area;
	private JButton b;
	private JTextField t;
	
	//functionen som gör hela gui och dens alla olika delar som button textfield och textarea
	public LaborationView() {
		
		super("Mflix");
		setSize(400,500);
    	setLayout(null);
    	
    	area = new JTextArea();
    	area.setBounds(10,10,365,400);
    	area.setLineWrap(true); 
		
    	t = new JTextField("");
    	t.setBounds(10,415,260,40);
    	
    	b = new JButton("SÖK");
    	b.setBounds(275, 415, 100, 40);
    	
    	
    	add(area);
    	add(t);
    	add(b);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	setVisible(true);
	}
	
	// settar texten i textarean är vi säger till vilken text som kommer vara i "area" i textarean
	public void setTextArea(String text) {
		area.setText(text);
	}
	
	// är så gör vi en get för textfielden där vi hämtar valuen i "t" / textfielden 
	public String getTextField() {
		   return t.getText().trim();
	}
	
	// här är gör vi en actionlistener som gör "b" / buttton till klick bar
	public void addSokButtonListener(ActionListener e) {
	    b.addActionListener(e);
	}
	
	
	
}
