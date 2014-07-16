package com.anisakai.test.pageobjects

import org.openqa.selenium.{WebElement, By}
import scala.collection.mutable.ListBuffer
import java.util.List
import java.util.concurrent.TimeUnit
import com.anisakai.test.Config


/**
 * Created with IntelliJ IDEA.
 * User: gareth
 * Date: 9/9/13
 * Time: 1:35 PM
 * To change this template use File | Settings | File Templates.
 */
object VerifyTools extends VerifyTools

class VerifyTools extends Page {
  def checkTools(): Boolean = {
    var fails = ListBuffer[String]()
    var groupName: String = ""
    if (Config.defaultPortal == "neo") {
      groupName = "toolMenuLink"
    } else {
      groupName = "icon-sakai-"
    }
    try {
      for (i <- 0 until webDriver.findElements(By.xpath("//*[contains(@class, '"+groupName+"')]")).size) {
        switch to defaultContent
        var toolName = webDriver.findElements(By.xpath("//*[contains(@class, '"+groupName+"')]")).get(i).getText()
        click on webDriver.findElements(By.xpath("//*[contains(@class, '"+groupName+"')]")).get(i)
        webDriver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS)

        // If there is an iframe present we want to check inside of that iframe
        var iFrame = xpath("//iframe[contains(@title,'"+toolName+"')]").findElement(webDriver).isDefined
        if (!iFrame) {
          if (xpath("//h3[contains(text(), 'Error')]").findElement(webDriver).isDefined)
            fails += toolName
        } else {
          switch to frame(webDriver.findElement(By.xpath("//iframe[contains(@title,'"+toolName+"')]")))
          if (xpath("//h3[contains(text(), 'Error')]").findElement(webDriver).isDefined)
            fails += toolName
        }

        webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS)
      }
    } catch {
      case e: Exception => {
        e.printStackTrace()
        return hasFailed(fails, true)
      }
    }

    return hasFailed(fails)
  }

  def hasFailed(fails : ListBuffer[String]): Boolean = {
    return hasFailed(fails, false)
  }

  def hasFailed(fails : ListBuffer[String], failure: Boolean): Boolean = {
    if (fails.isEmpty || failure) {
      true
    } else {
      println("Failed Tools:")
      fails.foreach(e => println(e))
      if (fails.isEmpty) { println("None\n") }
      false
    }
  }

}
