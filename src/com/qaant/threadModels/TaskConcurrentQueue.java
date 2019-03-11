package com.qaant.threadModels;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskConcurrentQueue {


     //   System.out.println("Estudio Concurrent Linked Queue....");
        public ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();
        private volatile int threadsCompleted; // how many threads have finished?
        private volatile boolean running = true;
        private WorkerThread[] workers;

        public TaskConcurrentQueue(){}

        private class WorkerThread extends Thread {
            public void run() {
                try {
                    while (running) {
                        Runnable task = taskQueue.poll(); // Get a task from the queue.
                        if (task == null)
                            break; // (because the queue is empty)
                        task.run(); // Execute the task;
                    }
                } finally {
                    threadFinished(); // Records fact that this thread has terminated.
                }
            }

        }//end class WorkerThread


        public void startQueue() {

            int threadCount =20;
            workers = new WorkerThread[threadCount];
            running = true;
            for (int i = 0; i < threadCount; i++) {
                workers[i] = new WorkerThread();
                try {
                    workers[i].setPriority(Thread.currentThread().getPriority() - 1);
                } catch (Exception e) {

                }
                workers[i].start();

            }
        }
        public void stop(){
            running=false;
        }

        synchronized private void threadFinished() {
        threadsCompleted++;
        if (threadsCompleted == workers.length) { // all threads have finished
           // startButton.setText("Start Again");
           // startButton.setEnabled(true);
            running = false; // Make sure running is false after the thread ends.
            workers = null;
           // threadCountSelect.setEnabled(true); // re-enable pop-up menu
        }
    }

}
