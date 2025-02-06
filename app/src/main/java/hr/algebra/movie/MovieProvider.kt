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

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        when(URI_MATCHER.match(uri)){
            ITEMS -> return repository.delete(selection, selectionArgs)
            ITEM_ID -> {
                uri.lastPathSegment?.let { id ->
                    repository.delete("${Item::id.name} = ?", arrayOf(id))
                }

            }
        }
        throw IllegalArgumentException("WRONG URI: $uri")
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        val id = repository.insert(values)
        return ContentUris.withAppendedId(MOVIES_PROVIDER_CONTENT_URI, id)
    }

    override fun onCreate(): Boolean {
        repository = getRepository(context!!)
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? = repository.query(projection, selection, selectionArgs, sortOrder)
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        when(URI_MATCHER.match(uri)){
            ITEMS -> return repository.update(values,selection, selectionArgs)
            ITEM_ID -> {
                uri.lastPathSegment?.let { id ->
                    repository.update(values,"${Item::id.name} = ?", arrayOf(id))
                }

            }
        }
        throw IllegalArgumentException("WRONG URI: $uri")
    }
}