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

public class resumeDownlod extends DialogFragment {
	private final Database db;
	DialogProperties properties;
	FilePickerDialog pickerDialog;

	DialogProperties resumeProperties;
	FilePickerDialog resumeDialog;
	long contentLength = 0;
	int responceCode = 0;
	View dialogView;
	//Views
	//src
	TextInputLayout srcLayout;
	EditText srcEditText;
	TextView fileSize;
	ProgressBar downlodSizeProgress;
	//resumepath
	EditText resumefile;
	TextView resumefileChoosBtn;
	TextView startFrom;
	private boolean isVAlid = false;
	//brodcats
	private Intent brodcastIntent;
	private CDmMain cDmMain;

	public resumeDownlod(Database db) {
		this.db = db;
		this.brodcastIntent = new Intent();
		this.brodcastIntent.setAction(Actions.ACTION_addExMission);


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

		resumeProperties = new DialogProperties();
		resumeProperties.selection_mode = DialogConfigs.SINGLE_MODE;
		resumeProperties.selection_type = DialogConfigs.FILE_SELECT;
		resumeProperties.root = new File(DialogConfigs.STORAGE_DIR);
		resumeProperties.error_dir = new File(DialogConfigs.STORAGE_DIR);
		resumeProperties.offset = new File(DialogConfigs.STORAGE_DIR);
		resumeDialog = new FilePickerDialog(getContext(), this.resumeProperties);
		resumeDialog.setTitle("Select File You Want Resume");


	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


		Dialog dialog = super.onCreateDialog(savedInstanceState);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		this.dialogView = LayoutInflater.from(getContext()).inflate(R.layout.resumedownlod, null);
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

		//resumefile
		resumefile = dialogView.findViewById(R.id.resumelinkIn);
		resumefileChoosBtn = dialogView.findViewById(R.id.chooseExistFile);
		startFrom = dialogView.findViewById(R.id.startFrom);


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
							resumeDownlod.this.contentLength = len;
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
								String length = Formatter.formatFileSize(getContext(), len);
								fileSize.setText(length);
								srcLayout.setErrorEnabled(false);
								isVAlid = true;

							}

						}
					}.execute(s.toString());
				}


			}

		});


		resumefileChoosBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resumeDownlod.this.resumeDialog.show();
				resumeDownlod.this.resumeDialog.setDialogSelectionListener(new DialogSelectionListener() {
					@Override
					public void onSelectedFilePaths(String[] files) {
						try {
							File file = new File(files[0]);
							Log.d("fuck", "onSelectedFilePaths: path :" + file.getPath() + "Absolute path:" + file.getAbsolutePath() + "getPArenrt:" + file.getParent()
									+ "Get Cannocial:" + file.getCanonicalPath());
							long lengthL = file.length();

							String length = Formatter.formatFileSize(getContext(), lengthL);
							startFrom.setText("Start From : " + length);
							resumefile.setText(files[0]);


						} catch (Exception e) {
						}

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
						File f = new File(resumefile.getText().toString());
						if (resumefile.length() == 0) {
							Toast.makeText(getContext(), "No resume file", Toast.LENGTH_LONG).show();

						} else if (f.length() >= contentLength) {
							Toast.makeText(getContext(), "Cannot Resume it ", Toast.LENGTH_LONG).show();
						} else if (!URLUtil.isValidUrl(srcEditText.getText().toString())) {
							Toast.makeText(getContext(), "enter VAlid URL", Toast.LENGTH_LONG).show();
						} else {

							if (resumeDownlod.this.contentLength == -1 || responceCode != HttpURLConnection.HTTP_OK) {
								b.setText("Open In web Browser");
								Intent intent = new Intent(getContext(), DownlodWebView.class);
								intent.putExtra("URL", srcEditText.getText().toString());
								Log.d("fuck", "onClick: strt activury");
								getActivity().startActivityForResult(intent, 200);
							} else {
								//getDAta
								b.setText("Downlod");
								String src = srcEditText.getText().toString();
								String ExitsfileNam = resumefile.getText().toString();
								Log.d("fuck", "onClick: " + ExitsfileNam);
								int id = db.addItem(src, f.getParent(), f.getName());
								db.setDownloded(id, f.length());
								int i = f.getName().lastIndexOf('.');
								String extension = "undifinted";
								if (i > 0) {
									extension = f.getName().substring(i + 1);
								}
								db.setFileType(id, "." + extension);
								db.setFileSize(id, contentLength);

								Log.d("fuck", "onClick: " + extension + " :::" + contentLength);
								resumeDownlod.this.brodcastIntent.putExtra("ID", id);
								LocalBroadcastManager.getInstance(getContext()).sendBroadcast(brodcastIntent);
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

