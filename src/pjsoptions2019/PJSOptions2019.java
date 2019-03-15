/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pjsoptions2019;



import com.qaant.structures.Qunderlying;
import com.qaant.threadModels.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.qaant.threadModels.TGenericModel.arrayListDerivatives;

public class PJSOptions2019 {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
         /*
        JFrame mainWindow = new JFrame("Options Models")
        PanelOptions panel1= new PanelOptions();


        //Creating an Adding listenes

        ClickOnMainWindow clickListener1 = new ClickOnMainWindow();
        KeysOnMainWindow keysListener1  = new KeysOnMainWindow();
        FocusOnMainWindow focusListener1 = new FocusOnMainWindow();
        mainWindow.addMouseListener(clickListener1);
        mainWindow.addKeyListener(keysListener1);
        mainWindow.addFocusListener(focusListener1);


        panel1.setBackground(Color.BLACK);
        mainWindow.setContentPane(panel1);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (mainWindow.getWidth() > screenSize.width-100 || mainWindow.getHeight() > screenSize.height-100) {
            double scale1 = (double)(screenSize.width-100)/mainWindow.getWidth();
            double scale2 = (double)(screenSize.height-100)/mainWindow.getHeight();
            double scale = Math.min(scale1,scale2);
            mainWindow.setSize( (int)(scale*mainWindow.getWidth()), (int)(scale*mainWindow.getHeight()) );
        }
        mainWindow.setLocation( (screenSize.width - mainWindow.getWidth()) / 2,
                (screenSize.height -mainWindow.getHeight()) / 2 );
        mainWindow.requestFocusInWindow();
        mainWindow.setVisible(true);
    */








        // TODO code application logic here


        char contrato = 'F';
        char option = 'P';
        double undValue = 100;
        double X = 100;
        double days = 10;
        double vh30Und = 0.30;
        double riskFreeRate = .10;
        double divYield = 0;
        double mktValue = 3;
        int steps = 1000;


        Qunderlying someStock = new Qunderlying(contrato, undValue, vh30Und, divYield);


        //********************************** Estudio Threads
        ArrayList<TBinomialJR> arrayListOptions = new ArrayList<>();

        int processors = Runtime.getRuntime().availableProcessors();

        System.out.print("\nCantidad procesadores......: " + processors);

        long startTime = System.currentTimeMillis();

        TBinomialJR JRAC = new TBinomialJR('A', someStock, 'C', X, days, riskFreeRate, mktValue, steps);
        TBinomialJR JRAP = new TBinomialJR('A', someStock, 'P', X, days, riskFreeRate, mktValue, steps);
        TBinomialJR JREP = new TBinomialJR('E', someStock, 'P', X, days, riskFreeRate, mktValue, steps);


        //Los optionObject se mandan a un array list
        arrayListOptions.add(JRAC);
        arrayListOptions.add(JRAP);
        arrayListOptions.add(JREP);
        //arrayListOptions.add(opEFHeur);
        //arrayListOptions.add(opEFWeur);

        Thread[] worker = new Thread[arrayListOptions.size()];

        System.out.println("\nNumero elementos en Array: " + arrayListOptions.size());

        for (int i = 0; i < arrayListOptions.size(); i++) {
            worker[i] = new Thread(arrayListOptions.get(i));
            worker[i].start();
        }

        for (int i = 0; i < arrayListOptions.size(); i++) {
            try {
                worker[i].join();
            } catch (InterruptedException e) {
            }
        }

        for (int i = 0; i < arrayListOptions.size(); i++) {
            System.out.println("Binomial JR-Thread:" + Arrays.toString(arrayListOptions.get(i).getDerivativesArray()[0]));//+"Implied VLT.."+tOpt1.getImpliedVlt());

        }
        System.out.println("\nElapsed Time Total           :" + (System.currentTimeMillis() - startTime));


        //*****************************************************************************************
        // Estudio CRR
        startTime = System.currentTimeMillis();
        TBinomialCRR tOpt4 = new TBinomialCRR('A', someStock, 'C', X, days, riskFreeRate, mktValue, steps);
        TBinomialCRR tOpt5 = new TBinomialCRR('A', someStock, 'P', X, days, riskFreeRate, mktValue, steps);
        TBinomialCRR tOpt6 = new TBinomialCRR('E', someStock, 'P', X, days, riskFreeRate, mktValue, steps);

        Thread worker4 = new Thread(tOpt4);
        Thread worker5 = new Thread(tOpt5);
        Thread worker6 = new Thread(tOpt6);

        worker4.start();
        worker5.start();
        worker6.start();

        try {
            worker4.join();
            worker5.join();
            worker6.join();
        } catch (InterruptedException e) {
        }
        System.out.println("\nBinomial AMER CRR-Thread Call:" + Arrays.toString(tOpt4.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt1.getImpliedVlt());
        System.out.println("Binomial AMER CRR-Thread Put :" + Arrays.toString(tOpt5.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt2.getImpliedVlt());
        System.out.println("Binomial EUR  CRR-Thread Put :" + Arrays.toString(tOpt6.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt3.getImpliedVlt());

        System.out.println("\nElapsed Time Total           :" + (System.currentTimeMillis() - startTime));

        System.out.println("\nDays to Zero           :");
        tOpt4.setDaysToExpiration(0);
        tOpt4.run();
        System.out.println("\nDays to Zero Output:" + Arrays.toString(tOpt4.getDerivativesArray()[0]));


        //*****************************************************************************************
        // Estudio BS
        startTime = System.currentTimeMillis();
        TBlackScholes tOpt7 = new TBlackScholes(someStock, 'C', X, days, riskFreeRate, mktValue);
        TBlackScholes tOpt8 = new TBlackScholes(someStock, 'P', X, days, riskFreeRate, mktValue);

        Thread worker7 = new Thread(tOpt7);
        Thread worker8 = new Thread(tOpt8);

        worker7.start();
        worker8.start();

        try {
            worker7.join();
            worker8.join();
        } catch (InterruptedException e) {
        }

        System.out.println("\nBlack Scholes-Thread Call:" + Arrays.toString(tOpt7.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt1.getImpliedVlt());
        System.out.println("Black Scholes-Thread Put :" + Arrays.toString(tOpt8.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt2.getImpliedVlt());

        System.out.println("\nElapsed Time Total           :" + (System.currentTimeMillis() - startTime));

        //*****************************************************************************************
        // Estudio CV
        startTime = System.currentTimeMillis();

        TBinomialCV tOpt9 = new TBinomialCV(someStock, 'C', X, days, riskFreeRate, mktValue, steps);
        TBinomialCV tOpt10 = new TBinomialCV(someStock, 'P', X, days, riskFreeRate, mktValue, steps);

        Thread worker9 = new Thread(tOpt9);
        Thread worker10 = new Thread(tOpt10);

        worker9.start();
        worker10.start();


        try {
            worker9.join();
            worker10.join();
        } catch (InterruptedException e) {
        }

        System.out.println("\nBinomial CV Call:" + Arrays.toString(tOpt9.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt1.getImpliedVlt());
        System.out.println("Binomial CV Put :" + Arrays.toString(tOpt10.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt2.getImpliedVlt());

        System.out.println("\nElapsed Time Total           :" + (System.currentTimeMillis() - startTime));

        //***********************************************************
        // Estudio Whaley Thread
        startTime = System.currentTimeMillis();
        TWhaley tOptWC = new TWhaley(someStock, 'C', X, days, riskFreeRate, mktValue);
        TWhaley tOptWP = new TWhaley(someStock, 'P', X, days, riskFreeRate, mktValue);

        Thread worker11 = new Thread(tOptWC);
        Thread worker12 = new Thread(tOptWP);

        worker11.start();
        worker12.start();

        try {
            worker11.join();
            worker12.join();
        } catch (InterruptedException e) {
        }

        System.out.println("\nWhaley-Thread Call:" + Arrays.toString(tOptWC.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt1.getImpliedVlt());
        System.out.println("Whaley-Thread Put :" + Arrays.toString(tOptWP.getDerivativesArray()[0]));//+"Implied VLT.."+tOpt2.getImpliedVlt());
        System.out.println("\nElapsed Time Total           :" + (System.currentTimeMillis() - startTime));

        //***********************************

        JRAC.setOptionUndValue(undValue * .99);
        JRAC.run();


        System.out.println("CRR Caida 1%...:" + Arrays.toString(JRAC.getDerivativesArray()[0]));

        //***
        //El .run() podria incluirse en el method en TGenericModel, pero de esa forma no se maneja el Thread
        //Aqui se puede poner .run() o mandar a un thread
        JRAC.setOptionUndValue(undValue * 1.01);
        JRAC.run();

        System.out.println("CRR Suba 1%...:" + Arrays.toString(JRAC.getDerivativesArray()[0]));
        System.out.println("und value...:" + JRAC.getUnderlyingValue());

        //******* Tickets & Book *************************
        ArrayList<Tticket> book = new ArrayList<>();

        Tticket ticket = new Tticket(tOptWC, 1, 3.15, 1, 10);// option,+/-lots,price,multiplier,nodes
        Tticket ticket2 = new Tticket(tOptWC, 1, 3.00, 1, 10);
        book.add(ticket);
        book.add(ticket2);

        Thread[] ticketWorkers = new Thread[book.size()];

        for (int i = 0; i < book.size(); i++) {
            ticketWorkers[i] = new Thread(book.get(i));
            ticketWorkers[i].start();
        }

        for (int i = 0; i < book.size(); i++) {
            try {
                ticketWorkers[i].join();
            } catch (InterruptedException e) {
            }
        }

        System.out.println("Price Range....:" + Arrays.toString(ticket.getUnderlyingPriceRange()[0]));
        for (Tticket tticket : book) {
            System.out.println("PL Output.....:" + Arrays.toString(tticket.getPLOutput()[0]));
            System.out.println("IntelliJ version 3.0");
        }


        //**********************************************
        startTime = System.currentTimeMillis();
        arrayListOptions.clear();

        for (int i=0;i<10;i++) {
            TBinomialJR JRAP2=new TBinomialJR('A', someStock, 'P', X, days, riskFreeRate, mktValue, steps);
            arrayListOptions.add(JRAP2);
        }

        /*
        arrayListOptions.add(JRAP);
        arrayListOptions.add(JREP);
        arrayListOptions.add(tOpt4);
        arrayListOptions.add(tOpt5);
        arrayListOptions.add(tOpt6);

        for (TGenericModel arrayListOption : arrayListOptions) {
            arrayListOption.setOptionUndValue(101.0);
            arrayListOption.run();
        }
         */

        TaskConcurrentQueue tcQueue = new TaskConcurrentQueue();
        tcQueue.taskQueue.addAll(arrayListOptions);
        tcQueue.startQueue();
       //  tcQueue.endQueue();
        for (TGenericModel arrayListOption : arrayListOptions) {
            System.out.println("Queue-Thread:" + Arrays.toString(arrayListOption.getDerivativesArray()[0]));
        }

        /*
        for (double[] arrayDerivative: arrayListDerivatives) {
            System.out.println (Arrays.toString(arrayDerivative));
        }
        */

         System.out.println("\nElapsed Time Total           :" + (System.currentTimeMillis() - startTime));
    }//end Main


}//end Class
