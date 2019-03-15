/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qaant.threadModels;

import com.qaant.optionModels.QImpliedVolCalc;
import com.qaant.Qinterfaces.QOptionable;
import com.qaant.structures.Qoption;
import com.qaant.structures.Qunderlying;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author pauli
 */
public abstract class
TGenericModel extends Qoption implements QOptionable,Runnable{
    public static final ArrayList<double[]> arrayListDerivatives = new ArrayList<>();

    protected char tipoEjercicio;
    enum TipoEjercicio {AMERICAN,EUROPEAN}
            
    final static char EUROPEAN='E';
    final static char AMERICAN='A';
    long startTime, elapsedTime;
    double dayYear, sqrDayYear,z,underlyingNPV, interv;
    double prima=-2,delta=-2,gamma=-2,vega=-2,theta=-2,rho=-2,impliedVol=0;
    int cpFlag, modelNumber;
    boolean opcionConVida;
    private String pModelName;
    private double[][] derivativesArray = new double[1][10];

    public static final HashMap<Integer, String> modelMap =new HashMap<>();
    

    //Constructor 0:
    TGenericModel() {build();}
    
    //Constructor 1:
    TGenericModel(Qunderlying und, char callPut, double strike, double daysToExpiration, double rate, double optionMktValue){
        super(und, callPut, strike,daysToExpiration,rate, optionMktValue);
        build();
    }
    
    //Constructor 2:
    TGenericModel(char tipoContrato, double underlyingValue, double underlyingHistVolatility, double dividendRate, char callPut, double strike, double daysToExpiration, double rate, double optionMktValue){
        super(tipoContrato, underlyingValue, underlyingHistVolatility, dividendRate,callPut, strike,daysToExpiration,rate, optionMktValue);
        build();
    }
     
    //Constructor 3:
    TGenericModel(char tipoEjercicio, char tipoContrato, double underlyingValue, double underlyingHistVolatility, double dividendRate, char callPut, double strike, double daysToExpiration, double rate, double optionMktValue, int steps){
        super(tipoContrato, underlyingValue, underlyingHistVolatility, dividendRate,callPut, strike,daysToExpiration,rate, optionMktValue,steps);
        this.tipoEjercicio              =tipoEjercicio;
        build();
    }
    
    
    //Constructor 4:
    TGenericModel(char tipoEjercicio, Qunderlying und, char callPut, double strike, double daysToExpiration, double rate, double optionMktValue, int steps){
        super(und, callPut, strike,daysToExpiration,rate, optionMktValue,steps);
        this.tipoEjercicio              =tipoEjercicio;
        build();
    }
    
    
    private void build(){
        //startTime=System.currentTimeMillis();  
        
        this.dayYear              =daysToExpiration/365;
        this.sqrDayYear           =Math.sqrt(dayYear);
        this.cpFlag               =(callPut==CALL)?1:-1;
        this.interv               =dayYear/steps;
        this.opcionConVida        =daysToExpiration>0;
        this.z                    =Math.exp(-rate*dayYear/steps);
        this.underlyingNPV        =underlyingValue*Math.exp(-dividendRate*dayYear); 
      //  this.cpFlag               =(callPut==CALL)?1:-1;
        
       
    }// end of build()
    @Override
    abstract public void run(); //Cada modelo implementa runModel()
    


//Setters
    public void setDaysToExpiration(double days){
        this.daysToExpiration=days;
        build();
    }
    public void setRiskFreeRate(double rate){
        this.rate=rate;
        build();
        
    }
    public void setOptionMktValue(double mktValue){
        this.optionMktValue=mktValue;
        //build();
    }
    public void setOptionType(char opt){
        this.callPut=opt;
        build();
    }
    public void setVolatModel(double vlt){
        this.volatModel=vlt;
        build();
    }
    public void setOptionUndValue(double optUndValue){
        this.underlyingValue=optUndValue;
        build();
        
    }
    
    
    //Getters
    int getModelSteps(){
        return steps;
    }
    protected double getIntrinsicValue(){
        
        return Math.max((underlyingValue - strike)*cpFlag,0);
    }
    public double getTimeValue(){
        return optionMktValue-getIntrinsicValue();
    } 
    public double getImpliedVlt(){
        return derivativesArray[0][7];
    }
    public double[][] getDerivativesArray(){return derivativesArray;}
    public String getOptionString(){
        // builder.append("Ticker-");
        //builder.append(anUnderlying.getTicker());
        return String.valueOf(modelNumber) +
                "-" +
                pModelName +
                "/Option->" +
                callPut +
                "/strike->" +
                strike +
                "/prima->" +
                prima +
                "/delta->" +
                delta +
                "/gamma->" +
                gamma +
                "/vega->" +
                vega +
                "/theta->" +
                theta +
                "/rho->" +
                rho +
                "/optionMktValue->" +
                optionMktValue +
                "/impVlt->" +
                volatModel +
                "z";
    }//end getString
    @Override
    public String getModelName(){return pModelName;}
    @Override
    public double getPrima(){return prima;}
    @Override
    public double getDelta(){return delta;}
    @Override
    public double getGamma(){return gamma;}
    @Override
    public double getVega() {return vega;}
    @Override
    public double getTheta(){return theta;}
    @Override
    public double getRho()  {return rho;}
    
    public double getOptionMktValue(){return optionMktValue;}
    public char getTipoEjercicio(){return tipoEjercicio;}
    public char getCallPut(){return callPut;}
    public double getStrike(){return strike;}
  
    public double getTasa(){return rate;}
    public double getValueToFind(int i){
     
        if (i>9){i=0;}
        return derivativesArray[0][i];
    }  
    
    public double getPayoff(){
        return payoff(underlyingValue,strike,cpFlag);
        }
    
    protected double calcImpliedVlt(){
    impliedVol=volatModel;
            
        if(optionMktValue>0 && opcionConVida && strike!=0){
            double volMin;
            double volMax;
        
        if(prima<=optionMktValue){
           volMin=volatModel;
           volMax=volMin*3;
            }else{
                volMin=volatModel/3;
                volMax=volatModel;
            }
        //definicion de funcion para mandar a algo de impVlt (la dif entre valor mercado y valor teorico, buscamos que sea cero)      
        DoubleUnaryOperator difFunc = xVlt-> optionMktValue - modelGetPrima(xVlt);
        //Para calculos de implied vol
        int MAXITERATIONS = 50;
        double ACCURACY = 0.00009;

        // impliedVol= QImpliedVolCalc.bisection(difFunc, volMin, volMax, MAXITERATIONS, ACCURACY);
        // impliedVol= QImpliedVolCalc.ivNewton(difFunc, volatModel, vega, MAXITERATIONS,  ACCURACY);
        impliedVol= QImpliedVolCalc.turboNewton(difFunc, volatModel, vega, MAXITERATIONS, ACCURACY);
              
        }
        //System.out.println("Implied Vol   :"+impliedVol);
    return impliedVol;
    }
   
   abstract protected double modelGetPrima(double volForLambda); // cada modelo da su funcion
    
       
    public void opcionSinVida(){
        delta=cpFlag;  
        gamma=vega=theta=rho=0;
        prima = payoff(underlyingValue,strike,cpFlag);
      
    }
    
    protected double payoff(double underlyingValue, double strike, int cpFlag){
        return Math.max((underlyingValue - strike) * cpFlag, 0);
        }
    
    @Override
    public void fillDerivativesArray(){
        derivativesArray[0][0]=prima;
        derivativesArray[0][1]=delta;
        derivativesArray[0][2]=gamma;
        derivativesArray[0][3]=vega;
        derivativesArray[0][4]=theta;
        derivativesArray[0][5]=rho;
        derivativesArray[0][6]=optionMktValue;
        derivativesArray[0][7]=impliedVol;
        derivativesArray[0][8]=System.currentTimeMillis() - startTime;
        derivativesArray[0][9]=modelNumber;
      //  System.out.println("Derivatives Array:" + Arrays.toString(derivativesArray[0]));
      //  arrayListDerivatives.add(derivativesArray[0]);
    }
}
