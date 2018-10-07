package com.chrisrm.idea.legacy

@FunctionalInterface
interface Runner {
  @Throws(Exception::class)
  fun run()
}