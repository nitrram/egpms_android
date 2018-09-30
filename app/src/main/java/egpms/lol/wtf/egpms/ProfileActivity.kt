package egpms.lol.wtf.egpms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import egpms.lol.wtf.egpms.data.EAction
import egpms.lol.wtf.egpms.data.EProtocol
import egpms.lol.wtf.egpms.data.Preferences
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    lateinit var prefs: Preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        edit_proto!!.onItemSelectedListener = this

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, EProtocol.values())
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        edit_proto!!.adapter = aa

        prefs = Preferences(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if(intent == null) return

        if(intent!!.hasExtra(INTENT_PROFILE)) {
            val pp = prefs.get(intent!!.getStringExtra(INTENT_PROFILE))
            edit_name.setText(pp?.name)
            edit_address.setText(ip2str(pp?.ip))
            //edit_proto.set

        }
    }

    /*
    fun save() {

        val arr = prefs!!.getString(PROFILES, "")
        val profiles: ArrayList<Profile> = Gson().fromJson(arr)


        edit_name.text
        var pp: Profile? = null
        for(prof in profiles) {
            if(prof.name == edit_name.text.toString())
            {
                pp = prof
                break
            }
        }


        //val pr


        if(pp != null) {
            pp.proto = EProtocol.valueOf(edit_proto.getSelectedItem().toString())
            pp.ip = str2ip(edit_address.text.toString())
            pp.port = str2port(edit_port.text.toString())
            pp.pass = edit_pass.text.toString()
        }
        else {
            /*
            profiles!!.add(
                    Profile(
                            edit_name.text.toString(),
                            EProtocol.valueOf(edit_proto.getSelectedItem().toString()),
                            str2ip(edit_address.text.toString()),
                            5000, "dfdf"))
                            */
        }



        val ip: Int = 0

        //profiles!!.add(Profile("ll_ll", EProtocol.PMS21, ip, 5000, "athlon"))

      //  val editor = prefs!!.edit()
        //editor.putString(PROFILES, gson.toJson(arr))
       // editor.apply()


//        SharedPreferences appSharedPrefs = PreferenceManager  .getDefaultSharedPreferences(context.getApplicationContext());
//     SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
//     Gson gson = new Gson();
//     String json = gson.toJson(tasks); //tasks is an ArrayList instance variable
//     prefsEditor.putString("currentTasks", json);
//     prefsEditor.commit();
    }
*/


    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    fun ip2str(ip: Int?) : String {

        var ipStr = ""

        if(ip == null || ip == 0) {
            return ipStr
        }

        for(i in 0..3) {
            val num = (ip!! shr 8*i) and 0xff
            ipStr += num.toString()
            if(i != 3) ipStr += "."
        }

        return ipStr
    }

    fun str2ip(str: String) : Int {

        var ip = 0
        val dom = str.split(".")

        if(dom.size != 4) {
            return -1
        }

        for(i in dom.indices) {
            val num: Int? = dom[i].toIntOrNull()
            if(num != null && ((i > 0 && num >= 0) || (num > 0)) && num<=255) {
                ip = ip or (num shl 8*i)
            } else {
                return -2
            }
        }

        return ip
    }

    fun str2port(str: String) : Int {
        val port = str.toIntOrNull()
        if(port == null || (port !in 0..65536)) {
            return -1
        }

        return port
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