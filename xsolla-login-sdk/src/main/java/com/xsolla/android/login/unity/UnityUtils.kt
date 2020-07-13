package com.xsolla.android.login.unity

object UnityUtils {

    private lateinit var unityPlayer: Class<*>

    fun sendMessage(socialNetwork: String, status: String, body: String?) {
        try {
            if (!::unityPlayer.isInitialized) {
                unityPlayer = Class.forName("com.unity3d.player.UnityPlayer")
            }
            val method = unityPlayer.getMethod("UnitySendMessage", String::class.java, String::class.java, String::class.java)
            val unityArgument = with(StringBuilder()) {
                append(socialNetwork)
                append('#')
                append(status)
                if (body != null) {
                    append('#')
                    append(body)
                }
                toString()
            }
            method.invoke(unityPlayer, "SocialNetworks", "ReceiveSocialAuthResult", unityArgument)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}