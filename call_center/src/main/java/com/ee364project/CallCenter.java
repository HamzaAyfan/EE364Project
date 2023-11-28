package com.ee364project;

import java.util.HashMap;
import java.util.LinkedList;

import com.ee364project.helpers.Utilities;

public class CallCenter implements Simulated {
    // TODO: operating times
    private HashMap<Agent, Boolean> agentAvailability = new HashMap<>();
    private LinkedList<Agent> availableAgents;
    private int availableAgentsCount;

    public CallCenter(Agent[] agents) {
        this.availableAgentsCount = agents.length;
        this.availableAgents = new LinkedList<>();
        for (int i = 0; i < agents.length; i++) {
            this.availableAgents.add(agents[i]);
            this.agentAvailability.put(agents[i], true);
        }
    }

    public void releaseAgent(Agent agent) {
        availableAgents.add(agent);
        agentAvailability.put(agent, true);
        availableAgentsCount++;
    }

    public Agent assignAgent() {
        Agent agent = availableAgents.poll();
        availableAgentsCount--;
        agentAvailability.put(agent, false);
        return agent;
    }

    @Override
    public String toString() {
        return "CallCenter()";
    }

    private void idle(String msg) {
        Utilities.log(this, "idles", "", msg);
    }

    @Override
    public void step() {
        if (availableAgentsCount > 0) {
            Call call = Call.getACall();
            if (call == null) {
                // agents avaialable but no calls...
                idle("no calls");
            } else {
                // agents available and there is a call...
                call.setReciever(assignAgent());
                call.getReceiver().callInfo.called(call);
                call.startCall(this);
                Utilities.log(call.getReceiver(), "joined", call, null);
            }
        } else {
            // no agents available...
            idle("no agents free");
        }
    }
}
