package com.example.main;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements Initializable
{

    @FXML
    private Pane pane;
    @FXML
    private Label songLabel;
    @FXML
    private Button playButton, pauseButton, resetButton, previousButton, nextButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private Slider volumeSlider;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> songs;

    private int songNumber;
    private int[] speeds = {25, 50, 75, 100, 125, 150, 200};

    private Timer timer;
    private TimerTask task;
    private boolean running;


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        songs = new ArrayList<File>();

       directory = new File("/src/main/resources/com/example/main/music");

       files = directory.listFiles();



        if (files != null)
        {
            for (File file : files)
            {
                songs.add(file);

            }

        }
        media = new Media(songs.get(songNumber).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        songLabel.setText(songs.get(songNumber).getName());

        for (int i = 0; i < speeds.length; i++)
        {
            speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
        }

        speedBox.setOnAction(this::changeSpeed);


        volumeSlider.valueProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        songProgressBar.setStyle("-fx-accent: green;");
    }

    public void playMedia()
    {
        beginTimer();
        changeSpeed(null);
        mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        mediaPlayer.play();
    }

    public void pauseMedia()
    {
        cancelTimer();
        mediaPlayer.pause();
    }

    public void resetMedia()
    {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void previousMedia()
    {
        if (songNumber > 0)
        {
            songNumber--;
            mediaPlayer.stop();

            if (running)
            {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia(); // auto play the next song
        }
        else
        {
            songNumber = songs.size() - 1;
            mediaPlayer.stop();

            if (running)
            {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia(); // auto play the next song
        }
    }

    public void nextMedia()
    {
        if (songNumber < songs.size() - 1)
        {
            songNumber++;
            mediaPlayer.stop();

            if (running)
            {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia(); // auto play the next song
        }
        else
        {
            songNumber = 0;
            mediaPlayer.stop();

            if (running)
            {
                cancelTimer();
            }

            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            songLabel.setText(songs.get(songNumber).getName());

            playMedia(); // auto play the next song
        }
    }

    public void changeSpeed(ActionEvent event)
    {
        if (speedBox.getValue() == null)
        {
            mediaPlayer.setRate(1);
        }
        else
        {
            mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }

    }

    public void beginTimer()
    {
        timer = new Timer();
        task = new TimerTask()
        {
            public void run()
            {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress((current / end));

                if (current/end == 1)
                {
                    cancelTimer();
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void cancelTimer()
    {
        running = false;
        timer.cancel();
    }
}