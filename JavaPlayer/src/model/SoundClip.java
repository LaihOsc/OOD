package model;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * SoundClip is a class representing a digital
 * sound clip file on disk.
 */
public class SoundClip
{
	private final File file;
	private ArrayList<Album> albums;
	
	/**
	 * Make a SoundClip from a file.
	 * Requires file != null.
	 */
	public SoundClip(File inFile) {
		assertNotNull(inFile);
		file = inFile;
		albums = new ArrayList<Album>();
	}

	/**
	 * @return the file containing this sound clip.
	 */
	public File getFile() {
		return file;
	}
	
	public String toString(){
		return file.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		return 
			obj instanceof SoundClip
			&& ((SoundClip)obj).file.equals(file);
	}
	
	@Override
	public int hashCode() {
		return file.hashCode();
	}
	
	// Grab list of albums this song is in.
	public ArrayList<Album> getAlbums()
	{
		return new ArrayList<Album>(albums);
	}
	
	// Add or remove this song from album, DO NOT ACCESS THIS DIRECTLY!
	// Instead use Album.addSong(x) or Album.removeSong(x)
	public void addAlbum(Album a)
	{
		assertNotNull(a);
		assertFalse(albums.contains(a));
		
		albums.add(a);
	}
	public void removeAlbum(Album a)
	{
		assertNotNull(a);
		assertTrue(albums.contains(a));
		
		albums.remove(a);
	}
	
	// Serializer
	public void serializeOut(OutputStream ar) throws IOException
	{
		Album.serializeString(ar, file.toString());
	}
	
	public static SoundClip serializeIn(InputStream ar) throws IOException
	{
		String clipName = Album.serializeString(ar);
		return new SoundClip(new File(clipName));
	}
}
