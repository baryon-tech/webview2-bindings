package io.github.baryontech.natives.webview2

import kotlinx.cinterop.*
import platform.posix.IID
import platform.windows.HRESULT
import platform.windows.ULONG

// CFunction<(CPointer<TDelegate>?, HRESULT, CPointer<TReturn>?) -> ULONG>

fun <TDelegate : CStructVar, TDelegateV : CStructVar, TReturn : CPointed> delegate(
    callback: CPointer<CFunction<*>>
): CPointer<TDelegate> {
    val tDelVtblRep = nativeHeap.allocArray<ULongVarOf<ULong>>(4)

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

    val tDelRep = nativeHeap.alloc(tDelVtblRep.rawValue.toLong().toULong());

    return tDelRep.ptr.reinterpret();
}

