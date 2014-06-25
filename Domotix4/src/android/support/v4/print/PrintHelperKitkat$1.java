// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package android.support.v4.print;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;

// Referenced classes of package android.support.v4.print:
//            PrintHelperKitkat

class val.fittingMode extends PrintDocumentAdapter
{

    private PrintAttributes mAttributes;
    final PrintHelperKitkat this$0;
    final Bitmap val$bitmap;
    final int val$fittingMode;
    final String val$jobName;

    public void onLayout(PrintAttributes printattributes, PrintAttributes printattributes1, CancellationSignal cancellationsignal, android.print..LayoutResultCallback layoutresultcallback, Bundle bundle)
    {
        boolean flag = true;
        mAttributes = printattributes1;
        android.print.PrintDocumentInfo printdocumentinfo = (new android.print.ilder(val$jobName)).setContentType(flag).setPageCount(flag).build();
        if (printattributes1.equals(printattributes))
        {
            flag = false;
        }
        layoutresultcallback.onLayoutFinished(printdocumentinfo, flag);
    }

    public void onWrite(PageRange apagerange[], ParcelFileDescriptor parcelfiledescriptor, CancellationSignal cancellationsignal, android.print..WriteResultCallback writeresultcallback)
    {
        PrintedPdfDocument printedpdfdocument = new PrintedPdfDocument(mContext, mAttributes);
        android.graphics.pdf.esultCallback esultcallback;
        RectF rectf;
        Matrix matrix;
        float f;
        esultcallback = printedpdfdocument.startPage(1);
        rectf = new RectF(esultcallback.Info().getContentRect());
        matrix = new Matrix();
        f = rectf.width() / (float)val$bitmap.getWidth();
        if (val$fittingMode != 2) goto _L2; else goto _L1
_L1:
        float f1 = Math.max(f, rectf.height() / (float)val$bitmap.getHeight());
_L6:
        matrix.postScale(f1, f1);
        matrix.postTranslate((rectf.width() - f1 * (float)val$bitmap.getWidth()) / 2.0F, (rectf.height() - f1 * (float)val$bitmap.getHeight()) / 2.0F);
        esultcallback.Canvas().drawBitmap(val$bitmap, matrix, null);
        printedpdfdocument.finishPage(esultcallback);
        printedpdfdocument.writeTo(new FileOutputStream(parcelfiledescriptor.getFileDescriptor()));
        PageRange apagerange1[] = new PageRange[1];
        apagerange1[0] = PageRange.ALL_PAGES;
        writeresultcallback.onWriteFinished(apagerange1);
_L4:
        if (printedpdfdocument != null)
        {
            printedpdfdocument.close();
        }
        if (parcelfiledescriptor == null)
        {
            break MISSING_BLOCK_LABEL_229;
        }
        parcelfiledescriptor.close();
        return;
_L2:
        f1 = Math.min(f, rectf.height() / (float)val$bitmap.getHeight());
        continue; /* Loop/switch isn't completed */
        IOException ioexception1;
        ioexception1;
        Log.e("PrintHelperKitkat", "Error writing printed content", ioexception1);
        writeresultcallback.onWriteFailed(null);
        if (true) goto _L4; else goto _L3
_L3:
        Exception exception;
        exception;
        if (printedpdfdocument != null)
        {
            printedpdfdocument.close();
        }
        IOException ioexception2;
        if (parcelfiledescriptor != null)
        {
            try
            {
                parcelfiledescriptor.close();
            }
            catch (IOException ioexception) { }
        }
        throw exception;
        ioexception2;
        return;
        if (true) goto _L6; else goto _L5
_L5:
    }

    tCallback()
    {
        this$0 = final_printhelperkitkat;
        val$jobName = s;
        val$bitmap = bitmap1;
        val$fittingMode = I.this;
        super();
    }
}
