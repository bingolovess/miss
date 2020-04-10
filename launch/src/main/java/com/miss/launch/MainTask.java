package com.miss.launch;

public abstract class MainTask extends Task {

    @Override
    public boolean runOnMainThread() {
        return true;
    }

}
