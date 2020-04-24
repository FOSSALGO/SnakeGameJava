package fosalgo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PapanPermainan extends JPanel implements ActionListener{
    
    private static final int MAX_X = 16;
    private static final int MAX_Y = 32;
    private static final int CELL_SIZE = 40;
    private static final int WIDTH = MAX_Y * CELL_SIZE;
    private static final int HEIGHT = MAX_X * CELL_SIZE;
    private static final int DELAY = 140;//ubah nilai ini untuk mengubah speed permainan
    private static Timer timer;
    
    private static String[][] papan = new String[MAX_X][MAX_Y];
    private static Vector<Posisi>ular = new Vector<>();
    private static int arah = 1;//keterangan: 0=utara, 1=timur, 2=selatan, 3=barat
    private static Posisi posisiMakanan = null;
    private static boolean permainanSelesai = false;//digunakan untuk menandai kondisi game over
    
    public PapanPermainan(){
        inisialisasiPapanPermainan();
    }
    
    private void inisialisasiPapanPermainan(){
        addKeyListener(new PapanPermainan.TAdapter());
        setBackground(Color.decode("#2c3e50"));
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        inisialisasiGame();
    }
    
    private void inisialisasiGame(){
        bikinUlar();
        letakkanMakanan();
        perbaharuiPapan();
        timer = new Timer(DELAY, this);
        timer.start();
    }
    
    private static void bikinUlar(){
        ular = new Vector<>();
        ular.add(new Posisi(1,4));//index 0 = posisi kepala ular     
        ular.add(new Posisi(1,3));//badan
        ular.add(new Posisi(1,2));//badan
        ular.add(new Posisi(1,1));//ekor
    }
    
    private static void bersihkanPapan(){
        for(int i=0;i<papan.length;i++){
            for(int j=0;j<papan[i].length;j++){
                papan[i][j]=" ";//String untuk penanda ruang kosong
            }
        }
    }
    
    private static void letakkanMakanan(){
        int rx = (int)(Math.random()*(papan.length-1));
        int ry = (int)(Math.random()*(papan[rx].length-1));
        posisiMakanan = new Posisi(rx,ry);
    }
    
    private static void cekMakanan(){
        if(posisiMakanan !=null && ular != null && ular.size()>0){
            Posisi posKepala = ular.get(0);
            if(posisiMakanan.x == posKepala.x && posisiMakanan.y == posKepala.y){
                //terjadi collision kepala ular dan makanan
                //ular memakan makanan
                //panjang ular bertambah satu segmen
                Posisi posisiEkor = ular.get(ular.size()-1);
                Posisi ekorBaru = new Posisi(posisiEkor.x, posisiEkor.y);
                ular.add(ekorBaru);
                letakkanMakanan();
            }
        }
    }
    
    private static void cekTumbukan(){
        //jika ular menabrak badannya sendiri maka game over
        Posisi posisiKepala = ular.get(0);
        for(int i=ular.size()-1;i>0;i--){//pemeriksaan dari ekor
            Posisi pos = ular.get(i);//posisi segmen badan ular
            if(pos.x == posisiKepala.x && pos.y == posisiKepala.y){
                permainanSelesai = true;
                break;
            }                    
        }
        
        //cek tumbukan ke dinding
        //apakah ular menabrak dinding
        if(!permainanSelesai){
            if(posisiKepala.x < 0){
                permainanSelesai = true;
            }
            if(posisiKepala.x >= papan.length){
                permainanSelesai = true;
            }
            if(posisiKepala.y < 0){
                permainanSelesai = true;
            }
            if(posisiKepala.y >= papan[0].length){
                permainanSelesai = true;
            }
        }
        
        if(permainanSelesai){
            timer.stop();
        }        
    }
    
    private static void perbaharuiPapan(){
        bersihkanPapan();
        //letakkan ular ke papan
        //letakkan segmen badan ular
        for(int u=1;u<ular.size();u++){
            Posisi pos = ular.get(u);
            papan[pos.x][pos.y] = "O";// String O adalah penanda badan ular
        }
        
        //letakkan makanan
        if(posisiMakanan!=null){
            papan[posisiMakanan.x][posisiMakanan.y] = "M";//String M adalah penanda untuk makanan
        }
        
        //letakkan kepala ular        
        Posisi posKepala = ular.get(0);
        if(posKepala.x>=0&&posKepala.x<papan.length&&posKepala.y>0&&posKepala.y<papan[posKepala.x].length){
            papan[posKepala.x][posKepala.y]="X";//String X adalah penanda untuk kepala ular
        }//kepala tidak perlu digambar jika menumbuk/berada diluar papan
        
    }
    
    private static void bergerak(){
        //gerakan badan ular
        for(int i=ular.size()-1;i>0;i--){
            Posisi posBaru = ular.get(i-1);
            int bx = posBaru.x;
            int by = posBaru.y;
            Posisi pos = new Posisi(bx, by);
            ular.set(i, pos);
        }
        
        //gerakan untuk kepala ular diupdate berdasarkan arah
        Posisi posKepala = ular.get(0);
        int kx = posKepala.x;
        int ky = posKepala.y;
        switch(arah){
            case 0:
                kx--;
                break;
            case 1:
                ky++;
                break;
            case 2:
                kx++;
                break;
            case 3:
                ky--;
                break;
            default:
                break;
        }
        Posisi posKepalaBaru = new Posisi(kx,ky);
        ular.set(0, posKepalaBaru);       
    }
    
    private static void setArah(int arahBaru){
        //ular tidak dapat bergerak mundur haya maju, belok kanan dan belok kiri
        if(arahBaru == 0 && arah != 2){
            arah = arahBaru;
        }else if(arahBaru == 1 && arah != 3){
            arah = arahBaru;
        }else if(arahBaru == 2 && arah != 0){
            arah = arahBaru;
        }else if(arahBaru == 3 && arah != 1){
            arah = arahBaru;
        }
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        menggambarPapanPermainan(g);
    }
    
    private void menggambarPapanPermainan(Graphics g){
        if(!permainanSelesai){
            //gambar makanan
            g.setColor(Color.decode("#2ecc71"));
            g.fillRect(posisiMakanan.y*CELL_SIZE, posisiMakanan.x*CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            //gambar badan ular
            for(int u = 1;u<ular.size();u++){
                Posisi pos = ular.get(u);
                g.setColor(Color.decode("#f1c40f"));
                g.fillRect(pos.y*CELL_SIZE, pos.x*CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
            
            //gambar kepala ular
            Posisi posKepala = ular.get(0);
            g.setColor(Color.decode("#e67e22"));
            g.fillRect(posKepala.y*CELL_SIZE, posKepala.x*CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            Toolkit.getDefaultToolkit().sync();            
        }else{
            gameOver(g);
        }
    }
    
    private void gameOver(Graphics g){
        String pesan = "GAME OVER";
        Font small = new Font("Helvetica",Font.BOLD, 40);
        FontMetrics metr = getFontMetrics(small);
        g.setColor(Color.decode("#e67e22"));
        g.setFont(small);
        g.drawString(pesan, (WIDTH-metr.stringWidth(pesan))/2, HEIGHT/2);
        
        //tuliskan poin juga
        small = new Font("Helvetica",Font.BOLD, 30);
        metr = getFontMetrics(small);
        g.setColor(Color.decode("#f1c40f"));
        g.setFont(small);
        g.drawString("POIN: "+ular.size(), (WIDTH-metr.stringWidth(pesan))/2, 50+(HEIGHT/2));
    }
       
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(!permainanSelesai){
            cekMakanan();
            cekTumbukan();
            perbaharuiPapan();
            bergerak();
        }
        repaint();
    }
    
    private class TAdapter extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            int key = e.getKeyCode();
            
            if(key==KeyEvent.VK_UP){
                setArah(0);
            }else if(key==KeyEvent.VK_RIGHT){
                setArah(1);
            } else if(key==KeyEvent.VK_DOWN){
                setArah(2);
            } else if(key==KeyEvent.VK_LEFT){
                setArah(3);
            } 
            
        }
    }
    
}
