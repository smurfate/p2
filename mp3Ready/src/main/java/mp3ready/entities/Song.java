package mp3ready.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import android.util.Log;

public class Song implements Serializable {
/*
	{
		"SID": "1",
			"Hash": null,
			"SongID": null,
			"SName": "Surrogate People",
			"ArtistID": null,
			"Artist": "John Frusciante and Josh Klinghoffer",
			"description": null,
			"albumID": null,
			"album": null,
			"duration": null,
			"listeners": null,
			"scrobbles": null,
			"songcover": "http://img2-ak.lst.fm/i/u/174s/5653ec527bfc49cd93396530e32fba9a.jpg",
			"tags": null,
			"SimilarSong1": null,
			"SimilarSong2": null,
			"SimilarSong3": null,
			"SimilarSong4": null,
			"SimilarSong5": null,
			"SimilarSong6": null,
			"LikesCount": null,
			"DislikeCount": null,
			"RateValue": null,
			"RateCount": null,
			"file": "/a/mmusic/admin/audio/complete/song1.mp3"
	},
*/

	public Integer dbid;
	public String  SID;
	public String Hash;
	public String SongID;
	public String SName;
	public String ArtistID;
	public String Artist;
	public String albumID;
	public String album;
	public String duration;
	public String listeners;
	public String scrobbles;
	public String songcover;
	public String SimilarSong1;
	public String SimilarSong2;
	public String SimilarSong3;
	public String SimilarSong4;
	public String SimilarSong5;
	public String SimilarSong6;
	public String LikesCount;
	public String DislikeCount;
	public String RateValue;
	public String RateCount;
	public String file;

	public int whichPlayList;



	public final static String SERVER_IP = "172.93.148.138";
	public final static String HOST = SERVER_IP;
	public final static String HOST_URL = "http://" + HOST ;

	public final static String BASE_URL = "http://" + HOST
			+ "/a/mmusic/admin/api/";

	private static final long serialVersionUID = 1L;


	public String getSongURL()
	{
		return HOST_URL+file;
	}

}
