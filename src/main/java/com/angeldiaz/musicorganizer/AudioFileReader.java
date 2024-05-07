// **********************************************************************************
// Title: Music Organizer
// Author: Angel Diaz
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: AudioFileReader.java
// Description: Filters and reads files to be put in a HashMap
// **********************************************************************************
package com.angeldiaz.musicorganizer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AudioFileReader {
    private BST bst;

    public AudioFileReader() {
        this.bst = new BST();
    }

    public List<HashMap<String, String>> readAudioFiles() {
        // Restrictions for what is displayed and allowed to be uploaded
        String audioFormatsAccepted[] = new String[]{
                "mp3" // Ability to allow more audio formats.
        };
        ArrayList<HashMap<String, String>> metadataList = new ArrayList<>();

        // Grabbing files from the Music folder
        File folderDir = new File("src/main/resources/com/angeldiaz/musicorganizer/Music");
        File[] files = folderDir.listFiles();

        if (files != null) { // If files folder isn't empty
            for (File file : files) {
                for (String format : audioFormatsAccepted) {
                    if (file.getName().endsWith("." + format)) { // If ends with accepted audio format (e.g. mp3)
                        MetaDataExtractor extractor = new MetaDataExtractor();
                        HashMap<String, String> metadata = extractor.extractMetaData(file);
                        metadata.put("Path", file.getAbsolutePath());
                        bst.insert(metadata);
                        metadataList.add(metadata);
                        break;
                    }
                }
            }
        }
        return metadataList;
    }

    public List<HashMap<String, String>> searchSong(String title) {
        List<HashMap<String, String>> searchResults = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            // Search for matching songs
            List<HashMap<String, String>> results = bst.searchAll(title);
            if (results != null) {
                searchResults.addAll(results);
            }
        }
        return searchResults;
    }
}

