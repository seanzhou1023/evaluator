package org.scalaexercises.evaluator

import java.util.concurrent._
import java.security._
import scala.concurrent._
import java.util.concurrent._

package object sandbox {
  /**
    * A self-protecting security manager for the external code execution sandbox. It's
    * self-protecting in the sense that doesn't allow executed code to change the sandbox
    * settings or replace the JVM's security manager.
    *
    * References:
    *  - Evaluating the flexibility of the Java Sandbox https://www.cs.cmu.edu/~clegoues/docs/coker15acsac.pdf
    */
  class SandboxedSecurityManager extends SecurityManager {
    override def checkExec(cmd: String): Unit = {
      throw new SecurityException("Can not execute execute arbitrary commands in sandboxed code")
    }

    override def checkPermission(perm: Permission): Unit = {
      val result = sandboxedCheck(perm)

      result match {
        case Right(result) =>
        case Left(msg) => {
          throw new SecurityException(msg)
        }
      }
    }

    // runtime

    val exitVM = "exitVM.*".r
    val securityManager = ".+SecurityManager".r
    val classLoader = "createClassLoader"
    val accessDangerousPackage = "accessClassInPackage.sun.*".r

    def checkRuntimePermission(perm: RuntimePermission): Either[String, String] = {
      perm.getName match {
        case exitVM() => Left("Can not exit the VM in sandboxed code")
        case securityManager() => Left("Can not replace the security manager in sandboxed code")
        case `classLoader` => Left("Can not create a class loader in sandboxed code")
        case accessDangerousPackage() => Left("Can not access dangerous packages in sandboxed code")
        case other => Right(other)
      }
    }

    // reflection

    val suppressAccessChecks = "suppressAccessChecks"

    def checkReflectionPermission(perm: java.lang.reflect.ReflectPermission): Either[String, String] = {
      perm.getName match {
        case `suppressAccessChecks` => Left("Can not suppress access checks in sandboxed code")
        case other => Right(other)
      }
    }

    // security

    val setPolicy = "setPolicy"

    def checkSecurityPermission(perm: java.security.SecurityPermission): Either[String, String] = {
      perm.getName match {
        case `setPolicy` => Left("Can not change security policy in sandboxed code")
        case other => Right(other)
      }
    }

    def sandboxedCheck(perm: Permission): Either[String, String] = {
      perm match {
        case awt: java.awt.AWTPermission => Left("Can not access the AWT APIs in sanboxed code")
        case rt: RuntimePermission => checkRuntimePermission(rt)
        case sec: java.security.SecurityPermission => checkSecurityPermission(sec)
        case ref: java.lang.reflect.ReflectPermission => checkReflectionPermission(ref)
        case other =>      Right(other.getName)
      }
    }
  }
}
