package com.ee364project;

import java.util.HashMap;
import java.util.LinkedList;

import com.ee364project.helpers.Utilities;

public class CallCenter implements Simulated {
    
    private HashMap<Agent, Boolean> agentAvailability = new HashMap<>();
    private LinkedList<Agent> availableAgents;
    // private int availableAgentsCount;

    public CallCenter(Agent[] agents) {
        // this.availableAgentsCount = agents.length;
        this.availableAgents = new LinkedList<>();
        for (int i = 0; i < agents.length; i++) {
            this.availableAgents.add(agents[i]);
            this.agentAvailability.put(agents[i], true);
        }
    }

    public int getAvailableAgentsCount() {
        return availableAgents.size();
    }

    public Agent[] getAvailableAgents() {
        return this.availableAgents.toArray(new Agent[getAvailableAgentsCount()]);
    }

    public Agent[] getUnavailableAgents() {
        Agent[] availableAgents = new Agent[getUnavailableAgentsCount()];
        int i = 0;
        for (Agent agent : agentAvailability.keySet()) {
            if (agentAvailability.get(agent)) {
                availableAgents[i++] = agent;
            }
        }
        return availableAgents;
    }

    public int getUnavailableAgentsCount() {
        return agentAvailability.size() - availableAgents.size();
    }

    public void releaseAgent(Agent agent) {
        availableAgents.add(agent);
        agentAvailability.put(agent, true);
        // availableAgentsCount++;
    }

    // usless in phase 2
    public Agent assignAgent() {
        Agent agent = availableAgents.poll();
        // availableAgentsCount--;
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

    private Agent getBestAgent(Call call) {
        Agent selectedAgent = null;
        Department dep = call.getCaller().problemState.getLastProblem().getDepartment();
        for (Agent agent: availableAgents) {
            if (agent.getDepartment().getName() == dep.getName()) {
                selectedAgent = agent;
                break;
            }
        }
        if (selectedAgent == null) {
            selectedAgent = availableAgents.poll();
        } else {
            availableAgents.remove(selectedAgent);
        }
        agentAvailability.put(selectedAgent, false);
        return selectedAgent;
    }

    @Override
    public void step() {
        if (getAvailableAgentsCount() > 0) {
            Call call = Call.getACall();
            if (call == null) {
                // agents avaialable but no calls...
                idle("no calls");
            } else {
                // agents available and there is a call...
                call.setReciever(getBestAgent(call));
                // call.getReceiver().callInfo.newCall(call);
                call.connectCall(this);
                
                Utilities.log(call.getReceiver(), "joined", call, null);
            }
        } else {
            // no agents available...
            idle("no agents free");
        }
    }
}
