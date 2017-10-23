package com.bancsabadell.authsdk.data

import android.os.Parcel
import android.os.Parcelable

data class AuthResult(val error: String? = null, val tokenResponse: TokenResponse? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readParcelable(TokenResponse::class.java.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(error)
        parcel.writeParcelable(tokenResponse, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AuthResult> {
        override fun createFromParcel(parcel: Parcel): AuthResult {
            return AuthResult(parcel)
        }

        override fun newArray(size: Int): Array<AuthResult?> {
            return arrayOfNulls(size)
        }
    }

}