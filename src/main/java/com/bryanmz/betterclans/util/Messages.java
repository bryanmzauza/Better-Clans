package com.bryanmz.betterclans.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Servico de i18n simples baseado em YAML + MiniMessage.
 */
public final class Messages {

    private final Plugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private YamlConfiguration yaml;
    private Component prefix = Component.empty();

    public Messages(Plugin plugin) {
        this.plugin = plugin;
    }

    public void load(String language) {
        String resource = "lang/messages_" + language + ".yml";
        File outFile = new File(plugin.getDataFolder(), resource);
        if (!outFile.exists()) {
            plugin.saveResource(resource, false);
        }
        this.yaml = YamlConfiguration.loadConfiguration(outFile);

        // merge com default do jar (futuro) -> por enquanto so carrega externo
        InputStream def = plugin.getResource(resource);
        if (def != null) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(def, StandardCharsets.UTF_8));
            yaml.setDefaults(defaults);
        }

        String rawPrefix = yaml.getString("prefix", "");
        this.prefix = mm.deserialize(rawPrefix);
    }

    public Component get(String key, String... replacements) {
        String raw = yaml.getString(key, "<red>Missing key: " + key + "</red>");
        Map<String, String> map = toMap(replacements);
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> e : map.entrySet()) {
            builder.resolver(Placeholder.unparsed(e.getKey(), e.getValue()));
        }
        return prefix.append(mm.deserialize(raw, builder.build()));
    }

    public Component raw(String key, String... replacements) {
        String raw = yaml.getString(key, key);
        Map<String, String> map = toMap(replacements);
        TagResolver.Builder builder = TagResolver.builder();
        for (Map.Entry<String, String> e : map.entrySet()) {
            builder.resolver(Placeholder.unparsed(e.getKey(), e.getValue()));
        }
        return mm.deserialize(raw, builder.build());
    }

    private Map<String, String> toMap(String[] replacements) {
        Map<String, String> map = new HashMap<>();
        if (replacements == null) return map;
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Replacements devem vir em pares chave/valor.");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            map.put(replacements[i], replacements[i + 1]);
        }
        return map;
    }
}
