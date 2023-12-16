package com.example.keys2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelperKey(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DBHelperKey.DATABASE_NAME , factory, DBHelperKey.DATABASE_VERSION) {

    // ниже метод создания базы данных запросом sqlite
    override fun onCreate(db: SQLiteDatabase) {

        // мы вызываем sqlite
        // метод для выполнения нашего запроса

        db.execSQL("CREATE TABLE " + DBHelperKey.TABLE_NAME_KEY + " (" + DBHelperKey.ID_COL_KEY + " INTEGER PRIMARY KEY, " +
                DBHelperKey.KEY_COl_KEY + " TEXT," + DBHelperKey.YN_COL_KEY + " TEXT," + DBHelperKey.CAMPUS_COL_KEY + " TEXT," +
                DBHelperKey.COMMENTS_COL_KEY + " TEXT" + ")")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // этот метод проверяет, существует ли уже таблица
        db.execSQL("DROP TABLE IF EXISTS " + DBHelperKey.TABLE_NAME_KEY)
        onCreate(db)
    }

    fun getNameKey(): Cursor? {

        // здесь мы создаем читаемый
        // переменная нашей базы данных
        // так как мы хотим прочитать из него значение
        val db = this.readableDatabase

        // приведенный ниже код возвращает курсор на
        // считываем данные из базы
        return db.rawQuery("SELECT * FROM " + DBHelperKey.TABLE_NAME_KEY , null)
    }

    // Этот метод для добавления Ключей
    fun addKey(key : String, YNKey : String, campus : String, comments : String ){

        // ниже мы создаем
        // переменная значения содержимого
        val values = ContentValues()

        // вставляем наши значения
        // в виде пары ключ-значение
        values.put(DBHelperKey.KEY_COl_KEY , key)
        values.put(DBHelperKey.YN_COL_KEY , YNKey)
        values.put(DBHelperKey.CAMPUS_COL_KEY , campus)
        values.put(DBHelperKey.COMMENTS_COL_KEY , comments)


        // здесь мы создаем
        // перезаписываемая переменная
        // наша база данных, как мы хотим
        // вставляем значение в нашу базу данных
        val db = this.writableDatabase

        // все значения вставляются в базу данных
        db.insert(TABLE_NAME_KEY , null, values)

        // наконец-то мы
        // закрытие нашей базы данных
        db.close()
    }

    //Удаление строки
    fun deleteKey(iDStr: String){

        // ниже мы создаем
        // переменная значения содержимого
        val values = ContentValues()

        // здесь мы создаем
        // перезаписываемая переменная
        // наша база данных, как мы хотим
        // вставляем значение в нашу базу данных
        val db = this.writableDatabase

        // все значения вставляются в базу данных
        db.delete(DBHelperKey.TABLE_NAME_KEY , DBHelperKey.ID_COL_KEY + "=?", arrayOf(iDStr))

        // закрытие нашей базы данных
        db.close()
    }

    companion object {
        // здесь мы определили переменные для нашей базы данных

        // ниже переменная для имени базы данных
        private val DATABASE_NAME = "keys_key"

        // ниже переменная для версии базы данных
        private val DATABASE_VERSION = 1

                         //--===***** Данные по таблице базы ключей

        // ниже переменная для имени таблицы
        val TABLE_NAME_KEY = "keys_keys"

        // ниже переменная для столбца id
        val ID_COL_KEY = "id_keys"

        // ниже переменная для столбца street
        val KEY_COl_KEY = "key_keys"

        // ниже переменная для столбца house
        val YN_COL_KEY = "yn_keys"

        // ниже переменная для столбца campus
        val CAMPUS_COL_KEY = "campus_keys"

        // ниже переменная для столбца camments
        val COMMENTS_COL_KEY = "comments_keys"
    }
}