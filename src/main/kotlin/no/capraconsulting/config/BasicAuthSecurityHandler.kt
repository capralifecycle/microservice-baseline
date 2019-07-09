package no.capraconsulting.config

import org.eclipse.jetty.security.ConstraintMapping
import org.eclipse.jetty.security.ConstraintSecurityHandler
import org.eclipse.jetty.security.HashLoginService
import org.eclipse.jetty.security.SecurityHandler
import org.eclipse.jetty.security.UserStore
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.util.security.Constraint
import org.eclipse.jetty.util.security.Credential
import javax.ws.rs.HttpMethod

object BasicAuthSecurityHandler {

    private fun getDefaultConstraintMapping(): ConstraintMapping =
        ConstraintMapping().apply {
            constraint = Constraint().apply {
                name = Constraint.__BASIC_AUTH
                roles = arrayOf("user")
                authenticate = true
            }
            methodOmissions = arrayOf(HttpMethod.OPTIONS)
            pathSpec = "/*"
        }

    private fun getHealthConstraintMapping(): ConstraintMapping =
        ConstraintMapping().apply {
            constraint = Constraint().apply {
                authenticate = false
            }
            method = "GET"
            pathSpec = "/health/*"
        }

    fun getBasicAuthSecurityHandler(username: String, password: String, realm: String): SecurityHandler {

        val userStore = UserStore().apply {
            addUser(username, Credential.getCredential(password), arrayOf("user"))
        }

        return ConstraintSecurityHandler().apply {
            authenticator = BasicAuthenticator()
            loginService = HashLoginService().apply {
                setUserStore(userStore)
                name = realm
            }
            realmName = realm

            addConstraintMapping(getHealthConstraintMapping())
            addConstraintMapping(getDefaultConstraintMapping())
        }
    }
}
