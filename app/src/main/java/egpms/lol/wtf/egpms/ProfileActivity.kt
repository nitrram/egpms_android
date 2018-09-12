package egpms.lol.wtf.egpms

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import egpms.lol.wtf.egpms.data.EAction

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
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
