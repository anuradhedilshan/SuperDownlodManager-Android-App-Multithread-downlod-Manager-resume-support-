package lk.lab24.sdm;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.lab24.sdm.Backend.Actions;
import lk.lab24.sdm.Backend.DownlodInfo;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.viewHolder> {
	private static final String TAG = "fuck";
	HashMap<Integer, Object[]> nameWithID;
	List<Integer> ids = null;
	ProgressBar[] progressBars = null;
	TextView[] progressBarText = null;
	private Context context;
	private int pos;


	public RecycleViewAdapter(Context c, int pos) {
		this.context = c;
		this.pos = pos;
		BroadcastReceiver br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "onReceive: DataAddewd ");
				RecycleViewAdapter.this.nameWithID = new HashMap<>();
				RecycleViewAdapter.this.ids = new ArrayList<>();
				RecycleViewAdapter.this.nameWithID = new HashMap<>();
				Cursor c = null;
				if (pos == 0) {
					Log.d(TAG, "onReceive: 0");
					c = Database.readabelDb.rawQuery("SELECT * FROM queue ORDER BY id DESC", null);

				} else if (pos == 1) {
					Log.d(TAG, "onReceive: 1");
					c = Database.readabelDb.rawQuery("SELECT * FROM queue WHERE is_downloded =0 ORDER BY id DESC", null);

				} else if (pos == 2) {
					Log.d(TAG, "onReceive: 2");
					c = Database.readabelDb.rawQuery("SELECT * FROM queue WHERE is_downloded =1 ORDER BY id DESC", null);
				} else if (pos == 3) {
					Log.d(TAG, "onReceive: 3");
					c = Database.readabelDb.rawQuery("SELECT * FROM queue WHERE is_error=1 ORDER BY id DESC", null);
				}

				while (c.moveToNext()) {
					int id = c.getInt(c.getColumnIndex("id"));
					String src = c.getString(c.getColumnIndex("src"));
					String path = c.getString(c.getColumnIndex("path"));
					String file_name = c.getString(c.getColumnIndex("file_name"));
					String file_type = c.getString(c.getColumnIndex("file_type"));
					long fileLength = c.getLong(c.getColumnIndex("file_size"));
					long downloded = c.getLong(c.getColumnIndex("downloded"));
					boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
					boolean is_downloded = c.getInt(c.getColumnIndex("is_downloded")) == 1;
					nameWithID.put(id, new Object[]{src, path, file_name, is_pause, fileLength, downloded, file_type, is_downloded});
					ids.add(id);


				}
				notifyDataSetChanged();
				progressBars = new ProgressBar[ids.size()];
				progressBarText = new TextView[ids.size()];
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Actions.ACTION_update);
		intentFilter.addAction(Actions.ACTION_addMission);
		LocalBroadcastManager.getInstance(context).registerReceiver(br, intentFilter);
		br.onReceive(null, null);


		ResultReceiver r = new ResultReceiver(new Handler()) {
			@SuppressLint("SetTextI18n")
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				super.onReceiveResult(resultCode, resultData);
				int id = resultData.getInt("ID");
				int pos = ids.indexOf(id);
				if (resultCode == Actions.PROGRESS_UPDATE) {
					int pro = resultData.getInt("P");
					try {
						progressBarText[pos].setText("" + pro + "%");
						progressBars[pos].setVisibility(View.VISIBLE);
						progressBars[pos].setProgress(pro);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (resultCode == Actions.ERROR_UPDATE) {
					Log.d(TAG, "onReceiveResult: God error");
					br.onReceive(null, null);
					String error = resultData.getString("E");
					try {
						progressBarText[pos].setText(error);
						progressBars[pos].setVisibility(View.GONE);
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (resultCode == Actions.COMPLETE) {
					Log.d(TAG, "onReceiveResult: OnComplete");
					br.onReceive(null, null);
					notifyDataSetChanged();
					Log.d(TAG, "onReceiveResult: After Notifiy");

				}


			}
		};
		DownlodInfo.setResultReceiver(r);
	}


	void updateProgress(int pos, Bundle data) {
		int pro = data.getInt("P");
		try {
			this.progressBarText[pos].setText("" + pro + "%");
			this.progressBars[pos].setVisibility(View.VISIBLE);
			this.progressBars[pos].setProgress(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void showError(int pos, Bundle data) {
		String error = data.getString("E");
		try {
			this.progressBarText[pos].setText(error);
			this.progressBars[pos].setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void setComplte(int pos, Bundle data) {
		try {
			this.progressBarText[pos].setText("Downlod Complete");
			this.progressBars[pos].setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	int getProgress(long downloded, long filesize) {

		double p = (double) (downloded * 100) / filesize;
		final int currentProgress = (int) ((((double) downloded) / ((double) filesize)) * 100);
		Log.d("fuck", "getProgress::-   downlode : " + downloded + "filesize  =" + filesize + "getProgress: " + (int) currentProgress);
		return currentProgress;

	}

	@NonNull
	@Override
	public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View convertView = inflater.inflate(R.layout.downloditem, parent, false);
		viewHolder holder = new viewHolder(convertView);
		return holder;
	}


	@SuppressLint("ResourceAsColor")
	@Override
	public void onBindViewHolder(@NonNull viewHolder holder, int position) {
		if (ids != null) {
			int id = ids.get(position);
			Log.d(TAG, "onBindViewHolder: " + id);
			holder.downText.setText(nameWithID.get(id)[2].toString());
			holder.path.setText(nameWithID.get(id)[1].toString());
			Log.d(TAG, "onBindViewHolder: " + nameWithID.get(id)[4]);
			holder.typeText.setText(nameWithID.get(id)[6].toString());
			ProgressBar p = holder.downProgress;
			TextView pt = holder.downProgressText;
			progressBarText[position] = pt;
			progressBars[position] = p;
			p.setProgress(getProgress((long) nameWithID.get(id)[5], (long) nameWithID.get(id)[4]));
			String prog = String.valueOf(getProgress((long) nameWithID.get(id)[5], (long) nameWithID.get(id)[4]));
			Log.d(TAG, "onBindViewHolder: " + prog);
			pt.setText(prog + "%");
			holder.bytes.setText(Formatter.formatFileSize(context, (long) nameWithID.get(id)[4]));


			if ((boolean) nameWithID.get(id)[7]) {
				p.setProgress(100);
				pt.setText("Complete");
				holder.pause.setEnabled(false);
				holder.pause.setBackgroundResource(R.drawable.button_disabeld);
			} else {
				holder.pause.setBackgroundResource(R.drawable.cirecleimage);
				holder.pause.setPadding(5, 5, 5, 5);
				holder.pause.setEnabled(true);
			}

			if ((boolean) nameWithID.get(id)[3]) {
				holder.pause.setImageResource(R.drawable.start);
			} else {
				holder.pause.setImageResource(R.drawable.pause);
			}
			holder.pause.setOnClickListener(v -> {

				Cursor c = Database.readabelDb.rawQuery("SELECT is_pause FROM queue WHERE id=?", new String[]{Integer.toString(id)});
				c.moveToPosition(0);
				boolean is_pause = c.getInt(0) == 1;
				Log.d(TAG, "onBindViewHolder: Not True ::: " + is_pause);
				if (is_pause) {
					holder.pause.setImageResource(R.drawable.pause);
					Log.d(TAG, "onBindViewHolder:  isPauseTrue");
					LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_resume).putExtra("ID", id));
				} else {
					holder.pause.setImageResource(R.drawable.start);
					Log.d(TAG, "onBindViewHolder:  isPauseFalse");
					LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_pause).putExtra("ID", id));
				}


			});
			holder.cancel.setOnClickListener(v -> {
				LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Actions.ACTION_cancel).putExtra("ID", id));
			});


		}
	}


	@Override
	public int getItemCount() {
		return ids.size();
	}

	class viewHolder extends RecyclerView.ViewHolder {
		ImageView downImage;
		TextView downText;
		ImageButton downPs;
		ProgressBar downProgress;
		TextView downProgressText;
		ImageButton downCancel;
		TextView path;
		TextView typeText;
		ImageButton cancel;
		ImageButton pause;
		TextView bytes;

		public viewHolder(@NonNull View itemView) {
			super(itemView);
			downImage = itemView.findViewById(R.id.downImage);
			bytes = itemView.findViewById(R.id.bytes);
			downText = (TextView) itemView.findViewById(R.id.downText);
			downPs = itemView.findViewById(R.id.downPS);
			downProgress = itemView.findViewById(R.id.downprogressBar);
			downProgressText = itemView.findViewById(R.id.downprogressBarText);
			downCancel = itemView.findViewById(R.id.downCancel);
			path = itemView.findViewById(R.id.pathText);
			typeText = itemView.findViewById(R.id.type_text);
			cancel = itemView.findViewById(R.id.downCancel);
			pause = itemView.findViewById(R.id.downPS);

		}
	}
}
