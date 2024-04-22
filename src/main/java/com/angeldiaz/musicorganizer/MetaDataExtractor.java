// **********************************************************************************
// Title: Music Organizer
// Author: Angel Diaz
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: MetaDataExtractor.java
// Description: Uses mp3agic to extract metadata from files.
// **********************************************************************************
package com.angeldiaz.musicorganizer;

import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MetaDataExtractor {

    public HashMap<String, String> extractMetaData(File mp3File) {
        HashMap<String, String> metaDataMap = new HashMap<>();

        try {
            Mp3File mp3 = new Mp3File(mp3File);

            if(mp3.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3.getId3v1Tag();
                metaDataMap.put("Title", id3v1Tag.getTitle());
                metaDataMap.put("Artist", id3v1Tag.getArtist());
                metaDataMap.put("Album", id3v1Tag.getAlbum());
                metaDataMap.put("Year", id3v1Tag.getYear());
            }

            if(mp3.hasId3v2Tag()) {
                ID3v1 id3v2Tag = mp3.getId3v2Tag();
                metaDataMap.put("Title", id3v2Tag.getTitle());
                metaDataMap.put("Artist", id3v2Tag.getArtist());
                metaDataMap.put("Album", id3v2Tag.getAlbum());
                metaDataMap.put("Year", id3v2Tag.getYear());
            }

            if (metaDataMap.isEmpty()) { // If there is no metadata display the title as the mp3 file name
                metaDataMap.put("Title", mp3File.getName());
                metaDataMap.put("Artist", "Unknown");
                metaDataMap.put("Album", "Unknown");
                metaDataMap.put("Last Played", "Unknown");
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            throw new RuntimeException(e);
        }
        return metaDataMap;
    }
}





