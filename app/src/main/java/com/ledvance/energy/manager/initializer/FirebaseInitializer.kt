//package com.ledvance.energy.manager.initializer
//
//import android.content.Context
//import androidx.startup.Initializer
//import com.google.firebase.FirebaseApp
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import com.ledvance.utils.extensions.isDebuggable
//
///**
// * @author : jason yin
// * Email : j.yin@ledvance.com
// * Created date 2025/6/19 11:16
// * Describe : FirebaseInitializer
// */
//class FirebaseInitializer : Initializer<Boolean> {
//    override fun create(context: Context): Boolean {
//        FirebaseApp.initializeApp(context)
//        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !context.isDebuggable()
//        return true
//    }
//
//    override fun dependencies(): List<Class<out Initializer<*>>> {
//        return emptyList()
//    }
//}