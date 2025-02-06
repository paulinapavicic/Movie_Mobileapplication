package hr.algebra.movie.factory

import android.content.Context
import hr.algebra.movie.dao.MovieSqlHelper

fun getRepository(context: Context) = MovieSqlHelper(context)