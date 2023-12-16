package com.example.keys2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView

import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

import androidx.navigation.ui.AppBarConfiguration

import com.example.keys2.databinding.ActivityEditKeyBinding

class EditKeyActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEditKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window , false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_key)

        var strForDel: String = ""
        //Инициализация полей и кнопок
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnFind: Button = findViewById(R.id.btnFind)


        val OnOffSwitch: Switch = findViewById(R.id.switch3)

        val btnAdd: Button = findViewById(R.id.buttonAdd)
        val btnEdit: Button = findViewById(R.id.buttonEdit)
        val btnDel: Button = findViewById(R.id.buttonDelete)
        ClearFieldsResult()

        var findK = intent.getStringExtra("findKey").toString()
        var lengthK = intent.getStringExtra("lengthKey")

        //Проверяем передали ли из первого активити значение
        if (lengthK != null) {
            val editFindK: EditText = findViewById(R.id.editFindKey)
            editFindK.setText(findK)
            ClearFieldsResult()
            strForDel = findKey()
        } else {
            iniacKeyBD()
        }

        //Обработка нажатий кнопок и переключателя
        btnClear.setOnClickListener {
            //Очищаем все поля
            ClearFieldAll()
        }
        //Поиск ключа в БД
        btnFind.setOnClickListener {
            //Очищаем поля вывода
            ClearFieldsResult()
            strForDel = findKey()
        }
        //Добавление ключа в БД
        btnAdd.setOnClickListener {
            AddRecordKey()
            Toast.makeText(
                this , "Запись добавлена!!!" ,
                Toast.LENGTH_LONG
            ).show()
        }
        //Редактирование записей
        btnEdit.setOnClickListener {
            DeleteKey(strForDel)
            Toast.makeText(this , "Запись отредактирована!!!" ,
                Toast.LENGTH_LONG).show()
            AddRecordKey()
            Toast.makeText(this , "Запись отредактирована!!!" ,
                Toast.LENGTH_LONG).show()
        }
        //Удаление записей
        btnDel.setOnClickListener {
            DeleteKey(strForDel)
            Toast.makeText(
                this , "Запись удалена!!!" ,
                Toast.LENGTH_LONG
            ).show()
        }
        //Переключатель режима Редактирования
        OnOffSwitch.setOnCheckedChangeListener { button , b ->
            if (OnOffSwitch.isChecked) {
                btnAdd.isEnabled = true
                btnEdit.isEnabled = true
                btnDel.isEnabled = true
                btnFind.isEnabled = false
            } else {
                btnAdd.isEnabled = false
                btnEdit.isEnabled = false
                btnDel.isEnabled = false
                btnFind.isEnabled = true

            }
        }
    }

    //переключение между Активити (на Майн активити)
    fun goEditKey(view: View) {
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
    }

    //Очистка полей результата
    fun ClearFieldsResult() {
        val checkKey: CheckBox = findViewById(R.id.checkKey)
        val editMKU: EditText = findViewById(R.id.editMKU)
        val editComment: EditText = findViewById(R.id.editComments)
        checkKey.setText("")
        checkKey.isChecked = false
        checkKey.hint = "Наличие ключа"
        editMKU.setText("")
        editMKU.hint = "МКУ"
        editComment.setText("")
        editComment.hint = "Комментарий"

    }

    //Очистка всех полей
    fun ClearFieldAll() {
        val editFindKey: EditText = findViewById(R.id.editFindKey)
        editFindKey.setText("")
        editFindKey.hint = "Введите ключ"
        ClearFieldsResult()
    }

    //Функция добавления записи в таблицу
    //
    fun AddRecordKey() {
        //подключаемся в БД
        val db = DBHelperKey(this , null)
        //инициализация EDIT`ов
        val edtFindKey: EditText = findViewById(R.id.editFindKey)
        val checkKey: CheckBox = findViewById(R.id.checkKey)
        val editMKU: EditText = findViewById(R.id.editMKU)
        val editComments: EditText = findViewById(R.id.editComments)

        //Проверяем что поля не пустые, чтобы не добавлять в базу пустых записей
        if (edtFindKey.text.toString() == "") {
            Toast.makeText(
                this , "Введите ключ!!!" ,
                Toast.LENGTH_LONG
            ).show()
        } else {
            var key = edtFindKey.text.toString()
            var YNKey = checkKey.isChecked.toString()
            var campus = editMKU.text.toString()
            var comments = editComments.text.toString()
            // вызов метода для добавления
            // имя в нашу базу данных
            db.addKey(key , YNKey , campus , comments)
        }
    }

    //Удаление записи из БД
    fun DeleteKey(strId: String) {
        val editComment: EditText = findViewById(R.id.editComments)
        if (strId == ""){
            editComment.setText("Ключ не выбран!" + "\n" + "Выберете ключ!")
        }
        val db = DBHelperKey(this , null)
        db.deleteKey(strId)
    }

    //Функция поиска ключа в БД
    fun findKey(): String{
        var strForDel = ""
        val edtFindKey: EditText = findViewById(R.id.editFindKey)
        val checkKey: CheckBox = findViewById(R.id.checkKey)
        val editMKU: EditText = findViewById(R.id.editMKU)
        val editComments: EditText = findViewById(R.id.editComments)

        //Создание класса DBHelper
        val db = DBHelperKey(this , null)
        val cursor = db.getNameKey()
        if (cursor!!.moveToLast()) {
            //Переводрим курсор на первую позицию
            cursor!!.moveToFirst()
            //Поиск по первому элементу базы
            if (edtFindKey.text.toString() == cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))
                    .toString()
            ) {
                strForDel =
                    cursor.getString(cursor.getColumnIndex(DBHelperKey.ID_COL_KEY)).toString()
                checkKey.isChecked =
                    cursor.getString(cursor.getColumnIndex(DBHelperKey.YN_COL_KEY)).toBoolean()
                //Обработка checkBox со сменой цвета
                if (checkKey.isChecked == true) {
                    checkKey.setTextColor(Color.parseColor("#00FF00"))
                    checkKey.text = "Присутствует"
                } else {
                    checkKey.setTextColor(Color.parseColor("#FF0000"))
                    checkKey.text = "Отсутствует"
                }
                editMKU.setText(
                    cursor.getString(cursor.getColumnIndex(DBHelperKey.CAMPUS_COL_KEY)).toString()
                )
                editComments.setText(
                    cursor.getString(cursor.getColumnIndex(DBHelperKey.COMMENTS_COL_KEY))
                        .toString())
                //Возвращаем из функции данные для удаления  и редактирования
                return strForDel
            }
            //Поиск по остальным базам
            while (cursor.moveToNext()) {
                if (edtFindKey.text.toString() == cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))
                        .toString()
                ) {
                    strForDel =
                        cursor.getString(cursor.getColumnIndex(DBHelperKey.ID_COL_KEY)).toString()
                    checkKey.isChecked =
                        cursor.getString(cursor.getColumnIndex(DBHelperKey.YN_COL_KEY)).toBoolean()
                    //Обработка checkBox со сменой цвета
                    if (checkKey.isChecked == true) {
                        checkKey.setTextColor(Color.parseColor("#00FF00"))
                        checkKey.text = "Присутствует"
                    } else {
                        checkKey.setTextColor(Color.parseColor("#FF0000"))
                        checkKey.text = "Отсутствует"
                    }
                    editMKU.setText(
                        cursor.getString(cursor.getColumnIndex(DBHelperKey.CAMPUS_COL_KEY))
                            .toString()
                    )
                    editComments.setText(
                        cursor.getString(cursor.getColumnIndex(DBHelperKey.COMMENTS_COL_KEY))
                            .toString()
                    )
                    //Возвращаем из функции данные для удаления  и редактирования
                    return strForDel
                } else {
                    if (strForDel == "") {
                        editComments.setText(
                            "Искомый ключ не найден!!!\n" +
                                    "Добавьте в базу новый ключ!!!!"
                        )
                    }
                }
            }
        }else{
           editComments.setText("База ключей пуста!" + "\n" + "Добавьте перую запись!")
        }
        return strForDel
    }

    //составление списка ключей для подсказок
    fun iniacKeyBD() {

        //определяем длину базы данных
        var idLastDB: Int = 0
        //Создание класса DBHelper
        val db = DBHelperKey(this , null)
        val cursor = db.getNameKey()
        if (cursor!!.moveToLast()) {
            idLastDB = (cursor.getInt(cursor.getColumnIndex(DBHelperKey.ID_COL_KEY)))

            //автоподмтаноака для ключей
            var KeyArr: Array<String> = Array(idLastDB) { "" }
            cursor.moveToFirst()
            //первое значение вносим в массив как уникальное
            KeyArr[0] = (cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY)))
            var j = 0
            while (cursor.moveToNext()) {
                j++
                KeyArr[j] = cursor.getString(cursor.getColumnIndex(DBHelperKey.KEY_COl_KEY))
            }
            //Удаляем из массива НЕ УНИКАЛЬНЫЕ записи
            val KeyArKey = KeyArr.distinct()

            // Получаем ссылку на элемент AutoCompleteTextView в разметке
            val keyScr = findViewById<AutoCompleteTextView>(R.id.editFindKey)
            val infoKeyTxt: TextView = findViewById(R.id.infoKeyTxt)

            //Пишем адаптер
            val adapter = ArrayAdapter(this , android.R.layout.simple_list_item_1 , KeyArKey)
            (keyScr as AutoCompleteTextView?)?.setAdapter(adapter)

            val editComment: EditText = findViewById(R.id.editComments)
            infoKeyTxt.append("Присутствуют ключи: ")

            for (i in KeyArKey) {
                infoKeyTxt.append(" $i,")
            }
            cursor.close()
        } else {
            val editComment: EditText = findViewById(R.id.editComments)
            editComment.setText("База дынных ключей пуста!" + "\n" + "Добавьте первый ключ!")
        }
    }
}
