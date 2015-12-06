import javax.swing.*;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.sql.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Test extends JPanel {
	static int lcount;
	static int lccount;
	static int pcount;
	static double regions[] = new double[128];
	static double lions[] = new double[28];
	static double ponds[] = new double[48];
	static double ptemp[] = new double[2];
	double ltemp[]= new double[2];
	double rlions[];
	static int X, Y;
	boolean color;
	boolean status;
	static ResultSet rs, ls, ps, rc, lc, pc,lcc,pcc;
	
	Connection con;
	Statement stmt;
	String r, p, li;
	static JCheckBox check;
	static JFrame frame;
	public Test() {
		Dimension Screensize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((Screensize.width / 2) - 250, (Screensize.height / 2) - 250);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}

		catch (Exception e) {
			System.out.println("Error in setting WLAF " + e);
		}

		// Mouse Listener

	}

	public void connect() {

		try {
			con = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "sureshba", "Appu@123");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// For Regions;

		try {
			rs = stmt.executeQuery("select rshape from region");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double temp[] = new double[64];
		int j = 0;

		// convert STRUCT into geometry
		try {
			while (rs.next()) {
				// String x = rs.getString("LION_ID");
				STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
				JGeometry j_geom = JGeometry.load(st);
				temp = j_geom.getOrdinatesArray();
				for (int i = 0; i < temp.length; i++) {
					regions[j++] = temp[i];
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// For Lions

		try {
			ls = stmt.executeQuery("select lshape from lion");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double temp2[] = new double[2];
		int l = 0;
		try {
			while (ls.next()) {
				STRUCT lt = (oracle.sql.STRUCT) ls.getObject(1);
				JGeometry j_geom = JGeometry.load(lt);
				temp2 = j_geom.getPoint();
				for (int k = 0; k < temp2.length; k++) {
					lions[l++] = temp2[k];
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// For Pond

		try {
			ps = stmt.executeQuery("select pshape from pond");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double temp3[] = new double[6];
		int m = 0;
		try {
			while (ps.next()) {
				STRUCT pt = (oracle.sql.STRUCT) ps.getObject(1);
				JGeometry j_geom = JGeometry.load(pt);
				temp3 = j_geom.getOrdinatesArray();
				for (int k = 0; k < temp3.length; k = k + 1) {
					ponds[m++] = temp3[k];
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void draw(int posx, int posy) throws SQLException {
		// if starts
		//this.setVisible(false);

		check = new JCheckBox("show lions and ponds in the selected region");
		JPanel panel = new JPanel() {
			
					
			// Graphic start
			public void paint(Graphics g) {

				int xr[] = new int[4];
				int yr[] = new int[4];
				for (int i = 0; i < regions.length; i = i + 8) {
					int j = 0, k = 0;
					xr[j++] = (int) regions[i];
					xr[j++] = (int) regions[i + 2];
					xr[j++] = (int) regions[i + 4];
					xr[j++] = (int) regions[i + 6];
					yr[k++] = (int) regions[i + 1];
					yr[k++] = (int) regions[i + 3];
					yr[k++] = (int) regions[i + 5];
					yr[k++] = (int) regions[i + 7];
					// System.out.println("loop" + " i " + regions.length + " xr
					// : " +
					// xr[0] + " yr : " + yr[0]);
					g.setColor(Color.white);
					g.fillPolygon(xr, yr, 4);
					g.setColor(Color.black);
					g.drawPolygon(xr, yr, 4);
					//

				}
				// For Lions
				for (int j = 0; j < lions.length - 1; j = j + 2) {
					g.setColor(Color.green);
					g.fillOval((int) lions[j], (int) lions[j + 1], 4, 4);
					g.setColor(Color.black);
					g.drawOval((int) lions[j], (int) lions[j + 1], 4, 4);
				}

				// For Ponds
				for (int j = 0; j < ponds.length - 1; j = j + 6) {
					g.setColor(Color.blue);
					g.fillOval(((int) ponds[j] -15), ((int) ponds[j + 1] ), 30, 30);
					g.setColor(Color.black);
					g.drawOval(((int) ponds[j] -15), ((int) ponds[j + 1] ), 30, 30);

				}

				//
				// p=pc.getString(1);
				// System.out.println(p);
				// STRUCT pct = (oracle.sql.STRUCT) pc.getObject(1);
				// JGeometry j_geom = JGeometry.load(pct);
				// ptemp = j_geom.getOrdinatesArray();

				//Case when selected region has lion and pond
				
				if(check.isSelected()==true && rlions!=null && ptemp!=null && lcount>0 && pcount>0){
					
					g.setColor(Color.red);
					g.fillOval(((int) ptemp[0] -15), ((int) ptemp[1] ), 30, 30);
					g.setColor(Color.black);
					g.drawOval(((int) ptemp[0] -15), ((int) ptemp[1] ), 30, 30);
					
					
					//code for re draw of lion
					// For Lions
							for (int j = 0; j < rlions.length - 1; j = j + 2) {
							g.setColor(Color.red);
							g.fillOval((int) rlions[j], (int) rlions[j + 1], 4, 4);
							g.setColor(Color.black);
							g.drawOval((int) rlions[j], (int) rlions[j + 1], 4, 4);
						}
				}
				
				//Case when selected region has only lion
				else if(check.isSelected()==true && rlions!=null && lcount>0){
					
					for (int j = 0; j < rlions.length - 1; j = j + 2) {
					g.setColor(Color.red);
					g.fillOval((int) rlions[j], (int) rlions[j + 1], 4, 4);
					g.setColor(Color.black);
					g.drawOval((int) rlions[j], (int) rlions[j + 1], 4, 4);
				}
					
					
				}
				//case when selected region is empty
				else if(pcount==0 && lcount==0){
					
					
					
				}
				
				
				
				

			}

			// Graphic ends

		};

		frame = new JFrame();
		// frame.add(new Test());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		// JPanel panel = new JPanel(new GridLayout(1, 1));
		panel.setLayout(new GridLayout(1, 1));
		// panel.add(check);
		frame.add(panel);
		frame.setSize(800, 527);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);

		Container contentPane = frame.getContentPane();
		contentPane.add(panel, BorderLayout.CENTER);
		contentPane.add(check, BorderLayout.EAST);

		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) {
				super.mouseClicked(me);
				X = me.getX();
				Y = me.getY();
				//System.out.println(+X + " " + Y);
				if(check.isSelected()==true && X < 650 && Y < 650) {
					status = true;
					frame.setState(Frame.ICONIFIED);//to minimize the screen
					frame.setState(Frame.NORMAL); //to maximize the screen
					// Obtain the region id from clicked position

					try {
						rc = stmt.executeQuery(
								"select region_id from region where SDO_WITHIN_DISTANCE(rshape,   SDO_GEOMETRY(2001,NULL,SDO_POINT_TYPE("
										+ X + "," + Y + ", NULL),NULL,NULL), 'distance = 1') = 'TRUE'");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						while (rc.next()) {
							r = rc.getString(1);
							//System.out.println(r);
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// Obtain the count of number of ponds from clicked position
					
					try {
						pcc=stmt.executeQuery("select count(pond_id) from pond,region where SDO_RELATE(pshape,rshape,'mask=inside')='TRUE' and region_id='"
											+ r + "'");
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						while(pcc.next()){
							pcount=pcc.getInt(1);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					//obtain the lion count in region
					
					try {
						lcc=stmt.executeQuery("select count(lion_id) from lion,region where SDO_RELATE(lshape,rshape,'mask=inside')='TRUE' and region_id='"
								+ r + "'");
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						while(lcc.next()){
							lcount=lcc.getInt(1);	
							//System.out.println(lcount);
							}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//If selected region has a pond and lion
					
					if(pcount>0 && lcount >0){
						try {
							pc = stmt.executeQuery(
									"select pshape from pond,region where SDO_RELATE(pshape,rshape,'mask=inside')='TRUE' and region_id='"
											+ r + "'");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							while (pc.next()) {
								p = pc.getString(1);
								//System.out.println(p);
								STRUCT pct = (oracle.sql.STRUCT) pc.getObject(1);
								JGeometry j_geom = JGeometry.load(pct);
								ptemp = j_geom.getOrdinatesArray();

							}

							//for (int i = 0; i < ptemp.length; i++) {
								//System.out.println(ptemp[i]);
							//}

						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
						
						
						lccount=lcount*2;
						rlions = new double[lccount];
						
						try {
							lc = stmt.executeQuery(
									"select lshape from lion,region where SDO_RELATE(lshape,rshape,'mask=inside')='TRUE' and region_id='"
											+ r + "'");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						
						//Assigining the lion ordinate array in selected region
						
						try {
							int l=0;
							while(lc.next()){
								STRUCT lct = (oracle.sql.STRUCT) lc.getObject(1);
								JGeometry j_geom = JGeometry.load(lct);
							ltemp = j_geom.getPoint();
								for (int k = 0; k < ltemp.length; k++) {
									rlions[l++] = ltemp[k];
							}
							
							}
							//for(int i=0;i<rlions.length;i++)
							//{
							//	System.out.println(rlions[i]);
							//}
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
						
						
						
					}
					
					// If selected region has only lions
								
					else if(lcount>0){
						ptemp=null; //to reset the pond 
						lccount=lcount*2;
						rlions = new double[lccount];
						
						try {
							lc = stmt.executeQuery(
									"select lshape from lion,region where SDO_RELATE(lshape,rshape,'mask=inside')='TRUE' and region_id='"
											+ r + "'");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						
						try {
							int l=0;
							while(lc.next()){
								STRUCT lct = (oracle.sql.STRUCT) lc.getObject(1);
								JGeometry j_geom = JGeometry.load(lct);
							ltemp = j_geom.getPoint();
								for (int k = 0; k < ltemp.length; k++) {
									rlions[l++] = ltemp[k];
							}
							
							}
							//for(int i=0;i<rlions.length;i++)
							//{
								//System.out.println(rlions[i]);
							//}
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
				}
				
					//case when the selected region is empty initialize the ordiante array to null
					
					else{
						rlions=null;
						ptemp=null;
						ltemp=null;
						
					}
					
				}


			}
		});

		// if ends

		// else{

		// }

		this.setVisible(true);

	}

	public static void main(String[] args) throws SQLException {

		// repaint on Click
		// rc give the region from click
		// pc gives pond inside region
		// lc gives lion inside region

		Test t1 = new Test();
		//t1.setVisible(false);
		t1.connect();
		t1.draw(0, 0);
		while(check.isSelected() && X>0 && Y>0)
		{
			
			t1.draw(X, Y);
			
		}

    check.addItemListener(new ItemListener()
    		{
    			@Override
    			public void itemStateChanged(ItemEvent e)
    			{
    				if(e.getStateChange()!=1)
    				{
    					frame.setState(Frame.ICONIFIED);//to minimize the screen
						frame.setState(Frame.NORMAL); //to maximize the screen
    				}
    			}
    		});
		


	}

}
