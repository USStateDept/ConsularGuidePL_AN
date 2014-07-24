package com.agitive.usembassy.fragments.dialogFragments;

import java.util.ArrayList;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.fragments.layoutFragments.FileManagerFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.objects.FileItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FileManagerDeletingFilesDialogFragment extends DialogFragment {

	public static final String TAG = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.tag";
	public static final String ARGUMENTS_IDS_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsIdsKey";
	public static final String ARGUMENTS_NAMES_EN_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsNamesEnKey";
	public static final String ARGUMENTS_NAMES_PL_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsNamesPlKey";
	public static final String ARGUMENTS_UPDATED_DAYS_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsUpdatedDaysKey";
	public static final String ARGUMENTS_UPDATED_MONTHS_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsUpdatedMonthsKey";
	public static final String ARGUMENTS_UPDATED_YEARS_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsUpdatedYearsKey";
	public static final String ARGUMENTS_VERSIONS_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsVersionsKey";
	public static final String ARGUMENTS_SIZES_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsSizesKey";
	public static final String ARGUMENTS_URLS_EN_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsUrlsEnKey";
	public static final String ARGUMENTS_URLS_PL_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsUrlsPlKey";
	public static final String ARGUMENTS_DOWNLOADINGS_KEY = "com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment.argumentsDownloadingKey";
	
	private ArrayList<FileItem> files;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		createFilesFromArguments();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.file_manager_deleting_files_dialog_fragment_title));
		builder.setMessage(createMessage());
		builder.setPositiveButton(R.string.file_manager_deleting_files_dialog_fragment_ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				((FileManagerFragment)getParentFragment()).deleteFiles(files);
			}
			
		});
		
		return builder.create();
	}
	
	private void createFilesFromArguments() {
		this.files = new ArrayList<FileItem>();
		
		int[] ids = getArguments().getIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_IDS_KEY);
		String[] namesEn = getArguments().getStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_NAMES_EN_KEY);
		String[] namesPl = getArguments().getStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_NAMES_PL_KEY);
		int[] updatedDays = getArguments().getIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_UPDATED_DAYS_KEY);
		int[] updatedMonths = getArguments().getIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_UPDATED_MONTHS_KEY);
		int[] updatedYears = getArguments().getIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_UPDATED_YEARS_KEY);
		int[] versions = getArguments().getIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_VERSIONS_KEY);
		double[] sizes = getArguments().getDoubleArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_SIZES_KEY);
		String[] urlsEn = getArguments().getStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_URLS_EN_KEY);
		String[] urlsPl = getArguments().getStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_URLS_PL_KEY);
		boolean[] downloadings = getArguments().getBooleanArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_DOWNLOADINGS_KEY);
		
		
		for (int index = 0; index < ids.length; ++index) {
			FileItem fileItem = new FileItem(ids[index], namesEn[index], namesPl[index], updatedDays[index], updatedMonths[index], updatedYears[index], versions[index], sizes[index], urlsPl[index], urlsEn[index], downloadings[index]);
			this.files.add(fileItem);
		}
	}
	
	private String createMessage() {
		String message = "";
		for (FileItem file: this.files) {
			if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
				message += ("- " + file.getNameEn() + "\n");
			} else {
				message += ("- " + file.getNamePl() + "\n");
			}
		}
		
		return message;
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
}
