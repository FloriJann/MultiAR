package inovex.ad.multiar

import inovex.ad.multiar.polyViewerModule.AssetRepository
import inovex.ad.multiar.polyViewerModule.AssetsFragmentViewModel
import inovex.ad.multiar.polyViewerModule.poly.PolyApi
import inovex.ad.multiar.polyViewerModule.poly.UseCasePolyApi
import inovex.ad.multiar.walletModule.WalletViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {

    single { PolyApi.create() }
    single { UseCasePolyApi(get()) }

    single { AssetRepository(get(), get()) }

    viewModel { AssetsFragmentViewModel(get(), androidApplication()) }
    viewModel { WalletViewModel(androidApplication()) }
}