/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qaant.structures;

import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Paulino
 */
public class Qunderlying {
   
    enum tipoContrato {STOCK,FUTURES}
    protected final static char STOCK='S';
    protected final static char FUTURES='F';
    
    protected char  tipoContrato; //'S': Stock 'F':Futuro
    private String ticker,ISIN;
    protected String underlyingName;
    protected double underlyingValue;
    protected double underlyingHistVolatility;
    protected double dividendRate;
    
    protected int nodes=1;
    protected double[][] undPriceRange;//   = new double [1][step+1];
    private double Dstd=3;


    public Qunderlying(){buildPriceRange();}   
    public Qunderlying(@NotNull Qunderlying und){
        this.tipoContrato               =und.tipoContrato;
        this.underlyingValue            =und.underlyingValue;
        this.underlyingHistVolatility   =und.underlyingHistVolatility;
        this.dividendRate               =und.dividendRate;
        this.ticker                     =und.ticker;
        buildPriceRange();
    }
    public Qunderlying(char tipoContrato, double underlyingValue,double underlyingHistVolatility,double dividendRate)
    {
        this.tipoContrato               =tipoContrato;
        this.underlyingValue            =underlyingValue;
        this.underlyingHistVolatility   =underlyingHistVolatility;
        this.dividendRate               =dividendRate;
        ticker                          ="TICKER";
        buildPriceRange();
    }
    public Qunderlying(char tipoContrato, double underlyingValue,double underlyingHistVolatility,double dividendRate, int nodes)
    {
        this.tipoContrato               =tipoContrato;
        this.underlyingValue            =underlyingValue;
        this.underlyingHistVolatility   =underlyingHistVolatility;
        this.dividendRate               =dividendRate;
        this.nodes                      =nodes;
        ticker                          ="TICKER";
        
        buildPriceRange();
    }
    
        
    public Qunderlying(String ticker,char tipoContrato, double underlyingValue,double underlyingHistVolatility,double dividendRate){
        this.ticker                     =ticker;
        this.tipoContrato               =tipoContrato;
        this.underlyingValue            =underlyingValue;
        this.underlyingHistVolatility   =underlyingHistVolatility;
        this.dividendRate               =dividendRate;
        buildPriceRange();
    }
    private void buildPriceRange(){
        undPriceRange   = new double [1][nodes+1];

        double center = this.underlyingValue;
        double daysToProject = 30;
        double coeficiente = Math.sqrt(daysToProject / 365.0) * this.underlyingHistVolatility;
        double min = center * Math.exp(coeficiente * -Dstd);
        double max = center * Math.exp(coeficiente * Dstd);
        double ratioLog = Math.exp(Math.log(max / min) / nodes);
        for (int i=0;i<nodes+1;i++){undPriceRange[0][i]= min *Math.pow(ratioLog,i);}
    }
    
    public double[][] getUnderlyingPriceRange(){
        return undPriceRange;
    }
    
    
    //getters
    public char getTipoContrato(){return tipoContrato;}
    public double getUnderlyingValue(){return underlyingValue;}
    public double getUnderlyingHistVlt(){return underlyingHistVolatility;}
    public double getDividendRate(){return dividendRate;}
    public String getTicker(){return ticker;}
    
    //setters
    public void setTipoContrato(char TipoContrato){this.tipoContrato=TipoContrato;}
    public void setUnderlyingValue(double UnderlyingValue){underlyingValue=UnderlyingValue;}
    public void setUnderlyingHistVlt(double Volatility){underlyingHistVolatility=Volatility;}
    public void setDividendRate(double DividendRate){this.dividendRate=DividendRate;}
    public void setTicker(String ticker){this.ticker =ticker;}
    public void setDstd(double x){
        this.Dstd=x;
        buildPriceRange();
    }
    public void setNumberOfNodes(int nodes){
        this.nodes=nodes;
        buildPriceRange();
    }
    
    public String getUnderlyingString(){
        return "Ticker " +
                ticker +
                " Tipo Contrato " +
                tipoContrato +
                " Value: " +
                underlyingValue +
                " Historical Vlt: " +
                underlyingHistVolatility +
                " Dividend Rate: " +
                dividendRate +
                "-";
    }
}
