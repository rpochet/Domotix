// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package android.support.v4.print;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrintHelperKitkat
{

    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    private static final String LOG_TAG = "PrintHelperKitkat";
    private static final int MAX_PRINT_SIZE = 3500;
    public static final int SCALE_MODE_FILL = 2;
    public static final int SCALE_MODE_FIT = 1;
    int mColorMode;
    final Context mContext;
    int mScaleMode;

    PrintHelperKitkat(Context context)
    {
        mScaleMode = 2;
        mColorMode = 2;
        mContext = context;
    }

    private Bitmap loadBitmap(Uri uri, android.graphics.BitmapFactory.Options options)
        throws FileNotFoundException
    {
        InputStream inputstream;
        if (uri == null || mContext == null)
        {
            throw new IllegalArgumentException("bad argument to loadBitmap");
        }
        inputstream = null;
        Bitmap bitmap;
        inputstream = mContext.getContentResolver().openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(inputstream, null, options);
        if (inputstream != null)
        {
            try
            {
                inputstream.close();
            }
            catch (IOException ioexception1)
            {
                Log.w("PrintHelperKitkat", "close fail ", ioexception1);
                return bitmap;
            }
        }
        return bitmap;
        Exception exception;
        exception;
        if (inputstream != null)
        {
            try
            {
                inputstream.close();
            }
            catch (IOException ioexception)
            {
                Log.w("PrintHelperKitkat", "close fail ", ioexception);
            }
        }
        throw exception;
    }

    private Bitmap loadConstrainedBitmap(Uri uri, int i)
        throws FileNotFoundException
    {
        if (i <= 0 || uri == null || mContext == null)
        {
            throw new IllegalArgumentException("bad argument to getScaledBitmap");
        }
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        loadBitmap(uri, options);
        int j = options.outWidth;
        int k = options.outHeight;
        if (j > 0 && k > 0)
        {
            int l = Math.max(j, k);
            int i1;
            for (i1 = 1; l > i; i1 <<= 1)
            {
                l >>>= 1;
            }

            if (i1 > 0 && Math.min(j, k) / i1 > 0)
            {
                android.graphics.BitmapFactory.Options options1 = new android.graphics.BitmapFactory.Options();
                options1.inMutable = true;
                options1.inSampleSize = i1;
                return loadBitmap(uri, options1);
            }
        }
        return null;
    }

    public int getColorMode()
    {
        return mColorMode;
    }

    public int getScaleMode()
    {
        return mScaleMode;
    }

    public void printBitmap(final String jobName, final Bitmap bitmap)
    {
        if (bitmap == null)
        {
            return;
        }
        final int fittingMode = mScaleMode;
        PrintManager printmanager = (PrintManager)mContext.getSystemService("print");
        android.print.PrintAttributes.MediaSize mediasize = android.print.PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
        if (bitmap.getWidth() > bitmap.getHeight())
        {
            mediasize = android.print.PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
        }
        PrintAttributes printattributes = (new android.print.PrintAttributes.Builder()).setMediaSize(mediasize).setColorMode(mColorMode).build();
        printmanager.print(jobName, new PrintDocumentAdapter() {

            private PrintAttributes mAttributes;
            final PrintHelperKitkat this$0;
            final Bitmap val$bitmap;
            final int val$fittingMode;
            final String val$jobName;

            public void onLayout(PrintAttributes printattributes1, PrintAttributes printattributes2, CancellationSignal cancellationsignal, android.print.PrintDocumentAdapter.LayoutResultCallback layoutresultcallback, Bundle bundle)
            {
                boolean flag = true;
                mAttributes = printattributes2;
                android.print.PrintDocumentInfo printdocumentinfo = (new android.print.PrintDocumentInfo.Builder(jobName)).setContentType(flag).setPageCount(flag).build();
                if (printattributes2.equals(printattributes1))
                {
                    flag = false;
                }
                layoutresultcallback.onLayoutFinished(printdocumentinfo, flag);
            }

            public void onWrite(PageRange apagerange[], ParcelFileDescriptor parcelfiledescriptor, CancellationSignal cancellationsignal, android.print.PrintDocumentAdapter.WriteResultCallback writeresultcallback)
            {
                PrintedPdfDocument printedpdfdocument = new PrintedPdfDocument(mContext, mAttributes);
                android.graphics.pdf.PdfDocument.Page page;
                RectF rectf;
                Matrix matrix;
                float f;
                page = printedpdfdocument.startPage(1);
                rectf = new RectF(page.getInfo().getContentRect());
                matrix = new Matrix();
                f = rectf.width() / (float)bitmap.getWidth();
                if (fittingMode != 2) goto _L2; else goto _L1
_L1:
                float f1 = Math.max(f, rectf.height() / (float)bitmap.getHeight());
_L6:
                matrix.postScale(f1, f1);
                matrix.postTranslate((rectf.width() - f1 * (float)bitmap.getWidth()) / 2.0F, (rectf.height() - f1 * (float)bitmap.getHeight()) / 2.0F);
                page.getCanvas().drawBitmap(bitmap, matrix, null);
                printedpdfdocument.finishPage(page);
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
                f1 = Math.min(f, rectf.height() / (float)bitmap.getHeight());
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

            
            {
                this$0 = PrintHelperKitkat.this;
                jobName = s;
                bitmap = bitmap1;
                fittingMode = i;
                super();
            }
        }, printattributes);
    }

    public void printBitmap(String s, Uri uri)
        throws FileNotFoundException
    {
        printBitmap(s, loadConstrainedBitmap(uri, 3500));
    }

    public void setColorMode(int i)
    {
        mColorMode = i;
    }

    public void setScaleMode(int i)
    {
        mScaleMode = i;
    }
}
