package com.agitive.usembassy.adapters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment;
import com.agitive.usembassy.fragments.layoutFragments.FileManagerFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.objects.FileItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class FilesAdapter extends ArrayAdapter<FileItem> {
	
	private static final String EMAIL_CONTENT = "<!DOCTYPE html><html><body><h1>US Mission Poland</h1><p>Download file %s/%s</p><a href=\"91.121.155.99/files/get_en_file/%s\">English version</a><br/><a href=\"91.121.155.99/files/get_pl_file/%s\">Polish version</a></body></html>";
	
	private Context context;
	private ArrayList<FileItem> files;
	private int resourceId;
	private Fragment fragment;
	private View rows[];
	
	public FilesAdapter(Context context, int resourceId, ArrayList<FileItem> files, Fragment fragment) {
		super(context, resourceId, files);
		
		this.context = context;
		this.files = files;
		this.resourceId = resourceId;
		this.fragment = fragment;
		this.rows = new View[this.files.size()];
	}
	
	@Override
	public int getCount() {
		return this.files.size();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		LayoutInflater layoutInflater = ((Activity)this.context).getLayoutInflater();
		View row = layoutInflater.inflate(resourceId, parent, false);
		
		//setUpdateButtonDimensions(row);
		//setDeleteButtonDimensions(row);
		this.rows[position] = row;
		setName(position, row);
		setFileDetail1(position, row);
		setFileDetail2(position, row);
		setIcon(position, row);
		setProgressBar(position, row);
		setLeftMargin(position, row);
		setButtonSendOnClickListener(position, row);
		setUpdateButtonColor(position, row);
		setDeleteButtonColor(position, row);
		setUpdateButtonOnClickListener(position, row);
		setDeleteButtonOnClickListener(position, row);
		
		return row;
	}
	
	public void updateItemHeightAndChangeFrontLayoutBackgroundColor(int position) {
		setUpdateButtonHeight(position);
		setDeleteButtonHeight(position);
		setFrontLayoutBackgroungColor(position, this.context.getResources().getColor(R.color.file_manager_front_background_transparent));
	}
	
	private void setUpdateButtonHeight(int position) {
		RelativeLayout frontLayoutBackground = (RelativeLayout) this.rows[position].findViewById(R.id.swipe_list_view_front_layout_background);
		if (frontLayoutBackground == null) {
			return;
		}
		
		RelativeLayout updateLayout = (RelativeLayout) this.rows[position].findViewById(R.id.swipe_list_view_update_layout);
		if (updateLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) updateLayout.getLayoutParams();
		params.height = frontLayoutBackground.getHeight();
		updateLayout.setLayoutParams(params);
	}
	
	private void setDeleteButtonHeight(int position) {
		RelativeLayout frontLayoutBackground = (RelativeLayout) this.rows[position].findViewById(R.id.swipe_list_view_front_layout_background);
		if (frontLayoutBackground == null) {
			return;
		}
		
		RelativeLayout deleteLayout = (RelativeLayout) this.rows[position].findViewById(R.id.swipe_list_view_delete_layout);
		if (deleteLayout == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) deleteLayout.getLayoutParams();
		params.height = frontLayoutBackground.getHeight();
		deleteLayout.setLayoutParams(params);
	}
	
	private void setLeftMargin(int position, View row) {
		if (checkFileEntryExistInDatabase(this.files.get(position).getId()) &&
				!this.files.get(position).getDownloading()) {
			if (this.files.get(position).getVersion() > getfileVersion(this.files.get(position).getId())) {
				RelativeLayout frontLayoutBackground = (RelativeLayout) row.findViewById(R.id.swipe_list_view_front_layout_background);
				if (frontLayoutBackground == null) {
					return;
				}
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) frontLayoutBackground.getLayoutParams();
				params.leftMargin = (int) this.context.getApplicationContext().getResources().getDimension(R.dimen.margin_small);
				frontLayoutBackground.setLayoutParams(params);
				
				setFrontLayoutBackgroungColor(position, this.context.getResources().getColor(R.color.file_manager_front_background_orange));
			}	
		}
	}
	
	private void setFrontLayoutBackgroungColor(int position, int color) {
		RelativeLayout frontLayout = (RelativeLayout) this.rows[position].findViewById(R.id.swipe_list_view_front_layout);
		if (frontLayout == null) {
			return;
		}
		
		frontLayout.setBackgroundColor(color);
	}
	
	private void setProgressBarDimensions(View row) {
		ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.swipe_list_view_progress_bar);
		if (progressBar == null) {
			return;
		}
		
		Bitmap icon = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.file_manager_file_downloaded);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
		params.width = icon.getWidth();
		params.height = icon.getWidth();
		progressBar.setLayoutParams(params);
	}
	
	private void setProgressBar(int position, View row) {
		ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.swipe_list_view_progress_bar);
		if (progressBar == null) {
			return;
		}
		
		setProgressBarDimensions(row);
		
		if (this.files.get(position).getDownloading()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setFileIsDownloading(FileItem file) {
		file.setDownloading(true);
	}
	
	private void insertFileIntoDatabase(FileItem file) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.context.getApplicationContext());
		
		if (databaseAdapter.hasFile(file.getId())) {
			databaseAdapter.updateFile(file);
		} else {
			databaseAdapter.insertFile(file);
			
		}
	}
	
	private void setUpdateButtonOnClickListener(final int position, final View row) {
		RelativeLayout updateLayout = (RelativeLayout) row.findViewById(R.id.swipe_list_view_update_layout);
		if (updateLayout == null) {
			return;
		}
		
		updateLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isInternetConnection()) {
					return;
				}
						
				if (checkFileEntryExistInDatabase(files.get(position).getId()) &&
						!files.get(position).getDownloading()) {
					if (files.get(position).getVersion() > getfileVersion(files.get(position).getId())) {
						setFileIsDownloading(files.get(position));
						insertFileIntoDatabase(files.get(position));
						((FileManagerFragment)fragment).closeOpenedItems();
						notifyDataSetChanged();
						addOneFileDownloaderFragment(position);
					}
				
				}
			}
		});
	}
	
	private void addOneFileDownloaderFragment(int position) {
		FragmentManager fragmentManager = this.fragment.getChildFragmentManager();
		OneFileDownloaderFragment oneFileDownloaderFragment = new OneFileDownloaderFragment();
		oneFileDownloaderFragment.setArguments(createArgumentsForOneFileDownloaderFragment(this.files.get(position)));
		
		try {
			fragmentManager.beginTransaction().add(oneFileDownloaderFragment, null).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForOneFileDownloaderFragment(FileItem file) {
		Bundle arguments = new Bundle();
		arguments.putInt(OneFileDownloaderFragment.ARGUMENTS_FILE_ID_KEY, file.getId());
		arguments.putString(OneFileDownloaderFragment.ARGUMENTS_FILE_NAME_EN_KEY, file.getNameEn());
		arguments.putString(OneFileDownloaderFragment.ARGUMENTS_FILE_NAME_PL_KEY, file.getNamePl());
		arguments.putInt(OneFileDownloaderFragment.ARGUMENTS_FILE_UPDATED_DAY_KEY, file.getUpdatedDay());
		arguments.putInt(OneFileDownloaderFragment.ARGUMENTS_FILE_UPDATED_MONTH_KEY, file.getUpdatedMonth());
		arguments.putInt(OneFileDownloaderFragment.ARGUMENTS_FILE_UPDATED_YEAR_KEY, file.getUpdatedYear());
		arguments.putInt(OneFileDownloaderFragment.ARGUMENTS_FILE_VERSION_KEY, file.getVersion());
		arguments.putDouble(OneFileDownloaderFragment.ARGUMENTS_FILE_SIZE_KEY, file.getSize());
		arguments.putString(OneFileDownloaderFragment.ARGUMENTS_FILE_URL_EN_KEY, file.getUrlEn());
		arguments.putString(OneFileDownloaderFragment.ARGUMENTS_FILE_URL_PL_KEY, file.getUrlPl());
		arguments.putBoolean(OneFileDownloaderFragment.ARGUMENTS_FILE_DOWNLOADING_KEY, file.getDownloading());
		
		return arguments;
	}
	
	private void setIcon(int position, View row) {
		ImageView icon = (ImageView) row.findViewById(R.id.swipe_list_view_icon);
		if (icon == null) {
			return;
		}
		
		if (checkFileEntryExistInDatabase(this.files.get(position).getId()) &&
				!this.files.get(position).getDownloading()) {
			icon.setImageResource(R.drawable.file_manager_file_downloaded);
		} else {
			icon.setImageResource(R.drawable.file_manager_file_not_downloaded);
		}
		
		if (this.files.get(position).getDownloading()) {
			icon.setVisibility(View.INVISIBLE);
		} else {
			icon.setVisibility(View.VISIBLE);
		}
	}
	
	private void setDeleteButtonColor(final int position, View row) {
		RelativeLayout deleteLayout = (RelativeLayout) row.findViewById(R.id.swipe_list_view_delete_layout);
		if (deleteLayout == null) {
			return;
		}
		
		if (checkFileEntryExistInDatabase(this.files.get(position).getId())) {
			if (this.files.get(position).getDownloading()) {
				deleteLayout.setBackgroundColor(this.context.getResources().getColor(R.color.file_manager_not_active_button));
			} else {
				deleteLayout.setBackgroundColor(this.context.getResources().getColor(R.color.file_manager_delete_button));
			}
		} else {
			deleteLayout.setBackgroundColor(this.context.getResources().getColor(R.color.file_manager_not_active_button));
		}
	}
	
	public boolean isInternetConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
	
		return (networkInfo != null &&
				networkInfo.isConnected());
	}
	
	private void setUpdateButtonColor(int position, View row) {
		RelativeLayout updateLayout = (RelativeLayout) row.findViewById(R.id.swipe_list_view_update_layout);
		if (updateLayout == null) {
			return;
		}
		
		if (checkFileEntryExistInDatabase(this.files.get(position).getId()) &&
				!this.files.get(position).getDownloading()) {
			if (this.files.get(position).getVersion() > getfileVersion(this.files.get(position).getId())) {
				updateLayout.setBackgroundColor(this.context.getResources().getColor(R.color.file_manager_update_button));
			} else {
				updateLayout.setBackgroundColor(this.context.getResources().getColor(R.color.file_manager_not_active_button));
			}
		} else {
			updateLayout.setBackgroundColor(this.context.getResources().getColor(R.color.file_manager_not_active_button));
		}
	}
	
	private void startEmailActivity(FileItem file) {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/html");
		String emailContent = FilesAdapter.EMAIL_CONTENT;
		emailContent = String.format(emailContent, file.getNameEn(), file.getNamePl(), file.getId(), file.getId());
		emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(emailContent));
		context.startActivity(emailIntent);
	}
	
	private void setButtonSendOnClickListener(final int position, View row) {
		RelativeLayout buttonLayout = (RelativeLayout) row.findViewById(R.id.swipe_view_send_button_layout);
		if (buttonLayout == null) {
			return;
		}
		
		buttonLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startEmailActivity(files.get(position));
			}
			
		});
	}
	
	private void setFileDetail2(int position, View row) {
		String fileDateText = getDateText(position);
		CustomTextView fileDate = (CustomTextView) row.findViewById(R.id.swipe_list_view_file_date);
		if (fileDate == null) {
			return;
		}
		
		fileDate.setText(fileDateText);
		
		if (checkFileEntryExistInDatabase(this.files.get(position).getId()) &&
				!this.files.get(position).getDownloading()) {
			if (this.files.get(position).getVersion() > getfileVersion(this.files.get(position).getId())) {
				fileDate.setTextColor(this.context.getResources().getColor(R.color.file_manager_update_button));
			}
		}
	}
	
	private String getDateText(int position) {
		GregorianCalendar date = new GregorianCalendar(this.files.get(position).getUpdatedYear(), this.files.get(position).getUpdatedMonth() - 1, this.files.get(position).getUpdatedDay());
		SimpleDateFormat simpleDate;
		if (getAppLanguage().equals("EN")) {
			simpleDate = new SimpleDateFormat("LLLL dd, yyyy", Locale.US);
		} else {
			simpleDate = new SimpleDateFormat("dd.MM.yyyy");
		}
		
		return simpleDate.format(date.getTime());
	}
	
	private int getfileVersion(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.context);
		
		
		int version = databaseAdapter.getFile(id).getVersion();
		
		
		return version;
	}
	
	private boolean checkFileEntryExistInDatabase(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this.context);
		boolean result = databaseAdapter.hasFile(id);
		
		return result;
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = this.context.getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void setName(int position, View row) {
		CustomTextView fileName = (CustomTextView) row.findViewById(R.id.swipe_list_view_file_name);
		if (fileName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			fileName.setText(this.files.get(position).getNameEn());
		} else {
			fileName.setText(this.files.get(position).getNamePl());
		}
	}
	
	private void setFileDetail1(int position, View row) {
		CustomTextView fileDetail1 = (CustomTextView) row.findViewById(R.id.swipe_list_view_file_detail_1);
		if (fileDetail1 == null) {
			return;
		}
		
		String detail = "PDF  |  ";
		detail += this.files.get(position).getSize();
		detail += "MB  |";
		fileDetail1.setText(detail);
	}
	
	private void deleteFilePl(int position) {
		String fileName = files.get(position).getId() + "_PL.pdf";
		File file = new File(context.getFilesDir(), fileName);
		file.delete();
	}
	
	private void deleteFileEn(int position) {
		String fileName = files.get(position).getId() + "_EN.pdf";
		File file = new File(context.getFilesDir(), fileName);
		file.delete();
	}
	
	private void setDeleteButtonOnClickListener(final int position, View row) {
		RelativeLayout deleteLayout = (RelativeLayout) row.findViewById(R.id.swipe_list_view_delete_layout);
		if (deleteLayout == null) {
			return;
		}
		
		deleteLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkFileEntryExistInDatabase(files.get(position).getId()) &&
						!files.get(position).getDownloading()) {
					deleteFileEn(position);
					deleteFilePl(position);
					
					DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
					databaseAdapter.deleteFile(files.get(position).getId());
					
					((FileManagerFragment)fragment).closeOpenedItems();
					notifyDataSetChanged();
				}
			}
		});
	}
}
