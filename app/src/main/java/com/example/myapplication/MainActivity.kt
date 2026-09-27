package com.example.myapplication

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Html
import android.widget.TabHost
import android.widget.ListView
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : ComponentActivity() {
    private lateinit var fullName: EditText
    private lateinit var gender: RadioGroup
    private lateinit var course: Spinner
    private lateinit var difficulty: SeekBar
    private lateinit var calendar: CalendarView
    private lateinit var dateButton: Button
    private val birthDate = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }
    private var player: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
        )
        setContentView(R.layout.activity_main)

        val tabs = findViewById<TabHost>(android.R.id.tabhost)
        tabs.setup()
        tabs.addTab(tabs.newTabSpec("registration").setIndicator(getString(R.string.tab_registration)).setContent(R.id.form_scroll))
        tabs.addTab(tabs.newTabSpec("rules").setIndicator(getString(R.string.tab_rules)).setContent(R.id.rules_tab))
        tabs.addTab(tabs.newTabSpec("authors").setIndicator(getString(R.string.tab_authors)).setContent(R.id.authors_list))
        tabs.addTab(tabs.newTabSpec("settings").setIndicator(getString(R.string.tab_settings)).setContent(R.id.settings_tab))
        tabs.currentTab = savedInstanceState?.getInt("selectedTab") ?: 0
        setupRules()
        setupAuthors()
        setupSettings()
        ViewCompat.setOnApplyWindowInsetsListener(tabs) { view, insets ->
            val padding = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(padding.left, padding.top, padding.right, padding.bottom)
            insets
        }
        fullName = findViewById(R.id.full_name)
        val nameFilter = InputFilter { source, start, end, _, _, _ ->
            val input = source.subSequence(start, end)
            if (input.all { it.isLetter() || it == ' ' }) {
                null
            } else {
                input.filter { it.isLetter() || it == ' ' }
            }
        }
        fullName.filters = arrayOf(nameFilter, *fullName.filters)
        gender = findViewById(R.id.gender)
        course = findViewById(R.id.course)
        difficulty = findViewById(R.id.difficulty)
        calendar = findViewById(R.id.birth_calendar)
        dateButton = findViewById(R.id.birth_date_button)
        course.adapter = ArrayAdapter.createFromResource(
            this, R.array.courses, android.R.layout.simple_spinner_item
        ).apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        calendar.maxDate = System.currentTimeMillis()
        if (savedInstanceState != null) {
            birthDate.timeInMillis = savedInstanceState.getLong("birthDate")
            savedInstanceState.getBundle("player")?.let { saved ->
                player = Player(
                    saved.getString("name").orEmpty(),
                    Gender.valueOf(saved.getString("gender")!!),
                    saved.getInt("course"), saved.getInt("difficulty"),
                    BirthDate(saved.getInt("year"), saved.getInt("month"), saved.getInt("day"))
                )
            }
        }
        updateDate()
        updateDifficulty()
        player?.let(::showPlayer)

        calendar.setOnDateChangeListener { _, year, month, day ->
            birthDate.set(year, month, day)
            updateDateLabel()
        }
        // Быстрый переход к году рождения, без перелистывания календаря по месяцам.
        dateButton.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    birthDate.set(year, month, day)
                    updateDate()
                },
                birthDate.get(Calendar.YEAR), birthDate.get(Calendar.MONTH),
                birthDate.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = calendar.minDate
                datePicker.maxDate = calendar.maxDate
            }.show()
        }
        difficulty.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateDifficulty()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
        gender.setOnCheckedChangeListener { _, _ ->
            findViewById<TextView>(R.id.gender_error).visibility = View.GONE
        }
        findViewById<Button>(R.id.register).setOnClickListener { registerPlayer() }
    }

    private fun setupRules() {
        val html = resources.openRawResource(R.raw.rules).bufferedReader().use { it.readText() }
        findViewById<TextView>(R.id.rules_text).text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
    }

    private fun setupAuthors() {
        val authors = listOf(getString(R.string.author_name))
        findViewById<ListView>(R.id.authors_list).adapter = object : BaseAdapter() {
            override fun getCount() = authors.size
            override fun getItem(position: Int) = authors[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView ?: layoutInflater.inflate(R.layout.author_item, parent, false)
                row.findViewById<TextView>(R.id.author_name).text = authors[position]
                row.findViewById<ImageView>(R.id.author_photo).apply {
                    setImageResource(android.R.drawable.ic_menu_myplaces)
                    contentDescription = authors[position]
                }
                return row
            }
        }
    }

    private fun setupSettings() {
        val preferences = getSharedPreferences("game_settings", MODE_PRIVATE)
        fun bind(id: Int, label: Int, format: Int, key: String, initial: Int, step: Int = 1) {
            val bar = findViewById<SeekBar>(id)
            val text = findViewById<TextView>(label)
            bar.progress = preferences.getInt(key, initial) / step - 1
            fun update() { text.text = getString(format, (bar.progress + 1) * step) }
            update()
            bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    update()
                    preferences.edit().putInt(key, (progress + 1) * step).apply()
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            })
        }
        bind(R.id.game_speed, R.id.game_speed_label, R.string.game_speed_value, "speed", 5)
        bind(R.id.max_cockroaches, R.id.max_cockroaches_label, R.string.max_cockroaches_value, "cockroaches", 10)
        bind(R.id.bonus_interval, R.id.bonus_interval_label, R.string.bonus_interval_value, "bonusInterval", 10)
        bind(R.id.round_duration, R.id.round_duration_label, R.string.round_duration_value, "roundDuration", 60, 10)
    }

    private fun selectedDate() = BirthDate(
        birthDate.get(Calendar.YEAR), birthDate.get(Calendar.MONTH) + 1,
        birthDate.get(Calendar.DAY_OF_MONTH)
    )

    private fun updateDate() {
        calendar.setDate(birthDate.timeInMillis, false, true)
        updateDateLabel()
    }

    private fun updateDateLabel() {
        dateButton.text = getString(R.string.birth_date_value, selectedDate().formatted())
    }

    private fun updateDifficulty() {
        findViewById<TextView>(R.id.difficulty_label).text =
            getString(R.string.difficulty_value, difficulty.progress + 1)
    }

    private fun registerPlayer() {
        val name = fullName.text.toString().trim().replace(Regex("\\s+"), " ")
        if (name.isBlank()) {
            fullName.error = getString(R.string.name_required)
            fullName.requestFocus()
            return
        }
        val selectedGender = when (gender.checkedRadioButtonId) {
            R.id.gender_male -> Gender.MALE
            R.id.gender_female -> Gender.FEMALE
            else -> {
                findViewById<TextView>(R.id.gender_error).visibility = View.VISIBLE
                gender.requestFocus()
                return
            }
        }
        player = Player(name, selectedGender, course.selectedItemPosition + 1,
            difficulty.progress + 1, selectedDate())
        showPlayer(player!!)
        WindowCompat.getInsetsController(window, fullName).hide(WindowInsetsCompat.Type.ime())
        fullName.clearFocus()
        val scroll = findViewById<ScrollView>(R.id.form_scroll)
        scroll.post { scroll.smoothScrollTo(0, findViewById<View>(R.id.result_panel).top) }
    }

    private fun showPlayer(player: Player) {
        findViewById<TextView>(R.id.result).text = getString(
            R.string.player_summary, player.fullName,
            getString(if (player.gender == Gender.MALE) R.string.male else R.string.female),
            player.course, player.difficulty, player.birthDate.formatted(),
            getString(player.zodiac.nameResource)
        )
        findViewById<ImageView>(R.id.zodiac_image).apply {
            setImageDrawable(ZodiacDrawable(player.zodiac))
            contentDescription = getString(player.zodiac.nameResource)
        }
        findViewById<View>(R.id.result_panel).visibility = View.VISIBLE
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateDate()
        updateDifficulty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedTab", findViewById<TabHost>(android.R.id.tabhost).currentTab)
        outState.putLong("birthDate", birthDate.timeInMillis)
        player?.let {
            outState.putBundle("player", Bundle().apply {
                putString("name", it.fullName)
                putString("gender", it.gender.name)
                putInt("course", it.course)
                putInt("difficulty", it.difficulty)
                putInt("year", it.birthDate.year)
                putInt("month", it.birthDate.month)
                putInt("day", it.birthDate.day)
            })
        }
    }
}
