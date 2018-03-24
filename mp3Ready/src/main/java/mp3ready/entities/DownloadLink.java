package mp3ready.entities;

public class DownloadLink {
	public Integer dbid;
	public String id;
	public String size;
	public String bitrate;
	public String url;
	public String title;
	public String artist;
	public String songcover;
	public boolean isValid = false;
	public String duration;
	public String downloaded_path;
	public Integer download_state;
	public Integer download_progress;
}
