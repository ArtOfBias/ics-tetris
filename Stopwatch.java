public class Stopwatch {
    // TODO lapping?
    private double startTime;
    private double totalTime = 0;
    private boolean running = false;

    public void start(){
        if (!running){
            startTime = System.currentTimeMillis();
            running = true;
        }
        else {
            throw new IllegalStateException("Stopwatch already running");
        }
    }

    public void pause(){
        if (running){
            totalTime = elapsed();
            running = false;
        }
        else {
            throw new IllegalStateException("Stopwatch already paused");
        }
    }

    public void reset(){
        totalTime = 0;
        running = false;
    }

    public void restart(){
        reset();
        start();
    }

    public double elapsed(){
        if (running){
            return totalTime + System.currentTimeMillis() - startTime;
        }
        else return totalTime;
    }

    public boolean isRunning(){
        return running;
    }
}