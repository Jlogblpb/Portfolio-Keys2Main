package com.example.keys2



import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // ниже метод создания базы данных запросом sqlite
    override fun onCreate(db: SQLiteDatabase) {
        // ниже приведен запрос sqlite, где имена столбцов
        // вместе с их типами данных дается
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID_COL + " INTEGER PRIMARY KEY, " +
                STREET_COl + " TEXT," + HOUSE_COL + " TEXT," + KEY_COL + " TEXT," +
                CAMPUS_COL + " TEXT," + COMMENTS_COL + " TEXT" + ")")
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // этот метод проверяет, существует ли уже таблица
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // Этот метод для добавления данных в нашу базу данных
    fun addHouseKey(street : String, house : String, key : String, campus : String, comments : String ){

        // ниже мы создаем
        // переменная значения содержимого
        val values = ContentValues()

        // вставляем наши значения
        // в виде пары ключ-значение
        values.put(STREET_COl, street)
        values.put(HOUSE_COL, house)
        values.put(KEY_COL, key)
        values.put(CAMPUS_COL, campus)
        values.put(COMMENTS_COL, comments)


        // здесь мы создаем
        // перезаписываемая переменная
        // наша база данных, как мы хотим
        // вставляем значение в нашу базу данных
        val db = this.writableDatabase

        // все значения вставляются в базу данных
        db.insert(TABLE_NAME, null, values)

        // наконец-то мы
        // закрытие нашей базы данных
        db.close()
    }

    //Удаление строки
    fun deleteHouseKey(iDStr: String){

        // ниже мы создаем
        // переменная значения содержимого
        val values = ContentValues()

        // здесь мы создаем
        // перезаписываемая переменная
        // наша база данных, как мы хотим
        // вставляем значение в нашу базу данных
        val db = this.writableDatabase

        // все значения вставляются в базу данных
        db.delete(TABLE_NAME, ID_COL + "=?", arrayOf(iDStr))

        // закрытие нашей базы данных
        db.close()
    }

    // метод ниже для получения
    // все данные из нашей базы
    fun getName(): Cursor? {

        // здесь мы создаем читаемый
        // переменная нашей базы данных
        // так как мы хотим прочитать из него значение
        val db = this.readableDatabase

        // приведенный ниже код возвращает курсор на
        // считываем данные из базы
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
    }

    companion object{
        // здесь мы определили переменные для нашей базы данных


        //--===***** данные по теблице поиска ключей по улице и дому  *****===--

        // ниже переменная для имени базы данных
        private val DATABASE_NAME = "keys"

        // ниже переменная для версии базы данных
        private val DATABASE_VERSION = 1

        // ниже переменная для имени таблицы
        val TABLE_NAME = "adres_key"

        // ниже переменная для столбца id
        val ID_COL = "id"

        // ниже переменная для столбца street
        val STREET_COl = "street"

        // ниже переменная для столбца house
        val HOUSE_COL = "house"

        // ниже переменная для столбца key
        val KEY_COL = "key"

        // ниже переменная для столбца campus
        val CAMPUS_COL = "campus"

        // ниже переменная для столбца camments
        val COMMENTS_COL = "comments"

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