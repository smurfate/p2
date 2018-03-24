package mp3ready.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import mp3ready.entities.Song;

/**
 * Created by Saad on 3/17/2018.
 */

public class ListSongsResponse {
/*
    "list name": "2",
            "items": [
    {
        "SID": "1",
            "Hash": null,
            "SongID": null,
            "SName": "Surrogate People",
            "ArtistI
*/


    @SerializedName("list name")
    public String list_name;
    public List<Song> items;
}
