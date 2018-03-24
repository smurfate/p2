//package mp3ready.playlists;
//
//import java.util.List;
//
//import mp3ready.entities.Song;
//import mp3ready.ui.MainActivity;
//import mp3ready.util.Utilities;
//import android.util.Log;
//
///**
// * <p>
// * Factory to manage play list and getting next/prev song
// * </p>
// *
// * @author mhmd
// *
// */
//public class PlayListFactory {
//
//	private int CurrentPlayList = -1;
//	private MainActivity mainActivity;
//
//	public PlayListFactory(MainActivity context) {
//		this.mainActivity = context;
//	}
//
//	public void setCurrnetPLayList(int pl) {
//		this.CurrentPlayList = pl;
//	}
//
//	public int getCurrentPlayList() {
//		return this.CurrentPlayList;
//	}
//
//	/**
//	 * <p>
//	 * getting next song from current playlist
//	 * </p>
//	 *
//	 * @param isShuffle
//	 *            if true we getting random next song
//	 * @param currentPlayed
//	 *            the current played song
//	 * @return next song
//	 */
//	public Song getNext(boolean isShuffle, Song currentPlayed) {
//		List<Song> playListSongs = mainActivity.db
//				.getPlayList(this.CurrentPlayList);
//		if (playListSongs != null && playListSongs.size() > 0) {
//			if (currentPlayed == null) {
//				Log.e("PlayListFactory", "size > 0 & current null => first:"
//						+ playListSongs.get(0).SName);
//				return playListSongs.get(0);
//			}
//			if (playListSongs.size() == 1) {
//
//				if (playListSongs.get(0).SID.equals(currentPlayed.SID)) {
//					Log.e("PlayListFactory", "size = 1 => null");
//					return null;
//				} else {
//					Log.e("PlayListFactory", "size = 1 => first:"
//							+ playListSongs.get(0).SName);
//					return playListSongs.get(0);
//				}
//			} else {
//				int j = 0;
//				boolean found = false;
//				for (int i = 0; i < playListSongs.size(); i++) {
//					if (playListSongs.get(i).SID.equals(currentPlayed.SID)) {
//						found = true;
//						j = i;
//						break;
//					}
//				}
//				if (found) {
//					if (isShuffle) {
//						Song nxt = playListSongs.get(Utilities.randInt(j + 1,
//								playListSongs.size() - 1));
//						Log.e("PlayListFactory", "size > 1 => shuffle next:"
//								+ nxt.SName);
//						return nxt;
//					} else {
//						if (j < playListSongs.size() - 1) {
//							Song nxt = playListSongs.get(++j);
//							;
//							Log.e("PlayListFactory", "size > 1 => next:"
//									+ nxt.SName);
//							return nxt;
//						} else {
//							Log.e("PlayListFactory", "size > 1 => first:"
//									+ playListSongs.get(0).SName);
//							return playListSongs.get(0);
//						}
//					}
//				} else {
//					if (isShuffle) {
//						Song nxt = playListSongs.get(Utilities.randInt(0,
//								playListSongs.size() - 1));
//						Log.e("PlayListFactory",
//								"size > 1 & !found => shuffle:" + nxt.SName);
//						return nxt;
//					} else {
//						Log.e("PlayListFactory", "size > 1 & !found => first:"
//								+ playListSongs.get(0).SName);
//						return playListSongs.get(0);
//					}
//
//				}
//			}
//		} else {
//			Log.e("PlayListFactory", "size = 0  => null");
//			return null;
//		}
//	}
//
//	/**
//	 * <p>
//	 * getting prev song from current playlist
//	 * </p>
//	 *
//	 * @param isShuffle
//	 *            f true we getting random prev song
//	 * @param currentPlayed
//	 *            the current played song
//	 * @return prev song
//	 */
//	public Song getPrevious(boolean isShuffle, Song currentPlayed) {
//		List<Song> playListSongs = mainActivity.db
//				.getPlayList(this.CurrentPlayList);
//		if (playListSongs != null && playListSongs.size() > 0) {
//
//			if (currentPlayed == null) {
//				Log.e("PlayListFactory", "size > 0 & current null => first:"
//						+ playListSongs.get(0).SName);
//				return playListSongs.get(0);
//			}
//			if (playListSongs.size() == 1) {
//				Log.e("PlayListFactory", "size = 1 => null");
//				if (playListSongs.get(0).SID.equals(currentPlayed.SID)) {
//					return null;
//				} else {
//					Log.e("PlayListFactory", "size = 1 => first:"
//							+ playListSongs.get(0).SName);
//					return playListSongs.get(0);
//				}
//			} else {
//				int i = 0;
//				boolean found = false;
//				for (i = 0; i < playListSongs.size(); i++) {
//					if (playListSongs.get(i).SID.equals(currentPlayed.SID)) {
//						found = true;
//						break;
//					}
//				}
//
//				if (found) {
//					if (isShuffle) {
//						Song prev = playListSongs.get(Utilities.randInt(0,
//								i - 1));
//						Log.e("PlayListFactory", "size > 1 => shuffle prev:"
//								+ prev.SName);
//						return prev;
//					} else {
//						if (i > 0) {
//							Song prev = playListSongs.get(--i);
//							Log.e("PlayListFactory", "size > 1 => prev:"
//									+ prev.SName);
//							return prev;
//						} else {
//							Log.e("PlayListFactory",
//									"size > 1 => lastone:"
//											+ playListSongs.get(playListSongs
//													.size() - 1).SName);
//							return playListSongs.get(playListSongs.size() - 1);
//						}
//					}
//				} else {
//					if (isShuffle) {
//						Song prev = playListSongs.get(Utilities.randInt(0,
//								playListSongs.size() - 1));
//						Log.e("PlayListFactory",
//								"size > 1 & !found => shuffle:" + prev.SName);
//						return prev;
//					} else {
//						Log.e("PlayListFactory", "size > 1 & !found => first");
//						return playListSongs.get(0);
//					}
//				}
//			}
//		} else {
//			Log.e("PlayListFactory", "size = 0  => null");
//			return null;
//		}
//	}
//}
