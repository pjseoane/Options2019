package com.qaant.threadModels;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.qaant.threadModels.TGenericModel.arrayListDerivatives;

public class TaskConcurrentQueue {


     //   System.out.println("Estudio Concurrent Linked Queue....");
        public ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
        protected volatile int threadsCompleted; // how many threads have finished?
        private volatile boolean running = true;
        private WorkerThread[] workers;
        private int threadCount =20;
        private  int t=0;
        private long startTime;

        public TaskConcurrentQueue(){}

        private class WorkerThread extends Thread {

            public void run() {
                try {
                    while (running) {
                        Runnable task = taskQueue.poll(); // Get a task from the queue.

                        if (task == null)
                            break; // (because the queue is empty)
                         t++;
                        // System.out.println("Running task: "+t );
                        task.run(); // Execute the task;

                    }
                } finally {
                    threadFinished(); // Records fact that this thread has terminated.
                    //System.out.println("thread Finished tq:"+taskQueue.size());
                }
            }
        }//end class WorkerThread


        public void startQueueWorkers() {
            startTime = System.currentTimeMillis();
            arrayListDerivatives.clear();
            workers = new WorkerThread[threadCount];
            running = true;
            threadsCompleted = 0;  // Records how many of the threads have terminated.

            for (int i = 0; i < threadCount; i++) {
                workers[i] = new WorkerThread();
                try {
                    workers[i].setPriority(Thread.currentThread().getPriority() - 1);
                } catch (Exception e) {

                }
                workers[i].start();

            }


        }


        synchronized private void threadFinished() {
        threadsCompleted++;
        if (threadsCompleted == workers.length) { // all threads have finished
           // startButton.setText("Start Again");
           // startButton.setEnabled(true);
            System.out.println("Threads Finished");
            running = false; // Make sure running is false after the thread ends.
            workers = null;

            for (double[] arrayDerivative: arrayListDerivatives) {
            System.out.println ("Derivatives Array in task Q: "+ Arrays.toString(arrayDerivative));

            }
            System.out.println ("\nElapsed Time Total       :" + (System.currentTimeMillis() - startTime));

           // threadCountSelect.setEnabled(true); // re-enable pop-up menu
        }
    }

}
