package inovex.ad.multiar

import android.app.Application
import org.koin.android.ext.android.startKoin

class MultiArApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(appModule))
    }
}