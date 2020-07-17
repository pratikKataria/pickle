package com.pickleindia.pickle.interfaces;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Timeout extends TimerTask {
    int tol = 8000;
    private Timer timer = new Timer();

    public Timeout() {
        scheduleAtFixedRate();
    }

    public void schedule() {
        timer.schedule(this, tol);
    }

    public void scheduleAtFixedRate() {
        timer.scheduleAtFixedRate(this, 0, tol);
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    public void run() { }

}
