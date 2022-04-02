package pneus.vachileski_mobi_funcoes_genericas;

import android.content.Context;
import android.os.StrictMode;

import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;
import com.novoda.merlin.MerlinsBeard;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("Convert2Lambda")
public class CheckConnection {
    static int timeOut;
    static InetAddress inetAddress;

    public static boolean isConnected(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        MerlinsBeard merlinsBeard = new MerlinsBeard.Builder().build(context);
        return merlinsBeard.hasInternetAccess();
    }

    public static boolean internetConnAvaliable() {
        timeOut  = 3000;
        inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        inetAddress = InetAddress.getByName("1.1.1.1");
                        if (inetAddress == null) {
                            inetAddress = InetAddress.getByName("8.8.8.8");
                        }
                        return inetAddress;
                    } catch (UnknownHostException exception) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException | ExecutionException | TimeoutException exp) {
            exp.printStackTrace();
        }
        return inetAddress != null && !inetAddress.toString().equals("");
    }
}