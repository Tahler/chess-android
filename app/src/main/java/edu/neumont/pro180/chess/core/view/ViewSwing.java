//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Image;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.awt.event.WindowEvent;
//import java.awt.event.WindowListener;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import javax.imageio.ImageIO;
//import javax.swing.BoxLayout;
//import javax.swing.JButton;
//import javax.swing.JDialog;
//import javax.swing.JFrame;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.WindowConstants;
//
//
//public class ViewSwing extends JFrame implements View{
//	private static final long serialVersionUID = 1L;
//	private Model.MODE mode;
//	private boolean end;
//	private ChessBoard lastboard;
//	private List<ChessCmd> hist;
//	private ChessCmd newcmd;
//	private Map<Character, Image> imgs;
//	private Set<View.Listener> listeners;
//	private int mousex;
//	private int mousey;
//	private ChessBoardLoc mouse;
//	private ChessBoardLoc movef;
//	private ChessBoardLoc movet;
//	private Set<ChessBoardLoc> avails;
//
//
//	public ViewSwing(String[] args){
//		setTitle("Chess");
//		add(new JPanel(){
//			private static final long serialVersionUID = 1L;
//			public JPanel init(int w,int h){
//				Dimension d = new Dimension(w,h);
//				setMinimumSize(d);
//				setPreferredSize(d);
//				setSize(d);
//				setBackground(new Color(50,0,0));
//				setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
//				JButton hbtn = new JButton("Hint");
//				hbtn.addActionListener(new ActionListener(){
//					public void actionPerformed(ActionEvent arg0) {
//						Map<Integer,List<ChessCmd>> hintmap = lastboard.hint((""+lastboard.getlastmovecolor()).toLowerCase().equals("d")?'L':'D');
//						StringBuffer hints = new StringBuffer();
//						for(Entry<Integer,List<ChessCmd>> i : hintmap.entrySet()){
//							for(ChessCmd c : i.getValue()){
//								hints.append(c.getcmd());
//								hints.append('\n');
//							}
//						}
//						JOptionPane.showConfirmDialog(ViewSwing.this, hints, "Hint", JOptionPane.OK_OPTION);
//					}
//				});
//				this.add(hbtn);
//				addMouseMotionListener(new MouseMotionListener(){
//					public void mouseDragged(MouseEvent arg0) {
//
//					}
//					public void mouseMoved(MouseEvent arg0) {
//						mousex = arg0.getX();
//						mousey = arg0.getY();
//						mouse = getloc(mousex, mousey);
//						repaint();
//					}});
//				addMouseListener(new MouseListener(){
//					public void mouseClicked(MouseEvent arg0) {
//						if(mode==Model.MODE.PLAY){
//							if(movef==null){
//								movef = getloc(arg0.getX(), arg0.getY());
//								if(!(lastboard.getpos(movef)!=null && !lastboard.getpos(movef).isColor(lastboard.getlastmovecolor()))){
//									movef = null;
//								}
//								avails = lastboard.availmoves(movef);
//								repaint();
//							}
//							else{
//								movet = getloc(arg0.getX(), arg0.getY());
//								String extra = "";
//								if(lastboard.getpos(movef).isType('P')){
//									if(lastboard.getpos(movet)!=null){
//										extra = "*";
//									}
//									if((movet.getrowbyt()==0 || movet.getrowbyt()==7) && new ChessCmd(movef.toString()+movet+extra).validity(lastboard).isEmpty()){
//										String resp = (String) JOptionPane.showInputDialog(ViewSwing.this, "What would you like to promote your pawn to?", "Pawn Promotion", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Queen","Bishop","Knight","Rook"}, null);
//										if(resp!=null && resp.length()>0){
//											if(resp.equals("Queen")){
//												extra += "Q";
//											}
//											else if(resp.equals("Bishop")){
//												extra += "B";
//											}
//											else if(resp.equals("Knight")){
//												extra += "N";
//											}
//											else if(resp.equals("Rook")){
//												extra += "R";
//											}
//											extra += ""+lastboard.getpos(movef).getColor();
//										}
//										else{
//											extra = null;
//										}
//									}
//									else if(extra.isEmpty()){//en-passant
//										ChessCmd tmpcmd = new ChessCmd(movef.toString()+movet+"*");
//										if(tmpcmd.validity(lastboard).isEmpty()){
//											extra = "*";
//										}
//									}
//								}
//								else if(lastboard.getpos(movef).isType('K')){
//									if(movet.getcolbyt()-movef.getcolbyt()==2){
//										extra = new ChessBoardLoc((byte)7,movet.getrowbyt()).toString()+new ChessBoardLoc((byte)5,movet.getrowbyt());
//									}
//									else if(movet.getcolbyt()-movef.getcolbyt()==-2){
//										extra = new ChessBoardLoc((byte)0,movet.getrowbyt()).toString()+new ChessBoardLoc((byte)3,movet.getrowbyt());
//									}
//								}
//								if(extra != null){
//									newcmd = new ChessCmd(movef.toString()+movet+(!extra.isEmpty()?extra:(lastboard.getpos(movet)!=null?"*":"")));
//									movef = null;
//									avails.clear();
//								}
//							}
//						}
//					}
//					public void mouseEntered(MouseEvent arg0) {
//
//					}
//					public void mouseExited(MouseEvent arg0) {
//						mousex = -1;
//						mousey = -1;
//						mouse = getloc(mousex, mousey);
//						repaint();
//					}
//					public void mousePressed(MouseEvent arg0) {
//
//					}
//					public void mouseReleased(MouseEvent arg0) {
//
//					}
//				});
//				return this;
//			}
//			@Override
//			public void paint(Graphics g){
////				if(mode==Model.MODE.PLAY){
//					g.setColor(getBackground());
//					g.fillRect(0, 0, getWidth(), getHeight());
//					drawBoard(lastboard, g, 0,0, getWidth(), getHeight(), true);
////					if(possiblepromote){
//						//TODO: draw a box asking the user what piece to promote to
//
////					}
//					paintComponents(g);
////				}
////				else if(mode==Model.MODE.ASKEND){
//					//TODO: draw a question box asking the user whether or not to quit
//
////				}
////				else if(mode==Model.MODE.GOTO){
//					//TODO: draw the various states of the board which may be returned to
//
////				}
//			}
//			public void drawBoard(ChessBoard board, Graphics g, int x,int y, int width,int height, boolean headers){
//				if(board!=null){
//					int headsiz = headers?(Math.max(g.getFontMetrics().getHeight(), g.getFontMetrics().charWidth('M'))+2):0;
//					int tmpsiz = (width-headsiz*2)/8;
//					int sqsiz = (height-headsiz*2)/8;
//					if(sqsiz>tmpsiz){
//						sqsiz = tmpsiz;
//					}
//					int spacersizx = (width-8*sqsiz)/2;
//					int spacersizy = (height-8*sqsiz)/2;
//
//					if(headers){
//						g.setColor(new Color(175,125,25));
//						g.fillRect(spacersizx-headsiz+x, spacersizy-headsiz+y, 8*sqsiz+headsiz*2, 8*sqsiz+headsiz*2);
//					}
//					g.setColor(new Color(255,255,255));
//					/*
//					String[] lines = board.toString().split("\n");
//					g.setFont(new Font("Courier New", Font.PLAIN, 12));
//					for(int i=0; i<lines.length; i++){
//						g.drawString(lines[i], 20, (i+2)*g.getFontMetrics().getHeight()+20);
//					}
//					*/
//					/*
//					for(int i=0; i<9; i++){
//						g.drawLine(headsiz, i*sqsiz+headsiz, 8*sqsiz+headsiz, i*sqsiz+headsiz);
//						g.drawLine(i*sqsiz+headsiz, headsiz, i*sqsiz+headsiz, 8*sqsiz+headsiz);
//					}
//					*/
//					g.fillRect(spacersizx+x, spacersizy+y, 8*sqsiz, 8*sqsiz);
//					for(byte i=0; i<8; i++){
//						g.setColor(new Color(0,0,0));
//						if(headers){
//							g.drawString(""+ChessBoardLoc.colcharfrombyte(i), i*sqsiz+spacersizx+sqsiz/2+x, spacersizy-headsiz/4+y);
//							g.drawString(""+ChessBoardLoc.colcharfrombyte(i), i*sqsiz+spacersizx+sqsiz/2+x, height-spacersizy+headsiz*2/3+y);
//							g.drawString(""+(i+1), spacersizx-headsiz*2/3+x, (7-i)*sqsiz+spacersizy+sqsiz/2+y);
//							g.drawString(""+(i+1), width-spacersizx+headsiz/3+x, (7-i)*sqsiz+spacersizy+sqsiz/2+y);
//						}
//						for(byte j=0; j<8; j++){
//							ChessBoardLoc thisloc = new ChessBoardLoc(i,j);
//							if(headers && mouse!=null && mouse.getcolbyt()==i && mouse.getrowbyt()==j){
//								g.setColor(new Color(150,205,205));
//								g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
//							}
//							else if(headers && movef!=null && movef.getcolbyt()==i && movef.getrowbyt()==j){
//								if(i%2==j%2){
//									g.setColor(new Color(205,150,150));
//								}
//								else{
//									g.setColor(new Color(155,100,100));
//								}
//								g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
//							}
//							else if(headers && avails.contains(thisloc)){
//								if(i%2==j%2){
//									g.setColor(new Color(205,150,150));
//								}
//								else{
//									g.setColor(new Color(155,100,100));
//								}
//								g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
//							}
//							else if(i%2!=j%2){
//								g.setColor(new Color(0,0,0));
//								g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
//							}
//							ChessPiece piece = board.getpos(new ChessBoardLoc(i, j));
//							if(piece!=null){
//								Image img = imgs.get(piece.toString().charAt(0));
//								if(img==null){
//									if(piece.isColor('L')){
//										g.setColor(new Color(255,100,0));
//									}
//									else{
//										g.setColor(new Color(105,50,0));
//									}
//									g.drawString(piece.toString(), i*sqsiz+spacersizx+sqsiz/2+x, height-(j*sqsiz+spacersizy+sqsiz/2)+y);
//								}
//								else{
//									g.drawImage(img, i*sqsiz+spacersizx+x, height-((j+1)*sqsiz+spacersizy)+y, sqsiz,sqsiz, null);
//								}
//							}
//						}
//					}
//				}
//			}
//			public ChessBoardLoc getloc(int x, int y){
//				Graphics g = getGraphics();
//				int headsizx = g.getFontMetrics().charWidth('M')*2;
//				int headsizy = g.getFontMetrics().getHeight()*2;
//				int tmpsiz = (getWidth()-headsizx)/8;
//				int sqsiz = (getHeight()-headsizy)/8;
//				if(sqsiz>tmpsiz){
//					sqsiz = tmpsiz;
//				}
//				headsizx = (getWidth()-8*sqsiz)/2;
//				headsizy = (getHeight()-8*sqsiz)/2;
//
//				byte col = (byte)(x<headsizx?-1 : (x-headsizx)/sqsiz);
//				byte row = (byte)(getHeight()-y<headsizy?-1 : (getHeight()-(y+headsizy))/sqsiz);
//				return new ChessBoardLoc(col, row);
//			}
//
//		}.init(640,480));
//		pack();
//		setLocationRelativeTo(null);
//		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
////		setDefaultCloseOperation(EXIT_ON_CLOSE);
//		addWindowListener(new WindowListener(){
//			public void windowActivated(WindowEvent arg0) {
//			}
//			public void windowClosed(WindowEvent arg0) {
//			}
//			public void windowClosing(WindowEvent arg0) {
//				for(Listener l : listeners){
//					l.yesEnd();
//				}
//			}
//			public void windowDeactivated(WindowEvent arg0) {
//			}
//			public void windowDeiconified(WindowEvent arg0) {
//			}
//			public void windowIconified(WindowEvent arg0) {
//			}
//			public void windowOpened(WindowEvent arg0) {
//			}
//		});
//		setVisible(true);
//		listeners = new HashSet<View.Listener>();
//		imgs = new HashMap<Character, Image>();
//		try {
//			imgs.put('P', ImageIO.read(new File("res/PL.png")));
//			imgs.put('R', ImageIO.read(new File("res/RL.png")));
//			imgs.put('N', ImageIO.read(new File("res/NL.png")));
//			imgs.put('B', ImageIO.read(new File("res/BL.png")));
//			imgs.put('Q', ImageIO.read(new File("res/QL.png")));
//			imgs.put('K', ImageIO.read(new File("res/KL.png")));
//			imgs.put('p', ImageIO.read(new File("res/PD.png")));
//			imgs.put('r', ImageIO.read(new File("res/RD.png")));
//			imgs.put('n', ImageIO.read(new File("res/ND.png")));
//			imgs.put('b', ImageIO.read(new File("res/BD.png")));
//			imgs.put('q', ImageIO.read(new File("res/QD.png")));
//			imgs.put('k', ImageIO.read(new File("res/KD.png")));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		avails = new HashSet<ChessBoardLoc>();
//		/*
//		this.addKeyListener(new KeyListener(){
//			public void keyPressed(KeyEvent arg0) {
//
//			}
//			public void keyReleased(KeyEvent arg0) {
//				if(arg0.getKeyCode()==27){
//					for(Listener l : listeners){
//						ViewSwing.this.dispose();
//						l.yesEnd();
//						try {
//							instream.close();
//						} catch (IOException e) {
////							e.printStackTrace();
//						}
//						System.out.println("end");
//					}
//				}
//			}
//			public void keyTyped(KeyEvent arg0) {
//
//			}
//		});
//		*/
//	}
//
//	public void changedMode(Model model) {
//		mode = model.getMode();
//		if(mode==Model.MODE.ASKEND){
//			if(JOptionPane.showConfirmDialog(null, "Would you like to continue playing?", "More?", JOptionPane.YES_NO_OPTION)==0){
//				for(Listener l : listeners){
//					l.noEnd();
//				}
//			}
//			else{
//				for(Listener l : listeners){
//					l.yesEnd();
//					this.dispose();
//				}
//			}
//		}
//		else if(mode==Model.MODE.GOTO){
//			String[] cmdhist;
//			synchronized(hist){
//				cmdhist = new String[hist.size()+1];
//				cmdhist[0] = "0 - ";
//				for(int i=0, j=1; i<hist.size(); i++, j++){
//					cmdhist[j] = (j)+" - "+hist.get(i).getcmd();
//				}
//			}
//			String resp = (String) JOptionPane.showInputDialog(ViewSwing.this, "What move would you like to return to?", "Goto Move", JOptionPane.QUESTION_MESSAGE, null, cmdhist, null);
//			if(resp!=null && resp.length()>0){
//				int gotocmd = Integer.parseInt(resp.split(" ")[0]);
//				for(Listener l : listeners){
//					l.gotoMove(gotocmd);
//				}
//			}
//			else{
//				for(Listener l : listeners){
//					l.yesEnd();
//				}
//				this.dispose();
//			}
//		}
//	}
//	public void changedEnd(Model model){
//		end = model.getEnd();
//		if(end){
//			this.dispose();
//		}
//	}
//	public void changedBoard(Model model) {
//		lastboard = model.getBoard();
//		repaint();
//	}
//	public void changedError(Model model) {
//		//TODO: is this right?
//		new JDialog(this, model.getError(), null, null);
//	}
//	public void changedHistory(Model model) {
//		hist = model.getHistory();
//	}
//	public void play() {
//		if(newcmd!=null){
//			for(Listener l : listeners){
//				l.batchCmd(newcmd);
//				l.runCmds(true);
//			}
//			newcmd = null;
//		}
//	}
//
//	public void addListener(Listener l) {
//		listeners.add(l);
//	}
//	public void remListener(Listener l) {
//		listeners.remove(l);
//	}
//
//
//
//}
