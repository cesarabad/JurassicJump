package com.example.p5cav.usuarios

import android.graphics.Bitmap

class User (val username : String, var avatar : Bitmap) {

    var bestScore : Long = 0

    constructor(username: String, avatar: Bitmap, bestScore : Long) : this(username, avatar) {
        this.bestScore = bestScore
    }

    override fun equals(other: Any?): Boolean {
        return username == (other as User).username
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }
}