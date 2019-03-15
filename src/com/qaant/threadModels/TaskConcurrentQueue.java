package com.qaant.threadModels;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskConcurrentQueue {


     //   System.out.println("Estudio Concurrent Linked Queue....");
        public ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
        protected volatile int threadsCompleted; // how many threads have finished?
        private volatile boolean running = true;
        private WorkerThread[] workers;
        private int threadCount =20;
        private  int t=0;

        public TaskConcurrentQueue(){}

        private class WorkerThread extends Thread {

            public void run() {
                try {
                    while (running) {
                        Runnable task = taskQueue.poll(); // Get a task from the queue.

                        if (task == null)
                            break; // (because the queue is empty)
                         t++;
                         System.out.println("Running task: "+t );
                        task.run(); // Execute the task;

                    }
                } finally {
                    threadFinished(); // Records fact that this thread has terminated.
                    //System.out.println("thread Finished tq:"+taskQueue.size());
                }
            }
        }//end class WorkerThread


        public void startQueue() {

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
        public void endQueue(){

          //  running=false;
            for (int i = 0; i < threadCount; i++) {
                try{
                  //  if (workers[i].isAlive()) {
                        workers[i].join();
                  //  }
                } catch (InterruptedException e) {
                }
            }

        }

        synchronized private void threadFinished() {
        threadsCompleted++;
        if (threadsCompleted == workers.length) { // all threads have finished
           // startButton.setText("Start Again");
           // startButton.setEnabled(true);
            System.out.println("threads Finished");
            running = false; // Make sure running is false after the thread ends.
            workers = null;
           // threadCountSelect.setEnabled(true); // re-enable pop-up menu
        }
    }

}
