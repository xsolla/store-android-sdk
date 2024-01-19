package com.xsolla.android.login

class LoginConfig private constructor(
    internal val projectId: String,
    internal val oauthClientId: Int,
    internal val apiHost: String,
    internal val socialConfig: XLogin.SocialConfig? = null,
    internal val redirectScheme: String?,
    internal val redirectHost: String?
) {
    class OauthBuilder {
        private var projectId: String? = null
        private var oauthClientId: Int? = null
        private var apiHost: String? = null
        private var socialConfig: XLogin.SocialConfig? = null

        private var redirectScheme: String? = null
        private var redirectHost: String? = null

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

        fun setRedirectUriScheme(redirectScheme: String): OauthBuilder {
            this.redirectScheme = redirectScheme.lowercase()
            return this
        }

        fun setRedirectUriHost(redirectHost: String): OauthBuilder {
            this.redirectHost = redirectHost.lowercase()
            return this
        }

        fun setApiHost(apiHost: String): OauthBuilder {
            this.apiHost = apiHost.lowercase()
            return this
        }

        fun build(): LoginConfig {
            if (projectId == null) {
                throw IllegalStateException("Project ID is required for initialization Xsolla Login")
            }
            if (oauthClientId == null) {
                throw IllegalStateException("OAuth client ID is required for initialization Xsolla Login with OAuth")
            }

            if (apiHost == null) {
                apiHost = "login.xsolla.com"
            }

            return LoginConfig(
                projectId!!,
                oauthClientId!!,
                apiHost!!,
                socialConfig,
                redirectScheme,
                redirectHost
            )
        }
    }
}