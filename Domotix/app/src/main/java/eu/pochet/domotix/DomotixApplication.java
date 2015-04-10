package eu.pochet.domotix;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
    httpMethod = HttpSender.Method.PUT,
    reportType = HttpSender.Type.JSON,
    formUri = "http://192.168.1.4:5984/acra-myapp/_design/acra-storage/_update/report",
    formUriBasicAuthLogin = "rpochet",
    formUriBasicAuthPassword = "rpochet",
    customReportContent = {
        ReportField.APP_VERSION_CODE,
        ReportField.APP_VERSION_NAME,
        ReportField.ANDROID_VERSION,
        ReportField.PACKAGE_NAME,
        ReportField.REPORT_ID,
        ReportField.BUILD,
        ReportField.STACK_TRACE
    },
    mode = ReportingInteractionMode.TOAST,
    resToastText = R.string.app_name
)
public class DomotixApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
