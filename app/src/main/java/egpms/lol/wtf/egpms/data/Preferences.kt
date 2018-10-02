package egpms.lol.wtf.egpms.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Preferences(context: Context) {

    val PREFS_FILENAME = "egpms.lol.wtf.egpms.prefs"
    val PROFILES = "profiles"
    val LAST_PROFILE = "last_profile"
    var prefs: SharedPreferences? = null

    init {
        prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

    fun save(key: String, proto: EProtocol, ip: Int, port: Int, pass: String) {

        val profiles = ArrayList<Profile>()

        var pp: Profile? = get(key)

        if(pp != null) {
            pp.proto = proto
            pp.ip = ip
            pp.port = port
            pp.pass = pass
        }
        else {
            pp = Profile(key, proto, ip, port, pass)
        }

        profiles.add(pp)

        val oldProfs: ArrayList<Profile> = getAll()
        for(p in oldProfs) {
            if(p.name != pp.name) {
                profiles.add(p)
            }
        }

        /* replace json string any way */
        val editor = prefs!!.edit()
        editor.putString(PROFILES, Gson().toJson(profiles))
        editor.apply()
    }

    fun delete(key: String) {
        val profs = getAll().filter{ it.name != key }

        /* replace json string any way */
        val editor = prefs!!.edit()
        editor.putString(PROFILES, Gson().toJson(profs))
        editor.apply()
    }

    fun get(key: String) : Profile? {
        var pp: Profile? = null
        val arr = prefs!!.getString(PROFILES, "")
        if(!arr.isEmpty()) {
            val profiles: ArrayList<Profile> = Gson().fromJson(arr)


            for (prof in profiles) {
                if (prof.name == key) {
                    pp = prof
                    break
                }
            }
        }

        return pp
    }

    fun getAll() : ArrayList<Profile> {
        val arr = prefs!!.getString(PROFILES, "")
        if(arr.isEmpty()) {
            return ArrayList()
        }

        return Gson().fromJson(arr)
    }

    fun selectProfile(key: String) {
        val editor = prefs!!.edit()
        editor.putString(LAST_PROFILE, key)
        editor.apply()
    }

    fun getLast(): Profile? {
        val lastProfile = prefs!!.getString(LAST_PROFILE, "")

        return get(lastProfile)
    }
}