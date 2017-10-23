package com.bancsabadell.authsdk.data

import android.os.Parcel
import android.os.Parcelable

data class AuthData(val url: String, val debug: Boolean = false) : Parcelable {
    constructor(parcel: Parcel) : this(
            url = parcel.readString(),
            debug = parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeByte(if (debug) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AuthData> {
        override fun createFromParcel(parcel: Parcel): AuthData {
            return AuthData(parcel)
        }

        override fun newArray(size: Int): Array<AuthData?> {
            return arrayOfNulls(size)
        }
    }
}