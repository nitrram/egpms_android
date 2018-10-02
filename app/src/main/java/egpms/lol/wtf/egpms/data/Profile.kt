package egpms.lol.wtf.egpms.data

import android.os.Parcel
import android.os.Parcelable
import java.lang.StringBuilder

data class Profile(
        val name: String,
        var proto: EProtocol,
        var ip: Int,
        var port: Int,
        var pass:String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        EProtocol.valueOf(parcel.readString()),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(proto.str)
        parcel.writeInt(ip)
        parcel.writeInt(port)
        parcel.writeString(pass)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toProfileString() : String {
        val out = StringBuilder("\t")
        out.append(proto.str).append("\t").append(ip2str()).append("\t").
                append(port).append("\t").append(pass).append("\t")

        return out.toString()
    }

    fun ip2str() : String {

        var ipStr = ""

        if(ip == 0) {
            return ipStr
        }

        for(i in 0..3) {
            val num = (ip shr 8*i) and 0xff
            ipStr += num.toString()
            if(i != 3) ipStr += "."
        }

        return ipStr
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}
