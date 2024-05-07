// **********************************************************************************
// Title: Music Organizer
// Author: Angel Diaz
// Course Section: CMIS201-ONL1 (Seidel) Spring 2024
// File: MusicOrganizerController.java
// Description: Class that controls the scene. Contains methods that set up song information in the columns, sets what's playing, an upload method, and play buttons.
// **********************************************************************************
package com.angeldiaz.musicorganizer;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class MusicOrganizerController implements Initializable {

    private AudioFileReader audioFileReader = new AudioFileReader();
    private List<HashMap<String, String>> songList;
    private ObservableList<HashMap<String, String>> songListData = FXCollections.observableArrayList();
    private MediaPlayer mediaPlayer;
    private PlayCountManager playCountManager = new PlayCountManager();
    private HashTable genreHashTable = new HashTable();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        artistName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Artist")));
        albumName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Album")));
        playCount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("PlayCount")));
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get("Genre")));
        songName.setCellValueFactory(data -> {
            String songTitle = data.getValue().get("Title");
            if (songTitle != null) {
                return new SimpleStringProperty(songTitle);
            } else {
                String fileName = data.getValue().get("FileName"); // If there is no metadata the title of the song is displayed as the FileName
                return new SimpleStringProperty(fileName != null ? fileName : "");
            }
        });
        try {
            songList = audioFileReader.readAudioFiles();
            if (songList != null) {
                songListData.addAll(songList);
                songListTable.setItems(songListData);

                // Loads and sorts plays from most to least on start-up, and loads genres
                playCountManager.loadPlayCounts(songListData, genreHashTable);
                playCountManager.sortPlayCount(songListData);

            } else {
                songListTable.setPlaceholder(new Label("No songs found!"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lookup.textProperty().addListener((observable, oldValue, newValue) -> {
            List<HashMap<String, String>> filteredList = new ArrayList<>();
            if (newValue == null || newValue.isEmpty()) {
                filteredList.addAll(songList);
            } else {
                List<HashMap<String, String>> searchResults = audioFileReader.searchSong(newValue);
                if (searchResults != null && !searchResults.isEmpty()) {
                    filteredList.addAll(searchResults);
                }
            }
            songListData.setAll(filteredList);
        });
    }

    @FXML
    private Label songLabel, nowPlayingLabel;
    @FXML
    private TableView<HashMap<String, String>> songListTable;
    @FXML
    private TableColumn<HashMap<String, String>, String> artistName, songName, albumName, playCount;
    @FXML
    private TextField lookup;
    @FXML
    private TextField genreInput;
    @FXML
    private TableColumn<HashMap<String, String>, String> genreColumn;

// Code for buttons

    @FXML
    void getSong(MouseEvent event) {
        if (event.getClickCount() == 2) {
            HashMap<String, String> selectedSong = songListTable.getSelectionModel().getSelectedItem();
                if (selectedSong != null) {

                    playSelectedSong(selectedSong);
                }
        }
    }

    @FXML
    void playSong(MouseEvent event) {
        HashMap<String, String> selectedSong = songListTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            playSelectedSong(selectedSong);
        }
    }

    @FXML
    void playSelectedSong(HashMap<String, String> selectedSong) {
        playCountManager.addPlay(selectedSong);
        try {
            playCountManager.savePlayCounts(songListData, genreHashTable); // Save play counts along with genre tags
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // If a song is playing stop and dispose
        if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        // Plays song
        File selectedFile = new File(selectedSong.get("Path"));
        Media media = new Media(selectedFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            mediaPlayer.play();
            nowPlayingLabel.setText("Now Playing");
            songLabel.setText(selectedSong.getOrDefault("Title", selectedFile.getName()));
        });

        // When it ends, stop.
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
        });

        // Refreshes to update playCount
        songListTable.refresh();
    }


    @FXML
    void prevSong(MouseEvent event) {
        if (mediaPlayer != null) {
            int selectedIndex = songListTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex > 0) {
                HashMap<String, String> prevSong = songListData.get(selectedIndex - 1);
                playSelectedSong(prevSong);
                songListTable.getSelectionModel().select(selectedIndex - 1);
            }
        }
    }

    @FXML
    void nextSong(MouseEvent event) {
        if (mediaPlayer != null) {
            int selectedIndex = songListTable.getSelectionModel().getSelectedIndex();
            if (selectedIndex < songListData.size() - 1) {
                HashMap<String, String> nextSong = songListData.get(selectedIndex + 1);
                playSelectedSong(nextSong);
                songListTable.getSelectionModel().select(selectedIndex + 1);
            }
        }
    }

    @FXML
    void stopSong(MouseEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void sortPlayCount(MouseEvent mouseEvent) {
        playCountManager.sortPlayCount(songListData);
        songListTable.refresh();
    }

    public void uploadSong(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Songs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);

        if(selectedFiles != null) {
            for (File selectedFile : selectedFiles) {
                MetaDataExtractor extractor = new MetaDataExtractor();
                HashMap<String, String> metadata = extractor.extractMetaData(selectedFile);
                metadata.put("Path", selectedFile.getAbsolutePath());
                songListData.add(metadata);
                uploadToMusicFolder(selectedFile);
            }
        }
    }

    private void uploadToMusicFolder(File file) {
        File folderDir = new File("src/main/resources/com/angeldiaz/musicorganizer/Music");
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }

        File destFile = new File(folderDir, file.getName());
        try {
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            nowPlayingLabel.setText("Upload Failed");
        }
    }

    @FXML
    void addGenreTag(MouseEvent event) {
        HashMap<String, String> selectedSong = songListTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            String genre = genreInput.getText().trim();
            if (!genre.isEmpty()) {
                // Clear existing genre tags for the selected song
                String path = selectedSong.get("Path");
                Set<String> existingGenreTags = genreHashTable.getGenreTags(path);
                if (existingGenreTags != null) {
                    for (String existingTag : existingGenreTags) {
                        genreHashTable.removeGenreTag(path, existingTag);
                    }
                }

                // Add new genre tag
                genreHashTable.addGenreTag(path, genre);

                // Update genre in the song data
                selectedSong.put("Genre", genre);
                int index = songListData.indexOf(selectedSong);
                if (index != -1) {
                    songListData.set(index, selectedSong); // Update the observable list
                }

                // Save updated play counts and genre tags to fileInfo
                try {
                    playCountManager.savePlayCounts(songListData, genreHashTable);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    // Handle file not found exception
                }

                songListTable.refresh(); // Refresh the table view
            }
        }
    }
}