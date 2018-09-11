package egpms.lol.wtf.egpms

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.CompoundButton
import kotlinx.android.synthetic.main.activity_control.*

class ControlActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        // Handle navigation view item clicks here.
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

        val i = getStatus()

        updateSwitches(i)


        btn_sock_one.setOnCheckedChangeListener {v,b -> handleClick(v,b) }
        btn_sock_two.setOnCheckedChangeListener{v,b -> handleClick(v,b) }
        btn_sock_three.setOnCheckedChangeListener{v,b -> handleClick(v,b) }
        btn_sock_four.setOnCheckedChangeListener{v,b -> handleClick(v,b) }
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

        val res = setState(i)
        updateSwitches(res)
    }


    external fun getStatus(): Int

    external fun setState(abcd: Int): Int

    companion object {

        init {
            System.loadLibrary("native-lib")
        }
    }
}
