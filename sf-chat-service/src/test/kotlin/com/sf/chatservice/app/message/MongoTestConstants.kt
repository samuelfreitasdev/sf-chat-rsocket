package com.sf.chatservice.app.message

object MongoTestConstants {

    const val BITNAMI_MONGODB_IMAGE: String = "mongo"
//    const val BITNAMI_MONGODB_IMAGE: String = "bitnami/mongodb:8"
    const val USERNAME: String = "user"
    const val PASSWORD: String = "password"
    const val DATABASE: String = "db"
    const val PORT: Int = 27017

//    val WAIT_STRATEGY: AbstractWaitStrategy = LogMessageWaitStrategy.builder()
//        .command("mongo", "--quiet", "--port", "27017", "-u", "root", "-p", "password", "--eval", "rs.status().ok")
//        .expectedOutput("1\n")
//        .build()

}