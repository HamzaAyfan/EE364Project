package com.ee364project;

import java.util.ArrayList;

public class CallInfo {
    public ArrayList<Call> history = new ArrayList<>();
    private Call call = null;

    public void called(Call call) {
        this.history.add(call);
        this.call = call;
    }

    public boolean isInCall() {
        if (call == null) {
            return false;
        }
        return (call.getState() == Call.CallState.INCALL)
                || (call.getState() == Call.CallState.WAITING);
    }

    public Call getLastCall() {
        return this.call;
    }

    public long averageWaitTime() {
        long sum = 0;
        long n = 0;
        for (Call call : this.history) {
            sum += call.getWaitTime();
            n++;
        }
        if (n != 0) {
            return sum / n;
        } else {
            return -1;
        }
    }
}
