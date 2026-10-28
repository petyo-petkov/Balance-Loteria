package com.example.pruebas.di

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.room3.Room
import com.example.pruebas.data.BalanceRepoImpl
import com.example.pruebas.data.ScannerRepoImpl
import com.example.pruebas.data.db.AppDatabase
import com.example.pruebas.data.db.LotteryDatabaseRepoImpl
import com.example.pruebas.data.network.webView.WebViewRepoImpl
import com.example.pruebas.domain.BalanceRepo
import com.example.pruebas.domain.LotteryDatabaseRepo
import com.example.pruebas.domain.ScannerRepo
import com.example.pruebas.domain.WebViewRepo
import com.example.pruebas.presentation.ScannerViewModel
import com.example.pruebas.presentation.detailScreen.DetailViewModel
import com.example.pruebas.presentation.extraDetailScreen.ExtraDetailViewModel
import com.example.pruebas.presentation.homeScreen.HomeScreenViewModel
import com.example.pruebas.presentation.jackpotScreen.JackpotScreenViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.example.pruebas.domain.ProcessScannedTicketUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val scannerModule: Module = module {

    single<GmsBarcodeScanner> {
        val app: Application = get()
        val options: GmsBarcodeScannerOptions =
            GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_CODE_128
                )
                .build()
        GmsBarcodeScanning.getClient(app.applicationContext, options)
    }

    singleOf(::ScannerRepoImpl) bind ScannerRepo::class

}

val viewModelModule = module {
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::ScannerViewModel)
    viewModelOf(::JackpotScreenViewModel)
    viewModel { [ticketId: String] -> DetailViewModel(ticketId = ticketId, webViewRepo = get(), dbRepo = get()) }
    viewModel { [ticketId: String] -> ExtraDetailViewModel(ticketId = ticketId, webViewRepo = get(), dbRepo = get()) }
}

val useCaseModule = module {
    factoryOf(::ProcessScannedTicketUseCase)
}

val repositoryModule = module {
    singleOf(::LotteryDatabaseRepoImpl) bind LotteryDatabaseRepo::class
    singleOf(::BalanceRepoImpl) bind BalanceRepo::class
    singleOf(::WebViewRepoImpl) bind WebViewRepo::class
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            context = get(),
            klass = AppDatabase::class.java,
            name = "Lottery-DB"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<AppDatabase>().lotteryDao() }

}

val networkModule = module {
    single {
        WebView(get()).apply {
            settings.apply {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = false
                allowContentAccess = false
                javaScriptCanOpenWindowsAutomatically = false
                loadsImagesAutomatically = false
                blockNetworkImage = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            }
            visibility = View.INVISIBLE
            setLayerType(View.LAYER_TYPE_NONE, null)
        }
    }
}
