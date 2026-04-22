package com.bryanmz.betterclans.gladiator;

import com.bryanmz.betterclans.BetterClansPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Estado do evento Gladiador: clas inscritos e participantes vivos.
 */
public final class GladiatorEvent {

    private final BetterClansPlugin plugin;
    private final Set<UUID> registeredClans = new HashSet<>();
    private final Set<UUID> aliveParticipants = new HashSet<>();
    private State state = State.IDLE;

    public enum State { IDLE, SIGNUP_OPEN, PRE_MATCH, RUNNING, ENDED }

    public GladiatorEvent(BetterClansPlugin plugin) {
        this.plugin = plugin;
    }

    public State state() { return state; }
    public void setState(State state) { this.state = state; }

    public Set<UUID> registeredClans() { return registeredClans; }
    public Set<UUID> aliveParticipants() { return aliveParticipants; }

    public void reset() {
        registeredClans.clear();
        aliveParticipants.clear();
        state = State.IDLE;
    }
}
