package mp3ready.response;

import java.util.List;

import mp3ready.entities.Artist;
import mp3ready.entities.Song;

public class ArtistSongsResponse {

	public Artist artist;
	public int total_count;
	public List<Song> songs;

}
