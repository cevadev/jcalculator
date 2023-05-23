package org.ceva.jcalculator.components;

import org.ceva.jcalculator.api.KeyListener;
import org.ceva.jcalculator.util.CalcStandardLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class CalcMainPanel extends JPanel{
    private CalcStandardLookAndFeel lookAndFeel;
    private KeyListener keyListener;

    /**
     * Queremos que la calculadora puede ser manejada por medio del teclado
     * CalcMainPanel intercepta los eventos del teclado y los envia a la clase principal para que
     * la calculadora puede ser utilizada con el teclado
     * GlobalPanel es el componente que agrupa al teclado como a la pantalla por lo que es el
     * lugar mas general para recibir este tipo de eventos.
     */
    public CalcMainPanel(CalcStandardLookAndFeel lookAndFeel, KeyListener keyListener){
        super();
        this.lookAndFeel = lookAndFeel;
        this.keyListener = keyListener;

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(keyListener != null){
                    // leemos el caracter
                    char ch = e.getKeyChar();
                    if(((ch >= '0') && (ch <= '9')) || (ch == '+') || (ch == '-') || (ch == '*') || (ch == '/')
                            || (ch == '=') || (ch == '.') || (ch == '\n') || (ch == 8)){
                        // validamos si es enter
                        if(ch == '\n'){
                            ch = '=';
                        }
                        else if(ch == 8){
                            ch = 'C';
                        }
                        // enviamos a keypressed unicamente valores reconocidos
                        keyListener.keyPressed(ch);
                    }
                }
            }
        });
    }

    // metodo q realiza el dibujo estilo 3D en el Panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // para el efecto 3D dibujamos esquinas redondeadas de diferentes tonos de oscuro a claro
        Color c = lookAndFeel.getKeyboardBackgroundColor();

        // al dibujar rectangulos con esquinas redondeadas uno ligeramente mas pequeno que el otro
        // no queremos que los colores no vaya a cuadrar
        Stroke saveStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(3));
        for(int n = 0; n < 16; n++){
            g2d.setColor(new Color(n * (c.getRed() / 16), n * (c.getGreen() / 16), n * (c.getBlue() / 16)));
            // a partir de la coordenada n, n, hacemos un rectangulo del tamano getWidth - n*2, es decir
            // conforme n va aumentando el punto inicial del rectangulo se va moviendo en vertical
            g2d.drawRoundRect(n, n, getWidth() - (n * 2), getHeight()-(n * 2), 24, 24);
        }
        g2d.setStroke(saveStroke);
    }
}
