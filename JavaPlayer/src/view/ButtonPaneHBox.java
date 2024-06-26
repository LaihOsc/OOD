package view;


import java.util.List;

import controller.MusicOrganizerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Album;
import model.SoundClip;
import view.AlbumWindow;

public class ButtonPaneHBox extends HBox {

	private MusicOrganizerController controller;
	private MusicOrganizerWindow view;
	
	private Button newAlbumButton;
	private Button deleteAlbumButton;
	private Button addSoundClipsButton;
	private Button removeSoundClipsButton;	
	private Button playButton;
	private Button albumWindowButton;
	public static final int BUTTON_MIN_WIDTH = 150;

	
	
	public ButtonPaneHBox(MusicOrganizerController contr, MusicOrganizerWindow view) {
		super();
		this.controller = contr;
		this.view = view;
		
		newAlbumButton = createNewAlbumButton();
		this.getChildren().add(newAlbumButton);

		deleteAlbumButton = createDeleteAlbumButton();
		this.getChildren().add(deleteAlbumButton);
		
		addSoundClipsButton = createAddSoundClipsButton();
		this.getChildren().add(addSoundClipsButton);
		
		removeSoundClipsButton = createRemoveSoundClipsButton();
		this.getChildren().add(removeSoundClipsButton);
		
		playButton = createPlaySoundClipsButton();
		this.getChildren().add(playButton);
		
		albumWindowButton = createNewAlbumWindowButton();
		this.getChildren().add(albumWindowButton);
		

	}
	
	/*
	 * Each method below creates a single button. The buttons are also linked
	 * with event handlers, so that they react to the user clicking on the buttons
	 * in the user interface
	 */

	private Button createNewAlbumButton() {
		Button button = new Button("New Album");
		button.setTooltip(new Tooltip("Create new sub-album to selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(e->{
			Album album = view.getSelectedAlbum();
			if(album!=null)
			{
				String albName = view.promptForAlbumName();
				if(albName!=null)
					controller.addNewAlbum(album, albName);
			}
		});
		return button;
	}
	
	private Button createDeleteAlbumButton() {
		Button button = new Button("Remove Album");
		button.setTooltip(new Tooltip("Remove selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(e->{
			Album album = view.getSelectedAlbum();
			if(album!=null)
				controller.deleteAlbum(album);
		});
		return button;
	}
	
	private Button createAddSoundClipsButton() {
		Button button = new Button("Add Sound Clips");
		button.setTooltip(new Tooltip("Add selected sound clips to selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(e->{
			Album album = view.getSelectedAlbum();
			if(album!=null)
			{
				List<SoundClip> songs = view.getSelectedSoundClips();
				if(songs.size()>0)
					controller.addSoundClips(album, songs);
			}
		});
		return button;
	}
	
	private Button createRemoveSoundClipsButton() {
		Button button = new Button("Remove Sound Clips");
		button.setTooltip(new Tooltip("Remove selected sound clips from selected album"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(e->{
			Album album = view.getSelectedAlbum();
			if(album!=null)
			{
				List<SoundClip> songs = view.getSelectedSoundClips();
				if(songs.size()>0)
					controller.removeSoundClips(album, songs);
			}
		});
		return button;
	}
	
	private Button createPlaySoundClipsButton() {
		Button button = new Button("Play Sound Clips");
		button.setTooltip(new Tooltip("Play selected sound clips"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(e->{
			controller.playSoundClips();
		});
		return button;
	}
	
	private Button createNewAlbumWindowButton() {
		Button button = new Button("New album window");
		button.setTooltip(new Tooltip("Open an album in a new window"));
		button.setMinWidth(BUTTON_MIN_WIDTH);
		button.setOnAction(e->{
			Album selectedAlbum = view.getSelectedAlbum();
			if (selectedAlbum != null) {
				controller.openAlbumWindow(selectedAlbum);
			}
			// Open a window of the selected album
		});
		return button;
	}
}
