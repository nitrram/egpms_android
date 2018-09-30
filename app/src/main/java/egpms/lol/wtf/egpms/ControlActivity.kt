package egpms.lol.wtf.egpms

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.CompoundButton
import egpms.lol.wtf.egpms.data.EAction
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

class ControlActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val sockThread = newSingleThreadContext("sockThread")

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_add -> {
                // Handle the camera action

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        //initConfig("\tpms21\t176.107.123.100\t5000\tathlon")
        initConfig("\tpms21\t192.168.1.243\t5000\tathlon\t")
        disableBtns()

        launch(sockThread) {
            val work = async { getStatus() }
            val result = work.await()
            launch(UI) { onStatusRecv(result) }
        }

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
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun updateSwitches(i: Int) {
        // 1
        btn_sock_one.isChecked = ((i and 0xff) == 0xff)

        // 2
        btn_sock_two.isChecked = (((i shr 8) and 0xff) == 0xff)

        // 3
        btn_sock_three.isChecked = (((i shr 16) and 0xff) == 0xff)

        // 4
        btn_sock_four.isChecked = (((i shr 24) and 0xff) == 0xff)
    }


    fun handleClick(v : CompoundButton, b: Boolean) {
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

    
    fun disableBtns() {
       control_panel.isEnabled = false
    }

    fun onStatusRecv(value: Int) {
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
