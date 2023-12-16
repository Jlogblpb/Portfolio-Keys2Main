package com.example.keys2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import java.util.Collections.swap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //функция автоподстановки улиц в поле
        iniacBD()
        var strForDel = ""
        var YNKey: Boolean
        var stockKey: Boolean

        //инициализация конопок
        val editKey: TextView = findViewById(R.id.editKey)
        val editMku: TextView = findViewById(R.id.editMKU)
        val editComment: TextView = findViewById(R.id.editComment)
        val onOfCheck: Switch = findViewById(R.id.switch2)
        val btnAdd: Button = findViewById(R.id.buttonAdd)
        val btnSave: Button = findViewById(R.id.buttonEdit)
        val btnCancel: Button = findViewById(R.id.buttonDelete)
        val btnEdit: Button = findViewById(R.id.buttonEdit)
        val btnClear: Button = findViewById(R.id.buttonClear)
        val btnDelete: Button = findViewById(R.id.buttonDelete)
        val btnFind: Button = findViewById(R.id.buttonFind)
        val btnKeyInfo: Button = findViewById(R.id.buttonKeyInfo)
        val inputHome: AutoCompleteTextView = findViewById(R.id.autoCompleteHome)
        val inputStreet: AutoCompleteTextView = findViewById(R.id.autoCompleteStreet)

        //Обработка нажатия кнопки ПОИСК
        //поиск ключа по улице и дому
        btnFind.setOnClickListener {

            //Очищаем поля вывода
            ClearFieldsResult()

            //подключаемся в БД
            val db = DBHelper(this , null)
            val cursor = db.getName()

            if (cursor!!.moveToLast()) {
                // переводим курсор в первую позицию
                cursor!!.moveToFirst()
                //поиск по БД
                // Поиск по ПЕРВОМУ элементу базы
                if ((cursor.getString(cursor.getColumnIndex(DBHelper.STREET_COl))).toString()
                    == inputStreet.text.toString()
                ) {
                    if ((cursor.getString(cursor.getColumnIndex(DBHelper.HOUSE_COL))).toString()
                        == inputHome.text.toString()
                    ) {
                        strForDel = (cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)))
                        editKey.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COL)))
                        //обращаемся к функции определения есть ключ или нет
                        YNKey = FindYNKey(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COL)))
                        stockKey = FindStockKey(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COL)))
                        //Включаем/выключаем кнопку относительно того есть ключ или нет
                        if (YNKey == true)
                            btnKeyInfo.setBackgroundColor(Color.parseColor("#669900"))
                        else
                            btnKeyInfo.setBackgroundColor(Color.parseColor("#cc0000"))
                        if (stockKey==true)
                            btnKeyInfo.isEnabled = true
                        else
                            btnKeyInfo.isEnabled = false
                        editMku.setText(cursor.getString(cursor.getColumnIndex(DBHelper.CAMPUS_COL)))
                        editComment.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COMMENTS_COL)))
                    }
                }
                //Поиско по ОСТАЛЬНЫМ элементам базы
                while (cursor.moveToNext()) {
                    if ((cursor.getString(cursor.getColumnIndex(DBHelper.STREET_COl))).toString()
                        == inputStreet.text.toString()
                    ) {
                        if ((cursor.getString(cursor.getColumnIndex(DBHelper.HOUSE_COL))).toString()
                            == inputHome.text.toString()
                        ) {
                            strForDel = (cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)))
                            editKey.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COL)))
                            //обращаемся к функции определения есть ключ или нет
                            YNKey = FindYNKey(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COL)))
                            stockKey = FindStockKey(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COL)))
                            //Включаем/выключаем кнопку относительно того есть ключ или нет
                            if (YNKey == true)
                                btnKeyInfo.setBackgroundColor(Color.parseColor("#99cc00"))
                            else
                                btnKeyInfo.setBackgroundColor(Color.parseColor("#cc0000"))
                            if (stockKey==true)
                                btnKeyInfo.isEnabled = true
                            else
                                btnKeyInfo.isEnabled = false
                            editMku.setText(cursor.getString(cursor.getColumnIndex(DBHelper.CAMPUS_COL)))
                            editComment.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COMMENTS_COL)))
                            editComment.append("\n")
                            break
                        }
                    } else {
                        editComment.text =
                            "Искомый адрес не найден, попробуйте изменить условия поиска!" +
                                    " Обратите внимание, что корпус вводится через пробел!!!"
                    }
                }
                cursor.close()
            }else{
                val editComment: EditText = findViewById(R.id.editComment)
                editComment.setText("База адресов и ключей пуста!" + "\n" + "Добавьте перую запись!")
            }
        }

        //обработка кнопки KeyInfo
        btnKeyInfo.setOnClickListener{
            val intent = Intent(this@MainActivity, EditKeyActivity::class.java)
            // в ключ username пихаем текст из первого текстового поля
            intent.putExtra("findKey", editKey.text.toString())
            intent.putExtra("lengthKey", editKey.text.length.toString())
            startActivity(intent)
        }

        //ВКЛЮЧЕНИЕ РЕДАКТИРОВАНИЕ ЗАПИСЕЙ!!!!
        onOfCheck.setOnCheckedChangeListener { button , b ->
            if (onOfCheck.isChecked) {
                btnAdd.isEnabled = true
                btnSave.isEnabled = true
                btnCancel.isEnabled = true
                btnFind.isEnabled = false
                if ("Искомый адрес не найден, попробуйте изменить условия поиска!" +
                    " Обратите внимание, что корпус вводится через пробел!!!" ==
                    editComment.text.toString()) {
                    editComment.text = ""
                }
            } else {
                btnAdd.isEnabled = false
                btnSave.isEnabled = false
                btnCancel.isEnabled = false
                btnFind.isEnabled = true
            }
        }

        //Очистка полей, для новой записи
        btnClear.setOnClickListener {
            ClearFields()
        }

        //Обработчик кнопки Add, т.е. непосредственное добавление записи в БД
        btnAdd.setOnClickListener {
            AddRecord()
            Toast.makeText(
                this , "Запись добавлена!!!" ,
                Toast.LENGTH_LONG
            ).show()
        }
        //Обработчик кнопки Радактирования
        btnEdit.setOnClickListener {
            DeleteRecord(strForDel)
            AddRecord()
            Toast.makeText(
                this , "Запись отредактирована!!!" ,
                Toast.LENGTH_LONG
            ).show()
        }

        //Обработчик кнопки DELETE
        btnDelete.setOnClickListener {
            DeleteRecord(strForDel)

            // Тост за сообщение на экране
            Toast.makeText(
                this , "Запись удалена!!!" ,
                Toast.LENGTH_LONG
            ).show()
        }

        //Обработчик нажатия на поле "Дом"
        // Автозаполнение домов исходя из выбранной улицы
        inputHome.setOnFocusChangeListener { _, hasFocus ->
            val streetScr = findViewById<AutoCompleteTextView>(R.id.autoCompleteStreet)
            val editComment: EditText = findViewById(R.id.editComment)
            var street = streetScr.text.toString()
            //Создание класса DBHelper
            val db = DBHelper(this , null)
            // ниже переменная для курсора
            val cursor = db.getName()
            if (cursor!!.moveToLast()) {

                //Заполняем список home домами с переданной в функцияю улицы
                var home = listOf<String>()
                cursor.moveToFirst()
                //вносим первое значение исходя из улицы
                if (cursor.getString(cursor.getColumnIndex(DBHelper.STREET_COl)) == street)
                    home = home + ((cursor.getString(cursor.getColumnIndex(DBHelper.HOUSE_COL))))
                //остальные значения исходя из улицы
                while (cursor.moveToNext())
                {
                    if (cursor.getString(cursor.getColumnIndex(DBHelper.STREET_COl)) == street)
                        home = home + ((cursor.getString(cursor.getColumnIndex(DBHelper.HOUSE_COL))))
                }

                //Сортируем пузырьком дома в списке home
                var ggk:Int
                for (j in 0..home.size){
                    ggk=0
                    for (i in 0..home.size - 2){
                        if (home[i] > home[i+1]){
                            ggk++
                            swap(home, i,i+1)
                        }
                    }
                    if (ggk == 0)
                        break
                }

                // Получаем ссылку на элемент AutoCompleteTextView в разметке
                val homeScr = findViewById<AutoCompleteTextView>(R.id.autoCompleteHome)

                //Пишем адаптер
                val adapterH = ArrayAdapter(this , android.R.layout.simple_list_item_1 , home)
                (homeScr as AutoCompleteTextView?)?.setAdapter(adapterH)
                cursor.close()
            } else {
                editComment.setText("База адресов и ключей пуста!" + "\n" + "Добавьте перую запись!")
            }
        }
    }

    //Автозаполнение  улиц и домов
    fun iniacBD() {
        //определяем длину базы данных
        var idLastDB: Int = 0
        //Создание класса DBHelper
        val db = DBHelper(this , null)
        // ниже переменная для курсора
        // мы вызвали метод для получения
        // все имена из нашей базы данных
        // и добавить в текстовое представление имени
        val cursor = db.getName()
        if (cursor!!.moveToLast()) {
            idLastDB = (cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL).toInt()))

            //Автоподстановка для улиц и домов

            //сортируем улицы и дома без повторений
            var street: Array<String> = Array(idLastDB) { "" }
            //var home: Array<String> = Array(idLastDB) { "" }
            cursor.moveToFirst()
            //первое значение вносим в массив как уникальное
            street[0] = (cursor.getString(cursor.getColumnIndex(DBHelper.STREET_COl)))
            //home[0] = ((cursor.getString(cursor.getColumnIndex(DBHelper.HOUSE_COL))))
            var j = 0
            while (cursor.moveToNext()) {
                j++
                street[j] = cursor.getString(cursor.getColumnIndex(DBHelper.STREET_COl))
                //home[j] = ((cursor.getString(cursor.getColumnIndex(DBHelper.HOUSE_COL))))
            }
            val streets = street.distinct()

            // Получаем ссылку на элемент AutoCompleteTextView в разметке
            val streetScr = findViewById<AutoCompleteTextView>(R.id.autoCompleteStreet)

            //Пишем адаптер
            val adapter = ArrayAdapter(this , android.R.layout.simple_list_item_1 , streets)
            (streetScr as AutoCompleteTextView?)?.setAdapter(adapter)

        } else {
            val editComment: EditText = findViewById(R.id.editComment)
            editComment.setText("База адресов и ключей пуста!" + "\n" + "Добавьте перую запись!")
        }
    }

    //Функция добавления записи в таблицу
    fun AddRecord() {
        //подключаемся в БД
        val db = DBHelper(this , null)
        //инициализация EDIT`ов
        val inputStreet = findViewById<AutoCompleteTextView>(R.id.autoCompleteStreet)
        val inputHome = findViewById<AutoCompleteTextView>(R.id.autoCompleteHome)
        val editKey: TextView = findViewById(R.id.editKey)
        val editMku: TextView = findViewById(R.id.editMKU)
        val editComment: TextView = findViewById(R.id.editComment)

        //Проверяем что поля не пустые, чтобы не добавлять в базу пустых записей
        if (inputStreet.text.toString() == "") {
            Toast.makeText(
                this , "Введите название улицы!!!" ,
                Toast.LENGTH_LONG
            ).show()
        } else {
            if (inputHome.text.toString() == "") {
                Toast.makeText(
                    this , "Введите номер дома!!!" ,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                if (editKey.text.toString() == "") {
                    Toast.makeText(
                        this , "Введите ключ!!!" ,
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    var street = inputStreet.text.toString()
                    var home = inputHome.text.toString()
                    var key = editKey.text.toString()
                    var mku = editMku.text.toString()
                    var comment = editComment.text.toString()
                    // вызов метода для добавления
                    // имя в нашу базу данных
                    db.addHouseKey(street , home , key , mku , comment)
                    // Тост за сообщение на экране
                }
            }
        }
    }

    //Функция удаления записи из таблицы
    fun DeleteRecord(strId: String) {
        val db = DBHelper(this , null)
        db.deleteHouseKey(strId)
        Toast.makeText(
            this , "Запись удалена!!!" ,
            Toast.LENGTH_LONG
        ).show()
    }

    //Функция очистки тольео полей результата
    fun ClearFieldsResult() {
        val editKey: TextView = findViewById(R.id.editKey)
        val editMku: TextView = findViewById(R.id.editMKU)
        val editComment: TextView = findViewById(R.id.editComment)
        editKey.setText("")
        editKey.hint = "Тут будет ключ"
        editMku.setText("")
        editMku.hint = "Тут будет номер МКУ"
        editComment.setText("")
        editComment.hint = "Тут будет комментарий, конечно если найдется.."
    }

    //Функция очистки полей
    fun ClearFields() {
        val streetScr = findViewById<AutoCompleteTextView>(R.id.autoCompleteStreet)
        val homeScr = findViewById<AutoCompleteTextView>(R.id.autoCompleteHome)
        val editKey: TextView = findViewById(R.id.editKey)
        val editMku: TextView = findViewById(R.id.editMKU)
        val editComment: TextView = findViewById(R.id.editComment)
        val butKeyInfo: Button = findViewById(R.id.buttonKeyInfo)
        editKey.setText("")
        editKey.hint = "Тут будет ключ"
        editMku.setText("")
        editMku.hint = "Тут будет номер МКУ"
        editComment.setText("")
        editComment.hint = "Тут будет комментарий, конечно если найдется..."
        streetScr.setText("")
        streetScr.hint = "Введите название улицы"
        homeScr.setText("")
        homeScr.hint = "Введите номер дома"
        butKeyInfo.isEnabled = false
    }

    //Переключение между Activity
    fun goEditKey(view: View) {
        val intent = Intent(this , EditKeyActivity::class.java)
        startActivity(intent)
    }

    //Функция определения есть ли у ключ вообще
    fun FindStockKey(key: String): Boolean {
        //подключаемся к базе
        val dbK = DBHelperKey(this , null)
        val cursor = dbK.getNameKey()

        if (cursor!!.moveToLast()) {
            // переводим курсор в первую позицию
            cursor!!.moveToFirst()
            if ((cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))) == key)
            {
                return true
            }
            //Поиско по ОСТАЛЬНЫМ элементам базы
            while(cursor.moveToNext())
                if ((cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))) == key)
                {
                    return true
                }
        }
        cursor.close()
        return false
    }

    //Функция определения есть ли у нас ключ
    fun FindYNKey(key: String): Boolean {

        //подключаемся к базе
        val dbK = DBHelperKey(this , null)
        val cursor = dbK.getNameKey()

        if (cursor!!.moveToLast()) {
            // переводим курсор в первую позицию
            cursor!!.moveToFirst()
            if ((cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))) == key)
            {
               return cursor.getString(cursor.getColumnIndex(DBHelperKey.YN_COL_KEY)).toBoolean()
            }
            //Поиско по ОСТАЛЬНЫМ элементам базы
        while(cursor.moveToNext())
            if ((cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))) == key)
            {
               return cursor.getString(cursor.getColumnIndex(DBHelperKey.YN_COL_KEY)).toBoolean()
            }
        }
        cursor.close()
        return false
    }
}
