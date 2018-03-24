package mp3ready.util;

import mp3ready.entities.PlayList;

public interface ICallBack {

	public void onSelectPlayList(PlayList pl);

	public void onCreatePlayList(String pl_name);

	public void onUpdatePlayList(String pl_name);

	public void onSwitchToCreateMode();
}