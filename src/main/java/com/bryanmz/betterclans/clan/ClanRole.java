package com.bryanmz.betterclans.clan;

/**
 * Papel hierarquico de um membro dentro do cla.
 */
public enum ClanRole {
    LEADER,
    OFFICER,
    MEMBER;

    public boolean canInvite() {
        return this != MEMBER;
    }

    public boolean canKick() {
        return this != MEMBER;
    }

    public boolean canManage() {
        return this == LEADER;
    }

    public boolean isAtLeast(ClanRole other) {
        return this.ordinal() <= other.ordinal();
    }
}
