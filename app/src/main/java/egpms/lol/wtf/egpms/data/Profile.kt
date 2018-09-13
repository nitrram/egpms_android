package egpms.lol.wtf.egpms.data

import android.os.Parcel
import android.os.Parcelable

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

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}
