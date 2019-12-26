package com.philolog.hc;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
//import com.philolog.hc.SQLiteAssetHelper;

/*
  Copyright © 2017 Jeremy March. All rights reserved.

This file is part of philologus-Android.

    philologus-Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <https://www.gnu.org/licenses/>.

 */

/**
 * Created by jeremy on 1/30/18.
 */

public class HCDBHelperNew extends SQLiteAssetHelper {

    public static final String DATABASE_NAME = "hcdatadb1-5.sqlite";
    private static final int DATABASE_VERSION = 1;
    public String dbpath = "";
    private static HCDBHelperNew singleton;

    public static HCDBHelperNew getInstance(final Context context) {
        if (singleton == null) {
            singleton = new HCDBHelperNew(context);
        }
        return singleton;
    }

    public HCDBHelperNew(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbpath = mContext.getDatabasePath(DATABASE_NAME).toString();
    }

    /*
    public String getHCDBPath()
    {
        return mContext.getDatabasePath(DATABASE_NAME).toString();
        //return mContext.getDatabasePath(DATABASE_NAME).getPath();
    }
    */
}
