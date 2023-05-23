package org.ceva.jcalculator.components;

import org.ceva.jcalculator.api.KeyListener;
import org.ceva.jcalculator.util.CalcStandardLookAndFeel;

import javax.swing.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CalcKey extends JPanel{
    private boolean verticalKey = false;   // vertical flag: Si true, la tecla se agranda verticalmente si hay espacio
    // pressed, pushed flags para manejar los eventos del raton y determinar el estado de los botones
    private boolean pressed = false;
    // evento de la tecla presionada
    private boolean pushed = false;
    private String value;
    // KeyListener permite ser un observador de una tecla
    private KeyListener listener;
    private CalcStandardLookAndFeel lookAndFeel;
    private Color keyColor;

    public CalcKey(char value, CalcStandardLookAndFeel lookAndFeel ){
        super();
        this.value = String.valueOf(value);
        this.lookAndFeel = lookAndFeel;
        this.keyColor = lookAndFeel.getKeyColor();

        final CalcKey self = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    pressed = true;
                    pushed = true;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1){
                    if(pressed && (listener != null)){
                        // se genera el evento al presionar la tecla
                        listener.keyPressed(self.value.charAt(0));
                    }
                    pressed=false;
                    pushed=false;
                    repaint();
                }
            }

            // mouse ingresa al panel
            @Override
            public void mouseEntered(MouseEvent e) {
                if(pushed){
                    pressed=true;
                    repaint();
                }
            }

            // el mouse sale del panel
            @Override
            public void mouseExited(MouseEvent e) {
                pressed=false;
                repaint();
            }
        });
    }

    public CalcKey(char value, KeyListener listener, CalcStandardLookAndFeel lookAndFeel){
        this(value, lookAndFeel);
        this.listener = listener;
    }

    private static Color darkenColor(Color source, float percent) {
        Color res = new Color((int)(source.getRed()*percent),
                (int)(source.getGreen()*percent),
                (int)(source.getBlue()*percent));
        return res;
    }

    // metodo para cambiar el color a la tecla
    public void setKeyColor(Color keyColor){
        this.keyColor = keyColor;
    }

    // metodo para indicar la orientacion de la tecla
    public void setVerticalKey(){
        verticalKey = true;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(lookAndFeel.getKeyboardBackgroundColor());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Dibujar tecla con antialias
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        // metricas para dibujar
        int keySize;
        int margin; // Dejar un margen entre la tecla y el JPanel
        Rectangle rect;
        if (verticalKey) {
            // tamanio de la tecla
            keySize = Math.min(getWidth(), getHeight()/2);
            margin = keySize / 15;
            keySize -= margin*2;

            int vKeySize = getHeight()/2 + keySize;
            rect = new Rectangle(margin, margin, keySize, vKeySize);
        } else {
            keySize = Math.min(getWidth(), getHeight());
            margin = keySize / 15;
            keySize -= margin*2;

            rect = new Rectangle(margin, margin, keySize, keySize);
        }
        // redondeo de rectangulo: tecera parte del ancho de rect
        int arcWidth = rect.width / 3;
        // fin metricas para dibujar

        // dibujamos la sombra si no ha sido presionada
        if (!pressed) {
            int shadowOffset = margin;
            g2d.setColor(lookAndFeel.getKeyShadowColor());
            g2d.fillRoundRect(rect.x + shadowOffset, rect.y + shadowOffset, rect.width, rect.height, arcWidth, arcWidth);
        }

        // Orificio de la tecla (en negro)
        g2d.setColor(lookAndFeel.getKeyMarginColor());
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arcWidth, arcWidth);

        // Metricas para parte interior de tecla
        margin = rect.width / 15;
        rect.x += margin;
        // tecla presionada
        if (!pressed)
            rect.y++;
        else
            rect.y += margin;
        rect.width -= margin*2;
        rect.height -= margin*2;
        arcWidth = rect.width/3;

        // El rectangulo oscuro que representa el volumen de la tecla
        if (!pressed) {
            // darkenColor: oscurecemos el color pasado a la funcion
            g2d.setColor(darkenColor(keyColor, 0.5f));
            g2d.fillRoundRect(rect.x, rect.y+margin, rect.width, rect.height, arcWidth, arcWidth);
        }

        // Contorno
        g2d.setColor(keyColor);
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arcWidth, arcWidth);
        g2d.setColor(lookAndFeel.getKeyOutline());
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arcWidth, arcWidth);

        // Centro tecla
        margin = rect.width / 30;
        rect.x += margin;
        rect.y += margin;
        rect.width -= margin*2;
        rect.height -= margin*2;
        arcWidth = rect.width/3;
        g2d.setColor(keyColor);
        g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, arcWidth, arcWidth);

        if (!"L".equals(value)) {
            FontMetrics fm;
            Font font = lookAndFeel.getKeyFont();
            if (font != null) {
                fm = lookAndFeel.getKeyFontMetrics();
                g2d.setFont(font);
            } else {
                font = g2d.getFont().deriveFont(Font.BOLD, rect.width*0.4f);
                g2d.setFont(font);
                fm = g2d.getFontMetrics();
                lookAndFeel.setKeyFont(font, fm);
            }
            String label;
            if ("O".equals(value)) {
                label = "OFF";
            } else if ("C".equals(value)) {
                label = "C/AC";
            } else
                label = value;
            g2d.setColor(lookAndFeel.getKeyTxtColor());
            g2d.drawString(label, rect.x+rect.width/2 - fm.stringWidth(label)/2, rect.y+rect.height/2 + fm.getHeight()/3);
        } else {
            // Logo iluminacion
            int w = (int)(rect.width*0.3f);
            int thick = (int)(w * 0.27f);
            if (thick <= 2)
                thick = 2;

            g2d.setColor(lookAndFeel.getKeyTxtColor());

            g2d.fillOval(rect.x+rect.width/2 - w/2, rect.y+rect.height/2-w/2, w, w);

            Stroke saveStroke = g2d.getStroke();
            Stroke stroke = new BasicStroke(thick);
            g2d.setStroke(stroke);
            double angle = 270.0 - ((360.0 / 6)/2);
            for (int i=0; i<6; i++) {
                double cos = Math.cos(Math.toRadians(angle));
                double sin = Math.sin(Math.toRadians(angle));

                g2d.drawLine((int)(rect.x+rect.width/2 + cos*(w*0.9)),
                        (int)(rect.y+rect.height/2 - sin*(w*0.9)),
                        (int)(rect.x+rect.width/2 + cos*(w*1.3)),
                        (int)(rect.y+rect.height/2 - sin*(w*1.3)));

                angle += (360.0 / 6) % 360.0;
            }
            g2d.setStroke(saveStroke);
        }

    }
}
