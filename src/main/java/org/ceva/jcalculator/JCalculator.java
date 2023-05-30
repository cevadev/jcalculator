package org.ceva.jcalculator;

import org.ceva.jcalculator.api.KeyListener;
import org.ceva.jcalculator.business.Calculate;
import org.ceva.jcalculator.components.CalcMainPanel;
import org.ceva.jcalculator.components.KeyboardPanel;
import org.ceva.jcalculator.components.LCDPanel;
import org.ceva.jcalculator.util.CalcStandardLookAndFeel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class JCalculator implements KeyListener {
    private JFrame mainFrame;
    private int numDigits = 9;
    private LCDPanel screen;
    private KeyboardPanel keyPad;
    // almacenamos el valor de la tecla presionada
    private StringBuilder edValue;
    // almacenamos la operacion matematica a realizar (+,-,*,/)
    private char op;

    /**
     * Si se ingresa 5 + 3
     * 5 pasa a edValue
     * + pasa a op
     * 5 pasa a lastValue
     * 3 pasa a edValue
     */
    private StringBuilder lastValue;

    public JCalculator(){
        edValue = new StringBuilder();
        lastValue = new StringBuilder();
    }

    private void start(){
        javax.swing.SwingUtilities.invokeLater(()->{
            mainFrame = new JFrame("Calculator 8");
            // quitamos el titulo de ventana
            mainFrame.setUndecorated(true);
            Dimension minSize = new Dimension(300, (int)(300*1.6));
            mainFrame.setMinimumSize(minSize);
            mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            CalcStandardLookAndFeel lookAndFeel= new CalcStandardLookAndFeel();
            mainFrame.setBackground(lookAndFeel.getKeyboardBackgroundColor());
            // aplicamos el efecto 3D, empieza en oscuro y termina en claro
            CalcMainPanel globalPanel = new CalcMainPanel(lookAndFeel, this);
            // JPanel globalPanel = new JPanel();
            globalPanel.setBackground(lookAndFeel.getKeyboardBackgroundColor());
            LayoutManager layout = new BoxLayout(globalPanel, BoxLayout.PAGE_AXIS);
            globalPanel.setLayout(layout);

            // listener para movimiento del raton en la ventana
            MouseAdapter mouseAdapter = new MouseAdapter() {
                Point pt = new Point();
                Dimension size = new Dimension(); // tamano de la ventana
                // if true == move
                // if false == resize
                boolean moveOrResize;
                @Override
                public void mousePressed(MouseEvent e) {
                    // determinamos en que punto se presiono el raton
                    pt.x = e.getX();
                    pt.y = e.getY();

                    // si presionamos el raton en la iz inferior derecha
                    // se interpreta como queriendo modificar la ventana
                    size.setSize(mainFrame.getSize()); // pasamos tamano del JFrame
                    /**
                     * si el mouse se encuentra mas alla de los 40px y
                     * la coordenada y es mas grande que el alto de la ventana menos 40px
                     * entonces eso significa que se trata de un resize
                     * por otro lado
                     * Si es el punto esta fuera del rango de los 40px entonces
                     * significa que es un move pero si esta dentro del rango es un resize
                     */
                    moveOrResize = !((pt.x >= (size.width - 40)) && (pt.y >= (size.height - 40)));
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if(moveOrResize){
                        // el usuario mueve el raton mientras el boton se mantiene presionado
                        mainFrame.setLocation(e.getXOnScreen() - pt.x, e.getYOnScreen() - pt.y);
                    }
                    // cambiamos el tamano de la ventana
                    else{
                        mainFrame.setSize(
                                size.width + e.getX()-pt.x, size.height + e.getY() - pt.y
                        );
                    }
                }
            };
            globalPanel.addMouseListener(mouseAdapter);
            globalPanel.addMouseMotionListener(mouseAdapter);

            /**
             * Aplicamos al JFrame la forma (shape) rectangular con esquinas redondeadas
             * aplicamos esta forma en el evento que se produce cuando se cambia el
             * tamano de la ventana
             */
            globalPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // e.getComponent() nos dice cual componente se esta cambiando el tamano
                    Rectangle bounds = e.getComponent().getBounds();
                    screen.setPreferredSize(new Dimension(bounds.width, bounds.height/5));
                    keyPad.setPreferredSize(new Dimension(bounds.width, bounds.height*4/5));
                    // hacemos un re-layout
                    e.getComponent().validate();

                    // definimos nuestro shape
                    RoundRectangle2D rr2d = new RoundRectangle2D.Double(0,0, bounds.width, bounds.height, 24, 24);
                    mainFrame.setShape(rr2d);
                }
            });

            // cremos los componentes para agregarlos
            screen = new LCDPanel(numDigits, lookAndFeel);
            screen.setPreferredSize(new Dimension(minSize.width, minSize.height/5));
            keyPad = new KeyboardPanel(this, lookAndFeel);
            keyPad.setPreferredSize(new Dimension(minSize.width, minSize.height*4/5));

            globalPanel.setBorder(BorderFactory.createEmptyBorder(60,20,20,20));
            globalPanel.add(screen);
            globalPanel.add(keyPad);

            mainFrame.setContentPane(globalPanel);
            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            mainFrame.setVisible(true);

            // para que los eventos del teclado sean recibidos hay que indicarle a AWT
            // que el componente sera quien reciba los eventos con el metodo setRequestFocus()
            globalPanel.requestFocus();
        });
    }
    public static void main(String[] args) {
        new JCalculator().start();
    }

    private void compute(){
        try{
            boolean errorFlag = false;
            double res;
            Calculate calculate = new Calculate(edValue, lastValue, op);
            res = calculate.perform();
            // convertimos el resultado a String para presentarlo en la pantalla
            String strRes;
            // validaos q no haya errores
            if(!errorFlag){
                // si no hay errores, formateamos el resultado (res) hasta 9 decimales
                strRes = String.format(Locale.ROOT,"%.9f", res);
            }
            else{
                strRes = "";
            }
            // validamos caso de los ceros a la derecha despues del punto decimal
            int extra = strRes.length()-1; // obtenemos la posicion del ultimo caracter en strRes
            // mientras q el ultimo caracter es cero, decrementamos extra
            while((extra > 0) && (strRes.charAt(extra) == '0')){
                extra--;
            }
            // eliminamos los cero a la derecha despues del punto decimal
            if(extra < strRes.length() - 1){
                strRes = strRes.substring(0, extra+1);
            }

            // validamos el caso del punto decimal
            int inxDot = strRes.indexOf('.');
            if(inxDot == -1)
                inxDot = strRes.indexOf(',');
            int strResLen = strRes.length();
            if(inxDot >= 0){
                // si hay un indice, el punto decimal no lo tomamos para dibujar los digitos
                strResLen--;
            }
            // validamos si no nos excedemos del numero valido de digitos
            if(strResLen > numDigits){
                // la parte fraccionaria se trunca hasta la cantidad de numero disponble
                // si la cadena tiene un punto, tomamos un digito mas
                // tomamos todos los caracteres que se encuentren hasta el num de digitos disponibles
                strRes = strRes.substring(0, numDigits + (inxDot >= 0 ? 1 : 0));
                // 1234567890.123 la cadena es mayor a 9 digitos
                // tomamos los primero nueve es decir, 123456789
                // validamos si inxDot es mayor a la longitud del string que contiene 123456789
                // significa que truncamos el punto decimal
                if(inxDot > strRes.length()){
                    // se trunco el punto decimal
                    errorFlag = true;
                    strRes = "";
                }
            }
            if(errorFlag){
                screen.displayError();
            }
            // limpiamos las variables
            lastValue.setLength(0);
            edValue.setLength(0);
            edValue.append(strRes);
            op = 0;
        }
        catch(NumberFormatException e){
            System.out.println(e.getClass().getName() + " generated: " + e.getMessage());
        }
    }

    /**
     * Metodo que maneja la logica de la calculadora
     * Al presionar una tecla, almacenamos el valor de la tecla en una variable
     * @param code
     */
    @Override
    public void keyPressed(char code) {
        // C = Clear / CA = Clear All
        if(code == 'C'){
            if(edValue.length() > 0){
                // clear
                edValue.setLength(0);
            }
            // clear all
            else if(lastValue.length() > 0){
                lastValue.setLength(0);
                op = 0;
            }else{
                op = 0;
            }
            screen.setValue(0);
        }
        // caso OFF apagado
        else if(code == 'O'){
            // timer para apreciar al animacion al momento de cerra la calculadora
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mainFrame.dispose();
                }
            }, 400);
        }
        // caso si activamos la pantalla de iluminacion
        else if(code == 'L'){
            screen.setLight();
        }
        // validamos las operaciones basicas
        else if((code == '+') || (code == '-') || (code == '*') || (code == '/')){
            /**
             * en las calculadoras cuando escribimos 1 + 2 + automaticamente hace la suma y da 3
             * por lo que un signo + se puede leer como un realiza la operacion pendiente
             */
            if(op != 0){
                compute();
                if(edValue.length() > 0){
                    screen.setValue(edValue);
                }
            }
            // en el caso que comenzamos escribiendo el signo negativo
            else if((code == '-') && (lastValue.length() == 0) && (edValue.length() == 0)){
                edValue.append(code);
                return;
            }
            op =  code;
            // limpiamos lastValue
            lastValue.setLength(0);
            lastValue.append(edValue);
            edValue.setLength(0);
        }
        // validamos si se presiona el signo =
        else if(code == '='){
            // validamos en caso que no se ingreso numeros pero se presiona =
            if((op == 0) || (lastValue.length() == 0) || (edValue.length() == 0)){
                // no hacemos nada
                return;
            }
            compute();
            if(edValue.length() > 0){
                screen.setValue(edValue);
            }
        }
        // casos digitos o puntos decimal
        else{
            // donde se encuentra el punto decimal
            int decimalIndex = edValue.indexOf(".");
            if(decimalIndex == -1)
                decimalIndex = edValue.indexOf(",");
            // longitud de edValue dependiendo si hay o no un punto decimal
            int edValueLen = edValue.length() - (decimalIndex >= 0 ? 1 : 0);
            // validamos que la longitud de edValue sea menos al numero de digitos permitidos
            if(edValueLen < numDigits){
                // validamos el caso donde se coloca dos veces punto
                if((code == '.') || (code == ',')){
                    // validamos si ya existe un punto decimal
                    if(decimalIndex >= 0){
                        return;
                    }
                    // si se presiona primero la tecla . edValue.lenght == 0, se debe anteponer un 0
                    if(edValue.length() == 0){
                        edValue.append("0");
                    }
                }
                edValue.append(code);
                screen.setValue(edValue);
            }
        }
    }
}