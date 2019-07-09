package no.capraconsulting.config

import org.eclipse.jetty.security.*
import org.eclipse.jetty.security.authentication.BasicAuthenticator
import org.eclipse.jetty.util.security.Constraint
import org.eclipse.jetty.util.security.Credential

import javax.ws.rs.HttpMethod
import javax.ws.rs.OPTIONS

object BasicAuthSecurityHandler {

    private val defaultConstraintMapping: ConstraintMapping
        get() {
            val constraint = Constraint()
            constraint.name = Constraint.__BASIC_AUTH
            constraint.roles = arrayOf("user")
            constraint.authenticate = true

            val constraintMapping = ConstraintMapping()
            constraintMapping.constraint = constraint
            constraintMapping.methodOmissions = arrayOf(HttpMethod.OPTIONS)
            constraintMapping.pathSpec = "/*"
            return constraintMapping
        }

    private val healthConstraintMapping: ConstraintMapping
        get() {
            val constraint = Constraint()
            constraint.authenticate = false

            val constraintMapping = ConstraintMapping()
            constraintMapping.constraint = constraint
            constraintMapping.method = "GET"
            constraintMapping.pathSpec = "/health/*"
            return constraintMapping
        }

    fun getBasicAuthSecurityHandler(username: String, password: String, realm: String): SecurityHandler {

        val userStore = UserStore()
        userStore.addUser(username, Credential.getCredential(password), arrayOf("user"))

        val hashLoginService = HashLoginService()
        hashLoginService.setUserStore(userStore)
        hashLoginService.name = realm

        val constraintSecurityHandler = ConstraintSecurityHandler()
        constraintSecurityHandler.authenticator = BasicAuthenticator()
        constraintSecurityHandler.loginService = hashLoginService
        constraintSecurityHandler.realmName = realm

        constraintSecurityHandler.addConstraintMapping(healthConstraintMapping)
        constraintSecurityHandler.addConstraintMapping(defaultConstraintMapping)

        return constraintSecurityHandler

    }
}
