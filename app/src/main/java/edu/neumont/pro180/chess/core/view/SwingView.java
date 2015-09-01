package edu.neumont.pro180.chess.core.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.neumont.pro180.chess.core.controller.Controller;
import edu.neumont.pro180.chess.core.model.Move;
import edu.neumont.pro180.chess.core.model.Piece;
import edu.neumont.pro180.chess.core.model.Tile;
import edu.neumont.pro180.chess.core.view.View;

/**
 * Created by Trevan on 8/31/2015.
 */
public class SwingView extends JFrame implements View{
	private static final long serialVersionUID = 1L;
	Piece[][] lastboard;
    int mousex;
    int mousey;
    Tile mouse;
    Tile movef;
    Tile movet;
    Move newcmd;
    List<Tile> avails;
    Map<Character, Image> imgs;

    public SwingView(){
        setTitle("Chess");
        add(new JPanel(){
            private static final long serialVersionUID = 1L;
            public JPanel init(int w, int h){
                Dimension d = new Dimension(w,h);
                setMinimumSize(d);
                setPreferredSize(d);
                setSize(d);
                setBackground(new Color(50,0,0));
                addMouseMotionListener(new MouseMotionListener(){
                    public void mouseDragged(MouseEvent arg0) {

                    }
                    public void mouseMoved(MouseEvent arg0) {
                        mousex = arg0.getX();
                        mousey = arg0.getY();
                        mouse = getloc(mousex, mousey);
                        repaint();
                    }});
                addMouseListener(new MouseListener(){
                    public void mouseClicked(MouseEvent arg0) {
//                        if(mode==Model.MODE.PLAY){
                            if(movef==null){
                                movef = getloc(arg0.getX(), arg0.getY());
//                                if(!(lastboard.getPieceAt(movef)!=null && !lastboard.getPieceAt(movef).isColor(lastboard.getlastmovecolor()))){
                                    movef = null;
//                                }
//                                avails = lastboard.availmoves(movef);
                                repaint();
                            }
                            else{
                                movet = getloc(arg0.getX(), arg0.getY());
                                newcmd = new Move(movef, movet);
                                movef = null;
                                avails.clear();
                            }
//                        }
                    }
                    public void mouseEntered(MouseEvent arg0) {

                    }
                    public void mouseExited(MouseEvent arg0) {
                        mousex = -1;
                        mousey = -1;
                        mouse = getloc(mousex, mousey);
                        repaint();
                    }
                    public void mousePressed(MouseEvent arg0) {

                    }
                    public void mouseReleased(MouseEvent arg0) {

                    }
                });
                return (JPanel)this;
            }
            public void paint(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                drawBoard(lastboard, g, 0, 0, getWidth(), getHeight(), true);
            }
            public Tile getloc(int x, int y){
                Graphics g = getGraphics();
                int headsizx = g.getFontMetrics().charWidth('M')*2;
                int headsizy = g.getFontMetrics().getHeight()*2;
                int tmpsiz = (getWidth()-headsizx)/8;
                int sqsiz = (getHeight()-headsizy)/8;
                if(sqsiz>tmpsiz){
                    sqsiz = tmpsiz;
                }
                headsizx = (getWidth()-8*sqsiz)/2;
                headsizy = (getHeight()-8*sqsiz)/2;

                int col = x<headsizx?-1 : (x-headsizx)/sqsiz;
                int row = getHeight()-y<headsizy?-1 : (getHeight()-(y+headsizy))/sqsiz;
                try{
                	return new Tile(col, row);
                } catch(IndexOutOfBoundsException e){
                	return null;
                }
            }
        }.init(640,480));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
		imgs = new HashMap<Character, Image>();
		String imgloc = "./res/drawable-hdpi/";
		File filoc;
		try {
			filoc = new File(imgloc, "pl.png"); if(filoc.exists()){imgs.put('P', ImageIO.read(filoc));}
			filoc = new File(imgloc, "rl.png"); if(filoc.exists()){imgs.put('R', ImageIO.read(filoc));}
			filoc = new File(imgloc, "nl.png"); if(filoc.exists()){imgs.put('N', ImageIO.read(filoc));}
			filoc = new File(imgloc, "bl.png"); if(filoc.exists()){imgs.put('B', ImageIO.read(filoc));}
			filoc = new File(imgloc, "ql.png"); if(filoc.exists()){imgs.put('Q', ImageIO.read(filoc));}
			filoc = new File(imgloc, "kl.png"); if(filoc.exists()){imgs.put('K', ImageIO.read(filoc));}
			filoc = new File(imgloc, "pd.png"); if(filoc.exists()){imgs.put('p', ImageIO.read(filoc));}
			filoc = new File(imgloc, "rd.png"); if(filoc.exists()){imgs.put('r', ImageIO.read(filoc));}
			filoc = new File(imgloc, "nd.png"); if(filoc.exists()){imgs.put('n', ImageIO.read(filoc));}
			filoc = new File(imgloc, "bd.png"); if(filoc.exists()){imgs.put('b', ImageIO.read(filoc));}
			filoc = new File(imgloc, "qd.png"); if(filoc.exists()){imgs.put('q', ImageIO.read(filoc));}
			filoc = new File(imgloc, "kd.png"); if(filoc.exists()){imgs.put('k', ImageIO.read(filoc));}
		} catch (IOException e) {
			e.printStackTrace();
		}
		avails = new ArrayList<Tile>();
    }

    public void drawBoard(Piece[][] board, Graphics g, int x,int y, int width,int height, boolean headers) {
        if (board != null) {
            int headsiz = headers ? (Math.max(g.getFontMetrics().getHeight(), g.getFontMetrics().charWidth('M')) + 2) : 0;
            int tmpsiz = (width - headsiz * 2) / 8;
            int sqsiz = (height - headsiz * 2) / 8;
            if (sqsiz > tmpsiz) {
                sqsiz = tmpsiz;
            }
            int spacersizx = (width - 8 * sqsiz) / 2;
            int spacersizy = (height - 8 * sqsiz) / 2;

            if (headers) {
                g.setColor(new Color(175, 125, 25));
                g.fillRect(spacersizx - headsiz + x, spacersizy - headsiz + y, 8 * sqsiz + headsiz * 2, 8 * sqsiz + headsiz * 2);
            }
            g.setColor(new Color(255, 255, 255));
            g.fillRect(spacersizx+x, spacersizy+y, 8*sqsiz, 8*sqsiz);
            for(int i=0; i<8; i++){
                g.setColor(new Color(0,0,0));
//                if(headers){
//                    g.drawString(""+ChessBoardLoc.colcharfrombyte(i), i*sqsiz+spacersizx+sqsiz/2+x, spacersizy-headsiz/4+y);
//                    g.drawString(""+ChessBoardLoc.colcharfrombyte(i), i*sqsiz+spacersizx+sqsiz/2+x, height-spacersizy+headsiz*2/3+y);
//                    g.drawString(""+(i+1), spacersizx-headsiz*2/3+x, (7-i)*sqsiz+spacersizy+sqsiz/2+y);
//                    g.drawString(""+(i+1), width-spacersizx+headsiz/3+x, (7-i)*sqsiz+spacersizy+sqsiz/2+y);
//                }
                for(int j=0; j<8; j++){
                    Tile thisloc = new Tile(i,j);
                    if(headers && mouse!=null && thisloc.equals(mouse)){
                        g.setColor(new Color(150,205,205));
                        g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
                    }
                    else if(headers && movef!=null && thisloc.equals(movef)){
                        if(i%2==j%2){
                            g.setColor(new Color(205,150,150));
                        }
                        else{
                            g.setColor(new Color(155,100,100));
                        }
                        g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
                    }
                    else if(headers && avails.contains(thisloc)){
                        if(i%2==j%2){
                            g.setColor(new Color(205,150,150));
                        }
                        else{
                            g.setColor(new Color(155,100,100));
                        }
                        g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
                    }
                    else if(i%2!=j%2){
                        g.setColor(new Color(0,0,0));
                        g.fillRect(i*sqsiz+spacersizx+x, height-(j*sqsiz+spacersizy)+y, sqsiz, -1*sqsiz);
                    }
                    Piece piece = board[thisloc.y][thisloc.x];
                    if(piece!=null){
                        Image img = imgs.get(piece.toCharTeam());
                        if(img==null){
//                            if(piece.isColor('L')){
//                                g.setColor(new Color(255,100,0));
//                            }
//                            else{
//                                g.setColor(new Color(105,50,0));
//                            }
                            g.drawString(piece.toString(), i*sqsiz+spacersizx+sqsiz/2+x, height-(j*sqsiz+spacersizy+sqsiz/2)+y);
                        }
                        else{
                            g.drawImage(img, i*sqsiz+spacersizx+x, height-((j+1)*sqsiz+spacersizy)+y, sqsiz,sqsiz, null);
                        }
                    }
                }
            }

        }
    }

	@Override
	public void notifyIsInCheck() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Move readMove() {
        do{
            if(newcmd!=null) {
                return newcmd;
            }
            Thread.yield();
        }while(true);
	}
	@Override
	public Piece.Type getPawnPromotion() {
        String resp = (String) JOptionPane.showInputDialog(this, "What would you like to promote your pawn to?", "Pawn Promotion", JOptionPane.QUESTION_MESSAGE, null, new String[]{"Queen","Bishop","Knight","Rook"}, null);
        if(resp!=null && resp.length()>0){
            if(resp.equals("Queen")){
                return Piece.Type.QUEEN;
            }
            else if(resp.equals("Bishop")){
                return Piece.Type.BISHOP;
            }
            else if(resp.equals("Knight")){
                return Piece.Type.KNIGHT;
            }
            else if(resp.equals("Rook")){
                return Piece.Type.ROOK;
            }
        }
        return null;
	}
	@Override
	public void displayBoard(Piece[][] pieces) {
		lastboard = pieces;
		repaint();
	}
	@Override
	public void highlightTiles(Tile start, List<Tile> ends) {
		movef = start;
		avails = ends;
		repaint();
	}
	@Override
	public void setListener(Listener listener) {
		// TODO Auto-generated method stub
		
	}
	
    public static void main(String[] args){
    	Controller cont = new Controller(new SwingView());
    	cont.play();
    }

}
