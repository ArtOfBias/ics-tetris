// class Stopwatch
// runs in the background
// used to time the drops for the blocks (this class is used in GamePanel for actual functionality)
// also used to repeat key presses

package src;

public class Stopwatch {
    private double startTime;
    private double totalTime = 0;
    private boolean running = false;

    // method start
    // starts the stopwatch (notes the start time)
    public void start(){
        if (!running){
            startTime = System.currentTimeMillis();
            running = true;
        }
        else {
            throw new IllegalStateException("Stopwatch already running");
        }
    }

    // method pause()
    // pauses the stopwatch (notes the amount of time elapsed)
    public void pause(){
        if (running){
            totalTime = elapsed();
            running = false;
        }
        else {
            throw new IllegalStateException("Stopwatch already paused");
        }
    }

    // method reset()
    // resets the stopwatch (resets the time to 0)
    public void reset(){
        totalTime = 0;
        running = false;
    }

    // method restart()
    // resets the stopwatch and then starts it
    public void restart(){
        reset();
        start();
    }

    // method elapsed()
    // returns the time that has elapsed
    public double elapsed(){
        if (running){
            return totalTime + System.currentTimeMillis() - startTime;
        }
        else return totalTime;
    }

    // method isRunning
    // returns whether the stopwatch is running or not
    public boolean isRunning(){
        return running;
    }
}