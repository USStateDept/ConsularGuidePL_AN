package com.agitive.usembassy.fragments.layoutFragments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.agitive.usembassy.R;
import com.agitive.usembassy.activities.MainActivity;
import com.agitive.usembassy.adapters.FilesAdapter;
import com.agitive.usembassy.databases.DatabaseAdapter;
import com.agitive.usembassy.databases.DatabaseReader;
import com.agitive.usembassy.databases.FileManagerLayout;
import com.agitive.usembassy.fragments.asyncTaskFragments.OneFileDownloaderFragment;
import com.agitive.usembassy.fragments.dialogFragments.FileErrorDialogFragment;
import com.agitive.usembassy.fragments.dialogFragments.FileManagerDeletingFilesDialogFragment;
import com.agitive.usembassy.fragments.dialogFragments.ServerErrorDialogFragment;
import com.agitive.usembassy.global.Global;
import com.agitive.usembassy.interfaces.FragmentAboveInterface;
import com.agitive.usembassy.interfaces.LayoutFragmentInterface;
import com.agitive.usembassy.layouts.CustomTextView;
import com.agitive.usembassy.libraries.SwipeListView.BaseSwipeListViewListener;
import com.agitive.usembassy.libraries.SwipeListView.SwipeListView;
import com.agitive.usembassy.network.FileDownloaderAsyncTask;
import com.agitive.usembassy.objects.FileItem;
import com.radaee.reader.PDFReaderAct;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class FileManagerFragment extends Fragment implements LayoutFragmentInterface, FragmentAboveInterface {

	public static final String LAYOUT_ID_KEY = "com.agitive.usembassy.fragments.FileManagerFragment.layoutIdKey";
	
	private View rootView;
	private boolean canScroll;
	private HashMap<Integer, FileItem> fileLayoutIdFileItem;
	private HashMap<Integer, Integer> fileLayoutIdIconId;
	private HashMap<Integer, Integer> fileLayoutIdUpdateButtonId;
	private HashMap<Integer, Integer> fileLayoutIdDeleteButtonId;
	private HashMap<Integer, Integer> fileLayoutIdDateViewId;
	private HashMap<Integer, Integer> fileLayoutIdProgressBarId;
	private HashMap<Integer, Integer> deleteButtonIdFileLayoutId;
	private HashMap<Integer, Integer> updateButtonIdFileLayoutId;
	
	private static final double OFFSET_MULTIPLIER_PORTRAIT = 1;
	private static final double OFFSET_MULTIPLIER_LANDSCAPE = Global.LEFT_COLUMN_WIDTH_TO_DISPLAY_WIDTH;
	private static final String FILE_NAME_KEY_FOR_INTENT = "FileName";
	
	private FileManagerLayout fileManagerLayout;
	private ArrayList<FileItem> files;
	private FileDownloaderAsyncTask fileDownloader;
	
	@SuppressLint("UseSparseArrays")
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
		this.rootView = inflater.inflate(R.layout.file_manager_layout, container, false);
		
		removeFileManagerDeletingFilesDialogFragment();
		removeServerErrorDialogFragment();
		
    	setLayout();
    	setMarginForEmblem();
        setLayoutName();
        setBackButton();
        setSwipeListView();
        setEmptyList();
        setContent(savedInstanceState);
        
        return this.rootView;
    }
	
	@Override
	public void onDetach() {
		super.onDetach();
		
		if (this.fileDownloader != null) {
			this.fileDownloader.cancel(true);
		}
	}
	
	@Override
	public void changeLanguage() {
		showMainProgressBar();
		
		Handler handler = new Handler();
		handler.post(new Runnable() {

			@Override
			public void run() {
				setLayoutName();
		        setSwipeListView();
		        changeLanguageInSwipeListView();
		        
		        hideMainProgressBar();
			}
			
		});
	}
	
	@Override
	public void setContent() {
		if (!isOnline()) {
			setFiles(getFilesFromDatabase());
			showNoInternetForFilesListToast();
			return;
		}
		
		fileDownloader = new FileDownloaderAsyncTask(this);
    	fileDownloader.execute();
	}
	
	public void closeOpenedItems() {
		SwipeListView swipeListView = (SwipeListView) rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		swipeListView.closeOpenedItems();
	}
	
	private void setContent(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			return;
		}
		
		setContent();
	}
	
	public void removeOneFileDownloaderFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		Fragment oneFileDownloaderFragment = fragmentManager.findFragmentByTag(OneFileDownloaderFragment.TAG);
		if (oneFileDownloaderFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(oneFileDownloaderFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setFiles(ArrayList<FileItem> files) {	
		if (files == null) {
			showServerError();
			hideMainProgressBar();
			
			return;
		}
		
		this.files = files;
		
		deleteFilesWhichAreNotPresetInServerResponse(this.files);
		FilesAdapter filesAdapter = new FilesAdapter(getActivity(), R.layout.swipe_list_view_row_layout, files, this);
		SwipeListView swipeListView  = (SwipeListView) this.rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		swipeListView.setAdapter(filesAdapter);
		hideMainProgressBar();
	}
	
	public void setDownloadingError(FileItem file) {
		showFileError();
		removeFileFromDatabase(file);
		this.setFileIsNotDownloading(file);
		
		updateFilesInLocalVariable(file);
		
		SwipeListView swipeListView = (SwipeListView) this.rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		closeOpenedItems();
		((FilesAdapter)swipeListView.getAdapter()).notifyDataSetChanged();
	}
	
	public void deleteFiles(ArrayList<FileItem> filesToDelete) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity().getApplicationContext());
		
		for (FileItem file: filesToDelete) {
			deleteFileEn(file.getId());
			deleteFilePl(file.getId());
			
			databaseAdapter.deleteFile(file.getId());
		}
	}
	
	private void deleteFilePl(int id) {
		String fileName = id + "_PL.pdf";
		File file = new File(getActivity().getApplicationContext().getFilesDir(), fileName);
		file.delete();
	}
	
	private void deleteFileEn(int id) {
		String fileName = id + "_EN.pdf";
		File file = new File(getActivity().getApplicationContext().getFilesDir(), fileName);
		file.delete();
	}
	
	private boolean isFileByIdInArrayList(ArrayList<FileItem> files, int id) {
		for (FileItem file: files) {
			if (file.getId() == id) {
				return true;
			}
		}
		
		return false;
	}
	
	private void deleteFilesWhichAreNotPresetInServerResponse(ArrayList<FileItem> serverFilesInResponse) {
		ArrayList<FileItem> filesFromDatabase = getFilesFromDatabase();
		ArrayList<FileItem> filesToDelete = new ArrayList<FileItem>();
		
		for (FileItem file: filesFromDatabase) {
			if (!isFileByIdInArrayList(serverFilesInResponse, file.getId())) {
				filesToDelete.add(file);
			}
		}
		
		if (filesToDelete.size() == 0) {
			return;
		}
		
		showDialogAboutFilesToDelete(filesToDelete);
	}
	
	private Bundle createArgumentsForDeletingFilesDialgoFragment(ArrayList<FileItem> filesToDelete) {
		Bundle arguments = new Bundle();
		
		int[] ids = new int[filesToDelete.size()];
		String[] namesEn = new String[filesToDelete.size()];
		String[] namesPl = new String[filesToDelete.size()];
		int[] updatedDays = new int[filesToDelete.size()];
		int[] updatedMonths = new int[filesToDelete.size()];
		int[] updatedYears = new int[filesToDelete.size()];
		int[] versions = new int[filesToDelete.size()];
		double[] sizes = new double[filesToDelete.size()];
		String[] urlsEn = new String[filesToDelete.size()];
		String[] urlsPl = new String[filesToDelete.size()];
		boolean[] downloadings = new boolean[filesToDelete.size()];
		
		int index = 0;
		for (FileItem file: filesToDelete) {
			ids[index] = file.getId();
			namesEn[index] = file.getNameEn();
			namesPl[index] = file.getNamePl();
			updatedDays[index] = file.getUpdatedDay();
			updatedMonths[index] = file.getUpdatedMonth();
			updatedYears[index] = file.getUpdatedYear();
			versions[index] = file.getVersion();
			sizes[index] = file.getSize();
			urlsEn[index] = file.getUrlEn();
			urlsPl[index] = file.getUrlPl();
			downloadings[index] = file.getDownloading();
			
			++index;
		}
		
		arguments.putIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_IDS_KEY, ids);
		arguments.putStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_NAMES_EN_KEY, namesEn);
		arguments.putStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_NAMES_PL_KEY, namesPl);
		arguments.putIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_UPDATED_DAYS_KEY, updatedDays);
		arguments.putIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_UPDATED_MONTHS_KEY, updatedMonths);
		arguments.putIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_UPDATED_YEARS_KEY, updatedYears);
		arguments.putIntArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_VERSIONS_KEY, versions);
		arguments.putDoubleArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_SIZES_KEY, sizes);
		arguments.putStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_URLS_EN_KEY, urlsEn);
		arguments.putStringArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_URLS_PL_KEY, urlsPl);
		arguments.putBooleanArray(FileManagerDeletingFilesDialogFragment.ARGUMENTS_DOWNLOADINGS_KEY, downloadings);
		
		return arguments;
	}
	
	private void showDialogAboutFilesToDelete(ArrayList<FileItem> filesToDelete) {
		FileManagerDeletingFilesDialogFragment fileManagerDeletingFilesDialogFragment = new FileManagerDeletingFilesDialogFragment();
		fileManagerDeletingFilesDialogFragment.setArguments(createArgumentsForDeletingFilesDialgoFragment(filesToDelete));
		fileManagerDeletingFilesDialogFragment.setCancelable(false);
		
		FragmentManager fragmentManager = this.getChildFragmentManager();
		try {
			fragmentManager.beginTransaction().add(fileManagerDeletingFilesDialogFragment, FileManagerDeletingFilesDialogFragment.TAG).commit();
		} catch(IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<FileItem> getFilesFromDatabase() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		ArrayList<FileItem> filesFromDatabase = databaseAdapter.getAllFiles();
		
		return filesFromDatabase;
	}
	
	private boolean isOnline() {
	    ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
	}

	private void showNoInternetForFilesListToast() {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.file_manager_no_internet_files_list, Toast.LENGTH_LONG);
	    toast.show();
	}
	
	private void showNoInternetForFileDownloadingToast() {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(), R.string.file_manager_no_internet_file_downloading, Toast.LENGTH_LONG);
	    toast.show();
	}
	
	private void setEmptyList() {
		this.files = new ArrayList<FileItem>();
		
		FilesAdapter filesAdapter = new FilesAdapter(getActivity(), R.layout.swipe_list_view_row_layout, this.files, this);
		SwipeListView swipeListView  = (SwipeListView) this.rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		swipeListView.setAdapter(filesAdapter);
	}
	
	private void removeServerErrorDialogFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		DialogFragment serverErrorDialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(ServerErrorDialogFragment.TAG);
		if (serverErrorDialogFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(serverErrorDialogFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void removeFileManagerDeletingFilesDialogFragment() {
		FragmentManager fragmentManager = getChildFragmentManager();
		DialogFragment fileManagerDeletingFilesDialogFragment = (DialogFragment) fragmentManager.findFragmentByTag(FileManagerDeletingFilesDialogFragment.TAG);
		if (fileManagerDeletingFilesDialogFragment != null) {
			try {
				fragmentManager.beginTransaction().remove(fileManagerDeletingFilesDialogFragment).commit();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void showServerError() {
		ServerErrorDialogFragment serverErrorDialogFragment = new ServerErrorDialogFragment();
		serverErrorDialogFragment.setArguments(createArgumentsForServerErrorDialogFragment());
		serverErrorDialogFragment.setCancelable(false);
		
		FragmentManager fragmentManager = getChildFragmentManager();
		try {
			fragmentManager.beginTransaction().add(serverErrorDialogFragment, ServerErrorDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private void showFileError() {
		FileErrorDialogFragment fileErrorDialogFragment = new FileErrorDialogFragment();
		fileErrorDialogFragment.setCancelable(false);
		FragmentManager fragmentManager = getChildFragmentManager();
		
		try {
			fragmentManager.beginTransaction().add(fileErrorDialogFragment, FileErrorDialogFragment.TAG).commit();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	private Bundle createArgumentsForServerErrorDialogFragment() {
		Bundle arguments = new Bundle();
		arguments.putString(ServerErrorDialogFragment.MESSAGE_KEY, getResources().getString(R.string.file_manager_server_error_message));
		
		return arguments;
	}
	
	private boolean checkFileEntryExistInDatabase(int id) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
		boolean result = databaseAdapter.hasFile(id);
		
		return result;
	}
	
	public void setDownloadedFile(FileItem fileItem) {
		setFileIsNotDownloading(fileItem);
		insertFileIntoDatabase(fileItem);
		
		updateFilesInLocalVariable(fileItem);
		
		SwipeListView swipeListView  = (SwipeListView) this.rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		closeOpenedItems();
		((FilesAdapter)swipeListView.getAdapter()).notifyDataSetChanged();
	}
	
	private void updateFilesInLocalVariable(FileItem fileItem) {
		int position = findFilePositionById(fileItem.getId());
		if (position == -1) {
			return;
		} else {
			this.files.set(position, fileItem);
		}
	}
	
	private int findFilePositionById(int id) {
		int position = 0;
		
		for (FileItem file: this.files) {
			if (file.getId() == id) {
				return position;
			}
			
			++position;
		}
		
		return -1;
	}
	
	private void insertFileIntoDatabase(FileItem file) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity().getApplicationContext());
		
		if (databaseAdapter.hasFile(file.getId())) {
			databaseAdapter.updateFile(file);
		} else {
			databaseAdapter.insertFile(file);
			
		}
	}
	
	private void removeFileFromDatabase(FileItem file) {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity().getApplicationContext());
		
		if (!databaseAdapter.hasFile(file.getId())) {
			return;
		}
		
		databaseAdapter.deleteFile(file.getId());
	}
	
	private void changeLanguageInSwipeListView() {
		CustomTextView updateTextMeter = (CustomTextView) this.rootView.findViewById(R.id.swipe_list_view_update_text_meter);
		if (updateTextMeter == null) {
			return;
		}
		
		updateTextMeter.setText(R.string.update);
		
		CustomTextView deleteTextMeter = (CustomTextView) this.rootView.findViewById(R.id.swipe_list_view_delete_text_meter);
		if (deleteTextMeter == null) {
			return;
		}
		
		deleteTextMeter.setText(R.string.delete);
		
		setSwipeListViewRightOffset();
		setSwipeListViewLeftOffset();
		
		SwipeListView swipeListView = (SwipeListView) rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		closeOpenedItems();
		((FilesAdapter)swipeListView.getAdapter()).notifyDataSetChanged();
	}
	
	private void setSwipeListViewRightOffset() {
		final RelativeLayout updateLayoutMeter = (RelativeLayout) this.rootView.findViewById(R.id.swipe_list_view_update_layout_meter);
		if (updateLayoutMeter == null) {
			return;
		}
		
		ViewTreeObserver updateTextMeterObserver = updateLayoutMeter.getViewTreeObserver();
		updateTextMeterObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				SwipeListView swipeListView = (SwipeListView) rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
				if (swipeListView == null) {
					return;
				}
				
				swipeListView.setOffsetRight((float) (getDisplayWidth() * getOffsetMultiplier() - updateLayoutMeter.getWidth()));
			}
			
		});
	}
	
	private int getOrientation() {
		return getResources().getConfiguration().orientation;
	}
	
	private double getOffsetMultiplier() {
		if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
			return FileManagerFragment.OFFSET_MULTIPLIER_PORTRAIT;
		} else {
			return FileManagerFragment.OFFSET_MULTIPLIER_LANDSCAPE;
		}
	}
	
	private void setSwipeListViewLeftOffset() {
		final RelativeLayout deleteLayoutMeter = (RelativeLayout) this.rootView.findViewById(R.id.swipe_list_view_delete_layout_meter);
		if (deleteLayoutMeter == null) {
			return;
		}
		
		ViewTreeObserver updateTextMeterObserver = deleteLayoutMeter.getViewTreeObserver();
		updateTextMeterObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean ready = false;
			
			@Override
			public void onGlobalLayout() {
				if (this.ready) {
					return;
				}
				this.ready = true;
				
				SwipeListView swipeListView = (SwipeListView) rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
				if (swipeListView == null) {
					return;
				}
				
				swipeListView.setOffsetLeft((float) (getDisplayWidth() * getOffsetMultiplier() - deleteLayoutMeter.getWidth()));
			}
			
		});
	}
	
	@SuppressWarnings("deprecation")
	private int getDisplayWidth() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		
		if (Build.VERSION.SDK_INT < 13) {
			return display.getWidth();
		} else {
			Point size = new Point();
			display.getSize(size);
			
			return size.x;
		}
	}
	
	public HashMap<Integer, FileItem> getFileLayoutIdFileItem() {
		return this.fileLayoutIdFileItem;
	}
	
	public HashMap<Integer, Integer> getFileLayoutIdIconId() {
		return this.fileLayoutIdIconId;
	}
	
	public HashMap<Integer, Integer> getFileLayoutIdUpdateButtonId() {
		return this.fileLayoutIdUpdateButtonId;
	}
	
	public HashMap<Integer, Integer> getFileLayoutIdDeleteButtonId() {
		return this.fileLayoutIdDeleteButtonId;
	}
	
	public HashMap<Integer, Integer> getFileLayoutIdDateViewId() {
		return this.fileLayoutIdDateViewId;
	}
	
	public HashMap<Integer, Integer> getFileLayoutIdProgressBarId() {
		return this.fileLayoutIdProgressBarId;
	}
	
	public HashMap<Integer, Integer> getDeleteButtonIdFileLayoutId() {
		return this.deleteButtonIdFileLayoutId;
	}
	
	public HashMap<Integer, Integer> getUpdateButtonIdFileLayoutId() {
		return this.updateButtonIdFileLayoutId;
	}

	public View getRootView() {
		return this.rootView;
	}
	
	public void setCanScroll(boolean canScroll) {
		this.canScroll = canScroll;
	}
	
	public boolean getCanScroll() {
		return this.canScroll;
	}
	
	private void setSwipeListView() {
		setSwipeListViewListener();
		setSwipeListViewOnLongPress();
		setSwipeListViewRightOffset();
		setSwipeListViewLeftOffset();
	}
	
	private void setSwipeListViewOnLongPress() {
		SwipeListView swipeListView = (SwipeListView) rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		swipeListView.setSwipeOpenOnLongPress(false);
	}
	
	private void setSwipeListViewListener() {
		final SwipeListView swipeListView = (SwipeListView) rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
			@Override
	         public void onClickFrontView(int position) {
				executeOnClickFrontView(position);
	         }
			
			@Override
			public void onClickBackView(int position) {
				executeOnClickFrontView(position);
				swipeListView.closeAnimate(position);
			}
			
			@Override
			public void onStartOpen(int position, int action, boolean right) {
				((FilesAdapter)swipeListView.getAdapter()).updateItemHeightAndChangeFrontLayoutBackgroundColor(position);
			}
			
		});
	}
	
	private void executeOnClickFrontView(int position) {
		if (checkFileEntryExistInDatabase(files.get(position).getId())) {
			if (this.files.get(position).getDownloading()) {
				return;
			}
			
			openPDFReader(position);
		} else {
			if (this.files.get(position).getDownloading()) {
				return;
			}
			
			if (!isOnline()) {
				showNoInternetForFileDownloadingToast();
				return;
			}
			
			downloadFile(position);
		}
	}
	
	private String getAppLanguage() {
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE);

		return sharedPreferences.getString(Global.SHARED_PREFERENCES_LANGUAGE_KEY, Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH);
	}
	
	private void openPDFReader(int position) {
	    String fileName = this.files.get(position).getId() + "_" + getAppLanguage() + ".pdf";    
		Intent pdfReaderIntent = new Intent(getActivity(), PDFReaderAct.class);
		pdfReaderIntent.putExtra(FileManagerFragment.FILE_NAME_KEY_FOR_INTENT, fileName);
		startActivity(pdfReaderIntent);
	}
	
	private void setMarginForEmblem() {
		CustomTextView menuName = (CustomTextView) this.rootView.findViewById(R.id.file_manager_layout_layout_name);
		if (menuName == null) {
			return;
		}
		
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) menuName.getLayoutParams();
		params.topMargin = ((MainActivity)getActivity()).getEmblemPartOutsideSize();
		params.topMargin += getActivity().getApplicationContext().getResources().getDimension(R.dimen.margin_small);
		menuName.setLayoutParams(params);
	}
	
	private void setLayoutName() {
		CustomTextView layoutName = (CustomTextView) this.rootView.findViewById(R.id.file_manager_layout_layout_name);
		if (layoutName == null) {
			return;
		}
		
		if (getAppLanguage().equals(Global.SHARED_PREFERENCES_LANGUAGE_ENGLISH)) {
			layoutName.setText(this.fileManagerLayout.getTitleEn());
		} else {
			layoutName.setText(this.fileManagerLayout.getTitlePl());
		}
	}
	
	private void setBackButton() {
		ImageView backButton = (ImageView) this.rootView.findViewById(R.id.file_manager_layout_back_button);
		if (backButton == null) {
			return;
		}
		
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().onBackPressed();	
			}
			
		});
	}
	
	private void setFileIsDownloading(FileItem file) {
		file.setDownloading(true);
	}
	
	private void setFileIsNotDownloading(FileItem file) {
		file.setDownloading(false);
	}
	
	private void downloadFile(int position) {	
		SwipeListView swipeListView = (SwipeListView) this.rootView.findViewById(R.id.file_manager_layout_swipe_list_view);
		if (swipeListView == null) {
			return;
		}
		
		setFileIsDownloading(this.files.get(position));
		insertFileIntoDatabase(this.files.get(position));
		closeOpenedItems();
		((FilesAdapter)swipeListView.getAdapter()).notifyDataSetChanged();
		addOneFileDownloaderFragment(position);
	}
	
	private void addOneFileDownloaderFragment(int position) {
		OneFileDownloaderFragment oneFileDownloaderFragment = new OneFileDownloaderFragment();
		oneFileDownloaderFragment.setArguments(createArgumentsForOneFileDownloaderFragment(files.get(position)));
		FragmentManager fragmentManager = getChildFragmentManager();
		
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
	
	private void hideMainProgressBar() {	
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.file_manager_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.INVISIBLE);
	}
	
	private void showMainProgressBar() {	
		RelativeLayout mainProgressBarLayout = (RelativeLayout) this.rootView.findViewById(R.id.file_manager_layout_main_progress_bar_layout);
		if (mainProgressBarLayout == null) {
			return;
		}
		
		mainProgressBarLayout.setVisibility(RelativeLayout.VISIBLE);
	}
	
	private void setLayout() {
		DatabaseReader databaseReader = new DatabaseReader(getActivity());
		int id = getArguments().getInt(FileManagerFragment.LAYOUT_ID_KEY);
		this.fileManagerLayout = (FileManagerLayout) databaseReader.getLayout(id);
	}
}
