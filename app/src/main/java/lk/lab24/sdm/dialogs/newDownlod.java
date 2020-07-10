package lk.lab24.sdm.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import lk.lab24.sdm.Backend.Actions;
import lk.lab24.sdm.Backend.CDmMain;
import lk.lab24.sdm.Database;
import lk.lab24.sdm.DownlodWebView;
import lk.lab24.sdm.R;

public class newDownlod extends DialogFragment {
	private final Database db;
	DialogProperties properties;
	FilePickerDialog pickerDialog;
	long contentLength = 0;
	int responceCode = 0;
	View dialogView;
	//Views
	//src
	TextInputLayout srcLayout;
	EditText srcEditText;
	TextView fileSize;
	ProgressBar downlodSizeProgress;
	//downlodedFilename
	TextInputLayout fileNameLAyout;
	EditText fileName;
	//downlod Path
	EditText downPath;
	TextView chooseBtn;
	private boolean isVAlid = false;
	//brodcats
	private Intent brodcastIntent;
	private CDmMain cDmMain;

	public newDownlod(Database db) {
		this.db = db;
		this.brodcastIntent = new Intent();
		this.brodcastIntent.setAction(Actions.ACTION_addMission);



	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		properties = new DialogProperties();
		properties.selection_mode = DialogConfigs.SINGLE_MODE;
		properties.selection_type = DialogConfigs.DIR_SELECT;
		properties.root = new File(DialogConfigs.STORAGE_DIR);
		properties.error_dir = new File(DialogConfigs.STORAGE_DIR);
		properties.offset = new File(DialogConfigs.STORAGE_DIR);
		this.pickerDialog = new FilePickerDialog(getContext(), this.properties);
		pickerDialog.setTitle("Select a Downloded Folder");


	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


		Dialog dialog = super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		this.dialogView = LayoutInflater.from(getContext()).inflate(R.layout.newdownlod, null);
		//instalize
		return this.instalize(builder);


	}

	private Dialog instalize(AlertDialog.Builder builder) {
		//create dialog and set View
		builder.setView(dialogView).setPositiveButton("Downlod", null).setNegativeButton("cancel", null);
		final AlertDialog dialogM = builder.create();

		//installize Views
		//src
		srcLayout = dialogView.findViewById(R.id.srcLayout_new);
		srcEditText = dialogView.findViewById(R.id.src_new);
		fileSize = dialogView.findViewById(R.id.downSize_new);
		downlodSizeProgress = dialogView.findViewById(R.id.downSizeProgress_new);

		//downlodedFilename
		fileNameLAyout = dialogView.findViewById(R.id.fileNameLayout_new);
		fileName = dialogView.findViewById(R.id.downlodFileName_new);

		//downlod Path
		downPath = dialogView.findViewById(R.id.path_new);
		chooseBtn = dialogView.findViewById(R.id.path_Choose_new);


		srcEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@SuppressLint("StaticFieldLeak")
			@Override
			public void afterTextChanged(final Editable s) {
				if (!URLUtil.isValidUrl(s.toString())) {
					srcLayout.setError("Url Not VAlid");

				} else {
					srcLayout.setErrorEnabled(false);
					new AsyncTask<String, Void, Long>() {
						@Override
						protected Long doInBackground(String... strings) {
							long size = 0;
							try {

								URL url = new URL(strings[0]);
								HttpURLConnection http = (HttpURLConnection) openC(url);
								responceCode = http.getResponseCode();
								size = http.getContentLength();
								Log.d("fuck", http.getContent().toString() + "doInBackground: size" + http.getContentLength() + "protocol = " + http.getResponseCode());

							} catch (Exception e) {
								e.printStackTrace();
							}
							return size;
						}

						@Override
						protected void onPreExecute() {
							super.onPreExecute();
							srcLayout.setErrorEnabled(false);
							downlodSizeProgress.setVisibility(View.VISIBLE);
							fileSize.setVisibility(View.GONE);
						}


						@Override
						protected void onPostExecute(Long len) {
							super.onPostExecute(len);
							newDownlod.this.contentLength = len;
							downlodSizeProgress.setVisibility(View.GONE);
							fileSize.setVisibility(View.VISIBLE);
							if (len == -1 || responceCode != HttpURLConnection.HTTP_OK) {
								Log.d("fuck", "onPostExecute: len:" + len + "responce :" + responceCode);
								fileSize.setVisibility(View.VISIBLE);
								fileSize.setText("Damn-- This is Not a Downlod Link :(");
								Toast.makeText(getContext(), "cannnotGetContentLength", Toast.LENGTH_LONG).show();
								Button b = dialogM.getButton(AlertDialog.BUTTON_POSITIVE);
								b.setText("OPen In Browese");
							} else {
								Button b = dialogM.getButton(AlertDialog.BUTTON_POSITIVE);
								b.setText("Downlod");
								String src = srcEditText.getText().toString();
								String[] w = src.split("/|\\.|//");
								String length = Formatter.formatFileSize(getContext(), len);
								fileSize.setText(length);
								fileName.setText(w[(w.length - 2)]);
								srcLayout.setErrorEnabled(false);
								isVAlid = true;

							}

						}
					}.execute(s.toString());
				}


			}

		});


		chooseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				newDownlod.this.pickerDialog.show();
				newDownlod.this.pickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
					@Override
					public void onSelectedFilePaths(String[] files) {
						downPath.setText(files[0]);
					}
				});
			}
		});
//Dialog

		dialogM.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				final Button b = dialogM.getButton(AlertDialog.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						if (fileName.length() == 0) {

							fileNameLAyout.setError("Cannot Empty");
							Toast.makeText(getContext(), "file NAme Empty", Toast.LENGTH_LONG).show();

						} else if (downPath.length() == 0) {
							Toast.makeText(getContext(), "enter VAlid Path", Toast.LENGTH_LONG).show();
						} else if (!URLUtil.isValidUrl(srcEditText.getText().toString())) {
							Toast.makeText(getContext(), "enter VAlid URL", Toast.LENGTH_LONG).show();
						} else {

							if (newDownlod.this.contentLength == -1 || responceCode != HttpURLConnection.HTTP_OK) {
								b.setText("Open In web Browser");
								Intent intent = new Intent(getContext(), DownlodWebView.class);
								intent.putExtra("URL", srcEditText.getText().toString());
								Log.d("fuck", "onClick: strt activury");
								getActivity().startActivityForResult(intent, 200);
							} else {
								//getDAta
								b.setText("Downlod");
								String src = srcEditText.getText().toString();
								String path = downPath.getText().toString();
								String fileNam = fileName.getText().toString();
								int id = db.addItem(src, path, fileNam);
								Toast.makeText(getContext(), id + "id : downlod", Toast.LENGTH_LONG).show();
								newDownlod.this.brodcastIntent.putExtra("ID", id);
								LocalBroadcastManager.getInstance(getContext()).sendBroadcast(newDownlod.this.brodcastIntent);
								dialogM.dismiss();

							}

						}
					}
				});

			}
		});
		return dialogM;
	}


	public void setValues(String url) {
		srcEditText.setText(url);

	}

	public URLConnection openC(URL u) throws IOException {
		URLConnection con = u.openConnection();
		return con;
	}

}


//                Cursor c = MyIntentService.this.readabelDb.rawQuery("SELECT * FROM queue WHERE is_get = ?", new String[]{Integer.toString(0)});
//                while (c.moveToNext()) {
//                    int id = c.getInt(c.getColumnIndex("id"));
//                    String src = c.getString(c.getColumnIndex("src"));
//                    String path = c.getString(c.getColumnIndex("path"));
//                    String file_name = c.getString(c.getColumnIndex("file_name"));
//                    boolean is_pause = c.getInt(c.getColumnIndex("is_pause")) == 1;
//                    boolean is_cancel = c.getInt(c.getColumnIndex("is_cancel")) == 1;
//                    Log.d("fuck", src + "Intent Service id = " + id+"src = "+src);
//                    if (!is_pause) {
//                        cdm.addMision(id, src, path, file_name);
//                    }

