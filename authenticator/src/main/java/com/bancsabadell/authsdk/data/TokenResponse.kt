package com.bancsabadell.authsdk.data

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by Kame on 23/10/2017.
 */
data class TokenResponse(@JsonProperty("access_token") val accessToken: String,
                         @JsonProperty("token_type") val tokenType: String,
                         @JsonProperty("refresh_token") val refreshToken: String,
                         @JsonProperty("expires_in") val expiresId: Int,
                         @JsonProperty("scope") val scope: String,
                         @JsonProperty("client_id") val clientId: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accessToken)
        parcel.writeString(tokenType)
        parcel.writeString(refreshToken)
        parcel.writeInt(expiresId)
        parcel.writeString(scope)
        parcel.writeString(clientId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TokenResponse> {
        override fun createFromParcel(parcel: Parcel): TokenResponse {
            return TokenResponse(parcel)
        }

        override fun newArray(size: Int): Array<TokenResponse?> {
            return arrayOfNulls(size)
        }
    }
}
