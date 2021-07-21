package com.xsolla.android.login

class LoginConfig private constructor(
    val projectId: String,
    val oauthClientId: Int,
    val socialConfig: XLogin.SocialConfig? = null,
    val useOauth: Boolean
) {
    class OauthBuilder {
        private var projectId: String? = null
        private var oauthClientId: Int? = null
        private var socialConfig: XLogin.SocialConfig? = null

        fun setProjectId(projectId: String): OauthBuilder {
            this.projectId = projectId
            return this
        }

        fun setOauthClientId(oauthClientId: Int): OauthBuilder {
            this.oauthClientId = oauthClientId
            return this
        }

        fun setSocialConfig(socialConfig: XLogin.SocialConfig): OauthBuilder {
            this.socialConfig = socialConfig
            return this
        }

        fun build(): LoginConfig {
            if (projectId == null) {
                throw IllegalStateException("Project ID is required for initialization Xsolla Login")
            }
            if (oauthClientId == null) {
                throw IllegalStateException("OAuth client ID is required for initialization Xsolla Login with OAuth")
            }

            return LoginConfig(projectId!!, oauthClientId!!, socialConfig, true)
        }
    }

    class JwtBuilder {
        private var projectId: String? = null
        private var socialConfig: XLogin.SocialConfig? = null

        fun setProjectId(projectId: String): JwtBuilder {
            this.projectId = projectId
            return this
        }

        fun setSocialConfig(socialConfig: XLogin.SocialConfig): JwtBuilder {
            this.socialConfig = socialConfig
            return this
        }

        fun build(): LoginConfig {
            if (projectId == null) {
                throw IllegalStateException("Project ID is required for initialization Xsolla Login")
            }

            return LoginConfig(projectId!!, 0, socialConfig, false)
        }
    }
}