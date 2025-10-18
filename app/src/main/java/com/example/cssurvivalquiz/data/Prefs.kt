package com.example.cssurvivalquiz.data

import android.content.Context
import android.util.Patterns
import java.text.SimpleDateFormat
import java.util.*

object Prefs {
    private const val FILE = "quiz_prefs"
    private fun sp(ctx: Context) = ctx.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun saveUser(ctx: Context, first: String, last: String, dob: String, email: String, pass: String): Boolean {
        // basic validation (also done in UI)
        if (first.trim().length !in 3..30) return false
        if (last.trim().isEmpty()) return false
        if (dob.trim().isEmpty()) return false
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) return false
        if (pass.length < 4) return false

        sp(ctx).edit()
            .putString("user_first", first.trim())
            .putString("user_last", last.trim())
            .putString("user_dob", dob.trim())
            .putString("user_email", email.trim())
            .putString("user_pass", pass)
            .apply()
        return true
    }

    fun checkLogin(ctx: Context, email: String, pass: String): Boolean {
        val s = sp(ctx)
        return s.getString("user_email", "") == email.trim() &&
                s.getString("user_pass", "") == pass
    }

    // history helpers (for later screens)
    fun appendHistory(ctx: Context, score: Int, total: Int, streakMax: Int) {
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val s = sp(ctx)
        val count = s.getInt("history_count", 0)
        val line = "$now | $score/$total | streak:$streakMax"
        s.edit()
            .putString("history_item_$count", line)
            .putInt("history_count", count + 1)
            .apply()
    }

    fun readHistory(ctx: Context): List<String> {
        val s = sp(ctx)
        val count = s.getInt("history_count", 0)
        return (0 until count).mapNotNull { s.getString("history_item_$it", null) }.reversed()
    }
}