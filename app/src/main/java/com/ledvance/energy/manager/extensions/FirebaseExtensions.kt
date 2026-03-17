//package com.ledvance.energy.manager.extensions
//
//import com.google.firebase.crashlytics.FirebaseCrashlytics
//import timber.log.Timber
//
///**
// * @author : jason yin
// * Email : j.yin@ledvance.com
// * Created date 2025/6/20 10:02
// * Describe : FirebaseExtensions
// */
//fun reportNonFatalExceptionOnFirebase(throwable: Throwable) {
//    Timber.tag("Exception").e(throwable, "reportNonFatalExceptionOnFirebase: ")
//    FirebaseCrashlytics.getInstance().recordException(throwable)
//}