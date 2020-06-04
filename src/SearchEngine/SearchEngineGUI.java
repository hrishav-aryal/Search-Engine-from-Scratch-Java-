package SearchEngine;

import java.awt.EventQueue;
import java.awt.List;

import javax.swing.JFrame;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JTextField;

import org.jsoup.Jsoup;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.ActionEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;

import javax.swing.JPanel;

public class SearchEngineGUI {

	private static JFrame frame;
	private JTextField textField;
	private static JScrollPane scroll;
	
	private static JLabel links; 
	private static JLabel[] doc_links;
	private static JLabel desc[]; 
	
	private static String search_text;
	private static int numOfLabels = 0;
	
	private static invertedIndex index = new invertedIndex();
	private static Set<String> results = new HashSet<String>();
	private JLabel numOfResults;
	
	private static JScrollPane scrollPane;
	private static JPanel panel;
	
	private static PorterStemmer pss = new PorterStemmer();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SearchEngineGUI window = new SearchEngineGUI();
					window.frame.setVisible(true);
					
					
					//String[] files = new String[]{"C:\\ir\\file1.txt", "C:\\ir\\file2.txt", "C:\\ir\\file3.txt"};
					String[] filess = new String[]{"C:/ir/file1.html", "C:/ir/file2.html", "C:/ir/file3.html", "C:/ir/rocknroll.html"};
					
					String[] files = new String[14];
					
					for(int i=0; i<14; i++) {
						files[i] = "C:/ir/file" + i + ".html";
					}
					
					index.indexHtmlFiles(filess);

					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SearchEngineGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 855, 537);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(54, 28, 312, 39);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		
		links = new JLabel("");
		//links.setBackground(new Color(64, 64, 64));
		//links.setBounds(64, 119, 565, 302);
		//links.setOpaque(true);
		//frame.getContentPane().add(links);
		
		numOfResults = new JLabel("");
		numOfResults.setBounds(64, 80, 327, 33);
		frame.getContentPane().add(numOfResults);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setForeground(Color.WHITE);
		btnSearch.setBackground(Color.DARK_GRAY);
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				search_text = textField.getText();
				search_text = search_text.trim();
				removeAllResults();
				
				results = index.searchQuery(search_text);
				numOfLabels = results.size();
				
				
				
				if(search_text == null) {
					numOfResults.setText("Showing 0 results for " + "\"" + search_text + "\"" );
				}else {
					numOfResults.setText("Showing " + numOfLabels + " results for " + "\"" + search_text + "\"" );
				}
				
				//links.setText(results.iterator().next());
				makeJlabels(results);
				
				//scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				//scroll.setBounds(800, 0, 23, 449);
				//if(numOfLabels != 0){
					//scroll.add(links);
				//}
				//scroll.add(links);
				//frame.getContentPane().add(scroll);
				
			}
		});
		btnSearch.setBounds(379, 28, 139, 39);
		frame.getContentPane().add(btnSearch);
		
		panel = new JPanel();
		panel.setBorder(null);
		panel.setBackground(Color.WHITE);
		//scrollPane.setViewportView(panel);
		
		scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(45, 141, 681, 280);
		scrollPane.setBorder(null);
		frame.getContentPane().add(scrollPane);
		
		
		//scrollPane.validate();
		//scrollPane.repaint();
		
		
		
			
	}
	
	public static void removeAllResults() {		
		panel.removeAll();
		panel.validate();
		panel.repaint();
	}
	
	public static void makeJlabels(Set<String> query_results) {
		int len = query_results.size();
		doc_links = new JLabel[len];
		desc = new JLabel[len];
		
		String descp = null;
		
		int xLow = 54;
		int yLow = 50;
		int xUp = 565;
		int yUp = 99;
		
		
		int i = 0;
		for(String res: query_results) {
			
			yLow = yLow + 30;
			xUp = 565;
			yUp = 99;
			
			
			doc_links[i] = new JLabel("");
			//doc_links[i].setBounds(xLow, yLow, xUp, yUp);
			doc_links[i].setForeground(Color.BLUE.darker());
			doc_links[i].setBackground(Color.LIGHT_GRAY);
			//doc_links[i].setOpaque(true);
			//frame.getContentPane().add(doc_links[i]);
			
			File html = new File(res);
			org.jsoup.nodes.Document doc = null;
			try {
				doc = Jsoup.parse(html, "UTF-8");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			
			desc[i] = new JLabel("");
			desc[i].setText("<html><br>");
			
			
			
			descp = doc.body().text();
			ArrayList<Integer> positions = getWordPositions(descp);
			
			if(descp.length()>50) {
				//descp = descp.substring(0, 60);
			}
			
			for(int k=0; k<positions.size(); k++) {
				if(descp.length()>80) {
					if(desc[i].getText().length()>=80) {
						desc[i].setText(desc[i].getText()+ "<br>");
					}
					desc[i].setText(desc[i].getText() + ".." + descp.substring(positions.get(k), positions.get(k) + 80) +" ... ");
				}else {
					desc[i].setText(desc[i].getText() + ".." + descp.substring(positions.get(k), positions.get(k) + 4) +" ... ");
				}
				
			}
			
			//desc[i].setText(desc[i].getText() + descp);
			//System.out.println("\n\n" +descp);
			
			desc[i].setText(desc[i].getText() + "<html>");
			
					
			doc_links[i].setText("File -- " + doc.title());
			//doc_links[i].setText("here");
			doc_links[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			
			panel.add(doc_links[i]);
			panel.add(desc[i]);
			panel.add(Box.createRigidArea(new Dimension(3,30)));
			
			int j = i;
			doc_links[i].addMouseListener(new MouseAdapter() {
				 @Override
				    public void mouseClicked(MouseEvent e) {
				        // the user clicks on the label
					 	//JOptionPane.showMessageDialog(null, doc_links[j].getText());
					 	try {
					 		File htmlFile = new File(res); 
							Desktop.getDesktop().browse(htmlFile.toURI());
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				    }
				 
				    @Override
				    public void mouseEntered(MouseEvent e) {
				        // the mouse has entered the label
				    }
				 
				    @Override
				    public void mouseExited(MouseEvent e) {
				        // the mouse has exited the label
				    }
			});

			i++;
			
		}
		
		
		/*
		for(int i=0; i<100; i++) {
			
			yLow = yLow + 30;
			xUp = 565;
			yUp = 99;
			
			
			doc_links[i] = new JLabel("");
			//doc_links[i].setBounds(xLow, yLow, xUp, yUp);
			doc_links[i].setForeground(Color.BLACK);
			doc_links[i].setBackground(Color.LIGHT_GRAY);
			//doc_links[i].setOpaque(true);
			//frame.getContentPane().add(doc_links[i]);
			doc_links[i].setText("here");
			//doc_links[i].setText("here");

			//links.add(doc_links[i]);
			//frame.getContentPane().add(links);
			//links.setText(links.getText() + "I am here");
			panel.add(doc_links[i]);
			panel.add(Box.createRigidArea(new Dimension(3,30)));
			
			//scrollPane.add(links);
			//scrollPane.validate();
			//scrollPane.repaint();
			
			
		}
		
		*/
		
		BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
		
		
		//JLabel check = new JLabel("hehehe");
		//check.setBounds(54, 95, 565, 99);
		//frame.getContentPane().add(check);
		
	}
	
	public static ArrayList<Integer> getWordPositions(String text){
		ArrayList<Integer> positions = new ArrayList<Integer>();
		
		String[] query_terms = search_text.split("\\s");
		String[] text_terms = text.split("\\s");
		
		HashMap<String, Integer> terms_position = new HashMap<String, Integer>();
		
		int pos = 0;
		for(String term: text_terms) {
			Integer p = terms_position.get(term);
			if(p == null) {
				term = term.toLowerCase();
				//term = pss.stem(term);
				terms_position.put(term, pos);
			}
			pos = pos + term.length() + 1;
		}
				
		for(String word: query_terms) {
			word = word.toLowerCase();
			//word = pss.stem(word);
			Integer position = terms_position.get(word);
			if(position != null) {
				positions.add(position);
			}
		}
		
		return positions;
	}
}
