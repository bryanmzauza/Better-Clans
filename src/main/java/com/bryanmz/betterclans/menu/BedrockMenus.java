package com.bryanmz.betterclans.menu;

import com.bryanmz.betterclans.BetterClansPlugin;
import com.bryanmz.betterclans.clan.Clan;
import com.bryanmz.betterclans.clan.ClanMember;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Comparator;
import java.util.List;

/**
 * Formularios Bedrock (via Floodgate/Cumulus). Evitado em runtime se Floodgate nao estiver disponivel.
 */
final class BedrockMenus {

    private BedrockMenus() {}

    static void openMain(BetterClansPlugin plugin, Player player) {
        ClanMember me = plugin.clans().getMember(player.getUniqueId()).orElse(null);
        SimpleForm.Builder form = SimpleForm.builder().title("BetterClans");
        if (me == null) {
            form.content("Voce nao esta em um cla.");
            form.button("Ver lista de clas");
            form.button("Ranking (Top 10)");
            form.button("Fechar");
            form.validResultHandler((SimpleFormResponse r) -> {
                switch (r.clickedButtonId()) {
                    case 0 -> openClanList(plugin, player);
                    case 1 -> openTop(plugin, player);
                }
            });
        } else {
            Clan clan = plugin.clans().getById(me.clanId()).orElse(null);
            if (clan == null) return;
            form.content("Cla: [" + clan.tag() + "] " + clan.name() + "\n"
                    + "Nivel: " + clan.level() + " (" + clan.xp() + " XP)\n"
                    + "K/D: " + clan.kills() + "/" + clan.deaths() + " (" + String.format("%.2f", clan.kdRatio()) + ")\n"
                    + "Wins no Gladiador: " + clan.wins() + "\n"
                    + "Membros: " + plugin.clans().membersOf(clan.id()).size() + "\n"
                    + "Seu cargo: " + me.role().name());
            form.button("Informacoes detalhadas");
            form.button("Lista de clas");
            form.button("Ranking (Top 10)");
            form.button("Chat do cla");
            form.button("Chat da alianca");
            form.button("Gladiador: entrar");
            form.button("Gladiador: sair");
            form.button("Sair do cla");
            form.button("Fechar");
            form.validResultHandler((SimpleFormResponse r) -> {
                switch (r.clickedButtonId()) {
                    case 0 -> player.performCommand("clan info");
                    case 1 -> openClanList(plugin, player);
                    case 2 -> openTop(plugin, player);
                    case 3 -> player.performCommand("clan chat");
                    case 4 -> player.performCommand("clan allychat");
                    case 5 -> player.performCommand("gladiator join");
                    case 6 -> player.performCommand("gladiator leave");
                    case 7 -> player.performCommand("clan leave");
                }
            });
        }
        sendForm(player, form.build());
    }

    static void openClanList(BetterClansPlugin plugin, Player player) {
        List<Clan> clans = plugin.clans().all().stream()
                .sorted(Comparator.comparing(Clan::tag))
                .limit(30)
                .toList();
        SimpleForm.Builder form = SimpleForm.builder().title("Clas do servidor");
        if (clans.isEmpty()) {
            form.content("Nenhum cla encontrado.");
            form.button("Voltar");
            form.validResultHandler((SimpleFormResponse r) -> openMain(plugin, player));
            sendForm(player, form.build());
            return;
        }
        for (Clan c : clans) {
            form.button("[" + c.tag() + "] " + c.name());
        }
        form.button("Voltar");
        form.validResultHandler((SimpleFormResponse r) -> {
            int id = r.clickedButtonId();
            if (id < clans.size()) {
                player.performCommand("clan info " + clans.get(id).tag());
            } else {
                openMain(plugin, player);
            }
        });
        sendForm(player, form.build());
    }

    static void openTop(BetterClansPlugin plugin, Player player) {
        List<Clan> top = plugin.clans().all().stream()
                .sorted(Comparator.comparingLong(Clan::xp).reversed())
                .limit(10)
                .toList();
        StringBuilder content = new StringBuilder("Top 10 por XP:\n");
        int pos = 1;
        for (Clan c : top) {
            content.append("§e#").append(pos++).append(" §f[").append(c.tag()).append("] ")
                    .append(c.name()).append(" §7- ").append(c.xp()).append(" XP\n");
        }
        if (top.isEmpty()) content.append("Nenhum cla.");
        SimpleForm form = SimpleForm.builder()
                .title("Ranking - XP")
                .content(content.toString())
                .button("Fechar")
                .build();
        sendForm(player, form);
    }

    private static void sendForm(Player player, SimpleForm form) {
        try {
            FloodgateApi.getInstance().sendForm(player.getUniqueId(), form);
        } catch (Throwable t) {
            player.sendMessage("§cErro ao abrir menu Bedrock: " + t.getMessage());
        }
    }
}
