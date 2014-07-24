package com.agitive.usembassy.fragments.asyncTaskFragments;

import com.agitive.usembassy.fragments.layoutFragments.FileManagerFragment;
import com.agitive.usembassy.network.OneFileDownloaderAsyncTask;
import com.agitive.usembassy.objects.FileItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class OneFileDownloaderFragment extends Fragment {

	public static final String TAG = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.tag";
	public static final String ARGUMENTS_FILE_ID_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileIdKey";
	public static final String ARGUMENTS_FILE_NAME_EN_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileNameEnKey";
	public static final String ARGUMENTS_FILE_NAME_PL_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileNamePlKey";
	public static final String ARGUMENTS_FILE_UPDATED_DAY_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileUpdatedDayKey";
	public static final String ARGUMENTS_FILE_UPDATED_MONTH_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileUpdatedMonthKey";
	public static final String ARGUMENTS_FILE_UPDATED_YEAR_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileUpdatedYearKey";
	public static final String ARGUMENTS_FILE_VERSION_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileVersionKey";
	public static final String ARGUMENTS_FILE_SIZE_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileSizeKey";
	public static final String ARGUMENTS_FILE_URL_EN_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileUrlEnKey";
	public static final String ARGUMENTS_FILE_URL_PL_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileUrlPlKey";
	public static final String ARGUMENTS_FILE_DOWNLOADING_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.argumentsFileDownloadingKey";
	
	private static final String SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY = "com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment.savedInstanceStateIsFirstRunKey";
	private static OneFileDownloaderFragment oneFileDownloaderFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OneFileDownloaderFragment.oneFileDownloaderFragment = this;
		
		if (savedInstanceState != null) {
			return;
		}
		
		
		OneFileDownloaderAsyncTask oneFileDownloaderAsyncTask = new OneFileDownloaderAsyncTask(getActivity().getApplicationContext());	
		oneFileDownloaderAsyncTask.execute(createFileItemObjectFromArguments());
	}
	
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        
        savedInstanceState.putBoolean(OneFileDownloaderFragment.SAVED_INSTANCE_STATE_IS_FIRST_RUN_KEY, false);
    }
	
	private FileItem createFileItemObjectFromArguments() {
		int id = getArguments().getInt(OneFileDownloaderFragment.ARGUMENTS_FILE_ID_KEY);
		String nameEn = getArguments().getString(OneFileDownloaderFragment.ARGUMENTS_FILE_NAME_EN_KEY);
		String namePl = getArguments().getString(OneFileDownloaderFragment.ARGUMENTS_FILE_NAME_PL_KEY);
		int updatedDay = getArguments().getInt(OneFileDownloaderFragment.ARGUMENTS_FILE_UPDATED_DAY_KEY);
		int updatedMonth = getArguments().getInt(OneFileDownloaderFragment.ARGUMENTS_FILE_UPDATED_MONTH_KEY);
		int updatedYear = getArguments().getInt(OneFileDownloaderFragment.ARGUMENTS_FILE_UPDATED_YEAR_KEY);
		int version = getArguments().getInt(OneFileDownloaderFragment.ARGUMENTS_FILE_VERSION_KEY);
		double size = getArguments().getDouble(OneFileDownloaderFragment.ARGUMENTS_FILE_SIZE_KEY);
		String urlEn = getArguments().getString(OneFileDownloaderFragment.ARGUMENTS_FILE_URL_EN_KEY);
		String urlPl = getArguments().getString(OneFileDownloaderFragment.ARGUMENTS_FILE_URL_PL_KEY);
		boolean downloading = getArguments().getBoolean(OneFileDownloaderFragment.ARGUMENTS_FILE_DOWNLOADING_KEY);
		
		FileItem fileItem = new FileItem(id, nameEn, namePl, updatedDay, updatedMonth, updatedYear, version, size, urlPl, urlEn, downloading);
		
		return fileItem;
	}
	
	public static void setDownloadedFile(FileItem file) {
		if (OneFileDownloaderFragment.oneFileDownloaderFragment.getParentFragment() == null ||
				OneFileDownloaderFragment.oneFileDownloaderFragment.getActivity() == null) {
			return;
		}
		((FileManagerFragment)OneFileDownloaderFragment.oneFileDownloaderFragment.getParentFragment()).setDownloadedFile(file);
		((FileManagerFragment)OneFileDownloaderFragment.oneFileDownloaderFragment.getParentFragment()).removeOneFileDownloaderFragment();
	}
	
	public static void setDownloadingError(FileItem file) {
		((FileManagerFragment)OneFileDownloaderFragment.oneFileDownloaderFragment.getParentFragment()).setDownloadingError(file);
		((FileManagerFragment)OneFileDownloaderFragment.oneFileDownloaderFragment.getParentFragment()).removeOneFileDownloaderFragment();
	}
}
