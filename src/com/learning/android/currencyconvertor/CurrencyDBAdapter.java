package com.learning.android.currencyconvertor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @author Li Ji
 *
 */
public class CurrencyDBAdapter {
	
	 
	 
	//Constant Variables for Key Columns
	public static final String KEY_ID = "_id";
	public static final String KEY_FROM_CURR_ID = "from_curr_id";
	public static final String KEY_TO_CURR_ID = "to_curr_id";
	public static final String KEY_FROM_CURR = "from_curr";
	public static final String KEY_TO_CURR = "to_curr";
	public static final String KEY_FROM_CURR_AMT = "from_curr_amt";
	public static final String KEY_TO_CURR_AMT = "to_curr_amt";
	public static final String KEY_RATE_ADV = "rate_adv";
	public static final String KEY_RATE_REV = "rate_rev";
	public static final String KEY_DIRECTION = "direction";
	public static final String KEY_SELECTION = "selection";
	public static final String KEY_FAVOURITE = "favourite";
	public static final String KEY_CURRENCY = "currency";
	public static final String KEY_CURR_DESC = "curr_desc";
	public static final String KEY_FLAG = "flag";
	public static final String KEY_CURRPAIR_ID = "curr_pair_id";
	
	//Constant Names for tables
	public static final String TBL_XCHGRATE = "xchgrates";
	public static final String TBL_CURR = "currencies";
	private static final String TBL_CURR_PAIR = "currpairselected";
	
	public static final int FAVOR = 1;
	public static final int UNFAVOR = 0;
	public static final int ALL = -1;
	
	/* Currency Selected Mode
	 * 0 --> Only for previous selected currencies
	 * -1 --> Select All currencies
	 */
	public static int[] SELECTION_MODE = {0,-1,1};
	
	
	public static int DATA_INSERT = 0;
	public static int DATA_UPDATE = 1;
	

	//private static final String TAG = "CurrencyDBAdapter";
	private DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;
	
	//Create Xchge Rate Table, this table is to store all the Xchg Rate basing on from_currency to to_currency
	private static final String DATA_CREATE_XCHGRATE = 
			"create table "+TBL_XCHGRATE+"(_id integer primary key autoincrement," +
			"from_curr_id integer not null," +
			"to_curr_id integer not null," +
			"from_curr text not null," +
			"to_curr text not null," +
			"from_curr_amt double,"+
			"to_curr_amt double,"+
			"rate_adv double," +
			"rate_rev double," +
			"direction boolean," +
			"selection integer," +
			"FOREIGN KEY(from_curr_id) REFERENCES currencies(_id),"+
			"FOREIGN KEY(to_curr_id) REFERENCES currencies(_id)"+
			")";
	
	private static final String DATA_CREATE_CURRPAIRSELECTED = 
		"create table "+TBL_CURR_PAIR+"(_id integer primary key autoincrement," +
				"curr_pair_id integer not null," +
				"selection integer," +
				"from_curr_amt double," +
				"to_curr_amt double," +
				"direction boolean," +
				"FOREIGN KEY(curr_pair_id) REFERENCES "+TBL_XCHGRATE+"(_id))";
	//Create Currencies Table, this table is to store all the currencies that can be selected to the exchange rate
	private static final String DATA_CREATE_CURRENCY = 
		"create table "+TBL_CURR+"(_id integer primary key autoincrement," +
		"currency text not null," +
		"favourite boolean," +
		"curr_desc text," +
		"flag int)";
	
	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 43;
	
	private static  Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper{
		private static String DB_PATH = "/data/data/com.learning.android.currencyconvertor/databases/";
		 private static String DB_NAME = "data";
		 private SQLiteDatabase myDataBase; 
		 private  Context myContext;

		  DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	            this.myContext = context;
	        }

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			//db.execSQL(DATA_CREATE_CURRENCY);
			//db.execSQL(DATA_CREATE_XCHGRATE);
			//db.execSQL(DATA_CREATE_CURRPAIRSELECTED);
			//fillCurrency(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//System.out.println("updating database from version "+ oldVersion+" to "+ newVersion + " , which will destroy all the old data");
			 //db.execSQL("DROP TABLE IF EXISTS xchgrates");
			 //db.execSQL("DROP TABLE IF EXISTS currencies");
			 //db.execSQL("DROP TABLE IF EXISTS "+TBL_CURR_PAIR);
	         //onCreate(db);
	         //fillCurrency(db);	         
		}
		
		/**
	     * Creates a empty database on the system and rewrites it with your own database.
	     * */
	    public void createDataBase() throws IOException{
	 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	this.getReadableDatabase();
	 
	        	try {
	 
	    			copyDataBase();
	 
	    		} catch (IOException e) {
	 
	        		throw new Error("Error copying database");
	 
	        	}
	    	}
	 
	    }
	    
	    /**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     */
	    private boolean checkDataBase(){
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	 
	    	}catch(SQLiteException e){
	 
	    		//database does't exist yet.
	 
	    	}
	 
	    	if(checkDB != null){
	 
	    		checkDB.close();
	 
	    	}
	 
	    	return checkDB != null ? true : false;
	    }
	 
	    /**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transfering bytestream.
	     * */
	    private void copyDataBase() throws IOException{
	 
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
		 
	    public void openDataBase() throws SQLException{
	    	 
	    	//Open the database
	        String myPath = DB_PATH + DB_NAME;
	    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	 
	    }
	 
	    
	
	    @Override
		public synchronized void close() {
			// TODO Auto-generated method stub
	    	if(myDataBase != null)
    		    myDataBase.close();
	    	
			super.close();
			
		}

		/**
	     * @param db
	     */
	    public  void fillCurrency(SQLiteDatabase db) {

			// TODO Auto-generated method stub
	    	boolean fav;
	    	String[] currencies = mCtx.getResources().getStringArray(R.array.currencies);
	    	String[] curr_desc = mCtx.getResources().getStringArray(R.array.curr_desc);
	    	int [] flags = new int[] {
	    			R.drawable.albanian,
	    			R.drawable.algerian,
	    			R.drawable.argentine,
	    			R.drawable.australian,
	    			R.drawable.bahraini,
	    			R.drawable.bangladesh,
	    			R.drawable.brazilian,
	    			R.drawable.british,
	    			R.drawable.canadian,
	    			R.drawable.chilean,
	    			R.drawable.chinese,
	    			R.drawable.colombian,
	    			R.drawable.costa,
	    			R.drawable.croatian,
	    			R.drawable.czech,
	    			R.drawable.danish,
	    			R.drawable.dominican,
	    			R.drawable.egyptian,
	    			R.drawable.euro,
	    			R.drawable.hongkong,
	    			R.drawable.hungarian,
	    			R.drawable.iceland,
	    			R.drawable.indian,
	    			R.drawable.indonesian,
	    			R.drawable.iraqi,
	    			R.drawable.israeli,
	    			R.drawable.jamaican,
	    			R.drawable.japanese,
	    			R.drawable.jordanian,
	    			R.drawable.kenyan,
	    			R.drawable.korean,
	    			R.drawable.kuwaiti,
	    			R.drawable.malaysian,
	    			R.drawable.mauritius,
	    			R.drawable.mexican,
	    			R.drawable.moroccan,
	    			R.drawable.nepalese,
	    			R.drawable.newzealand,
	    			R.drawable.nigerian,
	    			R.drawable.norwegian,
	    			R.drawable.omani,
	    			R.drawable.pakistani,
	    			R.drawable.peruvian,
	    			R.drawable.philippine,
	    			R.drawable.polish,
	    			R.drawable.qatar,
	    			R.drawable.russian,
	    			R.drawable.saudiarabian,
	    			R.drawable.singapore,
	    			R.drawable.southafrican,
	    			R.drawable.srilanka,
	    			R.drawable.swedish,
	    			R.drawable.turkey,
	    			R.drawable.swiss,
	    			R.drawable.thai,
	    			R.drawable.tunisian,
	    			R.drawable.us,
	    			R.drawable.uae,
	    			R.drawable.ukraine,
	    			R.drawable.vietnam
	    	};
	    	
	    	
	    	for(int i = 0;i<currencies.length;i++)
	    	{
	    		if(i<7) fav = true;
	    		else fav = false;
	    		addCurrency(db,currencies[i], flags[i],  fav, curr_desc[i]);
	    	}
	    	
	    	loadXchgRates(db);
		}
		
	    /**
	     * Add New Currency to table currencies
	     */
	    public long addCurrency(SQLiteDatabase db,String currency,  int flag, boolean favourite, String desc)
	    {
	    	ContentValues args = new ContentValues();
	    	args.put(KEY_CURRENCY, currency);
	    	args.put(KEY_FAVOURITE, favourite);
	    	args.put(KEY_FLAG, flag);
	    	args.put(KEY_CURR_DESC, desc);
	    	
			return db.insert(TBL_CURR, null, args);
	    }
	    
	    /**
	     * Load currencies stored in currencies to xchgrates Table
	     */
	    
	    public void loadXchgRates(SQLiteDatabase db)
	    {
	    	
			String load = "insert into "+TBL_XCHGRATE+"(from_curr_id, to_curr_id," +
					                             "from_curr,to_curr)" +
					     "select a._id from_curr_id," +
					     "b._id to_curr_id," +
					     "a.currency from_curr," +
					     "b.currency to_curr " +
					     "from currencies a, currencies b " +
					     "where a._id <> b._id and b._id < a._id";
			
			//String select ="update xchgrates set selection = _id where _id < 1 ";
			try {
			//System.out.println("XCHGRATES Table is loaded!");
			db.execSQL(load);
			//db.execSQL(select);
			} 
			catch (Exception e)
			{
			System.out.println("Exception: "+e.toString());
			}
	    }
	}
	
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public CurrencyDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    
    /**
     * Loading the selected currency pairs
     * @param select 
     *   0 only for previously selected currencies ;
     *   -1 for all currencies in the table;
     */
    public Cursor getSelectedCurrencies()
    {
    	String sql = "select xchg._id, xchg.from_curr_id, xchg.from_curr, " +
    			      "xchg.to_curr_id, xchg.to_curr, currp.direction, currp._id selection,froc.flag from_curr_flag, " +
    			      "toc.flag to_curr_flag, xchg.rate_adv, xchg.rate_rev, currp.from_curr_amt, currp.to_curr_amt " +
    			      "from "+TBL_CURR_PAIR+" currp, "+TBL_XCHGRATE+" xchg, "+TBL_CURR+" froc, "+TBL_CURR+" toc " +
    			      "where currp.curr_pair_id = xchg._id and xchg.from_curr_id = froc._id " +
    			      "and xchg.to_curr_id = toc._id order by currp._id";
    	
    	return mDb.rawQuery(sql, null);
    	//return null;
    }
    
    public Cursor getFavCurrencies()
    {
    	 String sql = "select xchg._id, xchg.from_curr_id, xchg.from_curr, " +
			"xchg.to_curr_id, xchg.to_curr, 0 direction, 0 selection, froc.flag from_curr_flag, " +
				"toc.flag to_curr_flag, xchg.rate_adv, xchg.rate_rev, 0 from_curr_amt, 0 to_curr_amt " +
				"from "+TBL_XCHGRATE+" xchg, "+TBL_CURR+" froc, "+TBL_CURR+" toc " +
				"where xchg.from_curr_id = froc._id " +
				"and froc.favourite = 1 and toc.favourite = 1 " +
				"and xchg.to_curr_id = toc._id order by xchg._id desc";
    	
    	return mDb.rawQuery(sql, null);
    }
    
    public Cursor getAllCurrencies()
    {
    	String sql = "select xchg._id, xchg.from_curr_id, xchg.from_curr, " +
	      				"xchg.to_curr_id, xchg.to_curr, 0 direction, 0 selection, froc.flag from_curr_flag, " +
	      				"toc.flag to_curr_flag, xchg.rate_adv, xchg.rate_rev, 0 from_curr_amt, 0 to_curr_amt " +
	      				"from "+TBL_XCHGRATE+" xchg, "+TBL_CURR+" froc, "+TBL_CURR+" toc " +
	      				"where xchg.from_curr_id = froc._id " +
	      				"and xchg.to_curr_id = toc._id order by xchg._id";
    	
    	return mDb.rawQuery(sql, null);
    }
    
    /**
     * fetchCurrencyConfig Select Currency Configuration Information
     * 
     * @param favourite true -> only for Currencies Marked as favourite
     *                  false -> All the Currencies
     * @return Cursor contains currencies configuration information
     */
    public Cursor fetchCurrencyConfig(int favourite)
    {
    	return  mDb.query(TBL_CURR, new String[]{ KEY_ID,KEY_CURRENCY,KEY_CURR_DESC,KEY_FAVOURITE,KEY_FLAG}, KEY_FAVOURITE+">?", new String[]{favourite+""}, null, null, KEY_ID);
    	//return  mDb.query(TBL_CURR, new String[]{ KEY_ID,KEY_CURRENCY,KEY_CURR_DESC,KEY_FAVOURITE,KEY_FLAG}, KEY_FAVOURITE+">?", new String[]{favourite+""}, null, null, KEY_CURRENCY);
    }
    
    
    public Cursor fetchSpinnerCurrency(int favourite, int currencyExcluded)
    {
		//return mDb.query(TBL_XCHGRATE, new String[]{ KEY_ID,KEY_CURRENCY,KEY_CURR_DESC,KEY_FAVOURITE,KEY_FLAG}, KEY_FAVOURITE+">? AND "+ KEY_ID + "<> ?" , new String[]{favourite+"",currencyExcluded+""}, null, null, KEY_ID);
    	return mDb.query(TBL_XCHGRATE, new String[]{ KEY_ID,KEY_CURRENCY,KEY_CURR_DESC,KEY_FAVOURITE,KEY_FLAG}, KEY_FAVOURITE+">? AND "+ KEY_ID + "<> ?" , new String[]{favourite+"",currencyExcluded+""}, null, null, KEY_CURRENCY);
    }
    
    /*
     * Update Currencies Information in xchgrate Table basing on row_id
     */
    public synchronized int updateXchgRate (int rowId, float rateAdv, float rateRev)
    {
    	ContentValues xchgRecord = new ContentValues();
    	
    	if(rateAdv > 0)
    	xchgRecord.put(KEY_RATE_ADV, rateAdv);
    	if(rateRev > 0)
    	xchgRecord.put(KEY_RATE_REV, rateRev);
    	
    	int rn =0;
    	
    	if(xchgRecord.size()>0)
    	{
	    	try{
	    		
	    		  rn= mDb.update(TBL_XCHGRATE, xchgRecord, "_id="+rowId, null);
	    	} catch(Exception e)
	    	{
	    		System.out.println("Database update excpetion "+e.toString());
	    	}
    	}
    	
    	return rn;
    }
    
    public synchronized int updateCurrencies(int rowId, int image, String currName, String currDesc, int favorite)
    {
    	ContentValues currRecord = new ContentValues();
    	
    	if(image > 0)
    	{
    		currRecord.put(KEY_FLAG, image);
    	}
    	if(currName!=null)
    	{
    		currRecord.put(KEY_CURRENCY, currName);
    	}
    	if(currDesc!=null)
    	{
    		currRecord.put(KEY_CURR_DESC, currDesc);
    	}
    	if(favorite == 0||favorite == 1)
    	{
    		currRecord.put(KEY_FAVOURITE, favorite);
    	}
    	
    	int rn = 0;
    	
    	if(currRecord.size()>0)
    	{
    		try{
	    		
	    		  rn= mDb.update(TBL_CURR, currRecord, "_id="+rowId, null);
	    	} catch(Exception e)
	    	{
	    		System.out.println("Database update excpetion "+e.toString());
	    	}
    	}
    	
		return rn;
    }
    
    /**
     * Update currpairselected Table columns curr_pair_id, selection, direction, from_curr_amt, to_curr_amt
     * @param rowId
     * @param selection
     * @param direction
     * @param fromCurrAmt
     * @param toCurrAmt
     * @return
     */
    public synchronized int updateCurrPairSelected(int selection, int currPairId,int direction, double fromCurrAmt, double toCurrAmt)
    {
    	ContentValues currencyPairRecord = new ContentValues();
    	
    	if( currPairId>0)
    		currencyPairRecord.put(KEY_CURRPAIR_ID, currPairId);
    	
    	if(direction == 0 || direction==1)
    		currencyPairRecord.put(KEY_DIRECTION, direction);
    	
    	if(fromCurrAmt!=0)
    		currencyPairRecord.put(KEY_FROM_CURR_AMT, fromCurrAmt);
    	
    	if(toCurrAmt!=0)
    		currencyPairRecord.put(KEY_TO_CURR_AMT, toCurrAmt);
//    	//mDb.update(TBL_XCHGRATE, values, whereClause, whereArgs)
    	
        int rn =0;
    	
    	if(currencyPairRecord.size()>0)
    	{
	    	try{
	    		
	    		 rn= mDb.update(TBL_CURR_PAIR, currencyPairRecord, "_id="+selection, null);
	    	} catch(Exception e)
	    	{
	    		System.out.println("Database update excpetion "+e.toString());
	    	}
    	}
    	
    	return rn;
    }
    
    
    public synchronized int updateCurrencyFavour(int id, boolean fav)
    {
    	ContentValues currencyPairRecord = new ContentValues();
    	
    	currencyPairRecord.put(KEY_FAVOURITE, fav);
    	
    	int rn = 0;
    	
    	try {
    		rn = mDb.update(TBL_CURR, currencyPairRecord, "_id="+id, null);
    	} catch(Exception e)
    	{
    		System.out.println("Currency Update Error!");
    	}
    	return rn;
    }

    /**
     * Delete currency Pair from Database
     * @param selection
     * @return
     */
    public  synchronized int deleteCurrPairSelected(int selection)
    {
    	int rn;
    	rn = mDb.delete(TBL_CURR_PAIR, "_id=?", new String[]{selection+""});
    	return rn;
    }
    
    public synchronized int insertCurrPair(int currentPairId, int direction, double fromCurrAmt, double toCurrAmt)
    {
    	ContentValues  currencyPairRecord = new ContentValues();
    	int rn = 0;
    	
    	//if( currentPairId>0)
    		currencyPairRecord.put(KEY_CURRPAIR_ID, currentPairId);
    	
    	if(direction == 0 || direction==1)
    		currencyPairRecord.put(KEY_DIRECTION, direction);
    	
    	if(fromCurrAmt > 0)
    		currencyPairRecord.put(KEY_FROM_CURR_AMT, fromCurrAmt);
    	
    	if(toCurrAmt > 0)
    		currencyPairRecord.put(KEY_TO_CURR_AMT, toCurrAmt);
    	
    	rn = (int) mDb.insert(TBL_CURR_PAIR, null, currencyPairRecord);
		return rn;
		
    }
    
    
     
    /**
     * To Get latest exchange rate from data base and populate them in Array List
     * @param currencyXchgRatePair
     * @return CurrencyXchgRatePair
     */
    public CurrencyXchgRatePair getNewCurrencyPair(CurrencyXchgRatePair currencyXchgRatePair,int type)
    {
    	
       	
//    	if(updateXchgRate(currencyXchgRatePair.getDbRowID(), 0, 0, 0, 0, 1, 1)==0)
//    	{
//    		System.out.println("Database Update "+currencyXchgRatePair.getDbRowID());
//    		return null;
//    	}
    	
    	
    	
    	
    	//Get CurrencyXchgRatePair basing on From Currency ID & To Currency ID
		return setNewCurrencyPair(currencyXchgRatePair,type);
    }
    
    
    /**
     * Get Lastest New data from database and set them in CurrencyXchgRatePair
     * @param currPair
     * @return
     */
    public CurrencyXchgRatePair setNewCurrencyPair(CurrencyXchgRatePair currPair, int type)
    {
    	String sql = "Select _id,1 from_curr_amt,1 to_curr_amt,rate_adv,rate_rev,from_curr_id, to_curr_id, 0 direction, 0 selection " +
	     "from "+TBL_XCHGRATE+" where from_curr_id in ("+currPair.getFromCurrID()+", "+currPair.getToCurrID()+") " +
	     "and to_curr_id in ("+currPair.getFromCurrID()+", "+currPair.getToCurrID()+");";

        if(currPair.getFromCurrID()!= currPair.getToCurrID())
        {
    	Cursor currXchgRateCursor = mDb.rawQuery(sql, null);
    	
    	
    	currXchgRateCursor.moveToFirst();
    	
    	currPair.setDirection(!currPair.isDirection()?(!(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(KEY_FROM_CURR_ID))==currPair.getFromCurrID())):!(!(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(KEY_FROM_CURR_ID))==currPair.getFromCurrID())));
    	
    	currPair.setDbRowID(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_ID)));
    	currPair.setFromCurrID(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_FROM_CURR_ID)));
    	currPair.setToCurrID(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_TO_CURR_ID)));
    	currPair.setRateADV(currXchgRateCursor.getFloat(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_RATE_ADV)));
    	currPair.setRateREV(currXchgRateCursor.getFloat(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_RATE_REV)));
    	//currPair.setSelection(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_SELECTION)));
        }
    	//Update Column of Direction and Selection from Database
    	if(type == DATA_UPDATE)
    	{
	    	if(updateCurrPairSelected(currPair.getSelection(), currPair.getDbRowID(),currPair.isDirection()==false?0:1,0,0)==0)
	    	{
	    		return null;
	    	}
    	}
    	
    	if(type == DATA_INSERT)
    	{
    		int rn;
    		rn = insertCurrPair(currPair.getDbRowID(),currPair.isDirection()==false?1:0,currPair.getFromCurrAMT(),currPair.getToCurrAMT());
    		if(rn==-1)
    			return null;
    		else
    			currPair.setSelection(rn);
    	}
       
    	//currPair.setSelection(currXchgRateCursor.getInt(currXchgRateCursor.getColumnIndex(CurrencyDBAdapter.KEY_SELECTION)));

    	return currPair;
    }
    
   
    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         Initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public CurrencyDBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        try {
        	 
        	mDbHelper.createDataBase();
 
		 	} catch (IOException ioe) {
		 
		 		throw new Error("Unable to create database");
		 
		 	}
 
	 	try {
	 
	 		mDbHelper.openDataBase();
	 
	 	}catch(SQLException sqle){
	 
	 		throw sqle;
	 
	 	}
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
}
