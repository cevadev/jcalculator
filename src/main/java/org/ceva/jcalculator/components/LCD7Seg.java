package org.ceva.jcalculator.components;

import java.awt.*;

public class LCD7Seg {
    // definimos el bit que corresponde a cada segmento
    public static final int SEGMENT_0 = 0x1; // bit 1 - binario    1
    public static final int SEGMENT_1 = 0x2; // bit 2 - binario   10
    public static final int SEGMENT_2 = 0x4; // bit 4 - binario  100
    public static final int SEGMENT_3 = 0x8; // bit 8 - binario 1000
    public static final int SEGMENT_4 = 0x10; // bit 16
    public static final int SEGMENT_5 = 0x20; // bit 32
    public static final int SEGMENT_6 = 0x40; // bit 64

    private Color segmentColor = new Color(0x000000);
    private Color disableSegmentColor = new Color(0,0,0,16);
    private Color shadowColor = new Color(0,0,0,64);

    // arreglo de poligonos para los segmentos del digito
    Polygon segments[];
    Polygon segmentsShadows[];
    // guardamos en cada bit del byte la representacion de uno de los segmentos, es decir, al tener 7 segmentos
    // usaremos 7 bits del byte para saber cual esta encendido y cual no,
    private byte segmentState; // representa el digito que se va pintar
    private int x;
    private int y;
    private int digitWidth;
    private int segmentWidth;
    public LCD7Seg(int x, int y, int digitWidth){
        // definimos los bits que vamos a encender
        this.segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_2 | SEGMENT_3 | SEGMENT_4 | SEGMENT_5 | SEGMENT_6;
        this.x = x;
        this.y = y;
        this.digitWidth = digitWidth;

        // El tamanio vertical sera dos veces el tamanio del digito o el horizontal
        initSegments();
    }
    public void setPosition(int x, int y, int digitWidth) {
        this.x = x;
        this.y = y;
        this.digitWidth = digitWidth;
        initSegments();
    }

    public int getXPosition() {
        return x;
    }

    public int getYPosition() {
        return y;
    }

    public int getDigitWith() {
        return digitWidth;
    }


    // hacemos que el digito sea ligeramente mas delgado. calculamos el ancho del sgmento basado en el
    // ancho del rectangulo
    public int getSegmentWidth(int digitWidth){
        return (int)((digitWidth *  0.13f) * 2);
    }

    // inicializamos los valores de los segmentos
    private void initSegments(){
        // creamos los 7 segmentos para dibujar un digito
        segments = new Polygon[7];

        Rectangle r = new Rectangle(x, y, digitWidth, digitWidth * 2);
        segmentWidth = getSegmentWidth(r.width);
        // w espacio para el rectangulo interno
        int w = segmentWidth /2;
        int gap = (int)(r.width * 0.03f);
        // nos aseguramos que siempre exista por lo menos 1 pixel de distancia
        if(gap == 0){
            gap = 1;
        }
        Point points[] = new Point[]{
                // representamos los puntos para el segmento 0 de nuestro disenio
                new Point(r.x + w * 2, r.y),
                new Point(r.x + r.width - w * 2, r.y),
                new Point(r.x + w, r.y + w),
                new Point(r.x + r.width - w, r.y + w),
                new Point(r.x + w * 2, r.y + w * 2),
                new Point(r.x + r.width - w * 2, r.y + w * 2)
        };

        // definicion de polygono
        Polygon p;
        p = new Polygon();
        p.addPoint(points[0].x + gap, points[0].y);
        p.addPoint(points[1].x - gap, points[1].y);
        p.addPoint(points[3].x - gap, points[3].y);
        p.addPoint(points[5].x - gap, points[5].y);
        p.addPoint(points[4].x + gap, points[4].y);
        p.addPoint(points[2].x + gap, points[2].y);

        segments[0] = p;

        // los segmentos 3 y 6 son similares al segmento 0 por lo que guardamos los puntos del segmento o (p)
        // arreglo de puntos x
        int xPoints[] = new int[p.npoints];
        // arreglo de puntos y
        int yPoints[] = new int[p.npoints];
        // segmento 3
        for(int n = 0; n < xPoints.length; n++){
            xPoints[n] = p.xpoints[n];
            yPoints[n] = p.ypoints[n] + r.height / 2 - w;
        }
        p = new Polygon(xPoints, yPoints, xPoints.length);
        segments[3] = p;

        // segmento 6
        for(int n = 0; n < xPoints.length; n++){
            yPoints[n] = p.ypoints[n] + r.height / 2 - w;
        }
        p = new Polygon(xPoints, yPoints, xPoints.length);
        segments[6] = p;

        // definicion de segmentos verticales que son 1, 2, 4, 5
        points = new Point[]{
                new Point(r.x + w, r.y + w),
                new Point(r.x, r.y + w*2),
                new Point(r.x + w*2, r.y + w*2),
                new Point(r.x, r.y + r.height/2 - w),
                new Point(r.x + w*2, r.y + r.height/2 - w),
                new Point(r.x + w, r.y + r.height/2)
        };
        p = new Polygon();
        p.addPoint(points[0].x, points[0].y + gap);
        p.addPoint(points[2].x, points[2].y+gap);
        p.addPoint(points[4].x, points[4].y-gap);
        p.addPoint(points[5].x, points[5].y-gap);
        p.addPoint(points[3].x, points[3].y-gap);
        p.addPoint(points[1].x, points[1].y+gap);
        segments[1] = p;

        // inicializamos los arreglos xPoints, yPoints para el manejo de los segmentos 2, 4, 5
        for(int n = 0; n < xPoints.length; n++){
            // movemos las coordenadas a la derecha para el segmento 2
            xPoints[n] = p.xpoints[n] + r.width - w * 2;
            yPoints[n] = p.ypoints[n];
        }
        p = new Polygon(xPoints, yPoints, xPoints.length);
        segments[2] = p;

        // segmento 4
        p = segments[1];
        for(int n = 0; n < xPoints.length; n++){
            xPoints[n] = p.xpoints[n];
            yPoints[n] = p.ypoints[n] + r.height/2 - w;
        }
        p = new Polygon(xPoints, yPoints, xPoints.length);
        segments[4] = p;

        // segmento 5 (tomamos el segmento 4 y lo recorremos a la derecha)
        p = segments[2];
        for (int n = 0; n < xPoints.length; n++){
            xPoints[n] = p.xpoints[n];
            yPoints[n] = p.ypoints[n] + r.height/2 - w;
        }
        p = new Polygon(xPoints, yPoints, xPoints.length);
        segments[5] = p;

        // definimos los segmentos para sombras
        if(digitWidth >= 32){
            // aplicamos sombras
            int xOffset = (digitWidth/32) * 2; // un 32 avo del ancho del digito
            int yOffset = xOffset;
            segmentsShadows = new Polygon[segments.length];
            for(int nPoly = 0; nPoly < segmentsShadows.length; nPoly++){
                for(int n = 0; n < xPoints.length; n++){
                    xPoints[n] = segments[nPoly].xpoints[n] + xOffset;
                    yPoints[n] = segments[nPoly].ypoints[n] + yOffset;
                }
                p = new Polygon(xPoints, yPoints, xPoints.length);
                segmentsShadows[nPoly] = p;
            }
        }
    }

    public void setSegmentColor(Color segmentColor) {
        this.segmentColor = segmentColor;
    }

    public void setShadowColor(Color shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getSegmentWidth() {
        return segmentWidth;
    }

    public void setValue(int value){
        switch (value) {
            case 0:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_2 | SEGMENT_4 | SEGMENT_5 | SEGMENT_6;
                break;
            case 1:
                segmentState = SEGMENT_2 | SEGMENT_5;
                break;
            case 2:
                segmentState = SEGMENT_0 | SEGMENT_2 | SEGMENT_3 | SEGMENT_4 | SEGMENT_6;
                break;
            case 3:
                segmentState = SEGMENT_0 | SEGMENT_2 | SEGMENT_3 | SEGMENT_5 | SEGMENT_6;
                break;
            case 4:
                segmentState = SEGMENT_1 | SEGMENT_2 | SEGMENT_3 | SEGMENT_5;
                break;
            case 5:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_3 | SEGMENT_5 | SEGMENT_6;
                break;
            case 6:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_3 | SEGMENT_4 | SEGMENT_5 | SEGMENT_6;
                break;
            case 7:
                segmentState = SEGMENT_0 | SEGMENT_2 | SEGMENT_5;
                break;
            case 8:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_2 | SEGMENT_3 | SEGMENT_4 | SEGMENT_5 | SEGMENT_6;
                break;
            case 9:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_2 | SEGMENT_3 | SEGMENT_5 | SEGMENT_6;
                break;
            case 10:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_2 | SEGMENT_3 | SEGMENT_4 | SEGMENT_5;
                break;
            case 11:
                segmentState = SEGMENT_1 | SEGMENT_3 | SEGMENT_4 | SEGMENT_5 | SEGMENT_6;
                break;
            case 12:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_4 | SEGMENT_6;
                break;
            case 13:
                segmentState = SEGMENT_2 | SEGMENT_3 | SEGMENT_4 | SEGMENT_5 | SEGMENT_6;
                break;
            case 14:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_3 | SEGMENT_4 | SEGMENT_6;
                break;
            case 15:
                segmentState = SEGMENT_0 | SEGMENT_1 | SEGMENT_3 | SEGMENT_4;
                break;
            case -1:
                // apagamos todos los segmentos
                segmentState = 0;
                break;
            default:
                segmentState = SEGMENT_3;
        }
    }

    public void setSegments(int mask) {
        segmentState = (byte) mask;
    }

    public void paintComponent(Graphics2D g2d){
        byte mask = 1;
        for(int n = 0; n < segments.length; n++){
            // preguntamos si el primer bit esta encendido
            if((segmentState & mask) != 0){
                // dibujamos las sombras
                if(segmentsShadows != null){
                    g2d.setColor(shadowColor);
                    g2d.fillPolygon(segmentsShadows[n]);
                }
                g2d.setColor(segmentColor);
            }else{
                g2d.setColor(disableSegmentColor);
            }
            g2d.fillPolygon(segments[n]);
            mask *= 2;
        }
    }
}
