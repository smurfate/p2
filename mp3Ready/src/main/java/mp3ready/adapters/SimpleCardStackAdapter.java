package mp3ready.adapters;

import mp3ready.entities.Song;
import mp3ready.ui.MainActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;
import com.zgm.mp3ready.R;

/**
 * get card view to render song tinder
 * 
 * @author mhmd
 * 
 */
public abstract class SimpleCardStackAdapter extends CardStackAdapter {

	private MainActivity maingActivity;

	public SimpleCardStackAdapter(MainActivity mContext) {
		super(mContext);
		maingActivity = mContext;
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(maingActivity);
			convertView = inflater.inflate(R.layout.song_card_layout, parent,
					false);
			assert convertView != null;
		}
		ImageView song_card_cover = (ImageView) convertView
				.findViewById(R.id.song_card_cover);
		Song song = (Song) model.getDefindObject();

		if (song.songcover != null && !song.songcover.equals("")) {
			maingActivity.mPicasso.load(song.songcover).noFade().fit()
					.into(song_card_cover);
		} else {
			song_card_cover.setImageDrawable(maingActivity.getResources()
					.getDrawable(R.drawable.default_song_cover));
		}
		ImageView song_card_play_pause = (ImageView) convertView
				.findViewById(R.id.song_card_play_pause);
		song_card_play_pause.setTag(model);

		song_card_play_pause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				CardModel card = (CardModel) v.getTag();
				v.setEnabled(false);
				playSong(card);
			}
		});

		TextView song_card_name = (TextView) convertView
				.findViewById(R.id.song_card_name);
		song_card_name.setText(song.SName);

		return convertView;
	}

	public void popModel(CardModel model) {
		mData.remove(model);
		notifyDataSetChanged();
	}

	protected abstract void playSong(CardModel card);
}
