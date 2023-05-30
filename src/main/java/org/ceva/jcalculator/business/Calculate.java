package org.ceva.jcalculator.business;

import java.util.Locale;

public class Calculate {
    private StringBuilder edValue;
    private StringBuilder lastValue;
    private double res;
    private boolean errorFlag = false;
    private char op;

    public Calculate(StringBuilder edValue, StringBuilder lastValue, char op){
        this.edValue = edValue;
        this.lastValue = lastValue;
        this.res = 0;
        this.op = op;
    }

    public double perform(){
        try{
            double d0 = Double.parseDouble(lastValue.toString());
            double d1 = Double.parseDouble(edValue.toString());

            switch (op){
                case '+':
                    res = d0 + d1;
                    break;
                case '-':
                    res = d0 - d1;
                    break;
                case '*':
                    res = d0 * d1;
                    break;
                case '/':
                    res = d0 / d1;
                    if((Double.isNaN(res)) || (Double.isInfinite(res))){
                        errorFlag = true;
                    }
                    else if(Math.abs(res) < Double.MIN_NORMAL){
                        res = 0;
                    }
                    break;
                default:
                    res = 0;
            }
        }
        catch(NumberFormatException e){
            System.out.println(e.getClass().getName() + " generated: " + e.getMessage());
        }
        return res;
    }
}
