package hr.algebra.movie

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import hr.algebra.movie.dao.Repository
import hr.algebra.movie.factory.getRepository
import hr.algebra.movie.model.Item

private const val AUTHORITY = "hr.algebra.movie.api.provider"
private const val PATH = "items"
val MOVIES_PROVIDER_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$PATH")

private const val ITEMS = 10
private const val ITEM_ID = 20

private val URI_MATCHER = with(UriMatcher(UriMatcher.NO_MATCH)) {
    addURI(AUTHORITY, PATH, ITEMS)
    addURI(AUTHORITY, "$PATH/#", ITEM_ID)
    this
}

class MovieProvider : ContentProvider() {

    private lateinit var repository: Repository

    override fun onCreate(): Boolean {
        repository = getRepository(context!!)
        return true



    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = when (URI_MATCHER.match(uri)) {
            ITEMS -> repository.query(projection, selection, selectionArgs, sortOrder)
            ITEM_ID -> {
                val id = uri.lastPathSegment ?: return null
                repository.query(projection, "id = ?", arrayOf(id), sortOrder)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor?.setNotificationUri(context!!.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return when (URI_MATCHER.match(uri)) {
            ITEMS -> {
                val id = repository.insert(values)
                context!!.contentResolver.notifyChange(uri, null) // Notify data change
                ContentUris.withAppendedId(MOVIES_PROVIDER_CONTENT_URI, id)
            }
            else -> throw IllegalArgumentException("Invalid URI: $uri")
        }
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        return when (URI_MATCHER.match(uri)) {
            ITEMS -> {
                val count = repository.update(values, selection, selectionArgs)
                context!!.contentResolver.notifyChange(uri, null) // Notify data change
                count
            }
            ITEM_ID -> {
                val id = uri.lastPathSegment ?: return 0
                val count = repository.update(values, "id = ?", arrayOf(id))
                context!!.contentResolver.notifyChange(uri, null) // Notify data change
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return when (URI_MATCHER.match(uri)) {
            ITEMS -> {
                val count = repository.delete(selection, selectionArgs)
                context!!.contentResolver.notifyChange(uri, null) // Notify data change
                count
            }
            ITEM_ID -> {
                val id = uri.lastPathSegment ?: return 0
                val count = repository.delete("id = ?", arrayOf(id))
                context!!.contentResolver.notifyChange(uri, null) // Notify data change
                count
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? {
        return when (URI_MATCHER.match(uri)) {
            ITEMS -> "vnd.android.cursor.dir/$AUTHORITY.$PATH"
            ITEM_ID -> "vnd.android.cursor.item/$AUTHORITY.$PATH"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

}