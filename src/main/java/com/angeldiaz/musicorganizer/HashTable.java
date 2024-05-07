package com.angeldiaz.musicorganizer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashTable {
    private Map<String, Set<String>> genreHashTable;

    public HashTable() {
        this.genreHashTable = new HashMap<>();
    }

    public void addGenreTag(String songPath, String genreTag) {
        genreHashTable.computeIfAbsent(songPath, k -> new HashSet<>()).add(genreTag);
    }

    public void removeGenreTag(String songPath, String genreTag) {
        Set<String> tags = genreHashTable.get(songPath);
        if (tags != null) {
            tags.remove(genreTag);
            if (tags.isEmpty()) {
                genreHashTable.remove(songPath);
            }
        }
    }

    public Set<String> getGenreTags(String songPath) {
        return genreHashTable.getOrDefault(songPath, new HashSet<>());
    }
}