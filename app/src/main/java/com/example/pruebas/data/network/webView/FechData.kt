package com.example.pruebas.data.network.webView

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds


private val webViewMutex = Mutex()

@SuppressLint("SetJavaScriptEnabled")
suspend fun fetchData(
    webView: WebView,
    url: String,
    fetchFun: () -> String,
): String = webViewMutex.withLock {
    withContext(Dispatchers.Main) {
        try {
            withTimeout(20000.milliseconds) { // 20 segundos de timeout
                suspendCancellableCoroutine { continuation ->
                    // Limpiar el estado anterior del WebView
                    webView.webViewClient = WebViewClient()
                    webView.removeJavascriptInterface("AndroidInterface")

                    webView.apply {
                        addJavascriptInterface(
                            JavaScriptInterface.JavaScriptInterface { data ->
                                if (continuation.isActive) {
                                    continuation.resume(data)
                                }
                            }, "AndroidInterface",
                        )

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                evaluateJavascript(fetchFun()) { }
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                Log.e("fetchData", "Error cargando página: $description")
                                if (continuation.isActive) {
                                    continuation.resume("Error: $description")
                                }
                            }
                        }
                        Log.d("fetchData", "Cargando URL: $url")
                        loadUrl(url)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("fetchData", "Error o Timeout cargando URL: $url", e)
            "Error: ${e.message}"
        }
    }
}


fun getPremio(gameID: String): String {
    return """
              (function() {
                    var boton = document.getElementById('qa_comprobador-formulario-botonComprobar-$gameID');
                    if (boton) {
                        boton.click();
                        setTimeout(function() {
                            var premioElement = document.getElementById('qa_comprobador-cantidadPremio-$gameID-1');
                            var premioText = premioElement ? premioElement.innerText : "0.0";
                            AndroidInterface.sendData(premioText);
                        }, 1000);
                    } else {
                        AndroidInterface.sendData("Error Boton");
                    }
              }
              )();
              """
}

fun getRawString(): String {
    return """
              (function() {
              setTimeout(function() {
                 
                 var preContent = document.querySelector('pre')?.innerText
                 AndroidInterface.sendData(preContent);
          
             }, 500); 
             })(); 
           """
}
