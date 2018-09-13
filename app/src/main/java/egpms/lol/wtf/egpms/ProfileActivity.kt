package egpms.lol.wtf.egpms

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import egpms.lol.wtf.egpms.data.EAction

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import egpms.lol.wtf.egpms.data.EProtocol
import egpms.lol.wtf.egpms.data.Profile

class ProfileActivity : AppCompatActivity() {

    val PREFS_FILENAME = "egpms.lol.wtf.egpms.prefs"
    val PROFILES = "profiles"
    var prefs: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        prefs = this.getSharedPreferences(PREFS_FILENAME, 0)
    }

    fun save() {
        val gson = Gson()

        val arr = prefs!!.getString(PROFILES, "")
        val profiles = gson.fromJson(arr, ArrayList::class.java)


        val ip: Int = 0

        profiles!!.append(Profile("ll_ll", EProtocol.PMS21, ip, 5000, "athlon"))

        val editor = prefs!!.edit()
        //editor.putString(PROFILES, gson.toJson(arr))
        editor.apply()


//        SharedPreferences appSharedPrefs = PreferenceManager  .getDefaultSharedPreferences(context.getApplicationContext());
//     SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
//     Gson gson = new Gson();
//     String json = gson.toJson(tasks); //tasks is an ArrayList instance variable
//     prefsEditor.putString("currentTasks", json);
//     prefsEditor.commit();
    }

    companion object {
        private val INTENT_ACTION = "action_id"
        private val INTENT_PROFILE = "profile_id"

        fun newIntent(context: Context, action: EAction, profile: String = ""): Intent {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra(INTENT_ACTION, action.value)
            if(!profile.isEmpty())
                intent.putExtra(INTENT_PROFILE, profile)
            return intent
        }
    }
}
