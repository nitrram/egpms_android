package egpms.lol.wtf.egpms.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Preferences(context: Context) {

    val PREFS_FILENAME = "egpms.lol.wtf.egpms.prefs"
    val PROFILES = "profiles"
    var prefs: SharedPreferences? = null

    init {
        prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    fun save(key: String, proto: EProtocol, ip: Int, port: Int, pass: String) {

        val arr = prefs!!.getString(PROFILES, "")
        val profiles: ArrayList<Profile> = Gson().fromJson(arr)

        var pp: Profile? = get(key)

        if(pp != null) {
            pp.proto = proto
            pp.ip = ip
            pp.port = port
            pp.pass = pass
        }
        else {
            profiles.add(
                    Profile(key, proto, ip, port, pass)
            )
        }

        val editor = prefs!!.edit()
        editor.putString(PROFILES, Gson().toJson(arr))
        editor.apply()
    }

    fun get(key: String) : Profile? {
        val arr = prefs!!.getString(PROFILES, "")
        val profiles: ArrayList<Profile> = Gson().fromJson(arr)

        var pp: Profile? = null
        for(prof in profiles) {
            if(prof.name == key)
            {
                pp = prof
                break
            }
        }

        return pp
    }

}