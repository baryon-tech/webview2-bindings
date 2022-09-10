import de.saschat.cinterop.webview2.CreateCoreWebView2Environment
import de.saschat.cinterop.webview2.ICoreWebView2CreateCoreWebView2EnvironmentCompletedHandler
import de.saschat.cinterop.webview2.ICoreWebView2CreateCoreWebView2EnvironmentCompletedHandlerVtbl
import de.saschat.cinterop.webview2.ICoreWebView2Environment
import kotlinx.cinterop.*
import platform.posix.IID
import platform.windows.CoInitializeEx
import platform.windows.HRESULT
import platform.windows.ULONG

fun <TDelegate : CStructVar, TDelegateV : CStructVar, TReturn : CPointed> createHandler(
    callback: CPointer<CFunction<(CPointer<TDelegate>?, HRESULT, CPointer<TReturn>?) -> ULONG>>
): CPointer<TDelegate> {
    val scope = MemScope();
    val tDelVtblRep = scope.allocArray<ULongVarOf<ULong>>(4)

    val QueryInterface = staticCFunction { a: CPointer<TDelegate>?,
                                           b: CPointer<IID>?,
                                           c: CPointer<CPointerVarOf<CPointer<out CPointed>>>? ->
        0
    }
    val AddRef = staticCFunction { a: CPointer<TDelegate>?
        ->
        1u as ULONG
    }
    val Release = staticCFunction { a: CPointer<TDelegate>?
        ->
        1u as ULONG
    }

    tDelVtblRep[0] = QueryInterface.rawValue.toLong().toULong();
    tDelVtblRep[1] = AddRef.rawValue.toLong().toULong();
    tDelVtblRep[2] = Release.rawValue.toLong().toULong();
    tDelVtblRep[3] = callback.rawValue.toLong().toULong();

    val tDelRep = scope.alloc(tDelVtblRep.rawValue.toLong().toULong());

    return tDelRep.ptr.reinterpret();
}
