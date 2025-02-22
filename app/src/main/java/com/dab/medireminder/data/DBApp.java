package com.dab.medireminder.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.dab.medireminder.data.model.Advisory;
import com.dab.medireminder.data.model.BloodPressure;
import com.dab.medireminder.data.model.Medicine;
import com.dab.medireminder.data.model.MedicineTimer;

import java.util.ArrayList;

public class DBApp extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Medicine.db";
    private static final String TABLE_TIMER = "tblTimer";
    private static final String TABLE_MEDICINE = "tblMedicine";
    private static final String TABLE_BLOOD_PRESSURE = "tblBloodPressure";
    private static final String TABLE_ADVISORY = "tblAdvisory";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DOSE = "dose";
    private static final String COLUMN_TIME = "timer";
    private static final String COLUMN_REPEAT = "repeat";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_BLOOD_MIN = "blood_min";
    private static final String COLUMN_BLOOD_MAX = "blood_max";
    private static final String COLUMN_CONTENT = "content";

    private static final int DB_VERSION = 1;

    public DBApp(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    public DBApp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TIMER
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_NAME + " Text, "
                + COLUMN_DOSE + " Text, "
                + COLUMN_TIME + " Text, "
                + COLUMN_REPEAT + " Text, "
                + COLUMN_IMAGE + " Text "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MEDICINE
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_NAME + " Text, "
                + COLUMN_DOSE + " Text, "
                + COLUMN_IMAGE + " Text "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BLOOD_PRESSURE
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_BLOOD_MAX + " Integer, "
                + COLUMN_BLOOD_MIN + " Integer, "
                + COLUMN_TIME + " Text "
                + ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ADVISORY
                + "(" + COLUMN_ID + " Text PRIMARY KEY, "
                + COLUMN_CONTENT + " Text, "
                + COLUMN_IMAGE + " Text, "
                + COLUMN_TIME + " Text "
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean addTimer(MedicineTimer medicineTimer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, medicineTimer.getId());
        contentValues.put(COLUMN_NAME, medicineTimer.getName());
        contentValues.put(COLUMN_DOSE, medicineTimer.getDose());
        contentValues.put(COLUMN_TIME, medicineTimer.getTimer());
        contentValues.put(COLUMN_REPEAT, medicineTimer.getRepeat());
        contentValues.put(COLUMN_IMAGE, medicineTimer.getIcon());
        long success = db.insert(TABLE_TIMER, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    public void deleteTimer(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMER, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteAllTimer() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TIMER, null, null);
        db.close();
    }

    public ArrayList<MedicineTimer> getMedicineTimer() {
        ArrayList<MedicineTimer> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_TIMER, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    MedicineTimer medicineTimer = new MedicineTimer();
                    medicineTimer.setId(res.getString(res.getColumnIndex(COLUMN_ID)));
                    medicineTimer.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicineTimer.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    medicineTimer.setTimer(res.getString(res.getColumnIndex(COLUMN_TIME)));
                    medicineTimer.setRepeat(res.getString(res.getColumnIndex(COLUMN_REPEAT)));
                    medicineTimer.setIcon(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    itemList.add(medicineTimer);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {

        }
        return itemList;
    }

    public boolean hasTimer(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_TIMER, new String[]{COLUMN_ID}, COLUMN_ID + "=?",
                    new String[]{id}, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                boolean isHas = false;
                while (!cursor.isAfterLast()) {
                    String linkRes = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                    if (!TextUtils.isEmpty(linkRes)) {
                        isHas = true;
                    }
                    break;
                }
                cursor.close();
                return isHas;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public MedicineTimer getMedicineTimer(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_TIMER + " WHERE " + COLUMN_ID + " = '" + id + "'", null);
            MedicineTimer medicineTimer = new MedicineTimer();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    medicineTimer.setId(res.getString(res.getColumnIndex(COLUMN_ID)));
                    medicineTimer.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicineTimer.setTimer(res.getString(res.getColumnIndex(COLUMN_TIME)));
                    medicineTimer.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    medicineTimer.setRepeat(res.getString(res.getColumnIndex(COLUMN_REPEAT)));
                    medicineTimer.setIcon(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    break;
                }
                res.close();
            }
            db.close();

            if (!TextUtils.isEmpty(medicineTimer.getName())) {
                return medicineTimer;
            }
        } catch (Exception e) {

        }
        return null;
    }


    public boolean addMedicine(Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, medicine.getName());
        contentValues.put(COLUMN_NAME, medicine.getName());
        contentValues.put(COLUMN_DOSE, medicine.getDose());
        contentValues.put(COLUMN_IMAGE, medicine.getImage());
        long success = db.insert(TABLE_MEDICINE, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    public void deleteMedicine(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINE, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    public Medicine getMedicine(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_MEDICINE + " WHERE " + COLUMN_ID + " = '" + id + "'", null);
            Medicine medicine = new Medicine();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    medicine.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicine.setImage(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    medicine.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    break;
                }
                res.close();
            }
            db.close();

            if (!TextUtils.isEmpty(medicine.getName())) {
                return medicine;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public ArrayList<Medicine> getMedicine() {
        ArrayList<Medicine> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_MEDICINE, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    Medicine medicineTimer = new Medicine();
                    medicineTimer.setName(res.getString(res.getColumnIndex(COLUMN_NAME)));
                    medicineTimer.setDose(res.getString(res.getColumnIndex(COLUMN_DOSE)));
                    medicineTimer.setImage(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    itemList.add(medicineTimer);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {

        }
        return itemList;
    }


    public boolean addBloodPressure(BloodPressure medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, String.valueOf(medicine.getTimer()));
        contentValues.put(COLUMN_BLOOD_MAX, medicine.getMax());
        contentValues.put(COLUMN_BLOOD_MIN, medicine.getMin());
        contentValues.put(COLUMN_TIME, String.valueOf(medicine.getTimer()));
        long success = db.insert(TABLE_BLOOD_PRESSURE, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    public void deleteBloodPressure(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BLOOD_PRESSURE, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    public BloodPressure getBloodPressure(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_BLOOD_PRESSURE + " WHERE " + COLUMN_ID + " = '" + id + "'", null);
            BloodPressure bloodPressure = new BloodPressure();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    bloodPressure.setMax(res.getInt(res.getColumnIndex(COLUMN_BLOOD_MAX)));
                    bloodPressure.setMin(res.getInt(res.getColumnIndex(COLUMN_BLOOD_MIN)));
                    bloodPressure.setTimer(Long.parseLong(res.getString(res.getColumnIndex(COLUMN_TIME))));
                    break;
                }
                res.close();
            }
            db.close();

            if (bloodPressure.getTimer() > 0) {
                return bloodPressure;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public ArrayList<BloodPressure> getBloodPressure() {
        ArrayList<BloodPressure> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_BLOOD_PRESSURE, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    BloodPressure bloodPressure = new BloodPressure();
                    bloodPressure.setMax(res.getInt(res.getColumnIndex(COLUMN_BLOOD_MAX)));
                    bloodPressure.setMin(res.getInt(res.getColumnIndex(COLUMN_BLOOD_MIN)));
                    bloodPressure.setTimer(Long.parseLong(res.getString(res.getColumnIndex(COLUMN_TIME))));
                    itemList.add(bloodPressure);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {

        }
        return itemList;
    }


    public boolean addAdvisory(Advisory medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, String.valueOf(medicine.getTime()));
        contentValues.put(COLUMN_CONTENT, medicine.getContent());
        contentValues.put(COLUMN_IMAGE, medicine.getImage());
        contentValues.put(COLUMN_TIME, String.valueOf(medicine.getTime()));
        long success = db.insert(TABLE_ADVISORY, null, contentValues);
        db.close();
        if (success != -1) {
            return true;
        }
        return false;
    }

    public void deleteAdvisory(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADVISORY, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    public Advisory getAdvisory(String id) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_ADVISORY + " WHERE " + COLUMN_ID + " = '" + id + "'", null);
            Advisory advisory = new Advisory();
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    advisory.setContent(res.getString(res.getColumnIndex(COLUMN_CONTENT)));
                    advisory.setImage(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    advisory.setTime(Long.parseLong(res.getString(res.getColumnIndex(COLUMN_TIME))));
                    break;
                }
                res.close();
            }
            db.close();

            if (advisory.getTime() > 0) {
                return advisory;
            }
        } catch (Exception e) {

        }
        return null;
    }

    public ArrayList<Advisory> getAdvisory() {
        ArrayList<Advisory> itemList = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from " + TABLE_ADVISORY, null);
            if (res != null) {
                res.moveToFirst();
                while (!res.isAfterLast()) {
                    Advisory advisory = new Advisory();
                    advisory.setContent(res.getString(res.getColumnIndex(COLUMN_CONTENT)));
                    advisory.setImage(res.getString(res.getColumnIndex(COLUMN_IMAGE)));
                    advisory.setTime(Long.parseLong(res.getString(res.getColumnIndex(COLUMN_TIME))));
                    itemList.add(advisory);
                    res.moveToNext();
                }
                res.close();
            }
            db.close();
        } catch (Exception e) {

        }
        return itemList;
    }
}