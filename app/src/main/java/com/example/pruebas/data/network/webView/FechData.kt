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
            if (!boton) {
                AndroidInterface.sendData('Error Boton');
                return;
            }
            boton.click();
            var premioEl = document.getElementById('qa_comprobador-cantidadPremio-$gameID-1');
            var observer = new MutationObserver(function(_, obs) {
                var text = premioEl ? premioEl.innerText.trim() : '';
                if (text.length > 0) {
                    obs.disconnect();
                    AndroidInterface.sendData(text);
                }
            });
            if (premioEl) {
                observer.observe(premioEl, { childList: true, subtree: true, characterData: true });
            } else {
                observer.observe(document.body, { childList: true, subtree: true });
            }
            setTimeout(function() {
                observer.disconnect();
                var premioEl = document.getElementById('qa_comprobador-cantidadPremio-$gameID-1');
                AndroidInterface.sendData(premioEl ? premioEl.innerText.trim() || '0.0' : '0.0');
            }, 3000);
        })();
    """
}

fun getRawString(): String {
    return """
        (function() {
            var pre = document.querySelector('pre');
            if (pre) {
                AndroidInterface.sendData(pre.innerText);
                return;
            }
            var observer = new MutationObserver(function(_, obs) {
                var pre = document.querySelector('pre');
                if (pre) {
                    obs.disconnect();
                    AndroidInterface.sendData(pre.innerText);
                }
            });
            observer.observe(document.body, { childList: true, subtree: true });
            setTimeout(function() {
                observer.disconnect();
                var pre = document.querySelector('pre');
                AndroidInterface.sendData(pre ? pre.innerText : 'Error: timeout');
            }, 3000);
        })();
    """
}
