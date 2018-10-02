package egpms.lol.wtf.egpms

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import egpms.lol.wtf.egpms.data.EAction
import egpms.lol.wtf.egpms.data.Preferences
import egpms.lol.wtf.egpms.data.Profile
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class ControlActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val sockThread = newSingleThreadContext("sockThread")

    private lateinit var prefs: Preferences

    private var lastProfile: Profile? = null

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        prefs.selectProfile(item.title.toString())

        updateProfiles()

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        prefs = Preferences(this)

        btn_sock_one.setOnCheckedChangeListener {v,b -> handleClick(v,b) }
        btn_sock_two.setOnCheckedChangeListener{v,b -> handleClick(v,b) }
        btn_sock_three.setOnCheckedChangeListener{v,b -> handleClick(v,b) }
        btn_sock_four.setOnCheckedChangeListener{v,b -> handleClick(v,b) }


         val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        fab.setOnClickListener { startActivity(ProfileActivity.newIntent(this, EAction.PROFILE_ADD)) }

        fab.setOnLongClickListener { deleteProfile() }
    }

    override fun onResume() {
        super.onResume()

        updateProfiles()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun updateSwitches(i: Int) {
        // 1
        btn_sock_one.isChecked = ((i and 0xff) == 0xff)

        // 2
        btn_sock_two.isChecked = (((i shr 8) and 0xff) == 0xff)

        // 3
        btn_sock_three.isChecked = (((i shr 16) and 0xff) == 0xff)

        // 4
        btn_sock_four.isChecked = (((i shr 24) and 0xff) == 0xff)
    }

    private fun deleteProfile() : Boolean {
        if(lastProfile != null) {
            prefs.delete(lastProfile!!.name)

            updateProfiles()
        }

        return true
    }

    private fun updateProfiles() {

        nav_view.menu.clear()

        var i = 0
        var lp: Profile? = null
        for(a in prefs.getAll()) {
            lp = a
            nav_view.menu.add(R.id.menu_group, i++, 0, a.name)
        }

        if(i == 1) {
            prefs.selectProfile(lp!!.name)
        }

        disableBtns()

        lastProfile = prefs.getLast()
        if(lastProfile != null) {

            for(a in 0..(i-1)) {
                if(nav_view.menu.getItem(a).title == lastProfile!!.name) {
                    nav_view.menu.getItem(a).isChecked = true
                }
            }

            //initConfig("\tpms21\t176.107.123.100\t5000\tathlon")
           // initConfig("\tpms21\t192.168.1.243\t5000\tathlon\t")
            initConfig(lastProfile!!.toProfileString())


            launch(sockThread) {
                val work = async { getStatus() }
                val result = work.await()
                launch(UI) { onStatusRecv(result) }
            }
        }
    }


    private fun handleClick(v : CompoundButton, b: Boolean) {
        var i :Int

        if(b)
            i = 0x01
        else
            i = 0x02

        when(v.id) {
            R.id.btn_sock_one -> i = i
            R.id.btn_sock_two ->  i = i shl 8
            R.id.btn_sock_three -> i = i shl 16
            R.id.btn_sock_four -> i= i shl 24
        }

        disableBtns()
        launch(sockThread) {
            val work = async { setState(i) }
            val result = work.await()
            launch(UI) { onStatusRecv(result) }
        }
    }

    
    private fun disableBtns() {
       control_panel.isEnabled = false
    }

    private fun onStatusRecv(value: Int) {
        control_panel.isEnabled = true

        updateSwitches(value)
    }


    external fun initConfig(s: String)

    external fun getStatus(): Int

    external fun setState(abcd: Int) : Int

    companion object {

        init {
            System.loadLibrary("native-lib")
        }
    }
}
