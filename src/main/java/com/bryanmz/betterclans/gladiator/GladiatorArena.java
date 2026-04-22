package com.bryanmz.betterclans.gladiator;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a arena fisica do Gladiador: lobby, spawns por cla e spawn de retorno.
 * As localizacoes sao populadas via /gladiator setlobby e /gladiator setspawn <n>.
 */
public final class GladiatorArena {

    private Location lobby;
    private Location returnSpawn;
    private final List<Location> spawns = new ArrayList<>();

    public Location lobby() { return lobby; }
    public void setLobby(Location lobby) { this.lobby = lobby; }

    public Location returnSpawn() { return returnSpawn; }
    public void setReturnSpawn(Location returnSpawn) { this.returnSpawn = returnSpawn; }

    public List<Location> spawns() { return spawns; }
    public void addSpawn(Location loc) { spawns.add(loc); }
    public void setSpawn(int index, Location loc) {
        while (spawns.size() <= index) spawns.add(null);
        spawns.set(index, loc);
    }
    public void clearSpawns() { spawns.clear(); }
}
