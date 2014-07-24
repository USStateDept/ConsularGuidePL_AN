package com.agitive.usembassy.databases;

import java.util.ArrayList;

import com.agitive.usembassy.objects.FileItem;
import com.agitive.usembassy.objects.RSSItem;
import com.agitive.usembassy.objects.Tweet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

public class DatabaseAdapter {
	
	public static final String TYPE_MENU = "menu"; // NO_UCD (use default)
	public static final String TYPE_TEXT = "text"; // NO_UCD (use default)
	public static final String TYPE_LIST = "list"; // NO_UCD (use default)
	public static final String TYPE_STEPS = "stps"; // NO_UCD (use default)
	public static final String TYPE_CONTACT = "cont"; // NO_UCD (use default)
	public static final String TYPE_STATUS = "stat"; // NO_UCD (use default)
	public static final String TYPE_FAQ = "faqs"; // NO_UCD (use default)
	public static final String TYPE_FILES = "file"; // NO_UCD (use default)
	public static final String TYPE_HEADLINES = "head"; // NO_UCD (use default)
	public static final String TYPE_PUBLICATIONS = "publ"; // NO_UCD (use default)
	public static final String TYPE_REPORTS = "repo"; // NO_UCD (use default)
	public static final String TYPE_VIDEOS = "vids"; // NO_UCD (use default)
	public static final String TYPE_FACEBOOK = "face"; // NO_UCD (use default)
	public static final String TYPE_PASSPORT = "pasp"; // NO_UCD (use default)
	
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "usembassydb.db";
	private static final String DB_LAYOUT_TABLE = "layout";
	private static final String DB_FILES_TABLE = "files";
	private static final String DB_RSS_TABLE = "rss";
	private static final String DB_TWEETS_TABLE = "tweets";
	
	private static final String KEY_ID = "id";
	private static final String ID_OPTIONS = "INTEGER";
	private static final int ID_COLUMN = 0;
	
	private static final String KEY_PARENT_ID = "parent_id";
	private static final String PARENT_ID_OPTIONS = "INTEGER";
	private static final int PARENT_ID_COLUMN = 1;
	
	private static final String KEY_INDEX = "indexx";
	private static final String INDEX_OPTIONS = "INTEGER";
	private static final int INDEX_COLUMN = 2;
	
	private static final String KEY_TITLE_EN = "title_en";
	private static final String TITLE_EN_OPTIONS = "TEXT";
	private static final int TITLE_EN_COLUMN = 3;
			
	private static final String KEY_TITLE_PL = "title_pl";
	private static final String TITLE_PL_OPTIONS = "TEXT";
	private static final int TITLE_PL_COLUMN = 4;
	
	private static final String KEY_VERSION = "version";
	private static final String VERSION_OPTIONS = "INTEGER";
	private static final int VERSION_COLUMN = 5;
	
	private static final String KEY_TYPE = "type";
	private static final String TYPE_OPTIONS = "TEXT";
	private static final int TYPE_COLUMN = 6;
	
	private static final String KEY_CONTENT_EN = "content_en";
	private static final String CONTENT_EN_OPTIONS = "TEXT";
	private static final int CONTENT_EN_COLUMN = 7;
	
	private static final String KEY_CONTENT_PL = "content_pl";
	private static final String CONTENT_PL_OPTIONS = "TEXT";
	private static final int CONTENT_PL_COLUMN = 8;
	
	private static final String KEY_ADDITIONAL_EN = "additional_en";
	private static final String ADDITIONAL_EN_OPTIONS = "TEXT";
	private static final int ADDITIONAL_EN_COLUMN = 9;
	
	private static final String KEY_ADDITIONAL_PL = "additional_pl";
	private static final String ADDITIONAL_PL_OPTIONS = "TEXT";
	private static final int ADDITIONAL_PL_COLUMN = 10;
	
	private static final String KEY_LATITUDE = "latitude";
	private static final String LATITUDE_OPTIONS = "REAL";
	private static final int LATITUDE_COLUMN = 11;
	
	private static final String KEY_LONGITUDE = "longitude";
	private static final String LONGITUDE_OPTIONS = "REAL";
	private static final int LONGITUDE_COLUMN = 12;
	
	private static final String KEY_ZOOM = "zoom";
	private static final String ZOOM_OPTIONS = "INTEGER";
	private static final int ZOOM_COLUMN = 13;
	
	// Files
	
	private static final String KEY_UPDATED_DAY = "updated_day";
	private static final String UPDATED_DAY_OPTIONS = "INTEGER";
	private static final int UPDATED_DAY_COLUMN = 1;
	
	private static final String KEY_UPDATED_MONTH = "updated_month";
	private static final String UPDATED_MONTH_OPTIONS = "INTEGER";
	private static final int UPDATED_MONTH_COLUMN = 2;
	
	private static final String KEY_UPDATED_YEAR = "updated_year";
	private static final String UPDATED_YEAR_OPTIONS = "INTEGER";
	private static final int UPDATED_YEAR_COLUMN = 3;
	
	private static final String KEY_NAME_EN = "name_en";
	private static final String NAME_EN_OPTIONS = "TEXT";
	private static final int NAME_EN_COLUMN = 4;
	
	private static final String KEY_NAME_PL = "name_pl";
	private static final String NAME_PL_OPTIONS = "TEXT";
	private static final int NAME_PL_COLUMN = 5;
	
	private static final String KEY_URL_EN = "url_en";
	private static final String URL_EN_OPTIONS = "TEXT";
	private static final int URL_EN_COLUMN = 6;
	
	private static final String KEY_URL_PL = "url_pl";
	private static final String URL_PL_OPTIONS = "TEXT";
	private static final int URL_PL_COLUMN = 7;
	
	private static final String KEY_SIZE = "size";
	private static final String SIZE_OPTIONS = "REAL";
	private static final int SIZE_COLUMN = 8;
	
	private static final String KEY_FILE_VERSION = "version";
	private static final String FILE_VERSION_OPTIONS = "INTEGER";
	private static final int FILE_VERSION_COLUMN = 9;
	
	private static final String KEY_FILE_DOWNLOADING = "downloading";
	private static final String FILE_DOWNLOADING_OPTIONS = "INTEGER";
	private static final int FILE_DOWNLOADING_COLUMN = 10;
	
	// RSS
	
	private static final String KEY_RSS_ID = "rss_id";
	private static final String RSS_ID_OPTIONS = "INTEGER";
	
	private static final String KEY_RSS_TITLE = "rss_title";
	private static final String RSS_TITLE_OPTIONS = "TEXT";
	
	private static final String KEY_RSS_SUBTITLE = "rss_subtitle";
	private static final String RSS_SUBTITLE_OPTIONS = "TEXT";
	
	private static final String KEY_RSS_LANGUAGE = "rss_language";
	private static final String RSS_LANGUAGE_OPTIONS = "TEXT";
	
	private static final String KEY_RSS_TEXT = "rss_text";
	private static final String RSS_TEXT_OPTIONS = "TEXT";
	
	//Tweets
	
	private static final String KEY_TWEET_NUMBER = "tweet_number";
	private static final String TWEET_NUMBER_OPTIONS = "INTEGER";
	
	private static final String KEY_TWEET_ID = "tweet_id";
	private static final String TWEET_ID_OPTIONS = "INTEGER";
	
	private static final String KEY_TWEET_TEXT = "tweet_text";
	private static final String TWEET_TEXT_OPTIONS = "TEXT";
	
	
	
	private static final String DB_CREATE_LAYOUT_TABLE =
			"CREATE TABLE " + DB_LAYOUT_TABLE + " ( " +
			KEY_ID + " " + ID_OPTIONS + ", " +
			KEY_PARENT_ID + " " + PARENT_ID_OPTIONS + ", " +
			KEY_INDEX + " " + INDEX_OPTIONS + ", " +
			KEY_TITLE_EN + " " + TITLE_EN_OPTIONS + ", " +
			KEY_TITLE_PL + " " + TITLE_PL_OPTIONS + ", " +
			KEY_VERSION + " " + VERSION_OPTIONS + ", " +
			KEY_TYPE + " " + TYPE_OPTIONS + ", " +
			KEY_CONTENT_EN + " " + CONTENT_EN_OPTIONS + ", " +
			KEY_CONTENT_PL + " " + CONTENT_PL_OPTIONS + ", " +
			KEY_ADDITIONAL_EN + " " + ADDITIONAL_EN_OPTIONS + ", " +
			KEY_ADDITIONAL_PL + " " + ADDITIONAL_PL_OPTIONS + ", " +
			KEY_LATITUDE + " " + LATITUDE_OPTIONS + ", " +
			KEY_LONGITUDE + " " + LONGITUDE_OPTIONS + ", " +
			KEY_ZOOM + " " + ZOOM_OPTIONS + " );";
	
	private static final String DB_CREATE_FILES_TABLE =
			"CREATE TABLE " + DB_FILES_TABLE + " ( " +
			KEY_ID + " " + ID_OPTIONS + ", " +
			KEY_UPDATED_DAY + " " + UPDATED_DAY_OPTIONS + ", " +
			KEY_UPDATED_MONTH + " " + UPDATED_MONTH_OPTIONS + ", " +
			KEY_UPDATED_YEAR + " " + UPDATED_YEAR_OPTIONS + ", " +
			KEY_NAME_EN + " " + NAME_EN_OPTIONS + ", " +
			KEY_NAME_PL + " " + NAME_PL_OPTIONS + ", " +
			KEY_URL_EN + " " + URL_EN_OPTIONS + ", " +
			KEY_URL_PL + " " + URL_PL_OPTIONS + ", " +
			KEY_SIZE + " " + SIZE_OPTIONS + ", " +
			KEY_FILE_VERSION + " " + FILE_VERSION_OPTIONS + ", " +
			KEY_FILE_DOWNLOADING + " " + FILE_DOWNLOADING_OPTIONS + " );";
	
	private static final String DB_CREATE_RSS_TABLE =
			"CREATE TABLE " + DB_RSS_TABLE + " ( " +
			KEY_RSS_ID + " " + RSS_ID_OPTIONS + ", " +
			KEY_RSS_TITLE + " " + RSS_TITLE_OPTIONS + ", " +
			KEY_RSS_SUBTITLE + " " + RSS_SUBTITLE_OPTIONS + ", " +
			KEY_RSS_LANGUAGE + " " + RSS_LANGUAGE_OPTIONS + ", " +
			KEY_RSS_TEXT + " " + RSS_TEXT_OPTIONS + " );";
	
	private static final String DB_CREATE_TWEETS_TABLE =
			"CREATE TABLE " + DB_TWEETS_TABLE + " ( " +
			KEY_TWEET_NUMBER + " " + TWEET_NUMBER_OPTIONS + ", " +
			KEY_TWEET_ID + " " + TWEET_ID_OPTIONS + ", " +
			KEY_TWEET_TEXT + " " + TWEET_TEXT_OPTIONS + " );";
	
	private static final String DROP_LAYOUT_TABLE = 
			"DROP TABLE IF EXISTS " + DB_LAYOUT_TABLE;
	
	private static final String DROP_FILES_TABLE = 
			"DROP TABLE IF EXISTS " + DB_FILES_TABLE;
	
	private static final String DROP_RSS_TABLE = 
			"DROP TABLE IF EXISTS " + DB_RSS_TABLE;
	
	private static final String DROP_TWEETS_TABLE = 
			"DROP TABLE IF EXISTS " + DB_TWEETS_TABLE;
	
	private SQLiteDatabase db;
	private Context context;
	private DatabaseHelper dbHelper;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		private static DatabaseHelper databaseHelper = null;
		
		private DatabaseHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}
		
		public static DatabaseHelper getInstance(Context context) {
			if (databaseHelper == null) {
				databaseHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
			}
			
			return databaseHelper;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE_LAYOUT_TABLE);
			db.execSQL(DB_CREATE_FILES_TABLE);
			db.execSQL(DB_CREATE_RSS_TABLE);
			db.execSQL(DB_CREATE_TWEETS_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_LAYOUT_TABLE);
			db.execSQL(DROP_FILES_TABLE);
			db.execSQL(DROP_RSS_TABLE);
			db.execSQL(DROP_TWEETS_TABLE);
			onCreate(db);
		}	
	}
	
	public DatabaseAdapter(Context context) {
		this.context = context;
		open();
	}
	
	private DatabaseAdapter open() {
		dbHelper = DatabaseHelper.getInstance(context);
		
		try {
			db = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			db = dbHelper.getReadableDatabase();
		}
		
		return this;
	}
	
	public long insertLayout(LayoutDatabase layout) {
		ContentValues newLayout = createContentValues(layout);
		
		return db.insert(DB_LAYOUT_TABLE, null, newLayout);
	}
	
	public boolean updateLayout(LayoutDatabase layout) {
		String where = DatabaseAdapter.KEY_ID + "=" + layout.getId();
		ContentValues newLayout = createContentValues(layout);
		
		return db.update(DB_LAYOUT_TABLE, newLayout, where, null) > 0;
	}
	
	public boolean deleteLayout(int id) {
		String where = DatabaseAdapter.KEY_ID + "=" + id;
		return db.delete(DB_LAYOUT_TABLE, where, null) > 0;
	}
	
	public long insertFile(FileItem file) {
		ContentValues newFile = createFileContentValues(file);

		return db.insert(DB_FILES_TABLE, null, newFile);
	}
	
	public boolean updateFile(FileItem file) {
		String where = DatabaseAdapter.KEY_ID + "=" + file.getId();
		ContentValues newFile = createFileContentValues(file);
		
		return db.update(DB_FILES_TABLE, newFile, where, null) > 0;
	}
	
	public boolean deleteFile(int id) {
		String where = DatabaseAdapter.KEY_ID + "=" + id;
		return db.delete(DB_FILES_TABLE, where, null) > 0;
	}
	
	public long insertRSS(RSSItem rssItem, int id) {
		ContentValues newRSS = createRSSItemContentValues(rssItem, id);

		return db.insert(DB_RSS_TABLE, null, newRSS);
	}
	
	public long insertTweet(Tweet tweet, int number) {
		ContentValues newTweet = createTweetContentValues(tweet, number);

		return db.insert(DB_TWEETS_TABLE, null, newTweet);
	}
	
	public LayoutDatabase getLayoutDatabase(long id) {
		String where = DatabaseAdapter.KEY_ID + "=" + id;
		String[] columns = {
				DatabaseAdapter.KEY_ID,
				DatabaseAdapter.KEY_PARENT_ID,
				DatabaseAdapter.KEY_INDEX,
				DatabaseAdapter.KEY_TITLE_EN,	
				DatabaseAdapter.KEY_TITLE_PL,
				DatabaseAdapter.KEY_VERSION,
				DatabaseAdapter.KEY_TYPE,
				DatabaseAdapter.KEY_CONTENT_EN,
				DatabaseAdapter.KEY_CONTENT_PL,
				DatabaseAdapter.KEY_ADDITIONAL_EN,
				DatabaseAdapter.KEY_ADDITIONAL_PL,
				DatabaseAdapter.KEY_LATITUDE,
				DatabaseAdapter.KEY_LONGITUDE,
				DatabaseAdapter.KEY_ZOOM
		};
		
		Cursor cursor = db.query(DB_LAYOUT_TABLE, columns, where, null, null, null, null, null);
		
		LayoutDatabase layoutDatabase = new LayoutDatabase();
		try {
			if (cursor != null && cursor.moveToFirst()) {
				layoutDatabase.setId(cursor.getInt(ID_COLUMN));
				layoutDatabase.setParentId(cursor.getInt(PARENT_ID_COLUMN));
				layoutDatabase.setIndex(cursor.getInt(INDEX_COLUMN));
				layoutDatabase.setTitleEn(cursor.getString(TITLE_EN_COLUMN));
				layoutDatabase.setTitlePl(cursor.getString(TITLE_PL_COLUMN));
				layoutDatabase.setVersion(cursor.getInt(VERSION_COLUMN));
				layoutDatabase.setType(cursor.getString(TYPE_COLUMN));
				layoutDatabase.setContentEn(cursor.getString(CONTENT_EN_COLUMN));
				layoutDatabase.setContentPl(cursor.getString(CONTENT_PL_COLUMN));
				layoutDatabase.setAdditionalEn(cursor.getString(ADDITIONAL_EN_COLUMN));
				layoutDatabase.setAdditionalPl(cursor.getString(ADDITIONAL_PL_COLUMN));
				layoutDatabase.setLatitude(cursor.getFloat(LATITUDE_COLUMN));
				layoutDatabase.setLongitude(cursor.getFloat(LONGITUDE_COLUMN));
				layoutDatabase.setZoom(cursor.getInt(ZOOM_COLUMN));
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return layoutDatabase;
	}
	
	public ArrayList<LayoutDatabase> getChildren(int id) { // NO_UCD (use default)
		String where = DatabaseAdapter.KEY_PARENT_ID + "=" + id;
		String[] columns = {
				DatabaseAdapter.KEY_ID
		};
		
		String orderBy = DatabaseAdapter.KEY_INDEX;
		
		Cursor cursor = db.query(DB_LAYOUT_TABLE, columns, where, null, null, null, orderBy, null);
		
		ArrayList<LayoutDatabase> result = new ArrayList<LayoutDatabase>();
		while (cursor != null && cursor.moveToNext()) {
			result.add(getLayoutDatabase(cursor.getInt(0)));
		}
		
		return result;
	}
	
	public LayoutDatabase getLayoutDatabaseByType(String type) { // NO_UCD (use default)
		String where = DatabaseAdapter.KEY_TYPE + "=\"" + type + "\"";
		String[] columns = {
				DatabaseAdapter.KEY_ID
		};
		
		Cursor cursor = db.query(DB_LAYOUT_TABLE, columns, where, null, null, null, null, null);
		
		LayoutDatabase result = null;
		if (cursor != null && cursor.moveToFirst()) {
			result = getLayoutDatabase(cursor.getInt(0));
		}
		
		return result;
	}
	
	public ArrayList<Pair<Integer, Integer>> getAllLayoutsVersions() {
		String[] columns = {
				DatabaseAdapter.KEY_ID,
				DatabaseAdapter.KEY_VERSION
		};
		
		Cursor cursor = db.query(DB_LAYOUT_TABLE, columns, null, null, null, null, null, null);
		
		ArrayList<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		try {
			while (cursor != null && cursor.moveToNext()) {
				result.add(new Pair<Integer, Integer>(cursor.getInt(0), cursor.getInt(1)));
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return result;
	};
	
	public FileItem getFile(long id) {
		String where = DatabaseAdapter.KEY_ID + "=" + id;
		String[] columns = {
				DatabaseAdapter.KEY_ID,
				DatabaseAdapter.KEY_UPDATED_DAY,
				DatabaseAdapter.KEY_UPDATED_MONTH,
				DatabaseAdapter.KEY_UPDATED_YEAR,	
				DatabaseAdapter.KEY_NAME_EN,
				DatabaseAdapter.KEY_NAME_PL,
				DatabaseAdapter.KEY_URL_EN,
				DatabaseAdapter.KEY_URL_PL,
				DatabaseAdapter.KEY_SIZE,
				DatabaseAdapter.KEY_FILE_VERSION,
				DatabaseAdapter.KEY_FILE_DOWNLOADING
		};
		
		Cursor cursor = db.query(DB_FILES_TABLE, columns, where, null, null, null, null, null);
		
		FileItem fileItem = null;
		
		try {
			if (cursor != null && cursor.moveToFirst()) {
				int fileId = cursor.getInt(ID_COLUMN);
				int updatedDay = cursor.getInt(UPDATED_DAY_COLUMN);
				int updatedMonth = cursor.getInt(UPDATED_MONTH_COLUMN);
				int updatedYear = cursor.getInt(UPDATED_YEAR_COLUMN);
				String nameEn = cursor.getString(NAME_EN_COLUMN);
				String namePl = cursor.getString(NAME_PL_COLUMN);
				String urlEn = cursor.getString(URL_EN_COLUMN);
				String urlPl = cursor.getString(URL_PL_COLUMN);
				double size = cursor.getDouble(SIZE_COLUMN);
				int version = cursor.getInt(FILE_VERSION_COLUMN);
				boolean downloading = (cursor.getInt(FILE_DOWNLOADING_COLUMN) == 1);
				
				fileItem = new FileItem(fileId, nameEn, namePl, updatedDay, updatedMonth, updatedYear, version, size, urlPl, urlEn, downloading);
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return fileItem;
	}
	
	public boolean hasFile(long id) {
		String where = DatabaseAdapter.KEY_ID + "=" + id;
		String[] columns = {
			DatabaseAdapter.KEY_ID,	
		};
		
		Cursor cursor = db.query(DB_FILES_TABLE, columns, where, null, null, null, null, null);
		
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return true;
			}
		} finally {
			if (cursor == null) {
				return false;
			}
			
			cursor.close();
		}
		
		return false;
	}
	
	public ArrayList<FileItem> getAllFiles() {
		String[] columns = {
				DatabaseAdapter.KEY_ID,
				DatabaseAdapter.KEY_UPDATED_DAY,
				DatabaseAdapter.KEY_UPDATED_MONTH,
				DatabaseAdapter.KEY_UPDATED_YEAR,	
				DatabaseAdapter.KEY_NAME_EN,
				DatabaseAdapter.KEY_NAME_PL,
				DatabaseAdapter.KEY_URL_EN,
				DatabaseAdapter.KEY_URL_PL,
				DatabaseAdapter.KEY_SIZE,
				DatabaseAdapter.KEY_FILE_VERSION,
				DatabaseAdapter.KEY_FILE_DOWNLOADING
		};
		
		Cursor cursor = db.query(DB_FILES_TABLE, columns, null, null, null, null, null, null);
		
		ArrayList<FileItem> result = new ArrayList<FileItem>();
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				int fileId = cursor.getInt(ID_COLUMN);
				int updatedDay = cursor.getInt(UPDATED_DAY_COLUMN);
				int updatedMonth = cursor.getInt(UPDATED_MONTH_COLUMN);
				int updatedYear = cursor.getInt(UPDATED_YEAR_COLUMN);
				String nameEn = cursor.getString(NAME_EN_COLUMN);
				String namePl = cursor.getString(NAME_PL_COLUMN);
				String urlEn = cursor.getString(URL_EN_COLUMN);
				String urlPl = cursor.getString(URL_PL_COLUMN);
				double size = cursor.getDouble(SIZE_COLUMN);
				int version = cursor.getInt(FILE_VERSION_COLUMN);
				boolean downloading = (cursor.getInt(FILE_DOWNLOADING_COLUMN) == 1);
				
				FileItem fileItem = new FileItem(fileId, nameEn, namePl, updatedDay, updatedMonth, updatedYear, version, size, urlPl, urlEn, downloading);
				result.add(fileItem);
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	public ArrayList<RSSItem> getAllRSSItems(String language) {
		String[] columns = {
				DatabaseAdapter.KEY_RSS_TITLE,
		};
		
		String orderBy = DatabaseAdapter.KEY_RSS_ID;
		String selection = DatabaseAdapter.KEY_RSS_LANGUAGE + "=\"" + language + "\"";
		
		Cursor cursor = db.query(DB_RSS_TABLE, columns, selection, null, null, null, orderBy, null);
		
		ArrayList<RSSItem> result = new ArrayList<RSSItem>();
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				String title = cursor.getString(0);
				
				result.add(getRSSItem(title));
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	public ArrayList<RSSItem> getRSSItems(int number, String language) {
		String[] columns = {
				DatabaseAdapter.KEY_RSS_TITLE,
		};
		
		String orderBy = DatabaseAdapter.KEY_RSS_ID;
		String selection = DatabaseAdapter.KEY_RSS_LANGUAGE + "=\"" + language + "\"";
		
		Cursor cursor = db.query(DB_RSS_TABLE, columns, selection, null, null, null, orderBy, Integer.toString(number));
		
		ArrayList<RSSItem> result = new ArrayList<RSSItem>();
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				String title = cursor.getString(0);
				
				result.add(getRSSItem(title));
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	public void deleteAllRSSItems() {
		String[] columns = {
				DatabaseAdapter.KEY_RSS_TITLE,
		};
		
		Cursor cursor = db.query(DB_RSS_TABLE, columns, null, null, null, null, null, null);
		
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				String title = cursor.getString(0);
				
				deleteRSSItem(title);
			}
		} finally {
			if (cursor == null) {
				return;
			}
			
			cursor.close();
		}
	}
	
	public ArrayList<Tweet> getAllTweets() {
		String[] columns = {
				DatabaseAdapter.KEY_TWEET_NUMBER,
		};
		
		String orderBy = DatabaseAdapter.KEY_TWEET_NUMBER;
		
		Cursor cursor = db.query(DB_TWEETS_TABLE, columns, null, null, null, null, orderBy, null);
		
		ArrayList<Tweet> result = new ArrayList<Tweet>();
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				int number = cursor.getInt(0);
				
				Tweet tweet = getTweet(number);
				if (tweet == null) {
					continue;
				}
				
				result.add(tweet);
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	public ArrayList<Tweet> getTweets(int number) {
		String[] columns = {
				DatabaseAdapter.KEY_TWEET_NUMBER,
		};
		
		String orderBy = DatabaseAdapter.KEY_TWEET_NUMBER;
		
		Cursor cursor = db.query(DB_TWEETS_TABLE, columns, null, null, null, null, orderBy, Integer.toString(number));
		
		ArrayList<Tweet> result = new ArrayList<Tweet>();
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				int tweetNumber = cursor.getInt(0);
				
				Tweet tweet = getTweet(tweetNumber);
				if (tweet == null) {
					continue;
				}
				
				result.add(tweet);
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return result;
	}
	
	public void deleteAllTweets() {
		String[] columns = {
				DatabaseAdapter.KEY_TWEET_NUMBER,
		};
		
		Cursor cursor = db.query(DB_TWEETS_TABLE, columns, null, null, null, null, null, null);
		
		
		try {
			while (cursor != null && cursor.moveToNext()) {
				int number = cursor.getInt(0);
				
				deleteTweet(number);
			}
		} finally {
			if (cursor == null) {
				return;
			}
			
			cursor.close();
		}
	}
	
	private ContentValues createContentValues(LayoutDatabase layout) {
		ContentValues newLayout = new ContentValues();
		newLayout.put(DatabaseAdapter.KEY_ID, layout.getId());
		newLayout.put(DatabaseAdapter.KEY_PARENT_ID, layout.getParentId());
		newLayout.put(DatabaseAdapter.KEY_INDEX, layout.getIndex());
		newLayout.put(DatabaseAdapter.KEY_TITLE_EN, layout.getTitleEn());
		newLayout.put(DatabaseAdapter.KEY_TITLE_PL, layout.getTitlePl());
		newLayout.put(DatabaseAdapter.KEY_VERSION, layout.getVersion());
		newLayout.put(DatabaseAdapter.KEY_TYPE, layout.getType());
		newLayout.put(DatabaseAdapter.KEY_CONTENT_EN, layout.getContentEn());
		newLayout.put(DatabaseAdapter.KEY_CONTENT_PL, layout.getContentPl());
		newLayout.put(DatabaseAdapter.KEY_ADDITIONAL_EN, layout.getAdditionalEn());
		newLayout.put(DatabaseAdapter.KEY_ADDITIONAL_PL, layout.getAdditionalPl());
		newLayout.put(DatabaseAdapter.KEY_LATITUDE, layout.getLatitude());
		newLayout.put(DatabaseAdapter.KEY_LONGITUDE, layout.getLongitude());
		newLayout.put(DatabaseAdapter.KEY_ZOOM, layout.getZoom());
		
		return newLayout;
	}
	
	private ContentValues createFileContentValues(FileItem file) {
		ContentValues newFile = new ContentValues();
		newFile.put(DatabaseAdapter.KEY_ID, file.getId());
		newFile.put(DatabaseAdapter.KEY_UPDATED_DAY, file.getUpdatedDay());
		newFile.put(DatabaseAdapter.KEY_UPDATED_MONTH, file.getUpdatedMonth());
		newFile.put(DatabaseAdapter.KEY_UPDATED_YEAR, file.getUpdatedYear());
		newFile.put(DatabaseAdapter.KEY_NAME_EN, file.getNameEn());
		newFile.put(DatabaseAdapter.KEY_NAME_PL, file.getNamePl());
		newFile.put(DatabaseAdapter.KEY_URL_EN, file.getUrlEn());
		newFile.put(DatabaseAdapter.KEY_URL_PL, file.getUrlPl());
		newFile.put(DatabaseAdapter.KEY_SIZE, file.getSize());
		newFile.put(DatabaseAdapter.KEY_FILE_VERSION, file.getVersion());
		newFile.put(DatabaseAdapter.KEY_FILE_DOWNLOADING, file.getDownloading());
		
		return newFile;
	}
	
	private ContentValues createRSSItemContentValues(RSSItem rssItem, int id) {
		ContentValues newRSS = new ContentValues();
		newRSS.put(DatabaseAdapter.KEY_RSS_ID, id);
		newRSS.put(DatabaseAdapter.KEY_RSS_TITLE, rssItem.getTitle());
		newRSS.put(DatabaseAdapter.KEY_RSS_SUBTITLE, rssItem.getSubtitle());
		newRSS.put(DatabaseAdapter.KEY_RSS_LANGUAGE, rssItem.getLanguage());
		newRSS.put(DatabaseAdapter.KEY_RSS_TEXT, rssItem.getText());
		
		return newRSS;
	}
	
	private ContentValues createTweetContentValues(Tweet tweet, int number) {
		ContentValues newTweet = new ContentValues();
		newTweet.put(DatabaseAdapter.KEY_TWEET_NUMBER, number);
		newTweet.put(DatabaseAdapter.KEY_TWEET_ID, tweet.getId());
		newTweet.put(DatabaseAdapter.KEY_TWEET_TEXT, tweet.getText());
		
		return newTweet;
	}
	
	private boolean deleteRSSItem(String title) {
		String where = DatabaseAdapter.KEY_RSS_TITLE + "=\"" + title + "\"";
		return db.delete(DB_RSS_TABLE, where, null) > 0;
	}
	
	private Tweet getTweet(int number) {
		String where = DatabaseAdapter.KEY_TWEET_NUMBER + "=" + number;
		
		String[] columns = {
				DatabaseAdapter.KEY_TWEET_ID,
				DatabaseAdapter.KEY_TWEET_TEXT
		};
		
		Cursor cursor = db.query(DB_TWEETS_TABLE, columns, where, null, null, null, null, null);
		
		Tweet tweet = null;
		
		try {
			if (cursor != null && cursor.moveToFirst()) {
				long id = cursor.getLong(0);
				String text = cursor.getString(1);
				
				tweet = new Tweet(id);
				tweet.setText(text);
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return tweet;
	}
	
	private RSSItem getRSSItem(String rssTitle) {
		String where = DatabaseAdapter.KEY_RSS_TITLE + "=\"" + rssTitle + "\"";
		
		String[] columns = {
				DatabaseAdapter.KEY_RSS_TITLE,
				DatabaseAdapter.KEY_RSS_SUBTITLE,
				DatabaseAdapter.KEY_RSS_LANGUAGE,
				DatabaseAdapter.KEY_RSS_TEXT
		};
		
		Cursor cursor = db.query(DB_RSS_TABLE, columns, where, null, null, null, null, null);
		
		RSSItem rssItem = new RSSItem();
		
		try {
			if (cursor != null && cursor.moveToFirst()) {
				String title = cursor.getString(0);
				String subtitle = cursor.getString(1);
				String language = cursor.getString(2);
				String text = cursor.getString(3);
				
				rssItem.setTitle(title);
				rssItem.setSubtitle(subtitle);
				rssItem.setLanguage(language);
				rssItem.setText(text);
			}
		} finally {
			if (cursor == null) {
				return null;
			}
			
			cursor.close();
		}
		
		return rssItem;
	}
	
	private boolean deleteTweet(int number) {
		String where = DatabaseAdapter.KEY_TWEET_NUMBER + "=" + number;
		return db.delete(DB_TWEETS_TABLE, where, null) > 0;
	}
 }
