package hr.algebra.movie.dao

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import hr.algebra.movie.model.Item

private const val DB_NAME = "items.db"
private const val DB_VERSION = 1
private const val TABLE_NAME = "items"
private val CREATE_TABLE = "create table $TABLE_NAME( " +
        "${Item::id.name} integer primary key autoincrement, " +
        "${Item::title.name} text not null, " +
        "${Item::overview.name} text not null, " +
        "${Item::poster_path.name} text not null, " +
        "${Item::release_date.name} text not null, " +
        "${Item::vote_average.name} double not null, " +  // Add comma here
        "${Item::vote_count.name} integer not null, " +
        "${Item::popularity.name} double not null, " +
        "${Item::backdrop_path.name} integer not null, " +
        "${Item::original_language.name} integer not null, " +
        "${Item::original_title.name} integer not null, " +
        "${Item::adult.name} integer not null, " +
        "${Item::video.name} integer not null, " +
        "${Item::genre_ids.name} text not null" +
        ")"
private const val DROP_TABLE = "drop table $TABLE_NAME"

class MovieSqlHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),Repository {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(DROP_TABLE)
        onCreate(db)
    }

    override fun delete(selection: String?, selectionArgs: Array<String>?) =
        writableDatabase.delete(TABLE_NAME, selection, selectionArgs)

    override fun insert(values: ContentValues?) = writableDatabase.insert(TABLE_NAME, null, values)

    override fun query(
        projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ) = readableDatabase.query(
        TABLE_NAME,
        projection,
        selection,
        selectionArgs,
        null,
        null,
        sortOrder
    )

    override fun update(values: ContentValues?, selection: String?, selectionArgs: Array<String>?) =
        writableDatabase.update(TABLE_NAME, values, selection, selectionArgs)
}