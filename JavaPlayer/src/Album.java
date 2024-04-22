import static org.junit.Assert.*;
import java.util.ArrayList;

/* Album:
 * Each song can be part of an album, an album may contain subalbums, each album must be named.
 * Use createAlbum or createSubAlbum to create a new album,
 * then addSong to add songs to the album.
 */
public class Album {

	private String albumName;
	private Album parentAlbum;
	private ArrayList<Album> subAlbums;
	private ArrayList<SoundClip> songList;
	
	private Album(String inName)
	{
		albumName = inName;
		parentAlbum = null;
		subAlbums = new ArrayList<Album>();
		songList = new ArrayList<SoundClip>();
	}
	
	/* Album factory: create a root album or a subalbum! */
	public static Album createAlbum(String inName)
	{
		return new Album(inName);
	}
	public static Album createSubAlbum(String inName, Album inParent)
	{
		assertNotNull(inParent);
		Album result = new Album(inName);
		inParent.addSubAlbum(result);
		return result;
	}
	
	// Get the name of this album.
	public String getAlbumName()
	{
		return albumName;
	}
	
	// Get parent album of this subalbum, or null if this is the root.
	public Album getParentAlbum()
	{
		return parentAlbum;
	}
	
	// Grab list of subalbums.
	public ArrayList<Album> getSubAlbums()
	{
		return new ArrayList<Album>(subAlbums);
	}
	
	public String toString()
	{
		return albumName + " (" + songList.size() + " songs)";
	}
	
	/* Add or remove a subalbum from this album.
	 * Note that you should verify with containsAlbum first before removing an album, and to verify that parent album is null before adding to one.
	 */
	public void addSubAlbum(Album album)
	{
		assertNotNull(album);
		assertNull(album.parentAlbum);
		assertFalse(containsAlbum(album));
		
		album.parentAlbum = this;
		subAlbums.add(album);
		
		assertTrue(containsAlbum(album));
	}
	public void removeSubAlbum(Album album)
	{
		assertNotNull(album);
		assertEquals(album.parentAlbum,this);
		assertTrue(containsAlbum(album));
		
		album.parentAlbum = null;
		subAlbums.remove(album);
		
		assertFalse(containsAlbum(album));
	}
	
	/* Add or remove a song from this album.
	 * Note that you shouldn't call addSong if its already part of this album, or removeSong if isn't part of this album.
	 * Always verify with containsSong first!
	 */
	public void addSong(SoundClip song)
	{
		assertNotNull(song);
		assertFalse(songList.contains(song));
		
		song.addAlbum(this);
		songList.add(song);
		
		assertTrue(containsSong(song));
	}
	public void removeSong(SoundClip song)
	{
		assertNotNull(song);
		assertTrue(songList.contains(song));
		song.removeAlbum(this);
		assertFalse(containsSong(song));
	}
	
	/* Check if an album is a subalbum of this album tree.
	 * Returns true if 'album' is a subalbum, or if it's a subalbum of subalbum.
	 */
	public boolean containsAlbum(Album album)
	{
		assertNotNull(album);
		if(subAlbums.contains(album))
			return true;
		for(Album a : subAlbums)
		{
			if(a.containsAlbum(album))
				return true;
		}
		return false;
	}
	
	/* Check if this album contains a song. */
	public boolean containsSong(SoundClip song)
	{
		assertNotNull(song);
		return songList.contains(song);
	}
	
	// Debug dump output!
	public void DumpAlbum(String intend)
	{
		System.out.println(intend+"[Album '"+albumName+"', Parent '"+(parentAlbum!=null ? parentAlbum.albumName : "null")+"] SubAlbums "+subAlbums.size());
		if( subAlbums.size()>0 )
		{
			System.out.println(intend+"{");
			for(Album a : subAlbums)
				a.DumpAlbum(intend+" ");
			System.out.println(intend+"}");
		}
		System.out.println(intend+"["+albumName+"] Songs "+songList.size());
		if( songList.size()>0 )
		{
			System.out.println(intend+"{");
			for(SoundClip s : songList)
				System.out.println(intend+" -"+s);
			System.out.println(intend+"}");
		}
	}
}
