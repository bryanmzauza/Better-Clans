package com.bryanmz.betterclans.duel;

import org.bukkit.Location;

import java.util.UUID;

public final class DuelSession {

    public enum State { PENDING, COUNTDOWN, ACTIVE, ENDED }

    private final UUID challenger;
    private final UUID challenged;
    private final double bet;
    private final long createdAt;
    private Location challengerOrigin;
    private Location challengedOrigin;
    private State state = State.PENDING;

    public DuelSession(UUID challenger, UUID challenged, double bet) {
        this.challenger = challenger;
        this.challenged = challenged;
        this.bet = bet;
        this.createdAt = System.currentTimeMillis();
    }

    public UUID challenger() { return challenger; }
    public UUID challenged() { return challenged; }
    public double bet() { return bet; }
    public long createdAt() { return createdAt; }
    public State state() { return state; }
    public void setState(State state) { this.state = state; }
    public Location challengerOrigin() { return challengerOrigin; }
    public void setChallengerOrigin(Location l) { this.challengerOrigin = l; }
    public Location challengedOrigin() { return challengedOrigin; }
    public void setChallengedOrigin(Location l) { this.challengedOrigin = l; }
}
