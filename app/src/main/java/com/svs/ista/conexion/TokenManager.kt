import android.content.Context
import android.preference.PreferenceManager

object TokenManager {
    private const val TOKEN_KEY = "token"

    fun saveToken(context: Context, token: String?) {
        val sharedPreferences = context.getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun clearToken(context: Context) {
        val sharedPreferences = context.getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }
}
