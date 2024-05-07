// **********************************************************************************
// Title: Music Organizer
// Author: Angel Diaz
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: PlayCountManager.java
// Description: Class for adding plays to songs. Contains methods to read, load, and sort from most plays to least using MergeSort.
// **********************************************************************************
package com.angeldiaz.musicorganizer;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class PlayCountManager {
    private static final String playCountFileName = "src/main/resources/com/angeldiaz/musicorganizer/fileInfo.txt";

    public void addPlay(HashMap<String, String> song) {
        int playCount = getPlayCount(song);
        song.put("PlayCount", String.valueOf(playCount + 1));
    }

    private int getPlayCount(HashMap<String, String> song) {
        return Integer.parseInt(song.getOrDefault("PlayCount", "0"));
    }

    public void savePlayCounts(List<HashMap<String, String>> songList, HashTable genreHashTable) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(new FileWriter(playCountFileName))) {
            for (HashMap<String, String> song : songList) {
                String genreTagsString = getGenreTagsString(genreHashTable, song.get("Path"));
                out.println(song.get("Path") + "," + getPlayCount(song) + "," + genreTagsString); // Include genre tags in the line
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getGenreTagsString(HashTable genreHashTable, String songPath) {
        Set<String> genreTags = genreHashTable.getGenreTags(songPath);
        if (genreTags != null && !genreTags.isEmpty()) {
            return String.join(",", genreTags);
        } else {
            return "";
        }
    }

    public void loadPlayCounts(List<HashMap<String, String>> songList, HashTable genreHashTable) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(playCountFileName))) {
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                String[] parts = currentLine.split(",");
                if (parts.length >= 2) {
                    String path = parts[0];
                    int playCount = Integer.parseInt(parts[1]);
                    String[] genreTags = parts.length > 2 ? parts[2].split(",") : new String[0]; // Check if there are genre tags
                    for (HashMap<String, String> song : songList) {
                        if (song.get("Path").equals(path)) {
                            song.put("PlayCount", String.valueOf(playCount));
                            for (String genreTag : genreTags) {
                                genreHashTable.addGenreTag(path, genreTag);
                            }
                            // Update the genre column in the song data
                            song.put("Genre", String.join(",", genreTags));
                            break;
                        }
                    }
                } else {
                    System.err.println("Not a valid play count");
                }
            }
        }
    }

   public void sortPlayCount(List<HashMap<String, String>> songList) {
        if(songList == null || songList.size() <= 0) {
            return;
        }
        mergeSort(songList, 0, songList.size() - 1);
   }

   private void mergeSort(List<HashMap<String, String>> songList, int start, int end) {
        if (start < end) {
            int mid = (start + end) / 2;
            mergeSort(songList, start, mid);
            mergeSort(songList, mid + 1, end);
            merge(songList, start, mid, end);
        }
   }

    private void merge(List<HashMap<String, String>> songList, int start, int mid, int end) {
        int n1 = mid - start + 1;
        int n2 = end - mid;

        List<HashMap<String, String>> left = new ArrayList<>(songList.subList(start, start + n1));
        List<HashMap<String, String>> right = new ArrayList<>(songList.subList(mid + 1, mid + 1 + n2));

        int i = 0, j = 0, k = start;
        while (i < n1 && j < n2) {
            int playCount1 = getPlayCount(left.get(i));
            int playCount2 = getPlayCount(right.get(j));
            if (playCount1 >= playCount2) {
                songList.set(k++, left.get(i++));
            } else {
                songList.set(k++, right.get(j++));
            }
        }

        while (i < n1) {
            songList.set(k++, left.get(i++));
        }

        while (j < n2) {
            songList.set(k++, right.get(j++));
        }
    }
}
