package com.ee364project;

import java.util.HashMap;
import java.util.LinkedList;
import com.ee364project.Fx.MainSceneController;

/**
 * Represents a simulated call center with agents, call handling, and availability tracking.
 * <p>
 * This class models the behavior of a call center, managing agents, call assignments, and availability.
 * It is part of a simulation and implements the {@link Simulated} interface for time-based steps.
 * </p>
 *
 * <b>Fields:</b>
 * <ul>
 *     <li>{@code HashMap<Agent, Boolean> agentAvailability}: A mapping of agents to their availability status.
 *     <li>{@code LinkedList<Agent> availableAgents}: A linked list containing available agents.
 *     <li>{@code MainSceneController msc}: The main scene controller for the call center simulation.
 *     <li>{@code static CallCenter callCenter}: The static instance of the {@code CallCenter}.
 * </ul>
 * 
 *
 * @author Team 2
 * @version 1.0
 * @since 2023-12-21
 * */
public class CallCenter implements Simulated {    
    private HashMap<Agent, Boolean> agentAvailability = new HashMap<>();
    private LinkedList<Agent> availableAgents;
    private MainSceneController msc = new MainSceneController();
    private static CallCenter callCenter;

/**
* Constructs a CallCenter object with the given array of agents. Initializes the list
* of available agents and sets their initial availability status to true. The constructed
* CallCenter instance is then assigned to the static callCenter field for global access.
*
*  @param agents An array of Agent objects representing the agents available in the call center.
*/
    public CallCenter(Agent[] agents) {
        this.availableAgents = new LinkedList<>();
        for (int i = 0; i < agents.length; i++) {
            Agent agent = agents[i];
            this.availableAgents.add(agent);
            this.agentAvailability.put(agent, true);
        }
        CallCenter.callCenter = this;
    }

/**
* Retrieves the global instance of the CallCenter. This method provides access to
* the singleton instance of the CallCenter class, allowing external classes to obtain
* information about the call center's state and configuration.
*
* @return The global instance of the CallCenter.
*/
    public static CallCenter getCallCentre(){
        return callCenter;
    }
/**
* Retrieves the current count of available agents in the call center.
*
* @return The number of agents currently available in the call center.
*/
    public int getAvailableAgentsCount() {
        return availableAgents.size();
    }
    /**
 * Retrieves an array containing the currently available agents in the call center.
 *
 * @return An array of Agent objects representing the agents currently available in the call center.
 */
    public Agent[] getAvailableAgents() {
        int avaialableCount = getAvailableAgentsCount();
        Agent[] agents = new Agent[avaialableCount];
        LinkedList<Agent> availableAgents = this.availableAgents;
        return availableAgents.toArray(agents);
    }
/**
 * Retrieves an array containing the currently unavailable agents in the call center.
 *
 * @return An array of Agent objects representing the agents currently unavailable in the call center.
 */
    public Agent[] getUnavailableAgents() {
        // Get the count of available agents
        int avaialableCount = getAvailableAgentsCount();
        // Create an array to store the unavailable agents
        Agent[] availableAgents = new Agent[avaialableCount];
         // Counter for tracking the position in the array
        int avaialableAgentsCounter = 0;
        // Iterate through agents, adding unavailable agents to the array
        for (Agent agent : agentAvailability.keySet()) {
            if (agentAvailability.get(agent)) {
                availableAgents[avaialableAgentsCounter++] = agent;
            }
        }

        // Return the array of unavailable agents
        return availableAgents;
    }
/**
 * Retrieves the current count of unavailable agents in the call center.
 *
 * @return The number of agents currently unavailable in the call center.
 */
    public int getUnavailableAgentsCount() {
        // Get the total count of all agents
        int allAgents = agentAvailability.size();
         // Get the count of available agents
        int avaialableAgents = availableAgents.size();
        // Calculate and return the count of unavailable agents
        return allAgents - avaialableAgents;
    }
/**
 * Releases the specified agent, marking them as available for handling tasks or calls.
 *
 * @param agent The Agent object to be released.
 */
    public void releaseAgent(Agent agent) {
        // Add the agent back to the list of available agents
        availableAgents.add(agent);
        // Update the agent's availability status to true
        agentAvailability.put(agent, true);
    }

 /**
 * Assigns an available agent for handling a task or call. The assigned agent is
 * removed from the list of available agents and marked as unavailable for further assignments.
 *
 * @return The Agent object that has been assigned for the task or call.
 */   
    public Agent assignAgent() {
        // Retrieve the next available agent from the front of the queue
        Agent agent = availableAgents.poll();
        // Update the agent's availability status to false
        agentAvailability.put(agent, false);
        // Return the assigned agent
        return agent;
    }
/**
 * Retrieves the best available agent for handling the given call based on department compatibility.
 * If there is an available agent in the same department as the call's associated problem, that agent
 * is selected. Otherwise, the method selects the next available agent.
 *
 * @param call The Call object representing the incoming call.
 * @return The Agent object selected to handle the call based on department compatibility.
 */
    private Agent getBestAgent(Call call) {
        //extracts information about pending call and underlying problem
        Agent selectedAgent = null;
        Customer customer = call.getCaller();
        ProblemInfo problemInfo = customer.getProblemInfo();
        Problem problem = problemInfo.getLastProblem();
        Department CustomerDepartment = problem.getDepartment();
        // Iterate through available agents to find one in the same department
        for (Agent agent: availableAgents) {
            Department AgentDepartment = agent.getDepartment();
            String AgentDepartmentname = AgentDepartment.getName();
            String ProblemDepartmentName = CustomerDepartment.getName();
            if (AgentDepartmentname == ProblemDepartmentName) {
                selectedAgent = agent;
                break;
            }
        }
        // If no agent in the same department is available, select the next available agent
        if (selectedAgent == null) {
            selectedAgent = availableAgents.poll();
        } else {
            // Remove the selected agent from the list of available agents
            availableAgents.remove(selectedAgent);
        }
        // Mark the selected agent as unavailable
        agentAvailability.put(selectedAgent, false);
        // Return the selected agent for handling the call
        return selectedAgent;
    }
/**
 * Executes a single step in the simulation, attempting to match available agents
 * with incoming calls. If there are available agents, it retrieves an incoming call
 * and assigns the best available agent to handle it. The method then updates the
 * call receiver and notifies the MainSceneController about the connecting call.
 * If no calls are available but there are agents, the method takes appropriate action.
 */
    @Override
    public void step() {
        // Check if there are available agents
        if (getAvailableAgentsCount() > 0) {
            // Retrieve an incoming call
            Call call = Call.getACall();
            if (call == null) {
                // agents avaialable but no calls...
            } else {
                // agents available and there is a call...
                // Assign the best available agent to handle the call
                Agent agent = getBestAgent(call);
                call.setReciever(agent);            
                // Notify MainSceneController about the connecting call to update the UI
                msc.connectingCall(call);
            }
        } 
    }
}
