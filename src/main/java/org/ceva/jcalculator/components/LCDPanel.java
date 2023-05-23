package org.ceva.jcalculator.components;

import org.ceva.jcalculator.util.CalcStandardLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import static org.ceva.jcalculator.components.LCD7Seg.SEGMENT_3;

public class LCDPanel extends JPanel{
    private int numDigits;
    private LCD7Seg digits[];
    // controlamos donde se encuentra el punto decimal
    private int dotIndex;
    private boolean light = false;
    private CalcStandardLookAndFeel lookAndFeel;

    public LCDPanel(int numDigits, CalcStandardLookAndFeel lookAndFeel) {
        super();
        this.lookAndFeel = lookAndFeel;
        this.numDigits = numDigits;
        digits = new LCD7Seg[numDigits];
        for (int n=0; n<numDigits; n++)
            digits[n] = new LCD7Seg(0, 0, 0);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setSize(getWidth(), getHeight());
            }
        });
        // ponemos la pantalla en cero
        setValue(0);
    }
    public void setLight() {
        light = !light;
        repaint();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        float fSegmentWidth = 0.26f;
        int digitWidth = width / (int)((numDigits + (fSegmentWidth * (numDigits+1))) + 0.9f);
        if ((digitWidth*2 + 20) > height) {
            digitWidth = (height-20) / 2;
        }
        int space = (int)(digitWidth * fSegmentWidth);

        int x = width - digitWidth*numDigits - space*(numDigits+1);
        int y = height/2 - digitWidth;
        for (int n=0; n<numDigits; n++) {
            int idx = numDigits - n - 1;
            digits[idx].setPosition(x, y, digitWidth);
            x += digitWidth + space;
        }
    }

    public void setValue(StringBuilder value) {
        int lenValue = value.length();
        // ubicamos la posicion del punto decimal si existe
        dotIndex = value.indexOf(".");
        if(dotIndex == -1)
            dotIndex = value.indexOf(",");
        // var n para dibujar digits de la pantalla
        int n = lenValue - 1;
        if (dotIndex == -1)
            dotIndex = 0;
        else {
            dotIndex = lenValue - dotIndex - 1;
            n--;
        }
        for(int i=0; i<numDigits; i++)
            digits[i].setValue(-1);
        int idx = 0;
        while (idx < lenValue) {
            char ch = value.charAt(idx);
            if ((ch >= '0') && (ch <= '9'))
                digits[n].setValue(ch - '0');
            else if ((ch == '.') || (ch == ',')) {
                idx++;
                continue;
            } else
                digits[n].setSegments(SEGMENT_3); // Signo de -
            n--;
            idx++;
        }
        repaint();
    }

    public void displayError() {
        // Display: Error
        for (int n=0; n<numDigits; n++) {
            digits[n].setValue(-1);
        }
        if (numDigits >= 5) {
            digits[4].setSegments(LCD7Seg.SEGMENT_0 | LCD7Seg.SEGMENT_1 | LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4 | LCD7Seg.SEGMENT_6);
            digits[3].setSegments(LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4);
            digits[2].setSegments(LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4);
            digits[1].setSegments(LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4 | LCD7Seg.SEGMENT_5 | LCD7Seg.SEGMENT_6);
            digits[0].setSegments(LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4);
        } else if (numDigits >= 3) {
            digits[2].setSegments(LCD7Seg.SEGMENT_0 | LCD7Seg.SEGMENT_1 | LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4 | LCD7Seg.SEGMENT_6);
            digits[1].setSegments(LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4);
            digits[0].setSegments(LCD7Seg.SEGMENT_3 | LCD7Seg.SEGMENT_4);
        } else {
            digits[0].setSegments(LCD7Seg.SEGMENT_3);
        }
        repaint();
    }

    public void setValue(long value) {
        int n = 0;
        boolean minus = value < 0;
        if (minus)
            value = -value;
        if (digits == null)
            return;
        while (n < numDigits) {
            if ((value == 0) && (n > 0)) {
                if (minus) {
                    digits[n].setSegments(LCD7Seg.SEGMENT_3);
                    minus = false;
                } else
                    digits[n].setValue(-1);
            } else
                digits[n].setValue((byte)(value % 10));
            value /= 10;
            n++;
        }

        if ((n == digits.length) && ((value > 0) || minus)) {
            displayError();
        }
        dotIndex = 0;
        repaint();
    }

    public void clear() {
        for (LCD7Seg d : digits)
            d.setValue(-1);
        dotIndex = 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(lookAndFeel.getKeyboardBackgroundColor());
        g2d.fillRect(0, 0, 24, 24);
        g2d.fillRect(0, getHeight()-24, 24, 24);
        g2d.fillRect(getWidth()-24, 0, 24, 24);
        g2d.fillRect(getWidth()-24, getHeight()-24, 24, 24);
        // si la pantalla no esta iluminada
        if (!light) {
            g2d.setColor(lookAndFeel.getScreenBackgroundColor());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        } else {
            g2d.setColor(lookAndFeel.getScreenLightBackgroundColor());
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

            /* Originalmente quería agregar un gradiente para que se viera como
            el led que iluminaba la pantalla, pero después de muchas pruebas no
            me gustó el resultado así que lo quité. De cualquier manera dejo el
            código comentado por si quieres intentar mejorarlo.
            */
//            Paint savePaint = g2d.getPaint();
//            RadialGradientPaint rp = new RadialGradientPaint(
//                    (float)(-(getWidth()/10)),
//                    (float)(getHeight()/2),
//                    (float)getWidth(),
//                    new float[] {0f, 0.4f},
//                    new Color[] {new Color(255, 255, 255), lookAndFeel.getScreenLightBackgroundColor()});
//            g2d.setPaint(rp);
//            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
//            g2d.setPaint(savePaint);
        }

        for (LCD7Seg d : digits) {
            d.paintComponent(g2d);
        }
        // dibujamos el punto decimal
        if (dotIndex >= 0) {
            LCD7Seg digit = digits[dotIndex];
            int x = digit.getXPosition() + digit.getDigitWith();
            int y = digit.getYPosition() + digit.getDigitWith()*2;
            // el punto sera un 26% del ancho del digito
            int size = (int)(digits[dotIndex].getDigitWith() * 0.26f);
            y -= size;
            int offset = digit.getDigitWith()/32 * 2;
            g2d.setColor(new Color(0, 0, 0, 64));
            g2d.fillOval(x+offset, y+offset, size, size);
            g2d.setColor(Color.BLACK);
            g2d.fillOval(x, y, size, size);
        }
    }
}
