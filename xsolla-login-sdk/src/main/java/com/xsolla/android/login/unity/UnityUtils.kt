package com.xsolla.android.login.unity

object UnityUtils {

    private lateinit var unityPlayer: Class<*>

    fun sendMessage(socialNetwork: String, status: String, body: String?) {
        try {
            if (!::unityPlayer.isInitialized) {
                unityPlayer = Class.forName("com.unity3d.player.UnityPlayer")
            }
            val method = unityPlayer.getMethod("UnitySendMessage", String::class.java, String::class.java, String::class.java)
            method.invoke(unityPlayer, "SocialNetworks", "ReceiveSocialAuthResult", arrayOf(socialNetwork, status, body))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}