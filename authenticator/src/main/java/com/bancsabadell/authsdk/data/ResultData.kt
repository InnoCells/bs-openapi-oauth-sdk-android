package com.bancsabadell.authsdk.data

import android.os.Parcel
import android.os.Parcelable

data class ResultData(val error: String? = null, val authData: AuthData? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(AuthData::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(error)
        parcel.writeParcelable(authData, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultData> {
        override fun createFromParcel(parcel: Parcel): ResultData {
            return ResultData(parcel)
        }

        override fun newArray(size: Int): Array<ResultData?> {
            return arrayOfNulls(size)
        }
    }

}